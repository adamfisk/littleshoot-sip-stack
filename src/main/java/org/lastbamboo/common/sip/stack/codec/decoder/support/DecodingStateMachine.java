package org.lastbamboo.common.sip.stack.codec.decoder.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
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
    private final Logger m_log = 
        LoggerFactory.getLogger(DecodingStateMachine.class);

    private final List<Object> m_childProducts = new ArrayList<Object>();
    
    private final ProtocolDecoderOutput m_childOutput = 
        new ProtocolDecoderOutput() 
        {
        public void flush() 
            {
            }

        public void write(final Object message) 
            {
            m_childProducts.add(message);
            }
        };
        
    private DecodingState currentState;

    protected abstract DecodingState init() throws Exception;
    protected abstract DecodingState finishDecode(List<Object> childProducts, 
        ProtocolDecoderOutput out) throws Exception;
    protected abstract void destroy() throws Exception;
  
    public DecodingState decode(final ByteBuffer in, 
        final ProtocolDecoderOutput out) throws Exception 
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
                state = state.decode(in, m_childOutput);

                // If finished, call finishDecode
                if (state == null)
                    {
                    if (m_log.isDebugEnabled())
                        {
                        debugStateTransition(oldState);
                        }
                    final DecodingState returningState = 
                        finishDecode(m_childProducts, out); 
                    
                    if (m_log.isDebugEnabled())
                        {
                        debugStateTransition2(returningState, in);
                        }
                    
                    return returningState;
                    }

                int newPos = in.position();

                // Wait for more data if nothing is consumed and state didn't
                // change.
                if (newPos == pos && oldState == state)
                    {
                    m_log.debug("No data consumed and no state change...returning");
                    break;
                    }
                pos = newPos;
                }

            return this;
            }
        catch (Exception e)
            {
            m_log.warn("Caught exception!!", e);
            state = null;
            throw e;
            }
        finally
            {
            this.currentState = state;

            // Destroy if decoding is finished or failed.
            if (state == null)
                {
                m_childProducts.clear();
                try
                    {
                    destroy();
                    }
                catch (Exception e2)
                    {
                    m_log.warn("Failed to destroy a decoding state machine.", e2);
                    }
                }
            }
        }

    private void debugStateTransition(DecodingState oldState)
        {
        m_log.debug("This state: {}", ClassUtils.getShortClassName(getClass()));
        m_log.debug("Got null from "+ClassUtils.getShortClassName(oldState.getClass()));
        m_log.debug("Finishing decode for state: {}", 
            ClassUtils.getShortClassName(getClass()));
        }
    
    private void debugStateTransition2(final DecodingState returningState, 
        final ByteBuffer in)
        {
        final String stateString;
        if (returningState != null)
            {
            stateString = 
                ClassUtils.getShortClassName(returningState.getClass());
            }
        else
            {
            stateString = null;
            }
        m_log.debug(ClassUtils.getShortClassName(getClass()) + 
            " transitioning to state: {}", stateString);
        
        m_log.debug("Remaining bytes: "+in.remaining());
        }
    }
