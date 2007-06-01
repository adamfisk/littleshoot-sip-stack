package org.lastbamboo.common.sip.stack.message;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;

public class SipMessageUtilsTest extends TestCase
    {

    public void testConvertHeaders() throws Exception
        {
        final String via1 = "SIP/2.0/TCP 192.168.1.10;branch=z9hG4bKbfe6d94";
        final String via2 = "SIP/2.0/TCP 192.168.1.10;received=127.0.0.1;branch=z9hG4bK7f1207f;rport=54192";
        final List<String> vias = new LinkedList<String>();
        vias.add(via1);
        vias.add(via2);
        final Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Via", vias);
        
        final Map<String, SipHeader> converted = SipMessageUtils.convertHeaders(headers);
        
        final SipHeader newVia = converted.get("Via");
        final List<SipHeaderValue> values = newVia.getValues();
        assertEquals(2, values.size());
        }
    }
