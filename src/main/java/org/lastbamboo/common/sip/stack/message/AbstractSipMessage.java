package org.lastbamboo.common.sip.stack.message;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;
import org.springframework.util.Assert;

/**
 * Abstracts out generalized functions common to all SIP messages.
 */
public abstract class AbstractSipMessage implements SipMessage
    {

    private static final Log LOG = LogFactory.getLog(AbstractSipMessage.class);
    
    private final Map m_headers;
    
    private final String m_method;

    private final String m_startLine;

    private final byte[] m_messageBody;
    
    private static final byte[] EMPTY_BODY = new byte[0];
    
    private byte[] m_messageBytes;

    /**
     * Creates a new SIP message with the specified first line of the message
     * and the specified headers.  This constructor creates a message with
     * no message body.
     * 
     * @param startLine The first line of the message.
     * @param headers The message headers.
     */
    public AbstractSipMessage(final String startLine, final Map headers)
        {
        this(startLine, headers, EMPTY_BODY);
        }
    
    /**
     * Creates a new SIP message with the specified first line of the message,
     * the specified headers, and the specified message body.
     * 
     * @param startLine The first line of the message.
     * @param headers The message headers.
     * @param body The message body.
     */
    public AbstractSipMessage(final String startLine, final Map headers, 
        final byte[] body)
        {
        this.m_startLine = startLine;
        this.m_headers = headers;
        final SipHeader cseq = 
            (SipHeader) headers.get(SipHeaderNames.CSEQ);
        this.m_method = SipMessageUtils.extractCSeqMethod(cseq);
        this.m_messageBody = body;
        this.m_messageBytes = createBytes(startLine, headers, body);
        
        // Make sure the content length is correct.
        checkContentLength(headers, body);
        }
    
    private void checkContentLength(final Map headers, final byte[] body)
        {
        final SipHeader contentLength = 
            (SipHeader) headers.get(SipHeaderNames.CONTENT_LENGTH);
        
        if (contentLength == null)
            {
            if (body.length != 0)
                {
                LOG.error("Should be an empty body!!");
                }
            return;
            }
        final int length = 
            Integer.parseInt(contentLength.getValue().getBaseValue());
        if (length != body.length)
            {
            LOG.error("Unexpected content length: expected: "+length+
                " but was: "+body.length);
            }
        Assert.isTrue(length == body.length, 
            "Unexpected content length: expected: "+length+" but was: "+
            body.length);
        }

    public SipHeader getHeader(final String headerName) 
        {
        return (SipHeader) this.m_headers.get(headerName);
        }
    
    public Map getHeaders()
        {
        // Return a copy of the headers to preserve the immutability of this
        // class.  This will only really get called when we're making a copy
        // of a message any way, so we might as well just make a copy.
        synchronized (this.m_headers)
            {
            return new ConcurrentHashMap(this.m_headers);
            }
        }
    
    public final byte[] getBytes()
        {
        return this.m_messageBytes;
        }
    
    private static final byte[] createBytes(final String firstLine, 
        final Map headers, final byte[] body)
        {
        final StringBuffer sb = new StringBuffer();
        sb.append(firstLine);
        sb.append("\r\n");
        sb.append(getHeaderString(headers.values()));
        sb.append("\r\n");
        final String headerString = sb.toString();
        try
            {
            final byte[] headerBytes = headerString.getBytes("US-ASCII");
            if (ArrayUtils.isEmpty(body))
                {
                if (LOG.isDebugEnabled())
                    {
                    LOG.debug("Returning only headers...");
                    }
                return headerBytes;
                }
            final byte[] messageBytes = 
                ArrayUtils.addAll(headerBytes, body);
            return messageBytes;
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("Encoding error!!", e);
            throw new IllegalStateException("Bad encoding??");
            }
        }
    
    private static String getHeaderString(final Collection headers)
        {
        final StringBuffer sb = new StringBuffer();
        for (final Iterator iter = headers.iterator(); iter.hasNext();)
            {
            final SipHeader curHeader = (SipHeader) iter.next();
            sb.append(curHeader.getName());
            sb.append(": ");
            appendHeaderValues(sb, curHeader.getValues());
            sb.append("\r\n");
            }
        return sb.toString();
        }
    
    private static void appendHeaderValues(final StringBuffer sb, 
        final List values)
        {
        for (final Iterator iter = values.iterator(); iter.hasNext();)
            {
            final SipHeaderValue value = (SipHeaderValue) iter.next();
            sb.append(value.getBaseValue());
            final Map params = value.getParams();
            for (final Iterator iterator = params.entrySet().iterator(); 
                iterator.hasNext();)
                {
                sb.append(";");
                final Map.Entry param = (Map.Entry) iterator.next();
                sb.append(param.getKey());
                sb.append("=");
                sb.append(param.getValue());
                }
            if (iter.hasNext())
                {
                sb.append(",");
                }
            }
        }

    public ByteBuffer toByteBuffer()
        {
        final byte[] bytes = getBytes();
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Returning bytes with length: "+bytes.length);
            }
        return ByteBuffer.wrap(bytes);
        }
    
    public byte[] getBody()
        {
        return this.m_messageBody;
        }
    
    public int getTotalLength() 
        {
        return getBytes().length;
        }
    
    public final String getBranchId()
        {
        final SipHeader via = 
            (SipHeader) this.m_headers.get(SipHeaderNames.VIA);
        return via.getValue().getParamValue("branch");
        }

    public final String getMethod()
        {
        return this.m_method;
        }

    public List getRouteSet()
        {
        final SipHeader recordRoute = 
            (SipHeader) this.m_headers.get(SipHeaderNames.RECORD_ROUTE);
        
        if (recordRoute == null)
            {
            return Collections.EMPTY_LIST;
            }
        return recordRoute.getValues();
        }
    
    public String getStartLine() 
        {
        return this.m_startLine;
        }
    
    public String toString()
        {
        return new String(getBytes());
        }

    }
