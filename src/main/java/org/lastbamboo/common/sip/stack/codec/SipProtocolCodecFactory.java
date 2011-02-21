package org.lastbamboo.common.sip.stack.codec;

import org.littleshoot.mina.filter.codec.ProtocolCodecFactory;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.lastbamboo.common.sip.stack.codec.decoder.SipMessageDecodingState;
import org.lastbamboo.common.sip.stack.codec.encoder.SipMessageProtocolEncoder;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.littleshoot.util.mina.StateMachineProtocolDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Codec factory for processing SIP server messages.
 */
public class SipProtocolCodecFactory implements ProtocolCodecFactory
    {

    private final Logger m_log = 
        LoggerFactory.getLogger(SipProtocolCodecFactory.class);
    private final SipHeaderFactory m_headerFactory;
    
    /**
     * Creates a new codec factory for SIP messages.
     * 
     * @param headerFactory The factory for creating SIP headers.
     */
    public SipProtocolCodecFactory(final SipHeaderFactory headerFactory)
        {
        m_headerFactory = headerFactory;
        }

    public ProtocolDecoder getDecoder() throws Exception
        {
        m_log.debug("Creating new decoder...");
        final SipMessageDecodingState startState = 
            new SipMessageDecodingState(m_headerFactory);

        return new StateMachineProtocolDecoder(startState);
        }
    
    public ProtocolEncoder getEncoder() throws Exception
        {
        return new SipMessageProtocolEncoder();
        }

    }
