package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Factory for createing unknown request types.  We just try to parse them
 * normally.
 */
public class UnknownSipRequestFactory implements SingleSipMessageFactory
    {

    private final URI m_uri;
    private final String m_method;

    /**
     * Create a new factory for unknown request types.
     * 
     * @param method The request method.
     * @param uri The request URI.
     */
    public UnknownSipRequestFactory(final String method, final URI uri)
        {
        m_method = method;
        m_uri = uri;
        }

    public SipMessage createSipMessage(Map<String, SipHeader> headers,
            ByteBuffer body) throws IOException
        {
        return new UnknownSipRequest(m_method, m_uri, headers, body);
        }

    }
