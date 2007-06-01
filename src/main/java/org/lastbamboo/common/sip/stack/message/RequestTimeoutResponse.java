package org.lastbamboo.common.sip.stack.message;

import java.util.Map;

import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Response message for a request timing out.
 */
public class RequestTimeoutResponse extends SipResponse
    {

    /**
     * Creates a new 408 Request Timeout response.
     * 
     * @param headers The headers in the message.
     */
    public RequestTimeoutResponse(final Map<String, SipHeader> headers)
        {
        super(SipResponseCode.REQUEST_TIMEOUT, "Request Timeout", headers);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitRequestTimedOut(this);
        }

    }
