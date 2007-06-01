package org.lastbamboo.common.sip.stack.codec.decoder.support;

import org.apache.mina.common.ByteBuffer;


/**
 * Consumes a {@link ByteBuffer} up to linear whitespace.<p>
 * 
 * Taken from the AsyncWeb project.  
 */
public abstract class ConsumeToLinearWhitespaceDecodingState extends
    ConsumeToDynamicTerminatorDecodingState
    {

    @Override
    protected boolean isTerminator(final byte b)
        {
        // Skip spaces and tabs.
        return (b == 32 || b == 9);
        }
    }
