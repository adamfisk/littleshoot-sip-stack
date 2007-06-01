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
* A decoder which writes all read bytes in to a known <code>Bytes</code>
* context until a <code>CRLF</code> has been encountered
*/
public abstract class ConsumeToCrlfDecodingState implements DecodingState {

  /**
   * Carriage return character
   */
  private static final byte CR = 13;
  
  /**
   * Line feed character
   */
  private static final byte LF = 10;
  
  private boolean lastIsCR;
  private ByteBuffer buffer;
  
  
  /**
   * Creates a new instance.
   */
  public ConsumeToCrlfDecodingState() {
  }
  
  public DecodingState decode(ByteBuffer in, ProtocolDecoderOutput out) throws Exception {
    int beginPos = in.position();
    int limit = in.limit();
    int terminatorPos = -1;
    
    for (int i = beginPos; i < limit; i++) {
      byte b = in.get(i);
      if (b == CR) {
        lastIsCR = true;
      } else {
        if (b == LF && lastIsCR) {
          terminatorPos = i;
          break;
        }
        lastIsCR = false;
      }
    }
    
    if (terminatorPos >= 0) {
      ByteBuffer product;
      
      int endPos = terminatorPos - 1;
      
      if (beginPos < endPos) {
        in.limit(endPos);
  
        if (buffer == null) {
          product = in.slice();
        } else {
          buffer.put(in);
          product = buffer.flip();
          buffer = null;
        }
        
        in.limit(limit);
      } else {
        // When input contained only CR or LF rather than actual data...
        if (buffer == null) {
          product = ByteBuffer.allocate(1);
          product.limit(0);
        } else {
          product = buffer.flip();
          buffer = null;
        }
      }
      in.position(terminatorPos + 1);
      return finishDecode(product, out);
    } else {
      in.position(beginPos);
      if (buffer == null) {
        buffer = ByteBuffer.allocate(in.remaining());
        buffer.setAutoExpand(true);
      } 
      
      buffer.put(in);
      if (lastIsCR) {
        buffer.position(buffer.position() - 1);
      }
      return this;
    }
  }

  protected abstract DecodingState finishDecode(ByteBuffer product, ProtocolDecoderOutput out) throws Exception;
}
