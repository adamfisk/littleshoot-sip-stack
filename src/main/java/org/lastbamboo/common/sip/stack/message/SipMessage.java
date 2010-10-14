package org.lastbamboo.common.sip.stack.message;

import java.util.List;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.offer.answer.OfferAnswerMessage;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;

/**
 * Interface for a single SIP message.
 */
public interface SipMessage extends OfferAnswerMessage
    {

    /**
     * Accessor for the branch ID for the topmost Via header of this message.
     * 
     * @return The branch ID for this message from the topmost Via header.
     */
    String getBranchId();

    /**
     * The method the SIP message, such as "INVITE" or "REGISTER".
     * 
     * @return The method of the message.
     */
    SipMethod getMethod();

    /**
     * Accessor for the complete header with the specified name.
     * 
     * @param headerName The name of the header to access.
     * @return The complete header instance.
     */
    SipHeader getHeader(final String headerName);

    /**
     * Accessor for the list of routes for this message.
     * 
     * @return The list of routes for this message.
     */
    List<SipHeaderValue> getRouteSet();
    
    /**
     * Accessor for the message body.
     * 
     * @return The message body.
     */
    ByteBuffer getBody();
    
    /**
     * Accepts the specified message visitor.
     * 
     * @param visitor The visitor to accept.
     */
    void accept(SipMessageVisitor visitor);

    /**
     * Accessor the first line of the message.  For requests, this is the 
     * request line containing the request URI.  For responses, this is the
     * status line.
     * 
     * @return The first line of the message, as in the request line for 
     * requests and the status line for responses.
     */
    String getStartLine();

    /**
     * Accessor for all headers in the message.
     * 
     * @return All headers in the message.
     */
    Map<String, SipHeader> getHeaders();
    
    }
