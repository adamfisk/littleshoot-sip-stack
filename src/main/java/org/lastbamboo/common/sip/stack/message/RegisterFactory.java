package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderNames;

/**
 * Factory for creating register requests.
 */
public final class RegisterFactory implements SingleSipMessageFactory
    {

    private final Log LOG = LogFactory.getLog(RegisterFactory.class);
    
    public SipMessage createSipMessage(final String requestLine, 
        final Map headers, final byte[] body) throws IOException
        {
        LOG.debug("Creating register...");
        if (!headers.containsKey(SipHeaderNames.VIA))
            {
            LOG.error("Should contain Via header: "+headers);
            throw new IllegalArgumentException(
                "Should contain Via header: "+headers);
            }
        return new Register(requestLine, headers);
        }

    }
