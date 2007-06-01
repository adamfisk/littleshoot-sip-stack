package org.lastbamboo.common.sip.stack.message;

import java.util.List;
import java.util.Map;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Interface for a single SIP message.
 */
public interface SipMessage
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
     * Accessor for the complete message bytes.
     * 
     * @return The complete message bytes.
     */
    //ByteBuffer getBytes();

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
    List getRouteSet();
    
    /**
     * Accessor for the message body.
     * 
     * @return The message body.
     */
    ByteBuffer getBody();

    /**
     * Accessor for the total length of the message including both the headers
     * and the message body.
     * 
     * @return The length of the message.
     */
    //int getTotalLength();
    
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
