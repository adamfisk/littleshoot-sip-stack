package org.lastbamboo.common.sip.stack.transport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoFuture;
import org.littleshoot.mina.common.IoFutureListener;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.WriteFuture;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.sip.stack.message.Invite;
import org.lastbamboo.common.sip.stack.message.Register;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipMessageUtils;
import org.lastbamboo.common.sip.stack.message.SipResponse;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.transaction.client.SipClientTransaction;
import org.lastbamboo.common.sip.stack.transaction.client.SipTransactionFactory;
import org.littleshoot.util.NetworkUtils;

/**
 * The transport layer implementation for TCP.
 */
public final class SipTcpTransportLayerImpl implements SipTcpTransportLayer, 
    IoFutureListener
    {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    
    /**
     * Map of InetSocketAddresses to IoSessions.
     */
    private final Map<InetSocketAddress, IoSession> m_socketAddressesToIo = 
        new ConcurrentHashMap<InetSocketAddress, IoSession>();
    private final SipHeaderFactory m_headerFactory;
    private final SipTransactionFactory m_transactionFactory;
    private final SipMessageFactory m_messageFactory;
    
    /**
     * Constructs an instance of the TCP transport layer.
     * 
     * @param transactionFactory The factory for creating transactions.
     * @param headerFactory The factory for creating any headers necessary
     * before writing messages, such as the Via header.
     * @param messageFactory The factory for creating SIP messages.
     */
    public SipTcpTransportLayerImpl(
        final SipTransactionFactory transactionFactory,
        final SipHeaderFactory headerFactory, 
        final SipMessageFactory messageFactory)
        {
        this.m_transactionFactory = transactionFactory;
        this.m_headerFactory = headerFactory;
        this.m_messageFactory = messageFactory;
        }
    
    public void addConnection(final IoSession io)
        {
        final InetSocketAddress remoteAddress = 
            (InetSocketAddress) io.getRemoteAddress();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Adding connection for socket address: "+remoteAddress);
            }
        this.m_socketAddressesToIo.put(remoteAddress, io);
        }
    
    public void removeConnection(final IoSession io)
        {
        final InetSocketAddress remoteAddress = 
            (InetSocketAddress) io.getRemoteAddress();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Removing connection for socket address: "+remoteAddress);
            }
        this.m_socketAddressesToIo.remove(remoteAddress);
        }
    
    public SipClientTransaction invite(final Invite request, 
        final IoSession io, final OfferAnswerTransactionListener transactionListener)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing request to: "+io);
            }
        final SipMessage viaAdded;
        try
            {
            viaAdded = addVia(request);
            }
        catch (final UnknownHostException e)
            {
            LOG.error("Could not get local host", e);
            return null;
            }
        // We need to create the transaction after adding the Via header
        // because the branch ID in the Via is used in the key for the
        // transaction.
        final SipClientTransaction clientTransaction = 
            this.m_transactionFactory.createClientTransaction(viaAdded, 
                transactionListener);
        
        write(viaAdded, io, true);
        return clientTransaction;
        }
    
    public SipClientTransaction register(final Register request, 
        final IoSession io, final OfferAnswerTransactionListener transactionListener)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing request to: "+io);
            }
        final SipMessage viaAdded;
        try
            {
            viaAdded = addVia(request);
            }
        catch (final UnknownHostException e)
            {
            LOG.error("Could not get local host", e);
            return null;
            }
        // We need to create the transaction after adding the Via header
        // because the branch ID in the Via is used in the key for the
        // transaction.
        final SipClientTransaction clientTransaction = 
            this.m_transactionFactory.createClientTransaction(viaAdded, 
                transactionListener);
        
        write(viaAdded, io, true);
        return clientTransaction;
        }

    public void writeRequestStatelessly(final Invite request, 
        final IoSession io)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing request statelessly...");
            }
        try
            {
            final SipMessage viaAdded = addVia(request);
            write(viaAdded, io);
            }
        catch (final UnknownHostException e)
            {
            LOG.error("Could not get local host", e);
            }  
        }
    
    private Register addVia(final Register request) throws UnknownHostException
        {
        final InetAddress localHost = NetworkUtils.getLocalHost();
        final SipHeader via = this.m_headerFactory.createSentByVia(localHost);
        
        return this.m_messageFactory.addVia(request, via);
        }

    private Invite addVia(final Invite request) throws UnknownHostException
        {
        final InetAddress localHost = NetworkUtils.getLocalHost();
        final SipHeader via = this.m_headerFactory.createSentByVia(localHost);
        
        return this.m_messageFactory.addVia(request, via);
        }

    public boolean writeResponse(final InetSocketAddress socketAddress, 
        final SipResponse response)
        {
        final IoSession io = this.m_socketAddressesToIo.get(socketAddress);
        
        if (io == null)
            {
            // This could be bad, although the user may have just left.
            writeDebugData(socketAddress);
            return false;
            }
        
        write(response, io);
        return true;
        }
    
    private void write(final SipMessage message, final IoSession io)
        {
        write(message, io, true);
        }
    
    private void write(final SipMessage message, final IoSession io, 
        final boolean listen)
        {
        final WriteFuture wf = io.write(message);
        if (listen)
            {
            wf.addListener(this);
            }
        }
    
    public WriteFuture writeCrlfKeepAlive(final IoSession io)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing double CRLF");
            }
        final String doubleCrlf = "\r\n\r\n";
        final ByteBuffer buf;
        try
            {
            buf = ByteBuffer.wrap(doubleCrlf.getBytes("US-ASCII"));
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("Bad encoding??", e);
            return null;
            }

        return io.write(buf);
        }

    public void writeResponse(final SipResponse response) throws IOException
        {
        final InetSocketAddress nextHop = 
            SipMessageUtils.extractNextHopFromVia(response);
        writeResponse(nextHop, response);
        }

    public boolean hasConnectionForAny(
        final Collection<InetSocketAddress> socketAddresses)
        {
        synchronized (this.m_socketAddressesToIo)
            {
            final Collection<InetSocketAddress> existingAddresses = 
                this.m_socketAddressesToIo.keySet();
            return CollectionUtils.containsAny(existingAddresses, 
                socketAddresses);
            }
        }

    public void writeRequest(
        final Collection<InetSocketAddress> socketAddresses, 
        final Invite request)
        {
        synchronized (this.m_socketAddressesToIo)
            {
            final Collection<InetSocketAddress> existingAddresses = 
                this.m_socketAddressesToIo.keySet();
            final Collection<InetSocketAddress> intersection = 
                CollectionUtils.intersection(existingAddresses, 
                    socketAddresses);
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Found "+intersection.size()+
                    " matching connections...");
                }
            for (final InetSocketAddress socketAddress : intersection)
                {
                final IoSession io = 
                    this.m_socketAddressesToIo.get(socketAddress);
                writeRequestStatelessly(request, io);
                
                if (LOG.isDebugEnabled())
                    {
                    LOG.debug("Sent request to: "+io);
                    }
                return;
                }
            }
        }

    public void operationComplete(final IoFuture future)
        {
        if (LOG.isDebugEnabled())
            {
            final IoSession sess = future.getSession();
            LOG.debug("Wrote messages: "+ sess.getWrittenMessages() + 
                " on session " + sess); 
            }
        }
    
    private void writeDebugData(final InetSocketAddress socketAddress)
        {
        LOG.warn("No connection for socket address: "+socketAddress);
        LOG.warn("hashCode(): "+socketAddress.hashCode());
        if (this.m_socketAddressesToIo.size() < 10)
            {
            LOG.warn("Existing connections: "+
                this.m_socketAddressesToIo);
            final StringBuilder sb = new StringBuilder();
            final Set<InetSocketAddress> keys = 
                this.m_socketAddressesToIo.keySet();
            for (final InetSocketAddress sa: keys)
                {
                sb.append(sa.toString());
                sb.append(" code: ");
                sb.append(sa.hashCode());
                sb.append(", ");
                }
            LOG.warn("hashCode()s: "+sb.toString());
            }
        }
    }
