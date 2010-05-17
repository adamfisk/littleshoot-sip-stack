package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Factory for creating a single SIP REGISTER request.
 */
public class SipRegisterFactory implements SingleSipMessageFactory
    {

    private final URI m_requestUri;

    /**
     * Creates a new REGISTER factory for a single request.
     * 
     * @param requestUri The request URI.
     */
    public SipRegisterFactory(final URI requestUri)
        {
        m_requestUri = requestUri;
        }

    public SipMessage createSipMessage(final Map<String, SipHeader> headers,
        final ByteBuffer body) throws IOException
        {
        return new Register(this.m_requestUri, headers);
        }

    }
