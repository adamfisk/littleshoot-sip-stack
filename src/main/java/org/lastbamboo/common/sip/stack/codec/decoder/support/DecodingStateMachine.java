package org.lastbamboo.common.sip.stack.codec.decoder.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Taken from AsyncWeb.  This is a decoding state that is itself a state 
 * machine.
 */
public abstract class DecodingStateMachine implements DecodingState 
    {
    private final Logger log = LoggerFactory.getLogger(DecodingStateMachine.class);

    private final List<Object> childProducts = new ArrayList<Object>();
    private final ProtocolDecoderOutput childOutput = new ProtocolDecoderOutput() 
        {
        public void flush() 
            {
            }

        public void write(Object message) 
            {
            childProducts.add(message);
            }
        };
        
    private DecodingState currentState;

    protected abstract DecodingState init() throws Exception;
    protected abstract DecodingState finishDecode(List<Object> childProducts, ProtocolDecoderOutput out) throws Exception;
    protected abstract void destroy() throws Exception;
  
    public DecodingState decode(ByteBuffer in, ProtocolDecoderOutput out) throws Exception 
        {
        DecodingState state = this.currentState;
        if (state == null)
            {
            state = init();
            }

        final int limit = in.limit();
        int pos = in.position();

        try
            {
            for (;;)
                {
                // Wait for more data if all data is consumed.
                if (pos == limit)
                    {
                    break;
                    }

                final DecodingState oldState = state;
                state = state.decode(in, childOutput);

                // If finished, call finishDecode
                if (state == null)
                    {
                    log.debug("Finishing decode...");
                    return finishDecode(childProducts, out);
                    }

                int newPos = in.position();

                // Wait for more data if nothing is consumed and state didn't
                // change.
                if (newPos == pos && oldState == state)
                    {
                    break;
                    }
                pos = newPos;
                }

            return this;
            }
        catch (Exception e)
            {
            log.warn("Caught exception!!", e);
            state = null;
            throw e;
            }
        finally
            {
            this.currentState = state;

            // Destroy if decoding is finished or failed.
            if (state == null)
                {
                childProducts.clear();
                try
                    {
                    destroy();
                    }
                catch (Exception e2)
                    {
                    log.warn("Failed to destroy a decoding state machine.", e2);
                    }
                }
            }
        }
    }
