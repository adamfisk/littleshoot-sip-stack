/*
 * Copyright 2006 The asyncWeb Team.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lastbamboo.common.sip.stack.codec.decoder.support;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A {@link DecodingState} which consumes all received bytes until a configured
 * number of read bytes has been reached.
 * 
 * Modified slightly for LittleShoot -- code from the great AsyncWeb project.
 * 
 * @author irvingd
 * @author trustin
 * @version $Rev: 237 $, $Date: 2007-03-02 03:18:55 -0500 (Fri, 02 Mar 2007) $
 */
public abstract class FixedLengthDecodingState implements DecodingState 
    {
    private static final Logger LOG = 
        LoggerFactory.getLogger(FixedLengthDecodingState.class);
    
    private final int m_length;

    private ByteBuffer m_buffer;

    /**
     * Constructs with a known decode length.
     * 
     * @param length The decode length
     */
    public FixedLengthDecodingState(int length)
        {
        this.m_length = length;
        }
  
    public DecodingState decode(final ByteBuffer in, 
        final ProtocolDecoderOutput out) throws Exception
        {
        if (m_buffer == null)
            {
            if (in.remaining() >= m_length)
                {
                int limit = in.limit();
                in.limit(in.position() + m_length);
                final ByteBuffer product = in.slice();
                in.position(in.position() + m_length);
                in.limit(limit);
                return finishDecode(product, out);
                }
            else
                {
                m_buffer = ByteBuffer.allocate(m_length);
                m_buffer.put(in);
                return this;
                }
            }
        else
            {
            if (in.remaining() >= m_length - m_buffer.position())
                {
                int limit = in.limit();
                in.limit(in.position() + m_length - m_buffer.position());
                m_buffer.put(in);
                in.limit(limit);
                ByteBuffer product = this.m_buffer;
                this.m_buffer = null;
                return finishDecode(product.flip(), out);
                }
            else
                {
                m_buffer.put(in);
                return this;
                }
            }
        }
  
    protected abstract DecodingState finishDecode(final ByteBuffer readData, 
        final ProtocolDecoderOutput out) throws Exception;
    }
