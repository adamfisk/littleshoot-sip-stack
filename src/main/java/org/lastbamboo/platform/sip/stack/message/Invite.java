package org.lastbamboo.platform.sip.stack.message;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A SIP INVTE message.  
 */
public class Invite extends AbstractSipMessage
    {

    /**
     * Logger for this class.
     */
    private static final Log LOG = LogFactory.getLog(Invite.class);
    
    /**
     * Creates a new INVITE request with the specified first line of the 
     * message, the specified headers, and the specified message body.
     * 
     * @param requestLine The first line of the message.
     * @param headers The message headers.
     * @param body The message body.
     */
    public Invite(final String requestLine, final Map headers, 
        final byte[] body)
        {
        super(requestLine, headers, body);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitInvite(this);
        }

    }
