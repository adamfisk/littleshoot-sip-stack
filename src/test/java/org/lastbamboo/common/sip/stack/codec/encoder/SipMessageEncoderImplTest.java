package org.lastbamboo.common.sip.stack.codec.encoder;

import java.net.URI;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.id.uuid.UUID;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.SimpleByteBufferAllocator;
import org.littleshoot.mina.filter.codec.ProtocolDecoder;
import org.littleshoot.mina.filter.codec.ProtocolDecoderOutput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lastbamboo.common.sip.stack.codec.SipProtocolCodecFactory;
import org.lastbamboo.common.sip.stack.message.DoubleCrlfKeepAlive;
import org.lastbamboo.common.sip.stack.message.Invite;
import org.lastbamboo.common.sip.stack.message.Register;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipMessageFactoryImpl;
import org.lastbamboo.common.sip.stack.message.SipResponse;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;
import org.littleshoot.util.mina.MinaUtils;

/**
 * Test for SIP message encoding.
 */
public class SipMessageEncoderImplTest
    {

    /**
     * MINA does some funky things if we don't do this first.
     */
    @Before
    public void setUp()
        {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        }
    
    /**
     * Tests SIP encoding.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @Test
    public void testEncoding() throws Exception
        {
        final SipMessageEncoder encoder = new SipMessageEncoderImpl();
        
        final Collection<SipMessage> messages = new LinkedList<SipMessage>();
        messages.add(createRegister());
        messages.add(createRegisterOk());
        messages.add(createInviteNoBody());
        messages.add(createInvite());
        messages.add(createInviteOk());
        messages.add(createInviteOkNoBody());
        messages.add(new DoubleCrlfKeepAlive());
        
        for (final SipMessage message : messages)
            {
            final ByteBuffer encoded = encoder.encode(message);
            
            //System.out.println("'"+MinaUtils.toAsciiString(encoded)+"'");
            
            // Make sure we can decode what we encoded.  A problem here *could*
            // always indicate a problem with the decoder as well.
            final SipMessage decoded = decode(encoded);
            
            Assert.assertEquals(message.getClass(), decoded.getClass());
            }
        }
    

    private static SipMessage decode(final ByteBuffer encoded) throws Exception
        {
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipProtocolCodecFactory codecFactory =
            new SipProtocolCodecFactory(headerFactory);
        
        final ProtocolDecoder decoder = codecFactory.getDecoder();
        
        final List<Object> messages = new LinkedList<Object>();
        final ProtocolDecoderOutput out = new ProtocolDecoderOutput()
            {
            public void flush() {}

            public void write(final Object message)
                {
                messages.add(message);
                }
            };
        decoder.decode(null, encoded, out);
        
        return (SipMessage) messages.iterator().next();
        }


    private static Invite createInviteNoBody() throws Exception
        {
        final String viaValue = "SIP/2.0/TCP 192.168.1.5;" +
            "branch=z9hG4bKd79225d,SIP/2.0/TCP 192.168.1.5;" +
            "received=127.0.0.1;branch=z9hG4bKf2b6a7e;rport=1348";
            
        return createNoBodyInvite(viaValue);
        }
    
    private static Invite createInvite() throws Exception
        {
        final String viaValue = "SIP/2.0/TCP 192.168.1.5;" +
            "branch=z9hG4bKd79225d,SIP/2.0/TCP 192.168.1.5;" +
            "received=127.0.0.1;branch=z9hG4bKf2b6a7e;rport=1348";
            
        return createInvite(viaValue);
        }
    
    private static Invite createNoBodyInvite(final String viaValue) throws Exception
        {
        return createInvite(viaValue, "");
        }
    
    private static Invite createInvite(final String viaValue) throws Exception
        {
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        return createInvite(viaValue, sdp);
        }
    
    private static Invite createInvite(final String viaValue, final String body) 
        throws Exception
        {
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final UUID instanceId = UUID.randomUUID();
        
        final Invite request = messageFactory.createInviteRequest("Bob", 
            new URI("sip:bob@biloxi.com"), new URI("alice@atlanta.com"), 
            instanceId, new URI("sip:alice@pc33.atlanta.com"), 
            ByteBuffer.wrap(body.getBytes("US-ASCII")));
        
        final SipHeader via = headerFactory.createHeader("Via", viaValue);
    
        return messageFactory.addVia(request, via);
        }
    
    
    private static Register createRegister() throws Exception
        {
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl();
        
        final URI contactUri = new URI("alice@atlanta.com");
        final UUID instanceId = UUID.randomUUID();
        
        final URI domain = new URI("sip:lastbamboo.org");
        final URI client = new URI("sip:4279@lastbamboo.org");
        return messageFactory.createRegisterRequest(
            domain, "Anonymous", client, instanceId, contactUri);
        }
    
    
    private static SipMessage createRegisterOk() throws Exception
        {
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl();
        final Register register = createRegister();
        return messageFactory.createRegisterOk(register);
        }
    
    private static SipResponse createInviteOk() 
        throws Exception
        {
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl();
        final UUID instanceId = UUID.randomUUID();
        final Invite invite = createInvite();
        
        final String sdp = 
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000";
        final URI contactUri = new URI("alice@atlanta.com");
        final SipResponse ok = 
            messageFactory.createInviteOk(invite, instanceId, contactUri, 
                MinaUtils.toBuf(sdp));
        
        return ok;
        }
    
    private static SipResponse createInviteOkNoBody() throws Exception
        {
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl();
        final UUID instanceId = UUID.randomUUID();
        final Invite invite = createInvite();
        final URI contactUri = new URI("alice@atlanta.com");
        final SipResponse ok = 
            messageFactory.createInviteOk(invite, instanceId, contactUri, 
                ByteBuffer.allocate(0));
        
        return ok;
        }
    }
