package org.lastbamboo.platform.sip.stack.message;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderNames;

/**
 * Factory for invite requests.
 */
public class InviteFactory implements SingleSipMessageFactory
    {

    private static final Log LOG = LogFactory.getLog(InviteFactory.class);
    
    public SipMessage createSipMessage(final String requestLine, 
        final Map headers, final byte[] body) throws IOException
        {
        LOG.debug("Creating invite...");
        if (!headers.containsKey(SipHeaderNames.VIA))
            {
            LOG.error("Should contain Via header: "+headers);
            throw new IllegalArgumentException(
                "Should contain Via header: "+headers);
            }
        return new Invite(requestLine, headers, body);
        }

    }
