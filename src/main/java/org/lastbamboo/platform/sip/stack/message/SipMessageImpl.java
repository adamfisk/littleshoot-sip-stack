package org.lastbamboo.platform.sip.stack.message;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Generalized SIP message.
 */
public class SipMessageImpl extends AbstractSipMessage
    {

    private static final Log LOG = LogFactory.getLog(SipMessageImpl.class);

    private static final byte[] EMPTY_BODY = new byte[0];
    
    /**
     * Creates a new message.
     * 
     * @param requestOrResponseLine The request or response line of the message.
     * @param headers The message headers.
     * @param body The message body.
     */
    public SipMessageImpl(final String requestOrResponseLine, final Map headers, 
        final byte[] body)
        {
        super(requestOrResponseLine, headers, body);
        }

    /**
     * Creates a new message.
     * 
     * @param requestOrResponseLine The request or response line of the message.
     * @param headers The message headers.
     */
    public SipMessageImpl(final String requestOrResponseLine, final Map headers)
        {
        super(requestOrResponseLine, headers, EMPTY_BODY);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        // This is a general message with no typing.
        LOG.warn("Calling accept on generic message -- no type to visit!!");
        }
    }
