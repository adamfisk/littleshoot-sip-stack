package org.lastbamboo.common.sip.stack.codec.decoder.support;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipCodecUtils;


/**
 * Consumes a {@link ByteBuffer} up to linear whitespace.<p>
 * 
 * Taken from the AsyncWeb project.  
 */
public abstract class ConsumeToLinearWhitespaceDecodingState extends
    ConsumeToTerminatorDecodingState
    {

    /**
     * Creates a new LWS decoding state.  This only checks for a single space
     * because that's what SIP typically requires.
     */
    public ConsumeToLinearWhitespaceDecodingState()
        {
        super(SipCodecUtils.SP);
        }
    }
