package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Factory for creating SIP responses.
 */
public class SipResponseFactory implements SingleSipMessageFactory
    {

    private final String m_reasonPhrase;
    private final int m_statusCode;

    /**
     * Creates a new factory for generating SIP responses.
     * 
     * @param statusCode The status code for the response.
     * @param reasonPhrase The reason phrase for the response.
     */
    public SipResponseFactory(final int statusCode, final String reasonPhrase)
        {
        m_reasonPhrase = reasonPhrase;
        m_statusCode = statusCode;
        }

    public SipMessage createSipMessage(final Map<String, SipHeader> headers,
        final ByteBuffer body) throws IOException
        {
        return new SipResponse(m_statusCode, m_reasonPhrase, headers, body);
        }

    }
