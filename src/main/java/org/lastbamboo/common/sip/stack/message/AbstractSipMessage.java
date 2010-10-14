package org.lastbamboo.common.sip.stack.message;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMessageType;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstracts out generalized functions common to all SIP messages.
 */
public abstract class AbstractSipMessage implements SipMessage
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(AbstractSipMessage.class);
    
    private final Map<String, SipHeader> m_headers;
    
    private final SipMethod m_method;

    private final String m_startLine;

    private final ByteBuffer m_messageBody;
    
    private static final ByteBuffer EMPTY_BODY = ByteBuffer.allocate(0);
    
    /**
     * Creates a new SIP message with the specified first line of the message,
     * the specified headers, and the specified message body.
     * 
     * @param startLine The first line of the message.
     * @param headers The message headers.
     * @param body The message body.
     */
    public AbstractSipMessage(final String startLine, final SipMethod method,
        final Map<String, SipHeader> headers, final ByteBuffer body)
        {
        this.m_startLine = startLine;
        this.m_headers = headers;
        this.m_method = method;
        this.m_messageBody = body.asReadOnlyBuffer();        
        }

    public AbstractSipMessage(final SipMethod method, final URI requestUri, 
        final Map<String, SipHeader> headers)
        {
        this(method, requestUri, headers, EMPTY_BODY);
        }

    public AbstractSipMessage(final SipMethod method, final URI requestUri, 
        final Map<String, SipHeader> headers, final ByteBuffer body)
        {
        this(createRequestLine(method, requestUri), method, headers, body);
        }

    public AbstractSipMessage(final int statusCode, final String reasonPhrase, 
        final Map<String, SipHeader> headers, final ByteBuffer body)
        {
        this(createResponseStatusLine(statusCode, reasonPhrase), 
            createMethod(headers), headers, body);
        }
    
    public AbstractSipMessage(final int statusCode, final String reasonPhrase, 
        final Map<String, SipHeader> headers)
        {
        this(createResponseStatusLine(statusCode, reasonPhrase), 
            createMethod(headers), headers, EMPTY_BODY);
        }

    private static SipMethod createMethod(final Map<String, SipHeader> headers)
        {
        final SipHeader cseq = headers.get(SipHeaderNames.CSEQ);
        final String methodString = SipMessageUtils.extractCSeqMethod(cseq);
        return SipMethod.valueOf(methodString);
        }

    private static String createResponseStatusLine(final int statusCode, 
        final String reasonPhrase)
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(SipMessageType.SIP_2_0.convert());
        sb.append(" ");
        sb.append(statusCode);
        sb.append(" ");
        sb.append(reasonPhrase);
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Returning response line: "+sb.toString());
            }
        return sb.toString();
        }

    private static String createRequestLine(final SipMethod method, 
        final URI requestUri)
        {
        return createRequestLine(method.name(), requestUri);
        }
    
    protected static String createRequestLine(final String method, 
        final URI requestUri)
        {
        final StringBuilder sb = new StringBuilder();
        sb.append(method);
        sb.append(" ");
        sb.append(requestUri);
        sb.append(" ");
        sb.append(SipMessageType.SIP_2_0.convert());
        return sb.toString();
        }
    

    public SipHeader getHeader(final String headerName) 
        {
        return this.m_headers.get(headerName);
        }
    
    public Map<String, SipHeader> getHeaders()
        {
        // Return a copy of the headers to preserve the immutability of this
        // class.  This will only really get called when we're making a copy
        // of a message any way, so we might as well just make a copy.
        synchronized (this.m_headers)
            {
            return new ConcurrentHashMap<String, SipHeader>(this.m_headers);
            }
        }
    
    public ByteBuffer getBody()
        {
        return this.m_messageBody;
        }
    
    public final String getBranchId()
        {
        final SipHeader via = this.m_headers.get(SipHeaderNames.VIA);
        return via.getValue().getParamValue("branch");
        }

    public final SipMethod getMethod()
        {
        return this.m_method;
        }

    public List<SipHeaderValue> getRouteSet()
        {
        final SipHeader recordRoute = 
            this.m_headers.get(SipHeaderNames.RECORD_ROUTE);
        
        if (recordRoute == null)
            {
            return Collections.emptyList();
            }
        return recordRoute.getValues();
        }
    
    public String getStartLine() 
        {
        return this.m_startLine;
        }

    public String getTransactionKey()
        {
        final String branchId = getBranchId();
        final SipMethod method = getMethod();
        return branchId + method.toString();
        }

    @Override
    public String toString()
        {
        return getClass().getSimpleName();
        }
    }
