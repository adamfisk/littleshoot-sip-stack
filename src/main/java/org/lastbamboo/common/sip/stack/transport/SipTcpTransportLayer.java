package org.lastbamboo.common.sip.stack.transport;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.WriteFuture;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.sip.stack.message.Invite;
import org.lastbamboo.common.sip.stack.message.Register;
import org.lastbamboo.common.sip.stack.message.SipResponse;
import org.lastbamboo.common.sip.stack.transaction.client.SipClientTransaction;

/**
 * Interface for calls to the TCP transport layer, as specified in section
 * 18 of RFC 3261, starting on page 141.  
 */
public interface SipTcpTransportLayer
    {

    /**
     * Adds a mapping between the remote host for the connection and
     * the class for reading and writing data from and to that host.
     * 
     * @param io The connection to the host.
     */
    void addConnection(IoSession io);
    
    /**
     * Writes a REGISTER request that is a part of a transaction.  This method 
     * should add the transaction to a transaction map prior to sending it to 
     * ensure we don't receive responses prior to the maintaining data for the 
     * transaction.
     * 
     * @param message The SIP message we're sending.
     * @param io The class for writing the message.
     * @param listener Class that listens for transaction events.
     * @return The client transaction for the request.
     */
    SipClientTransaction register(Register message, 
        IoSession io, OfferAnswerTransactionListener listener);

    /**
     * Writes a message that is a part of a transaction.  This method should
     * add the transaction to a transaction map prior to sending it to ensure
     * we don't receive responses prior to the maintaining data for the 
     * transaction.
     * 
     * @param message The SIP message we're sending.
     * @param io The class for writing the message.
     * @param listener Class that listens for transaction events.
     * @return The client transaction for the request.
     */
    SipClientTransaction invite(Invite message, 
        IoSession io, OfferAnswerTransactionListener listener);

    /**
     * Writes the specified request without creating a transaction.
     * 
     * @param request The SIP request to write.
     * @param io The class that will write hte message.
     */
    void writeRequestStatelessly(Invite request, IoSession io);

    /**
     * Writes the specified response message to the connection associated with
     * the specified address.
     * 
     * @param socketAddress The address and port remote of the client to send 
     * the response to. 
     * @param response The response to send.
     * @return Returns <code>true</code> of we successfull processed the 
     * response and passed it to the network for transport, otherwise 
     * <code>false</code>.
     */
    boolean writeResponse(InetSocketAddress socketAddress, SipResponse response);

    /**
     * Writes a response using the routing information in the topmost Via 
     * header.
     * 
     * @param response The response to write.
     * @throws IOException If we could not route the response for any reason.
     */
    void writeResponse(SipResponse response) throws IOException;

    /**
     * Checks whether or not the transport layer has a connection to any
     * of the specified socket addresses.
     * 
     * @param socketAddresses The collection of socket addresses to look for.
     * @return <code>true</code> if there is a connection to any of the 
     * specified addresses, otherwise <code>false</code>.
     */
    boolean hasConnectionForAny(Collection<InetSocketAddress> socketAddresses);

    /**
     * Writes the specified request to the first address in the collection
     * we have a connection for.
     * 
     * @param socketAddresses The socket addresses to attempt to send a 
     * message to.
     * @param request The request to send.
     */
    void writeRequest(Collection<InetSocketAddress> socketAddresses, 
        Invite request);

    /**
     * Writes a CRLF keep-alive message to the given reader/writer, as
     * specified in the SIP outbound drafts.  The keep-alive both keeps NATs
     * from closing active connections and supplies insurance to detect when
     * connections have been closed.
     * 
     * @param io The reader/writer to write the messages over.
     * @return The future for the write.
     */
    WriteFuture writeCrlfKeepAlive(IoSession io);

    /**
     * Removes this connection.
     * 
     * @param session The connection to remove.
     */
    void removeConnection(IoSession session);

    }
