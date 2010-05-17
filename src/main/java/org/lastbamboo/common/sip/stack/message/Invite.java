package org.lastbamboo.common.sip.stack.message;

import java.net.URI;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * A SIP INVITE message.  
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
     * @param requestUri The request URI.
     * @param headers The message headers.
     * @param body The message body.
     */
    public Invite(final URI requestUri, 
        final Map<String, SipHeader> headers, final ByteBuffer body)
        {
        super(SipMethod.INVITE, requestUri, headers, body);
        }

    /**
     * Creates a new INVITE request.
     * 
     * @param startLine The first line of the request.
     * @param headers The headers.
     * @param body The body.
     */
    public Invite(final String startLine, 
        final Map<String, SipHeader> headers, final ByteBuffer body)
        {
        super(startLine, SipMethod.INVITE, headers, body);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitInvite(this);
        }

    }
