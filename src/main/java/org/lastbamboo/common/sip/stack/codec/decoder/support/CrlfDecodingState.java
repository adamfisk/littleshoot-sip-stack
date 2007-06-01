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
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.sip.stack.codec.SipCodecUtils;


/**
 * Decodes a single <code>CRLF</code>.
 * If it is found, the bytes are consumed and <code>Boolean.TRUE</code>
 * is provided as the product. Otherwise, read bytes are pushed back
 * to the stream, and <code>Boolean.FALSE</code> is provided as the
 * product.
 * Note that if we find a CR but do not find a following LF, we raise
 * an error.
 * 
 * @author irvingd
 * @author trustin
 * @version $Rev$, $Date$
 */
public abstract class CrlfDecodingState implements DecodingState
    {

    private boolean m_hasCarriageReturn;

    public DecodingState decode(ByteBuffer in, ProtocolDecoderOutput out)
        throws Exception
        {
        boolean found = false;
        boolean finished = false;
        while (in.hasRemaining())
            {
            byte b = in.get();
            if (!m_hasCarriageReturn)
                {
                if (b == SipCodecUtils.CR)
                    {
                    m_hasCarriageReturn = true;
                    }
                else
                    {
                    if (b == SipCodecUtils.LF)
                        {
                        found = true;
                        }
                    else
                        {
                        in.position(in.position() - 1);
                        found = false;
                        }
                    finished = true;
                    break;
                    }
                }
            else
                {
                if (b == SipCodecUtils.LF)
                    {
                    found = true;
                    finished = true;
                    break;
                    }
                else
                    {
                    throw new ProtocolDecoderException(
                            "Expected LF after CR but was: " + b);
                    }
                }
            }

        if (finished)
            {
            m_hasCarriageReturn = false;
            return finishDecode(found, out);
            }
        else
            {
            return this;
            }
        }

    protected abstract DecodingState finishDecode(boolean foundCRLF,
            ProtocolDecoderOutput out) throws Exception;
    }
