package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Factory for creating a single SIP INVITE message.
 */
public class SipInviteFactory implements SingleSipMessageFactory
    {

    private final URI m_requestUri;

    /**
     * Creates a new factory for creating INVITE requests for the specified
     * URI.  This is typically only used to create a single INVITE.
     * 
     * @param requestUri The request URI for the INVITE.
     */
    public SipInviteFactory(final URI requestUri)
        {
        m_requestUri = requestUri;
        }

    public SipMessage createSipMessage(final Map<String, SipHeader> headers,
        final ByteBuffer body) throws IOException
        {
        return new Invite(this.m_requestUri, headers, body);
        }

    }
