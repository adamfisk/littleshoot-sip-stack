package org.lastbamboo.common.sip.stack.codec.decoder;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.littleshoot.mina.common.BufferDataException;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.littleshoot.mina.filter.codec.textline.LineDelimiter;
import org.littleshoot.mina.filter.codec.textline.TextLineDecoder;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Decodes SIP messages.
 */
public class SipMessageDecoder implements ProtocolDecoder
    {
    
    private static final Logger LOG = 
        LoggerFactory.getLogger(SipMessageDecoder.class);
    
    private static final String CONTEXT = TextLineDecoder.class.getName()
            + ".context";

    private final Charset charset;

    // final LineDelimiter delimiter;

    private ByteBuffer m_delimiterBuf;

    private int maxLineLength = 1024;

    /**
     * Creates a new instance with the current default {@link Charset}
     * and {@link LineDelimiter#AUTO} delimiter.
     */
    public SipMessageDecoder()
        {
        this.charset = Charset.forName("US-ASCII");
        this.m_delimiterBuf = createDelimiterBuf("\r\n\r\n");
        }

    private ByteBuffer createDelimiterBuf(final String delimiter)
        {
        ByteBuffer tmp = ByteBuffer.allocate(delimiter.length());
        try
            {
            tmp.putString(delimiter, charset.newEncoder());
            tmp.flip();
            return tmp;
            }
        catch (CharacterCodingException e)
            {
            LOG.error("Bad charset?", e);
            return null;
            }

        }

    public void decode(final IoSession session, final ByteBuffer in,
        final ProtocolDecoderOutput out) throws Exception
        {
        final Context ctx = getContext(session);
        
        final int matchCount = decodeNormal(in, ctx, out);
        
        ctx.setMatchCount(matchCount);
        }

    private Context getContext(final IoSession session)
        {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx == null)
            {
            ctx = new Context();
            session.setAttribute(CONTEXT, ctx);
            }
        return ctx;
        }

    public void finishDecode(IoSession session, ProtocolDecoderOutput out)
            throws Exception
        {
        }

    public void dispose(final IoSession session) throws Exception
        {
        final Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx != null)
            {
            
            // TODO: Re-enable this!!
            //ctx.getBuffer().release();
            session.removeAttribute(CONTEXT);
            }
        }

    private int decodeNormal(final ByteBuffer in, final Context ctx, 
        final ProtocolDecoderOutput out)
            throws IOException
        {
        
        final ByteBuffer buf = ctx.getBuffer(); 
        final int matchCount = ctx.getMatchCount(); 
        final CharsetDecoder decoder = ctx.getDecoder();

        // Try to find a match
        int oldPos = in.position();
        int oldLimit = in.limit();
        int count = matchCount;
        while (in.hasRemaining())
            {
            byte b = in.get();
            if (m_delimiterBuf.get(matchCount) == b)
                {
                count++;
                if (matchCount == m_delimiterBuf.limit())
                    {
                    // Found a match.
                    int pos = in.position();
                    in.limit(pos);
                    in.position(oldPos);

                    buf.put(in);
                    if (buf.position() > maxLineLength)
                        {
                        throw new BufferDataException("Line is too long: "
                                + buf.position());
                        }
                    buf.flip();
                    buf.limit(buf.limit() - matchCount);
                    
                    final String headers = buf.getString(decoder);
                    final Map<String, SipHeader> headersMap = 
                        createHeadersMap(headers);
                    
                    ctx.setHeaders(headersMap);
                    final long contentLength = getContentLength(headersMap);
                    
                    //out.write(headers);
                    buf.clear();

                    in.limit(oldLimit);
                    in.position(pos);
                    oldPos = pos;
                    count = 0;
                    }
                }
            else
                {
                count = 0;
                }
            }

        // Put remainder to buf.
        in.position(oldPos);
        buf.put(in);

        return count;
        }
    
    private long getContentLength(final Map<String, SipHeader> headersMap)
        {
        final SipHeader header = headersMap.get(SipHeaderNames.CONTENT_LENGTH);
        if (header == null)
            {
            LOG.debug("Did not get content length header -- no body");
            return 0;
            }
        
        final String headerValue = header.getValue().getBaseValue();
        return Long.parseLong(headerValue);
        }

    private Map<String, SipHeader> createHeadersMap(final String headers) 
        throws IOException
        {
        final Map<String, SipHeader> headersMap = 
            new ConcurrentHashMap<String, SipHeader>();
        
        final Scanner scan = new Scanner(headers);
        scan.useDelimiter("\r\n");
        while (scan.hasNext())
            {
            final String header = scan.next();
            addHeader(header, headersMap);
            }
        return headersMap;
        }

    private void addHeader(final String headerString, 
        final Map<String, SipHeader> headers) throws IOException 
        {
        /*
        final SipHeader header = 
            this.m_headerFactory.createHeader(headerString);
        headers.put(header.getName(), header);   
        */     
        }
    

    private class Context
        {
        private final CharsetDecoder decoder;

        private final ByteBuffer buf;

        private int matchCount = 0;

        private Map<String, SipHeader> m_headers;

        private Context()
            {
            decoder = charset.newDecoder();
            buf = ByteBuffer.allocate(80).setAutoExpand(true);
            }

        public void setHeaders(Map<String, SipHeader> headers)
            {
            this.m_headers = headers;
            }

        public CharsetDecoder getDecoder()
            {
            return decoder;
            }

        public ByteBuffer getBuffer()
            {
            return buf;
            }

        public int getMatchCount()
            {
            return matchCount;
            }

        public void setMatchCount(int matchCount)
            {
            this.matchCount = matchCount;
            }
        }
    }
