package org.lastbamboo.common.sip.stack.codec.decoder;

import java.net.URI;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.lang.math.NumberUtils;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.sip.stack.codec.SipMessageType;
import org.littleshoot.util.mina.ConsumeToCrlfDecodingState;
import org.littleshoot.util.mina.ConsumeToLinearWhitespaceDecodingState;
import org.littleshoot.util.mina.ConsumeToTerminatorDecodingState;
import org.littleshoot.util.mina.DecodingState;
import org.littleshoot.util.mina.DecodingStateMachine;
import org.littleshoot.util.mina.MinaCodecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decodes a SIP request or response line.  The format of requests and 
 * responses is specified in RFC 3261 as the following:<p>
 * 
 * Requests:
 * <pre>
 * Request-Line  =  Method SP Request-URI SP SIP-Version CRLF
 * </pre>
 * 
 * Responses:
 * <pre>
 * Status-Line  =  SIP-Version SP Status-Code SP Reason-Phrase CRLF
 * </pre>
 * 
 * <p>
 * This also decodes the SIP double CRLF keep-alive message, which is simply:
 * <pre>
 * CRLFCRLF
 * </pre>
 */
abstract class SipFirstLineDecodingState extends DecodingStateMachine 
    {
    
    private final Logger LOG = 
        LoggerFactory.getLogger(SipFirstLineDecodingState.class);

    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    private final CharsetDecoder m_utf8Decoder = UTF_8.newDecoder();
    
    private final CharsetDecoder m_asciiDecoder = US_ASCII.newDecoder();

    @Override
    protected DecodingState init() throws Exception
        {
        return new MessageTypeDecodingState();
        }

    @Override
    protected void destroy() throws Exception
        {
        }
    
    private final class MessageTypeDecodingState 
        extends ConsumeToTerminatorDecodingState
        {
        
        private MessageTypeDecodingState()
            {
            super(MinaCodecUtils.SPACE, MinaCodecUtils.CR);
            }

        @Override
        protected DecodingState finishDecode(final byte terminator, 
            final ByteBuffer product, final ProtocolDecoderOutput out) 
            throws Exception
            {
            final SipMessageType messageType = 
                determineMessageType(terminator, product);
            
            LOG.debug("Found message type: {}", messageType);
            out.write(messageType);
            
            switch (messageType)
                {
                case SIP_2_0:
                    return new ReadResponseStatusCodeState();
                case REGISTER:
                    return new ReadRequestUriState();
                case INVITE:
                    return new ReadRequestUriState();
                case DOUBLE_CRLF:
                    // Read the final LF CR LF
                    return new ReadLfCrlfDecodingState();
                case UNKNOWN:
                    // Maybe it's a method we don't know about?  Assume it's
                    // some sort of request and process it as such.
                    return new ReadRequestUriState();
                default:
                    return new ReadRequestUriState();
                }
            }

        private SipMessageType determineMessageType(final byte terminator, 
            final ByteBuffer product) throws CharacterCodingException
            {
            if (terminator == MinaCodecUtils.CR)
                {
                LOG.debug("Returning double CRLF");
                return SipMessageType.DOUBLE_CRLF;
                }
            else
                {
                final String firstWord = product.getString(m_asciiDecoder);

                if (!SipMessageType.contains(firstWord))
                    {
                    LOG.warn("Unknown message type: '{}'", firstWord);
                    return SipMessageType.UNKNOWN;
                    }
                
                else
                    {
                    LOG.debug("Matching message type for: {}", firstWord);
                    return SipMessageType.convert(firstWord);              
                    }
                }
            }
        };
    
    private final class ReadLfCrlfDecodingState 
        extends ConsumeToCrlfDecodingState
        {
        @Override
        protected DecodingState finishDecode(final ByteBuffer product,
            final ProtocolDecoderOutput out) throws Exception
            {
            // Read the final LFCRLF sequence of a CRLFCRLF keep-alive message.
            LOG.debug("Read final LF CR LF");
            return null;
            }
        };
    
    private final class ReadRequestUriState 
        extends ConsumeToLinearWhitespaceDecodingState
        {
        @Override
        protected DecodingState finishDecode(final byte foundTerminator,
            final ByteBuffer product, final ProtocolDecoderOutput out) 
            throws Exception
            {
            final String uri = product.getString(m_utf8Decoder);
            out.write(new URI(uri));
            return new ReadSipVersionState();
            }
        };
        
    private final class ReadSipVersionState 
        extends ConsumeToCrlfDecodingState
        {
        @Override
        protected DecodingState finishDecode(final ByteBuffer product,
            final ProtocolDecoderOutput out) throws Exception
            {
            final String version = product.getString(m_asciiDecoder);
            out.write(version);
            return null;
            }
        };
    
    private final class ReadResponseStatusCodeState extends
        ConsumeToLinearWhitespaceDecodingState
        {

        @Override
        protected DecodingState finishDecode(final byte foundTerminator,
            final ByteBuffer product, final ProtocolDecoderOutput out) 
            throws Exception
            {
            final String statusCodeString = product.getString(m_asciiDecoder);
            if (!NumberUtils.isNumber(statusCodeString))
                {
                LOG.warn("Bad status code: "+statusCodeString);
                throw new IllegalArgumentException(
                    "Bad status code: "+statusCodeString);
                }
            
            final Integer statusCode = Integer.decode(statusCodeString);
            out.write(statusCode);
            return new ReadResponseReasonPhraseState();
            }
        }
    
    private final class ReadResponseReasonPhraseState extends
        ConsumeToCrlfDecodingState
        {

        @Override
        protected DecodingState finishDecode(final ByteBuffer product, 
            final ProtocolDecoderOutput out) throws Exception
            {
            final String reasonPhrase = product.getString(m_asciiDecoder);
            out.write(reasonPhrase);
            return null;
            }
        }
    }
