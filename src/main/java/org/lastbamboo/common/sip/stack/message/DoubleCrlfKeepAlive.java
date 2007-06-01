package org.lastbamboo.common.sip.stack.message;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.util.StringUtils;

/**
 * A SIP "message" that is just a double CRLF keep alive message, as specified
 * in the SIP outbound draft at:
 *
 * http://www.ietf.org/internet-drafts/draft-ietf-sip-outbound-08.txt
 */
public class DoubleCrlfKeepAlive implements SipMessage
    {

    private static final ByteBuffer DOUBLE_CRLF = 
        ByteBuffer.wrap(StringUtils.toAsciiBytes("\r\n\r\n"));
    
    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);
    
    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitDoubleCrlfKeepAlive(this);
        }

    public ByteBuffer getBody()
        {
        return EMPTY_BYTE_BUFFER;
        }

    public String getBranchId()
        {
        return org.apache.commons.lang.StringUtils.EMPTY;
        }

    public ByteBuffer getBytes()
        {
        return DOUBLE_CRLF;
        }

    public SipHeader getHeader(final String headerName)
        {
        return null;
        }

    public Map<String, SipHeader> getHeaders()
        {
        return Collections.emptyMap();
        }

    public SipMethod getMethod()
        {
        return SipMethod.DOUBLE_CRLF_KEEP_ALIVE;
        }

    public List getRouteSet()
        {
        return Collections.EMPTY_LIST;
        }

    public String getStartLine()
        {
        return org.apache.commons.lang.StringUtils.EMPTY;
        }

    public int getTotalLength()
        {
        return DOUBLE_CRLF.capacity();
        }

    }
