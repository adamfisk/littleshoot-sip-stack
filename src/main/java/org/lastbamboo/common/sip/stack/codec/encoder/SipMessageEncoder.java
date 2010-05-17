package org.lastbamboo.common.sip.stack.codec.encoder;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.sip.stack.message.SipMessage;

/**
 * Encoder that simply converts SIP messages to {@link ByteBuffer}s.  This is
 * separate from the {@link ProtocolEncoder} for easier testing.
 */
public interface SipMessageEncoder
    {

    /**
     * Encodes the specified SIP message.
     * 
     * @param message The message to encode.
     * @return The {@link ByteBuffer} ready for writing (flipped).
     */
    ByteBuffer encode(SipMessage message);

    }
