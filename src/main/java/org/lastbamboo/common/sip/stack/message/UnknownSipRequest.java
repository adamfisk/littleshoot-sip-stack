package org.lastbamboo.common.sip.stack.message;

import java.net.URI;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Class containing data for an unknown SIP message.
 */
public class UnknownSipRequest extends AbstractSipMessage
    {
  
    /**
     * Creates a new unknown message.
     * 
     * @param method The request method string.
     * @param requestUri the URI for the request.
     * @param headers The message headers.
     * @param body The message body.
     */
    public UnknownSipRequest(final String method, final URI requestUri, 
        final Map<String, SipHeader> headers, final ByteBuffer body)
        {
        super(createRequestLine(method, requestUri), SipMethod.UNKNOWN, 
            headers, body);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitUnknownRequest(this);
        }

    }
