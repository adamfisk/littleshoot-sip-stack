package org.lastbamboo.platform.sip.stack.message;

import java.io.IOException;
import java.util.Map;

/**
 * Factory for creating unknown messages.
 */
public class UnknownRequestFactory implements SingleSipMessageFactory
    {

    public SipMessage createSipMessage(final String requestLine, 
        final Map headers, final byte[] body) throws IOException
        {
        return new UnknownMessage(requestLine, headers, body);
        }

    }
