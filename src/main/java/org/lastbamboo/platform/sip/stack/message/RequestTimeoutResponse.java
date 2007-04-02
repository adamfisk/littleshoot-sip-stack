package org.lastbamboo.platform.sip.stack.message;

import java.util.Map;

/**
 * Response message for a request timing out.
 */
public class RequestTimeoutResponse extends AbstractSipMessage
    {

    /**
     * Creates a new 408 Request Timeout response.
     * 
     * @param startLine The first line of the response.
     * @param headers The headers in the message.
     */
    public RequestTimeoutResponse(final String startLine, final Map headers)
        {
        super(startLine, headers);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitRequestTimedOut(this);
        }

    }
