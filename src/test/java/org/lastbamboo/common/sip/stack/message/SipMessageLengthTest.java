package org.lastbamboo.common.sip.stack.message;

import junit.framework.TestCase;


public class SipMessageLengthTest extends TestCase
    {

    
    public void testLength() throws Exception
        {
        final String msg1 = "INVITE sip:338728056@lastbamboo.org SIP/2.0\r\n"+
"To: Anonymous <sip:338728056@lastbamboo.org>\r\n"+
"Via: SIP/2.0/TCP 192.168.1.3;branch=z9hG4bK0a71e26\r\n"+
"CSeq: 3 INVITE\r\n"+
"Content-Length: 313\r\n"+
"From: Anonymous <sip:1199792423@lastbamboo.org>;tag=4743a26e-f\r\n"+
"Contact: <sip:1199792423@192.168.1.3>;+sip.instance=\"<urn:uuid:985e86e2-cf99-47b8-969a-bc07eb65e3c7>\"\r\n"+
"Call-ID: ae2866b-\r\n"+
"Max-Forwards: 70\r\n"+
"Expires: 7200\r\n"+
"\r\n"+
"v=0\r\n"+
"o=- 0 0 IN IP4 192.168.1.3\r\n"+
"s=-\r\n"+
"t=0 0\r\n"+
"m=message 62214 tcp-pass http\r\n"+
"c=IN IP4 72.3.139.235\r\n"+
"a=candidate:1 a1ef2e0b-7e6a-404b-9453-f027d3dad145 tcp-pass 3 72.3.139.235 62214\r\n"+
"m=message 8107 tcp-pass http\r\n"+
"c=IN IP4 192.168.1.3\r\n"+
"a=candidate:1 c6174cb1-fe91-4fb1-acf8-6e5528e6a3ce tcp-pass 1 192.168.1.3 8107\r\n";
        
        System.out.println("Length: "+msg1.length());
        
        final String msg2 = " sip:338728056@lastbamboo.org SIP/2.0\r\n"+
"To: Anonymous <sip:338728056@lastbamboo.org>\r\n"+
"Via: SIP/2.0/TCP 192.168.1.3;branch=z9hG4bKf35107c\r\n"+
"CSeq: 11 INVITE\r\n"+
"Content-Length: 319\r\n"+
"From: Anonymous <sip:1199792423@lastbamboo.org>;tag=eb2a0e9c-8\r\n"+
"Contact: <sip:1199792423@192.168.1.3>;+sip.instance=\"<urn:uuid:9821d8c2-244b-465b-81ba-44630f0102ec>\"\r\n"+
"Call-ID: f1daca5-\r\n"+
"Max-Forwards: 70\r\n"+
"Expires: 7200\r\n"+
"\r\n"+
"v=0\r\n"+
"o=- 0 0 IN IP4 192.168.1.3\r\n"+
"s=-\r\n"+
"t=0 0\r\n"+
"m=message 57371 tcp-pass http\r\n"+
"c=IN IP4 72.3.139.235\r\n"+
"a=candidate:1 4abe2ff5-4d1a-48e0-bb8b-0517c9f70bf7 tcp-pass 3 72.3.139.235 57371\r\n"+
"m=message 8107 tcp-pass http\r\n"+
"c=IN IP4 192.168.1.3\r\n"+
"a=candidate:1 34d4d782-b309-45ec-b73d-e1282aee6aea tcp-pass 1 192.168.1.3 8107\r\n";
        
        System.out.println("Lenghth: "+msg2.length());
        }
    }
