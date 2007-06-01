package org.lastbamboo.common.sip.stack.codec.encoder;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipCodecUtils;
import org.lastbamboo.common.sip.stack.message.SipMessage;
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
    
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    private final CharsetEncoder m_asciiEncoder = US_ASCII.newEncoder();

    public ByteBuffer encode(final SipMessage message)
        {
        m_asciiEncoder.reset();
        final ByteBuffer buffer = ByteBuffer.allocate(300);
        buffer.setAutoExpand(true);

        encodeStartLine(message, buffer);
        encodeHeaders(message, buffer);
        encodeBody(message, buffer);
        buffer.flip();
        
        return buffer;
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
        SipCodecUtils.appendCRLF(buffer);
        }

    private void encodeHeaders(final SipMessage message, 
        final ByteBuffer buffer)
        {
        final Map<String, SipHeader> headers = message.getHeaders();
        for (final Map.Entry<String, SipHeader> entry : headers.entrySet())
            {
            final SipHeader header = entry.getValue();
            final List<SipHeaderValue> values = header.getValues();
            try
                {
                buffer.putString(header.getName(), m_asciiEncoder);
                buffer.put(SipCodecUtils.COLON);
                buffer.put(SipCodecUtils.SP);
                appendHeaderValues(buffer, values);
                SipCodecUtils.appendCRLF(buffer);
                }
            catch (final CharacterCodingException e)
                {
                LOG.error("Bad encoding?", e);
                }
            }
        SipCodecUtils.appendCRLF(buffer);
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
                buffer.put(SipCodecUtils.SEMI_COLON);
                buffer.putString(entry.getKey(), m_asciiEncoder);
                buffer.put(SipCodecUtils.EQUALS);
                buffer.putString(entry.getValue(), m_asciiEncoder);
                }
            if (iter.hasNext())
                {
                buffer.put(SipCodecUtils.COMMA);
                }
            }
        }

    /**
     * Writes the response body bytes, if any, to the specified buffer
     * 
     * @param message The response
     * @param buffer The buffer to write to
     */
    private void encodeBody(final SipMessage message, final ByteBuffer buffer)
        {
        final ByteBuffer body = message.getBody();
        buffer.put(body);
        }

    }
