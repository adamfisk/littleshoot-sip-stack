package org.lastbamboo.common.sip.stack.codec.decoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.TreeMap;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.util.mina.ConsumeToCrlfDecodingState;
import org.lastbamboo.common.util.mina.ConsumeToTerminatorDecodingState;
import org.lastbamboo.common.util.mina.CrlfDecodingState;
import org.lastbamboo.common.util.mina.DecodingState;
import org.lastbamboo.common.util.mina.DecodingStateMachine;
import org.lastbamboo.common.util.mina.MinaCodecUtils;
import org.lastbamboo.common.util.mina.SpaceSkippingState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decodes SIP headers.
 */
abstract class SipHeaderDecodingState extends DecodingStateMachine 
    {
    
    private final Logger LOG = 
        LoggerFactory.getLogger(SipHeaderDecodingState.class);
    
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    private final CharsetDecoder m_asciiDecoder = US_ASCII.newDecoder();

    private Map<String, SipHeader> m_headers = 
        new TreeMap<String, SipHeader>();

    private final SipHeaderFactory m_headerFactory;
    
    /**
     * Creates a new decoding state for decoding SIP headers.
     * 
     * @param headerFactory The factory for creating header instances from 
     * read values.
     */
    public SipHeaderDecodingState(final SipHeaderFactory headerFactory)
        {
        m_headerFactory = headerFactory;
        }

    @Override
    protected DecodingState init() throws Exception
        {
        return new HeaderNameDecodingState();
        }

    @Override
    protected void destroy() throws Exception
        {
        }
    
    private final class HeaderNameDecodingState
        extends ConsumeToTerminatorDecodingState
        {

        private HeaderNameDecodingState()
            {
            super(MinaCodecUtils.COLON);
            }

        @Override
        protected DecodingState finishDecode(final byte foundTerminator,
            final ByteBuffer product, final ProtocolDecoderOutput out) 
            throws Exception
            {
            final String headerName = product.getString(m_asciiDecoder);
            return new AfterHeaderColonState(headerName);
            }
        
        }
    
    private final class AfterHeaderColonState extends SpaceSkippingState
        {

        private final String m_headerName;

        private AfterHeaderColonState(String headerName)
            {
            m_headerName = headerName;
            }

        @Override
        protected DecodingState finishDecode() throws Exception
            {
            return new HeaderValueDecodingState(this.m_headerName);
            }
    
        }
 
    private final class HeaderValueDecodingState 
        extends ConsumeToCrlfDecodingState
        {
    
        private final String m_headerName;

        private HeaderValueDecodingState(final String headerName)
            {
            m_headerName = headerName;
            }

        @Override
        protected DecodingState finishDecode(final ByteBuffer product, 
            final ProtocolDecoderOutput out) throws Exception
            {
            final String headerValue = product.getString(m_asciiDecoder);
            final SipHeader header = 
                m_headerFactory.createHeader(this.m_headerName, headerValue);
            m_headers.put(this.m_headerName, header);
            return new FindEmptyLine();
            }
    
        }
    
    private final class FindEmptyLine extends CrlfDecodingState
        {
        @Override
        protected DecodingState finishDecode(final boolean foundCrlf,
            final ProtocolDecoderOutput out) throws Exception
            {
            if (foundCrlf)
                {
                out.write(m_headers);
                // Reset the state.
                return null;
                }
            else
                {
                return new HeaderNameDecodingState();
                }
            }
        }
    }
