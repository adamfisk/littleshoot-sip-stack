package org.lastbamboo.common.sip.stack.message;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
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

    private static final byte[] DOUBLE_CRLF = 
        StringUtils.toAsciiBytes("\r\n\r\n");
    
    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitDoubleCrlfKeepAlive(this);
        }

    public byte[] getBody()
        {
        return ArrayUtils.EMPTY_BYTE_ARRAY;
        }

    public String getBranchId()
        {
        return org.apache.commons.lang.StringUtils.EMPTY;
        }

    public byte[] getBytes()
        {
        return DOUBLE_CRLF;
        }

    public SipHeader getHeader(final String headerName)
        {
        return null;
        }

    public Map getHeaders()
        {
        return Collections.emptyMap();
        }

    public String getMethod()
        {
        return org.apache.commons.lang.StringUtils.EMPTY;
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
        return DOUBLE_CRLF.length;
        }

    public ByteBuffer toByteBuffer()
        {
        return ByteBuffer.wrap(DOUBLE_CRLF);
        }

    }
