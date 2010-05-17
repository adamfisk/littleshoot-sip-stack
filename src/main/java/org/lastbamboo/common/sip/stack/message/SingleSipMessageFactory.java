package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Interface for classes responsible for creating individual SIP messages,
 * such as INVITE.
 */
public interface SingleSipMessageFactory
    {

    /**
     * Creates a new SIP message.
     * 
     * @param headers The {@link Map} of message headers.
     * @param body The message body.  This will frequently be empty, as many
     * messages do not contain a body.
     * @return A new SIP message instance from the request or response line,
     * the message headers, and the message body.
     * @throws IOException If there's any IO error creating the message.
     */
    SipMessage createSipMessage(final Map<String, SipHeader> headers, 
        final ByteBuffer body) throws IOException;
    }
