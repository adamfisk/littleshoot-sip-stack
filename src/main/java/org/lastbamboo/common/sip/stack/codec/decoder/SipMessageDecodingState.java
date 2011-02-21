package org.lastbamboo.common.sip.stack.codec.decoder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.SimpleByteBufferAllocator;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.sip.stack.codec.SipMessageType;
import org.lastbamboo.common.sip.stack.message.DoubleCrlfKeepAlive;
import org.lastbamboo.common.sip.stack.message.SingleSipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipInviteFactory;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageUtils;
import org.lastbamboo.common.sip.stack.message.SipRegisterFactory;
import org.lastbamboo.common.sip.stack.message.SipResponseFactory;
import org.lastbamboo.common.sip.stack.message.UnknownSipRequestFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.littleshoot.util.mina.DecodingState;
import org.littleshoot.util.mina.DecodingStateMachine;
import org.littleshoot.util.mina.FixedLengthDecodingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State machine for decoding SIP messages. 
 */
public class SipMessageDecodingState extends DecodingStateMachine 
    {

    private final Logger LOG = 
        LoggerFactory.getLogger(SipMessageDecodingState.class);
    
    /**
     * If this is not set for tests or anything else, it causes massive 
     * trauma.  In particular, ByteBuffer.allocate(0) creates a 
     * ByteBuffer with a capacity of 1 -- odd behavior that causes various
     * problems.
     */
    static
        {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        }
    
    private static final ByteBuffer EMPTY_BODY = ByteBuffer.allocate(0);
    
    private SingleSipMessageFactory m_messageFactory;

    private final SipHeaderFactory m_headerFactory;
    
    /**
     * Creates a new SIP message decoding state machine.
     * 
     * @param headerFactory The factory for creating SIP headers.
     */
    public SipMessageDecodingState(final SipHeaderFactory headerFactory)
        {
        m_headerFactory = headerFactory;
        }
    
    @Override
    protected DecodingState init()
        {
        return new ReadFirstLineState();
        }
    
    @Override
    protected DecodingState finishDecode(final List<Object> childProducts, 
        final ProtocolDecoderOutput out)
        {
        LOG.error("Got finish decode for full message");
        return null;
        }

    @Override
    protected void destroy()
        {
        }
    
    /**
     * Note this *extends* SipFirstLineDecodingState that does the decoding
     * of the line.
     */
    private final class ReadFirstLineState extends SipFirstLineDecodingState
        {

        @Override
        protected DecodingState finishDecode(final List<Object> childProducts, 
            final ProtocolDecoderOutput out) throws Exception
            {
            final SipMessageType messageType = 
                (SipMessageType) childProducts.get(0);
            
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Handling message type: "+messageType);
                }
            switch (messageType)
                {
                case SIP_2_0:
                    final int statusCode = 
                        ((Integer) childProducts.get(1)).intValue();
                    final String reasonPhrase = (String) childProducts.get(2);
                    m_messageFactory = 
                        new SipResponseFactory(statusCode, reasonPhrase);
                    break;
                case REGISTER:
                    final URI registerUri = (URI) childProducts.get(1);
                    m_messageFactory = new SipRegisterFactory(registerUri);
                    break;
                case INVITE:
                    final URI inviteUri = (URI) childProducts.get(1);
                    m_messageFactory = new SipInviteFactory(inviteUri);
                    break;
                case DOUBLE_CRLF:
                    // Just create the double CRLF keep alive here and write
                    // it out to be visited.
                    final SipMessage doubleCrlf = new DoubleCrlfKeepAlive();
                    out.write(doubleCrlf);
                    return null;
                case UNKNOWN:
                    final URI uri = (URI) childProducts.get(1);
                    m_messageFactory = 
                        new UnknownSipRequestFactory("Unknown", uri);
                    break;
                default:
                    LOG.warn("Not handling type: "+messageType);
                }
            return new ReadHeadersState(m_headerFactory);
            }
        }
    
    private final class ReadHeadersState extends SipHeaderDecodingState
        {

        private ReadHeadersState(final SipHeaderFactory headerFactory)
            {
            super(headerFactory);
            }

        @Override
        protected DecodingState finishDecode(final List<Object> childProducts, 
            final ProtocolDecoderOutput out) throws Exception
            {
            final Map<String, SipHeader> headers = 
                (Map<String, SipHeader>) childProducts.get(0);
            
            final int length = SipMessageUtils.extractContentLength(headers);
            if (length > 0)
                {
                LOG.debug("Reading body with length: {}", length);
                return new ReadBodyState(headers, length);
                }
            else
                {
                LOG.debug("Returning SIP message with NO body...");
                final SipMessage message = 
                    m_messageFactory.createSipMessage(headers, EMPTY_BODY);
                
                out.write(message);
                
                // We return null here to give back control to the outer
                // decoding state machine.
                return null;
                }
            }
        }
    
    
    private final class ReadBodyState extends FixedLengthDecodingState
        {

        private final Map<String, SipHeader> m_headers;

        private ReadBodyState(final Map<String, SipHeader> headers, 
            final int length)
            {
            super(length);
            m_headers = headers;
            }

        @Override
        protected DecodingState finishDecode(final ByteBuffer readData, 
            final ProtocolDecoderOutput out) throws Exception
            {
            LOG.debug("Returning SIP message with body...");
            final SipMessage message = 
                m_messageFactory.createSipMessage(m_headers, readData);
            out.write(message);
            return null;
            }
        }
    }

