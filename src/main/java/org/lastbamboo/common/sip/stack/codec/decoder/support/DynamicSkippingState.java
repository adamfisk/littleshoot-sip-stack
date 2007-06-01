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
 * Skips data until {@link #canSkip(byte)} returns <tt>false</tt>.
 * 
 * @author trustin
 * @version $Rev: 215 $, $Date: 2006-11-27 01:13:35 -0500 (Mon, 27 Nov 2006) $
 */
public abstract class DynamicSkippingState implements DecodingState
    {

    private int skippedBytes;

    public DecodingState decode(ByteBuffer in, ProtocolDecoderOutput out)
            throws Exception
        {
        int beginPos = in.position();
        int limit = in.limit();
        for (int i = beginPos; i < limit; i++)
            {
            byte b = in.get(i);
            if (!canSkip(b))
                {
                in.position(i);
                int answer = this.skippedBytes;
                this.skippedBytes = 0;
                return finishDecode(answer);
                }
            else
                {
                skippedBytes++;
                }
            }

        in.position(limit);
        return this;
        }

    protected abstract boolean canSkip(byte b);

    protected abstract DecodingState finishDecode(int skippedBytes)
        throws Exception;
    }
