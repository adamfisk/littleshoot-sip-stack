package org.lastbamboo.common.sip.stack.stubs;

import java.util.Map;
import java.util.TreeMap;

import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.message.SipResponse;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;

public class SipResponseStub extends SipResponse
    {

    public SipResponseStub()
        {
        super(200, "OK", createHeaders(), ByteBuffer.allocate(0));
        }

    private static Map<String, SipHeader> createHeaders()
        {
        final Map<String,SipHeader> headers = 
            new TreeMap<String, SipHeader>();
        
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipHeader header = headerFactory.createHeader("CSeq", "314159 INVITE");
        headers.put(header.getName(), header);
        return headers;
        }

    }
