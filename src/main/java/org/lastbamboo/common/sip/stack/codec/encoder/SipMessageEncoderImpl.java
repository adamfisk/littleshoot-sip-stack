package org.lastbamboo.common.sip.stack.codec.encoder;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.util.mina.MinaCodecUtils;
import org.lastbamboo.common.sip.stack.message.DoubleCrlfKeepAlive;
import org.lastbamboo.common.sip.stack.message.Invite;
import org.lastbamboo.common.sip.stack.message.Register;
import org.lastbamboo.common.sip.stack.message.RequestTimeoutResponse;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitor;
import org.lastbamboo.common.sip.stack.message.SipResponse;
import org.lastbamboo.common.sip.stack.message.UnknownSipRequest;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for simple conversion of SIP messages to {@link ByteBuffer}s.
 */
public class SipMessageEncoderImpl implements SipMessageEncoder
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(SipMessageEncoderImpl.class);
    
    public ByteBuffer encode(final SipMessage message)
        {
        final ByteBuffer buffer = ByteBuffer.allocate(300);
        buffer.setAutoExpand(true);
        
        final SipMessageVisitor visitor = new EncoderVisitor(buffer);
        message.accept(visitor);

        buffer.flip();
        
        return buffer;
        }


    private static final class EncoderVisitor implements SipMessageVisitor
        {
        private static final Charset US_ASCII = Charset.forName("US-ASCII");

        private final CharsetEncoder m_asciiEncoder = US_ASCII.newEncoder();

        private final ByteBuffer m_buffer;
        
        private EncoderVisitor(final ByteBuffer buffer)
            {
            this.m_buffer = buffer;
            }

        private final void standardEncode(final SipMessage message)
            {
            encodeStartLine(message, this.m_buffer);
            encodeHeaders(message, this.m_buffer);
            encodeBody(message, this.m_buffer);
            }
        
        public void visitDoubleCrlfKeepAlive(DoubleCrlfKeepAlive keepAlive)
            {
            MinaCodecUtils.appendCRLF(m_buffer);
            MinaCodecUtils.appendCRLF(m_buffer);
            }
    
        public void visitInvite(final Invite invite)
            {
            standardEncode(invite);
            }
    
        public void visitRegister(final Register register)
            {
            standardEncode(register);
            }
    
        public void visitRequestTimedOut(final RequestTimeoutResponse response)
            {
            standardEncode(response);
            }
    
        public void visitResponse(final SipResponse response)
            {
            standardEncode(response);
            }
    
        public void visitUnknownRequest(final UnknownSipRequest request)
            {
            }
    
        private void encodeStartLine(final SipMessage message, 
            final ByteBuffer buffer)
            {
            LOG.debug("Encoding start line: '{}'", message.getStartLine());
            try
                {
                buffer.putString(message.getStartLine(), m_asciiEncoder);
                }
            catch (final CharacterCodingException e)
                {
                LOG.error("Bad encoding?", e);
                }
            MinaCodecUtils.appendCRLF(buffer);
            }
    
        private void encodeHeaders(final SipMessage message, 
            final ByteBuffer buffer)
            {
            LOG.debug("Appending headers: {}", message.getHeaders());
            final Map<String, SipHeader> headers = message.getHeaders();
            for (final Map.Entry<String, SipHeader> entry : headers.entrySet())
                {
                final SipHeader header = entry.getValue();
                final List<SipHeaderValue> values = header.getValues();
                try
                    {
                    buffer.putString(header.getName(), m_asciiEncoder);
                    buffer.put(MinaCodecUtils.COLON);
                    buffer.put(MinaCodecUtils.SPACE);
                    appendHeaderValues(buffer, values);
                    MinaCodecUtils.appendCRLF(buffer);
                    }
                catch (final CharacterCodingException e)
                    {
                    LOG.error("Bad encoding?", e);
                    }
                }
            MinaCodecUtils.appendCRLF(buffer);
            }
    
        private void appendHeaderValues(final ByteBuffer buffer,
            final List<SipHeaderValue> values) throws CharacterCodingException
            {
            for (final Iterator<SipHeaderValue> iter = values.iterator(); iter
                    .hasNext();)
                {
                final SipHeaderValue value = iter.next();
                buffer.putString(value.getBaseValue(), m_asciiEncoder);
                final Map<String, String> params = value.getParams();
                final Set<Map.Entry<String, String>> entries = params.entrySet();
                for (final Map.Entry<String, String> entry : entries)
                    {
                    buffer.put(MinaCodecUtils.SEMI_COLON);
                    buffer.putString(entry.getKey(), m_asciiEncoder);
                    buffer.put(MinaCodecUtils.EQUALS);
                    buffer.putString(entry.getValue(), m_asciiEncoder);
                    }
                if (iter.hasNext())
                    {
                    buffer.put(MinaCodecUtils.COMMA);
                    }
                }
            }
    
        /**
         * Writes the message body bytes, if any, to the specified buffer
         * 
         * @param message The message.
         * @param buffer The buffer to write to
         */
        private void encodeBody(final SipMessage message, final ByteBuffer buffer)
            {
            final ByteBuffer body = message.getBody();
            buffer.put(body);
            }
        }
    }
