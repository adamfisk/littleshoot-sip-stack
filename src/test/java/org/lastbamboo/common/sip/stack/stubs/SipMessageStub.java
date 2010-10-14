package org.lastbamboo.common.sip.stack.stubs;

import java.util.List;
import java.util.Map;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitor;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;

/**
 * Stub for testing.
 */
public class SipMessageStub implements SipMessage
    {

    public String getBranchId()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public SipMethod getMethod()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public SipHeader getHeader(String headerName)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public List getRouteSet()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public ByteBuffer getBody()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void accept(SipMessageVisitor visitor)
        {
        // TODO Auto-generated method stub

        }

    public String getStartLine()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Map<String, SipHeader> getHeaders()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public String getTransactionKey() {
        // TODO Auto-generated method stub
        return null;
    }
    }
