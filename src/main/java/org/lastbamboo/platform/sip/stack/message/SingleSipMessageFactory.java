package org.lastbamboo.platform.sip.stack.message;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for classes responsible for creating individual SIP messages,
 * such as INVITE.
 */
public interface SingleSipMessageFactory
    {

    /**
     * Creates a new SIP message.
     * 
     * @param requestOrResponseLine The first line of the request or response.
     * @param headers The {@link Map} of message headers.
     * @param body The message body.  This will frequently be empty, as many
     * messages do not contain a body.
     * @return A new SIP message instance from the request or response line,
     * the message headers, and the message body.
     * @throws IOException If there's any IO error creating the message.
     */
    SipMessage createSipMessage(final String requestOrResponseLine, 
        final Map headers, final byte[] body) throws IOException;
    }
