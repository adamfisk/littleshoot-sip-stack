package org.lastbamboo.common.sip.stack.codec.decoder.support;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * Interface for the various decoding states.  From AsyncWeb.
 */
public interface DecodingState
    {
    
    /**
     * Decodes the input for this state.
     * 
     * @param in The read data.
     * @param out The output to write to.
     * @return The next decoding state.
     * @throws Exception If any error occurs.
     */
    DecodingState decode(ByteBuffer in, ProtocolDecoderOutput out)
        throws Exception;
    }
