package org.lastbamboo.common.sip.stack.stubs;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

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

    public String getMethod()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public byte[] getBytes()
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

    public ByteBuffer toByteBuffer()
        {
        return ByteBuffer.allocate(0);
        }

    public byte[] getBody()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public int getTotalLength()
        {
        // TODO Auto-generated method stub
        return 0;
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

    public Map getHeaders()
        {
        // TODO Auto-generated method stub
        return null;
        }

    }
