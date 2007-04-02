package org.lastbamboo.platform.sip.stack.message;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.platform.sip.stack.message.header.SipHeader;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderFactoryImpl;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderNames;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderParamNames;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderValue;

/**
 * Test for the SIP message factory.
 */
public class SipMessageFactoryImplTest extends TestCase
    {
    
    private static final Log LOG = 
        LogFactory.getLog(SipMessageFactoryImplTest.class);
    
    /**
     * Tests the method for stripping the top Via header.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testStripVia() throws Exception 
        {
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        
        final String messageString = 
            "INVITE sip:bob@biloxi.com SIP/2.0\r\n" +
            "Via: SIP/2.0/TCP 192.168.1.5;branch=z9hG4bKd79225d,SIP/2.0/TCP 192.168.1.5;received=127.0.0.1;branch=z9hG4bKf2b6a7e;rport=1348\r\n" +
            "To: Bob <bob@biloxi.com>\r\n" +
            "From: Alice <alice@atlanta.com>;tag=1928301774\r\n" +
            "Call-ID: a84b4c76e66710\r\n" +
            "CSeq: 314159 INVITE\r\n" +
            "Max-Forwards: 70\r\n" +
            "Date: Thu, 21 Feb 2002 13:02:03 GMT\r\n" +
            "Contact: <sip:alice@pc33.atlanta.com>\r\n" +
            "Content-Type: application/sdp\r\n" +
            "Content-Length: " + sdp.length() + "\r\n" +
            "\r\n" +
            sdp;
        
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final InetSocketAddress socketAddress = 
            new InetSocketAddress("46.76.32.1", 3525);
        
        final SipMessage request = 
            messageFactory.createSipMessage(messageString);
        
        final SipMessage invite = 
            messageFactory.createInviteToForward(socketAddress, request);
        final SipHeader via = invite.getHeader(SipHeaderNames.VIA);
        final List values = via.getValues();
        assertEquals(2, values.size());
        
        final SipMessage strippedViaMessage = messageFactory.stripVia(invite);
        final List strippedVias = 
            strippedViaMessage.getHeader(SipHeaderNames.VIA).getValues();
        assertEquals(1, strippedVias.size());
        }
    
    /**
     * Tests the method for creating a new message with an updated Via header
     * based on the address and port a message arrived from.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testModifyVia() throws Exception
        {
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        
        final String messageString = 
            "INVITE sip:bob@biloxi.com SIP/2.0\r\n" +
            "Via: SIP/2.0/TCP 192.168.1.5;branch=z9hG4bKd79225d,SIP/2.0/TCP 192.168.1.5;received=127.0.0.1;branch=z9hG4bKf2b6a7e;rport=1348\r\n" +
            "To: Bob <bob@biloxi.com>\r\n" +
            "From: Alice <alice@atlanta.com>;tag=1928301774\r\n" +
            "Call-ID: a84b4c76e66710\r\n" +
            "CSeq: 314159 INVITE\r\n" +
            "Max-Forwards: 70\r\n" +
            "Date: Thu, 21 Feb 2002 13:02:03 GMT\r\n" +
            "Contact: <sip:alice@pc33.atlanta.com>\r\n" +
            "Content-Type: application/sdp\r\n" +
            "Content-Length: " + sdp.length() + "\r\n" +
            "\r\n" +
            sdp;
        
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final InetSocketAddress socketAddress = 
            new InetSocketAddress("46.76.32.1", 3525);
        
        final SipMessage request = 
            messageFactory.createSipMessage(messageString);
        
        final SipMessage invite = 
            messageFactory.createInviteToForward(socketAddress, request);
        final SipHeader via = invite.getHeader(SipHeaderNames.VIA);
        final List values = via.getValues();
        assertEquals(2, values.size());
        
        final SipHeaderValue topVia = (SipHeaderValue) values.get(0);
        final Map params = topVia.getParams();
        final String rport = (String) params.get(SipHeaderParamNames.RPORT);
        assertNotNull(rport);
        assertEquals(socketAddress.getPort(), Integer.parseInt(rport));
        
        final String address = (String) params.get(SipHeaderParamNames.RECEIVED);
        assertNotNull(address);
        assertEquals(socketAddress.getAddress().getHostAddress(), address);
        }
    
    /**
     * Tests the method for adding the Via header.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testAddVia() throws Exception 
        {
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        
        final String messageString = 
            "INVITE sip:bob@biloxi.com SIP/2.0\r\n" +
            "Via: SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKnashds8\r\n" +
            "To: Bob <bob@biloxi.com>\r\n" +
            "From: Alice <alice@atlanta.com>;tag=1928301774\r\n" +
            "Call-ID: a84b4c76e66710\r\n" +
            "CSeq: 314159 INVITE\r\n" +
            "Max-Forwards: 70\r\n" +
            "Date: Thu, 21 Feb 2002 13:02:03 GMT\r\n" +
            "Contact: <sip:alice@pc33.atlanta.com>\r\n" +
            "Content-Type: application/sdp\r\n" +
            "Content-Length: " + sdp.length() + "\r\n" +
            "\r\n" +
            sdp;
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final InetSocketAddress socketAddress = 
            new InetSocketAddress("46.76.32.1", 3525);
        
        final SipMessage request = 
            messageFactory.createSipMessage(messageString);
        final SipHeader via = 
            headerFactory.createSentByVia(socketAddress.getAddress());
        final SipMessage message = messageFactory.addVia(request, via);
        final SipHeader via2 = message.getHeader(SipHeaderNames.VIA);
        final List vias = via2.getValues();
        assertEquals(2, vias.size());
        
        final SipHeaderValue topVia = via2.getValue();
        assertEquals(via.getValue(), topVia);
        
        final byte[] messageBytes = message.getBytes();
        final SipMessage invite = 
            messageFactory.createSipMessage(new String(messageBytes));
        
        final SipHeader fullCircleVia = invite.getHeader(SipHeaderNames.VIA);
        assertEquals(vias, fullCircleVia.getValues());

        LOG.debug("Message: "+message);
        }
    
    /**
     * Test to make sure the factory can handle headers with multiple values.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testHeadersWithMultipleValues() throws Exception
        {
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        
        // Use multiple via headers.
        final String message = 
            "INVITE sip:bob@biloxi.com SIP/2.0\r\n" +
            "Via: SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKnashds8," +
            "pc44.atlanta.com;branch=z9hG4bKnasROs8\r\n" +
            "To: Bob <bob@biloxi.com>\r\n" +
            "From: Alice <alice@atlanta.com>;tag=1928301774\r\n" +
            "Call-ID: a84b4c76e66710\r\n" +
            "CSeq: 314159 INVITE\r\n" +
            "Max-Forwards: 70\r\n" +
            "Date: Thu, 21 Feb 2002 13:02:03 GMT\r\n" +
            "Contact: <sip:alice@pc33.atlanta.com>\r\n" +
            "Content-Type: application/sdp\r\n" +
            "Content-Length: " + sdp.length() + "\r\n" +
            "\r\n" +
            sdp;
        
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        
        final SipMessage sipMessage = messageFactory.createSipMessage(message);
        final SipHeader via = sipMessage.getHeader(SipHeaderNames.VIA);
        final List vias = via.getValues();
        assertEquals(2, vias.size());
        }
    
    /**
     * Tests the method for creating SIP messages from raw strings.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testCreateMessageFromRawString() throws Exception
        {
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        final String message = 
            "INVITE sip:bob@biloxi.com SIP/2.0\r\n" +
            "Via: SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKnashds8\r\n" +
            "To: Bob <bob@biloxi.com>\r\n" +
            "From: Alice <alice@atlanta.com>;tag=1928301774\r\n" +
            "Call-ID: a84b4c76e66710\r\n" +
            "CSeq: 314159 INVITE\r\n" +
            "Max-Forwards: 70\r\n" +
            "Date: Thu, 21 Feb 2002 13:02:03 GMT\r\n" +
            "Contact: <sip:alice@pc33.atlanta.com>\r\n" +
            "Content-Type: application/sdp\r\n" +
            "Content-Length: " + sdp.length() + "\r\n" +
            "\r\n" +
            sdp;
        
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        
        final SipMessage sipMessage = messageFactory.createSipMessage(message);
        assertNotNull("Should have created message", sipMessage);
        final SipHeader contentLength = 
            sipMessage.getHeader(SipHeaderNames.CONTENT_LENGTH);
        assertNotNull(contentLength);
        assertEquals(sdp.length(), 
            Integer.parseInt(contentLength.getValue().getBaseValue()));
        
        final String incompleteMessage = 
            message.substring(0, message.length()-4);
        
        final SipMessage nullMessage = 
            messageFactory.createSipMessage(incompleteMessage);
        assertNull(nullMessage);
        
        final String completeMessage = incompleteMessage + "8000";
        final SipMessage notNullMessage = 
            messageFactory.createSipMessage(completeMessage);
        assertNotNull(notNullMessage);
        
        final byte[] body = notNullMessage.getBody();
        final String sdpFromMessage = new String(body);
        assertEquals("SDP is not equal", sdp, sdpFromMessage);
        }
    }
