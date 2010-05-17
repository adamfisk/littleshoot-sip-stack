package org.lastbamboo.common.sip.stack.message;

import java.net.URI;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * SIP REGISTER request class.
 */
public class Register extends AbstractSipMessage
    {

    private static final Log LOG = LogFactory.getLog(Register.class);
    
    /**
     * Creates a new register request.
     * 
     * @param requestUri The first line of the register request.
     * @param headers The request headers.
     */
    public Register(final URI requestUri, final Map<String, SipHeader> headers)
        {
        super(SipMethod.REGISTER, requestUri, headers);
        }

    /**
     * Creates a new REGISTER request.
     * 
     * @param startLine The first line of the request.
     * @param headers The headers.
     * @param body The body.
     */
    public Register(final String startLine, 
        final Map<String, SipHeader> headers, final ByteBuffer body)
        {
        super(startLine, SipMethod.REGISTER, headers, body);
        }

    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitRegister(this);
        }

    }
