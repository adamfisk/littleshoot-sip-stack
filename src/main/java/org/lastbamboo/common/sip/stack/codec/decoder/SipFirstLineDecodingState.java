package org.lastbamboo.common.sip.stack.codec.decoder;

import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.sip.stack.codec.SipMessageType;
import org.lastbamboo.common.sip.stack.codec.decoder.support.ConsumeToCrlfDecodingState;
import org.lastbamboo.common.sip.stack.codec.decoder.support.ConsumeToLinearWhitespaceDecodingState;
import org.lastbamboo.common.sip.stack.codec.decoder.support.DecodingState;
import org.lastbamboo.common.sip.stack.codec.decoder.support.DecodingStateMachine;
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
        return new ReadFirstWord();
        }

    @Override
    protected void destroy() throws Exception
        {
        }
    
    private final class ReadFirstWord 
        extends ConsumeToLinearWhitespaceDecodingState
        {
        @Override
        protected DecodingState finishDecode(final ByteBuffer product,
            final ProtocolDecoderOutput out) throws Exception
            {
            final String firstWord = product.getString(m_asciiDecoder);
            
            final SipMessageType messageType;
            if (!SipMessageType.contains(firstWord))
                {
                // Preserve the unknown message type.
                out.write(firstWord);
                messageType = SipMessageType.UNKNOWN;
                }
            
            else
                {
                messageType = SipMessageType.convert(firstWord);              
                }
            
            out.write(messageType);
            
            switch (messageType)
                {
                case SIP_2_0:
                    return new ReadResponseStatusCodeState();
                case REGISTER:
                    return new ReadRequestUriState();
                case INVITE:
                    return new ReadRequestUriState();
                default:
                    return new ReadRequestUriState();
                }
            }
        }
    
    private final class ReadRequestUriState 
        extends ConsumeToLinearWhitespaceDecodingState
        {
        @Override
        protected DecodingState finishDecode(final ByteBuffer product,
            final ProtocolDecoderOutput out) throws Exception
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
        protected DecodingState finishDecode(final ByteBuffer product, 
            final ProtocolDecoderOutput out) throws Exception
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
