package org.lastbamboo.platform.sip.stack.message;

import java.util.Map;

/**
 * Class containing data for an unknown SIP message.
 */
public class UnknownMessage extends AbstractSipMessage
    {
  
    /**
     * Creates a new unknown message.
     * 
     * @param requestOrResponseLine The first line of the request or of the
     * response.
     * @param headers The message headers.
     * @param body The message body.
     */
    public UnknownMessage(final String requestOrResponseLine, final Map headers, 
        final byte[] body)
        {
        super(requestOrResponseLine, headers, body);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitUnknownRequest(this);
        }

    }
