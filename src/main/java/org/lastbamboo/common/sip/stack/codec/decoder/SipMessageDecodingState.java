package org.lastbamboo.common.sip.stack.codec.decoder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.sip.stack.codec.SipMessageType;
import org.lastbamboo.common.sip.stack.codec.decoder.support.DecodingState;
import org.lastbamboo.common.sip.stack.codec.decoder.support.DecodingStateMachine;
import org.lastbamboo.common.sip.stack.codec.decoder.support.FixedLengthDecodingState;
import org.lastbamboo.common.sip.stack.message.SingleSipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipInviteFactory;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageUtils;
import org.lastbamboo.common.sip.stack.message.SipRegisterFactory;
import org.lastbamboo.common.sip.stack.message.SipResponseFactory;
import org.lastbamboo.common.sip.stack.message.UnknownSipRequestFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State machine for decoding SIP messages.  This whole state-machine scheme
 * is taken directly from AsyncWeb.  AsyncWeb rocks.  You should check it out.
 * Wish I could take more credit.
 */
public abstract class SipMessageDecodingState extends DecodingStateMachine 
    {

    private final Logger LOG = 
        LoggerFactory.getLogger(SipMessageDecodingState.class);
    
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
    protected DecodingState init() throws Exception
        {
        m_messageFactory = null;
        return new ReadFirstLineState();
        }

    @Override
    protected void destroy() throws Exception
        {
        }
    
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
                case UNKNOWN:
                    final String method = (String) childProducts.get(1);
                    final URI uri = (URI) childProducts.get(2);
                    LOG.warn("Unknown request method: "+method);
                    m_messageFactory = 
                        new UnknownSipRequestFactory(method, uri);
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
                
                return new ReadBodyState(headers, length);
                }
            else
                {
                LOG.debug("Creating no body message");
                final SipMessage message = 
                    m_messageFactory.createSipMessage(headers, EMPTY_BODY);
                
                out.write(message);
                
                // No body.  Go back to reading at the beginning.
                return SipMessageDecodingState.this.init();
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
            final SipMessage message = 
                m_messageFactory.createSipMessage(m_headers, readData);
            out.write(message);
            return SipMessageDecodingState.this.init();
            }
        }
    }
