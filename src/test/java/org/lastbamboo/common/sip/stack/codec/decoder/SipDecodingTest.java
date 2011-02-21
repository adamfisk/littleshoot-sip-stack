package org.lastbamboo.common.sip.stack.codec.decoder;

import java.net.URI;
import java.util.ArrayList;
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
import org.lastbamboo.common.sip.stack.codec.encoder.SipMessageEncoder;
import org.lastbamboo.common.sip.stack.codec.encoder.SipMessageEncoderImpl;
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
import org.littleshoot.util.mina.ByteBufferUtils;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for SIP message decoding. 
 */
public class SipDecodingTest
    {

    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
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
     * Test for basic SIP message decoding of many full messages.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @SuppressWarnings("boxing")
    @Test
    public void testBasicSipDecoding() throws Exception
        {
        final Invite request = createInvite();
        final SipMessageEncoder encoder = new SipMessageEncoderImpl();
        final ByteBuffer buf = encoder.encode(request);
        
        final Collection<ByteBuffer> buffers = new LinkedList<ByteBuffer>();
        for (int i = 0; i < 10; i++)
            {
            buffers.add(buf.duplicate());
            }
        final ByteBuffer manyMessages = ByteBufferUtils.combine(buffers);
        
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
        decoder.decode(null, manyMessages, out);
        
        Assert.assertEquals(10, messages.size());
        Assert.assertTrue(messages.iterator().next() instanceof Invite);
        Assert.assertTrue(!manyMessages.hasRemaining());
        }
    
    /**
     * Test for basic SIP message decoding of messages that are delivered
     * parially in various ways.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @SuppressWarnings("boxing")
    @Test
    public void testPartialMessageSipDecoding() throws Exception
        {
        final Invite request = createInvite();
        final SipMessageEncoder encoder = new SipMessageEncoderImpl();
        final ByteBuffer buf = encoder.encode(request);
        
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
            
        // Now test sending the message one byte at a time.
        for (int i = 0; i < buf.capacity(); i++)
            {
            // Always read one more byte.
            buf.limit(i + 1);
            decoder.decode(null, buf, out);
            }
        
        
        Assert.assertEquals(1, messages.size());
        Assert.assertTrue(messages.iterator().next() instanceof Invite);
        Assert.assertTrue(!buf.hasRemaining());
        }
    
    /**
     * "Ultimate" test combining many different types of messages partially
     * delivered.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    @SuppressWarnings("boxing")
    @Test
    public void ultimateSipDecodingTest() throws Exception
        {
        final Collection<SipMessage> messages = new LinkedList<SipMessage>();

        messages.add(createInvite());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(createNoBodyInvite());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(createInviteOk());
        messages.add(createInviteOk());
        messages.add(createNoBodyInvite());
        messages.add(createNoBodyInvite());
        messages.add(createInvite());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(createInvite());
        messages.add(createNoBodyInvite());
        messages.add(createInvite());
        messages.add(createInvite());
        messages.add(createInviteOk());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(createInvite());
        messages.add(createNoBodyInvite());
        messages.add(createInvite());
        messages.add(createInviteOk());
        messages.add(createInviteOk());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(new DoubleCrlfKeepAlive());
        messages.add(createInvite());
        messages.add(createNoBodyInvite());
        messages.add(createInviteOk());
        messages.add(createInvite());
        messages.add(createRegister());
        messages.add(createNoBodyInvite());
        messages.add(createInviteOkNoBody());
        messages.add(createRegisterOk());
        messages.add(createInvite());
        
        final ByteBuffer buf = combine(messages.toArray(new SipMessage[0]));
        
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipProtocolCodecFactory codecFactory =
            new SipProtocolCodecFactory(headerFactory);
        
        final ProtocolDecoder decoder = codecFactory.getDecoder();
        
        final List<Object> readMessages = new ArrayList<Object>();
        final ProtocolDecoderOutput out = new ProtocolDecoderOutput()
            {
            public void flush() {}

            public void write(final Object message)
                {
                m_log.debug("Decoded: "+message);
                readMessages.add(message);
                }
            };
            
        // Now test sending the message one byte at a time.
        for (int i = 0; i < buf.capacity(); i++)
            {
            // Always read one more byte.
            buf.limit(i + 1);
            decoder.decode(null, buf, out);
            }
        
        
        Assert.assertEquals(messages.size(), readMessages.size());
        Assert.assertTrue(!buf.hasRemaining());
        
        // Now make sure we read the expected messages.
        int index = 0;
        for (final SipMessage message : messages)
            {
            Assert.assertEquals("Unexpected class at index: "+index, 
                message.getClass(), readMessages.get(index).getClass());
            index++;
            }
        }

    private static ByteBuffer combine(final SipMessage... messages)
        {
        final SipMessageEncoder encoder = new SipMessageEncoderImpl();
        final Collection<ByteBuffer> buffers = new LinkedList<ByteBuffer>();
        for (int i = 0; i < messages.length; i++)
            {
            final ByteBuffer buf = encoder.encode(messages[i]);
            buffers.add(buf);
            }
        
        return ByteBufferUtils.combine(buffers);
        }
    
    private static Invite createNoBodyInvite() throws Exception
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
    
    private static SipResponse createInviteOk() throws Exception
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

    }
