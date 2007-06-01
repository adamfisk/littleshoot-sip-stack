package org.lastbamboo.common.sip.stack.codec;

import java.util.List;

import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.sip.stack.codec.decoder.SipMessageDecodingState;
import org.lastbamboo.common.sip.stack.codec.decoder.support.DecodingState;
import org.lastbamboo.common.sip.stack.codec.decoder.support.StateMachineProtocolDecoder;
import org.lastbamboo.common.sip.stack.codec.encoder.SipMessageProtocolEncoder;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Codec factory for processing SIP server messages.
 */
public class SipCodecFactory implements ProtocolCodecFactory
    {

    private final Logger LOG = 
        LoggerFactory.getLogger(SipCodecFactory.class);
    private final SipHeaderFactory m_headerFactory;
    
    private static int s_readMessages = 0;
    
    /**
     * Creates a new codec factory for SIP messages.
     * 
     * @param headerFactory The factory for creating SIP headers.
     */
    public SipCodecFactory(final SipHeaderFactory headerFactory)
        {
        m_headerFactory = headerFactory;
        }

    public ProtocolDecoder getDecoder() throws Exception
        {
        final SipMessageDecodingState startState = 
            new TopLevelSipMessageDecodingState(m_headerFactory);

        return new StateMachineProtocolDecoder(startState);
        }
    
    private final class TopLevelSipMessageDecodingState 
        extends SipMessageDecodingState
        {
        
        private TopLevelSipMessageDecodingState(
            final SipHeaderFactory headerFactory)
            {
            super(headerFactory);
            LOG.debug("Created new top level SIP message decoder...");
            }

        @Override
        protected DecodingState finishDecode(
            final List<Object> childProducts, 
            final ProtocolDecoderOutput out) throws Exception
            {
            s_readMessages++;
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Finished decoding message: "  + childProducts);
                LOG.debug("Now read "+s_readMessages+" messages...");
                }
            out.write(childProducts.get(0));
            return new TopLevelSipMessageDecodingState(m_headerFactory);
            }
    
        }

    public ProtocolEncoder getEncoder() throws Exception
        {
        return new SipMessageProtocolEncoder();
        }
    }
