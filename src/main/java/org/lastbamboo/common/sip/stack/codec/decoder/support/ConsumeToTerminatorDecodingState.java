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


/**
 * Consumes until a fixed (ASCII) character is reached.
 * The terminator is skipped.
 * 
 * @author irvingd
 * @author trustin
 * @version $Rev: 215 $, $Date: 2006-11-27 01:13:35 -0500 (Mon, 27 Nov 2006) $
 */
public abstract class ConsumeToTerminatorDecodingState implements DecodingState 
    {
    
    private ByteBuffer buffer;

    private final byte m_terminator;

    /**
     * Creates a new instance.
     * 
     * @param terminator The terminator.
     */
    protected ConsumeToTerminatorDecodingState(final byte terminator)
        {
        m_terminator = terminator;
        }

    public DecodingState decode(final ByteBuffer in, 
        final ProtocolDecoderOutput out) throws Exception
        {
        int beginPos = in.position();
        int terminatorPos = -1;
        int limit = in.limit();

        for (int i = beginPos; i < limit; i++)
            {
            byte b = in.get(i);
            if (b == this.m_terminator)
                {
                terminatorPos = i;
                break;
                }
            }

        if (terminatorPos >= 0)
            {
            final ByteBuffer product;

            if (beginPos < terminatorPos)
                {
                in.limit(terminatorPos);

                if (buffer == null)
                    {
                    product = in.slice();
                    }
                else
                    {
                    buffer.put(in);
                    product = buffer.flip();
                    buffer = null;
                    }

                in.limit(limit);
                }
            else
                {
                // When input contained only terminator rather than actual
                // data...
                if (buffer == null)
                    {
                    product = ByteBuffer.allocate(1);
                    product.limit(0);
                    }
                else
                    {
                    product = buffer.flip();
                    buffer = null;
                    }
                }
            
            in.position(terminatorPos + 1);
            return finishDecode(product, out);
            }
        else
            {
            if (buffer == null)
                {
                buffer = ByteBuffer.allocate(in.remaining());
                buffer.setAutoExpand(true);
                }
            buffer.put(in);
            return this;
            }
        }

    protected abstract DecodingState finishDecode(ByteBuffer product,
            ProtocolDecoderOutput out) throws Exception;
    }
