package org.lastbamboo.common.sip.stack.message;

import java.util.Map;

import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.littleshoot.mina.common.ByteBuffer;

/**
 * SIP response class.
 */
public class SipResponse extends AbstractSipMessage
    {

    private final int m_statusCode;
    private final String m_reasonPhrase;
    
    /**
     * Creates a new SIP response message.
     * 
     * @param statusCode The status code of the response.
     * @param reasonPhrase The reason phrase.
     * @param headers The message headers.
     * @param body The message body.
     */
    public SipResponse(final int statusCode, 
        final String reasonPhrase, Map<String, SipHeader> headers, 
        final ByteBuffer body)
        {
        super(statusCode, reasonPhrase, headers, body);
        this.m_statusCode = statusCode;
        this.m_reasonPhrase = reasonPhrase;
        }

    /**
     * Creates a new SIP response message.
     * 
     * @param statusCode The status code of the response.
     * @param reasonPhrase The reason phrase.
     * @param headers The message headers.
     */
    public SipResponse(final int statusCode, final String reasonPhrase, 
        final Map<String, SipHeader> headers)
        {
        super(statusCode, reasonPhrase, headers);
        this.m_statusCode = statusCode;
        this.m_reasonPhrase = reasonPhrase;
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitResponse(this);
        }

    public String getReasonPhrase()
        {
        return m_reasonPhrase;
        }

    public int getStatusCode()
        {
        return m_statusCode;
        }
    
    }
