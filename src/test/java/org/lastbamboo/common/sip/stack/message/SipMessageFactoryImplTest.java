package org.lastbamboo.common.sip.stack.message;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.id.uuid.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.mina.common.ByteBuffer;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderParamNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;

/**
 * Test for the SIP message factory.
 */
public class SipMessageFactoryImplTest extends TestCase
    {
    
    private final Logger LOG = LoggerFactory.getLogger(SipMessageFactoryImplTest.class);
    
    /**
     * Tests the method for stripping the top Via header.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testStripVia() throws Exception 
        {
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final InetSocketAddress socketAddress1 = 
            new InetSocketAddress("46.76.232.1", 5525);
        
        Invite request = createInvite();
        
        // Forward it so we have a via to strip.
        final Invite invite = 
            messageFactory.createInviteToForward(socketAddress1, request);
        
        final SipHeader via = invite.getHeader(SipHeaderNames.VIA);
        final List<SipHeaderValue> values = via.getValues();
        assertEquals(2, values.size());
        
        final SipResponse response = messageFactory.createInviteOk(
            request, UUID.randomUUID(), new URI("bob@cookies.com"), 
            request.getBody());
        
        final SipMessage strippedViaMessage = messageFactory.stripVia(response);
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
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final InetSocketAddress socketAddress = 
            new InetSocketAddress("46.76.32.1", 3525);
        
        final Invite request = createInvite();
        
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
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final InetSocketAddress socketAddress = 
            new InetSocketAddress("46.76.32.1", 3525);
        
        final String viaValue = 
            "SIP/2.0/UDP pc33.atlanta.com;branch=z9hG4bKnashds8";
        final Invite request = createInvite(viaValue);
        final SipHeader via = 
            headerFactory.createSentByVia(socketAddress.getAddress());
        final Invite message = messageFactory.addVia(request, via);
        final SipHeader via2 = message.getHeader(SipHeaderNames.VIA);
        final List<SipHeaderValue> vias = via2.getValues();
        assertEquals(2, vias.size());
        
        final SipHeaderValue topVia = via2.getValue();
        assertEquals(via.getValue(), topVia);
        }
    

    private Invite createInvite() throws Exception
        {
        final String viaValue = "SIP/2.0/TCP 192.168.1.5;" +
            "branch=z9hG4bKd79225d,SIP/2.0/TCP 192.168.1.5;" +
            "received=127.0.0.1;branch=z9hG4bKf2b6a7e;rport=1348";
            
        return createInvite(viaValue);
        }
    
    private Invite createInvite(final String viaValue) throws Exception
        {
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final UUID instanceId = UUID.randomUUID();
        
        Invite request = messageFactory.createInviteRequest("Bob", 
            new URI("sip:bob@biloxi.com"), new URI("alice@atlanta.com"), 
            instanceId, new URI("sip:alice@pc33.atlanta.com"), 
            ByteBuffer.wrap(sdp.getBytes("US-ASCII")));
        
        final SipHeader via = headerFactory.createHeader("Via", viaValue);

        request = messageFactory.addVia(request, via);
        return request;
        }
    }
