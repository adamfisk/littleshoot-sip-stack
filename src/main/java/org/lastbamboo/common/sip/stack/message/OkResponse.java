package org.lastbamboo.common.sip.stack.message;

import java.util.Map;

/**
 * SIP 200 OK response message.  This could be a response to any type of 
 * request, such as INVITE or REGISTER.
 */
public class OkResponse extends AbstractSipMessage
    {
    
    /**
     * Creates a new OK response instance with the specified first line of 
     * the message, the specified headers, and the specified message body.
     * 
     * @param responseLine The first line of the message.
     * @param headers The message headers.
     * @param body The message body.
     */
    public OkResponse(final String responseLine, final Map headers, 
        final byte[] body)
        {
        super(responseLine, headers, body);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitOk(this);
        }

    }
