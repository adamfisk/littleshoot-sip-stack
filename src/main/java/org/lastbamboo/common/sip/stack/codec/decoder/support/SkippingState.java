package org.lastbamboo.common.sip.stack.codec.decoder.support;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


/**
 * Skips data until a byte other than the byte to skip is reached.  Modified
 * from AsyncWeb code.
 */
public abstract class SkippingState implements DecodingState
    {

    private final byte m_byteToSkip;

    /**
     * Creates a new skipping state for the specified byte.
     * 
     * @param byteToSkip The byte to skip.
     */
    public SkippingState(final byte byteToSkip)
        {
        m_byteToSkip = byteToSkip;
        }

    public DecodingState decode(ByteBuffer in, ProtocolDecoderOutput out)
            throws Exception
        {
        final int beginPos = in.position();
        final int limit = in.limit();
        for (int i = beginPos; i < limit; i++)
            {
            final byte b = in.get(i);
            if (b != m_byteToSkip)
                {
                in.position(i);
                return finishDecode();
                }
            else
                {
                }
            }

        in.position(limit);
        return this;
        }

    protected abstract DecodingState finishDecode() throws Exception;
    }
