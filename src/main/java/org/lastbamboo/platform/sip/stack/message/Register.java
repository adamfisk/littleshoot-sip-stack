package org.lastbamboo.platform.sip.stack.message;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Register request class.
 */
public class Register extends AbstractSipMessage
    {

    private static final Log LOG = LogFactory.getLog(Register.class);
    
    /**
     * Creates a new register request.
     * 
     * @param requestLine The first line of the register request.
     * @param headers The request headers.
     */
    public Register(final String requestLine, final Map headers)
        {
        super(requestLine, headers);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitRegister(this);
        }

    }
