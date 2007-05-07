package org.lastbamboo.common.sip.stack.transport;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.protocol.CloseListener;
import org.lastbamboo.common.protocol.ReaderWriter;
import org.lastbamboo.common.protocol.ReaderWriterUtils;
import org.lastbamboo.common.protocol.WriteData;
import org.lastbamboo.common.protocol.WriteListener;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipMessageUtils;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.transaction.SipClientTransaction;
import org.lastbamboo.common.sip.stack.transaction.SipTransactionFactory;
import org.lastbamboo.common.sip.stack.transaction.SipTransactionListener;
import org.lastbamboo.common.util.ByteBufferUtils;
import org.lastbamboo.common.util.NetworkUtils;
import org.springframework.util.Assert;

/**
 * The transport layer implementation for TCP.
 */
public final class SipTcpTransportLayerImpl implements SipTcpTransportLayer,
    CloseListener, WriteListener
    {

    private static final Log LOG = 
        LogFactory.getLog(SipTcpTransportLayerImpl.class);
    
    /**
     * Map of InetSocketAddresses to ReaderWriters.
     */
    private final Map m_socketAddressesToReaderWriters = 
        new ConcurrentHashMap();
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
    
    public void addConnection(final ReaderWriter readerWriter)
        {
        final InetSocketAddress remoteAddress = 
            readerWriter.getRemoteSocketAddress();
        LOG.debug("Adding connection for socket address: "+remoteAddress);
        readerWriter.addCloseListener(this);
        this.m_socketAddressesToReaderWriters.put(remoteAddress, readerWriter);
        }
    
    public SipClientTransaction writeRequest(final SipMessage request, 
        final ReaderWriter readerWriter, 
        final SipTransactionListener transactionListener)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing request to: "+readerWriter);
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
        
        write(viaAdded, readerWriter);
        return clientTransaction;
        }

    public void writeRequestStatelessly(final SipMessage request, 
        final ReaderWriter readerWriter)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing request statelessly...");
            }
        try
            {
            final SipMessage viaAdded = addVia(request);
            write(viaAdded, readerWriter);
            }
        catch (final UnknownHostException e)
            {
            LOG.error("Could not get local host", e);
            }  
        }

    private SipMessage addVia(final SipMessage request) 
        throws UnknownHostException
        {
        final InetAddress localHost = NetworkUtils.getLocalHost();
        final SipHeader via = this.m_headerFactory.createSentByVia(localHost);
        
        return this.m_messageFactory.addVia(request, via);
        }

    public boolean writeResponse(final InetSocketAddress socketAddress, 
        final SipMessage response)
        {
        final ReaderWriter connection = 
            (ReaderWriter) this.m_socketAddressesToReaderWriters.get(
                socketAddress);
        
        if (connection == null)
            {
            if (LOG.isWarnEnabled())
                {
                LOG.warn("No connection for socket address: "+socketAddress);
                LOG.warn("hashCode(): "+socketAddress.hashCode());
                if (this.m_socketAddressesToReaderWriters.size() < 10)
                    {
                    LOG.warn("Existing connections: "+
                        this.m_socketAddressesToReaderWriters);
                    final StringBuffer sb = new StringBuffer();
                    final Collection keys = 
                        this.m_socketAddressesToReaderWriters.keySet();
                    for (final Iterator iter = keys.iterator(); iter.hasNext();)
                        {
                        final InetSocketAddress sa = 
                            (InetSocketAddress) iter.next();
                        sb.append(sa.toString());
                        sb.append(" code: ");
                        sb.append(sa.hashCode());
                        sb.append(", ");
                        }
                    LOG.warn("hashCode()s: "+sb.toString());
                    }
                }
            return false;
            }
        
        write(response, connection);
        return true;
        }
    
    private void write(final SipMessage message, 
        final ReaderWriter readerWriter)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Writing message: "+message);
            }
        final ByteBuffer buf = message.toByteBuffer();
        
        if (LOG.isDebugEnabled())
            {
            final String bufString = ByteBufferUtils.toString(buf);
            final String messageString = message.toString();
            if (!bufString.equals(messageString))
                {
                LOG.warn(bufString + " not equal to " + messageString);
                }
            Assert.isTrue(bufString.equals(messageString));
            }
        readerWriter.writeLater(buf, this);
        }
    
    public void writeCrlfKeepAlive(final ReaderWriter readerWriter)
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
            return;
            }

        readerWriter.writeLater(buf, this);
        }

    public void onClose(final ReaderWriter readerWriter)
        {
        ReaderWriterUtils.removeFromMapValues(
            this.m_socketAddressesToReaderWriters, readerWriter);
        }

    public void onWrite(final WriteData data)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Wrote data: "+data);
            }
        }

    public void writeResponse(final SipMessage response) throws IOException
        {
        final InetSocketAddress nextHop = 
            SipMessageUtils.extractNextHopFromVia(response);
        writeResponse(nextHop, response);
        }

    public boolean hasConnectionForAny(final Collection socketAddresses)
        {
        synchronized (this.m_socketAddressesToReaderWriters)
            {
            final Collection existingAddresses = 
                this.m_socketAddressesToReaderWriters.keySet();
            return CollectionUtils.containsAny(existingAddresses, 
                socketAddresses);
            }
        }

    public void writeRequest(final Collection socketAddresses, 
        final SipMessage request)
        {
        synchronized (this.m_socketAddressesToReaderWriters)
            {
            final Collection existingAddresses = 
                this.m_socketAddressesToReaderWriters.keySet();
            final Collection intersection = 
                CollectionUtils.intersection(existingAddresses, 
                    socketAddresses);
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Found "+intersection.size()+
                    " matching connections...");
                }
            for (final Iterator iter = intersection.iterator(); iter.hasNext();)
                {
                final InetSocketAddress socketAddress = 
                    (InetSocketAddress) iter.next();
                final ReaderWriter rw = 
                    (ReaderWriter) this.m_socketAddressesToReaderWriters.get(
                        socketAddress);
                writeRequestStatelessly(request, rw);
                LOG.debug("Sent request to: "+rw);
                return;
                }
            }
        }
    }
