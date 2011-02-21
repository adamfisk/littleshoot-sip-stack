package org.lastbamboo.common.sip.stack.message;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;
import org.littleshoot.util.StringUtils;

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
    
    public void accept(final SipMessageVisitor visitor)
        {
        visitor.visitDoubleCrlfKeepAlive(this);
        }

    public ByteBuffer getBody()
        {
        return DOUBLE_CRLF.duplicate();
        }

    public String getBranchId()
        {
        return org.apache.commons.lang.StringUtils.EMPTY;
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

    public List<SipHeaderValue> getRouteSet()
        {
        return Collections.emptyList();
        }
    
    public String getStartLine()
        {
        return org.apache.commons.lang.StringUtils.EMPTY;
        }

    public String getTransactionKey()
        {
        final String branchId = getBranchId();
        final SipMethod method = getMethod();
        return branchId + method.toString();
        }
    }
