package org.lastbamboo.common.sip.stack.message;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.stack.message.Invite;
import org.lastbamboo.common.sip.stack.message.OkResponse;
import org.lastbamboo.common.sip.stack.message.Register;
import org.lastbamboo.common.sip.stack.message.RequestTimeoutResponse;
import org.lastbamboo.common.sip.stack.message.SipMessageBufferReader;
import org.lastbamboo.common.sip.stack.message.SipMessageBufferReaderImpl;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipMessageFactoryImpl;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitor;
import org.lastbamboo.common.sip.stack.message.UnknownMessage;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;

/**
 * Test for reading SIP messages from ByteBuffers.
 */
public class SipMessageBufferReaderImplTest extends TestCase 
    implements SipMessageVisitor
    {
    
    private static final Log LOG = 
        LogFactory.getLog(SipMessageBufferReaderImplTest.class);

    private final String m_fullMessageString = 
        "INVITE sip:1@lastbamboo.org SIP/2.0\r\n" +
        "To: Anonymous <sip:1@lastbamboo.org>\r\n" +
        "Via: SIP/2.0/TCP 10.250.74.236;branch=z9hG4bK0363854\r\n" +
        "Content-Length: 160\r\n" +
        "CSeq: 9 INVITE\r\n" +
        "Contact: <sip:3@127.0.0.1>;+sip.instance=\"<urn:uuid:2d76276d-9b37-44b8-9658-7dadd4e5cbad>\"\r\n" +
        "From: Bob <sip:3@lastbamboo.org>;tag=63366469-4\r\n" +
        "Call-ID: 3a3c60b-\r\n" +
        "Max-Forwards: 70\r\n" +
        "Expires: 7200\r\n" +
        "\r\n" +
        "v=0\r\n" +
        "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
        "s=Session SDP\r\n" +
        "t=0 0\r\n" +
        "c=IN IP4 pc33.atlanta.com\r\n" +
        "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
        "a=rtpmap:0 PCMU/8000\r\n";
    private int m_numInvitesReceived;
    private int m_numDoubleCrlfsReceived;
    
    
    public void tearDown() throws Exception
        {
        this.m_numInvitesReceived = 0;
        this.m_numDoubleCrlfsReceived = 0;
        }
    
    /**
     * Test buffer reading with a focus on partial buffers.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testBufferReading() throws Exception
        {
        
        final String partialMessage = 
            "INVITE sip:2@lastbamboo.org SIP/2.0\r\n" +
            "To: Anonymous <sip:1@lastbamboo.org>\r\n" +
            "Via: SIP/2.0/TCP 10.250.74.236;branch=z9hG4bK0363854\r\n" +
            "Content-Length: 160\r\n" +
            "CSeq: 9 INVITE\r\n" +
            "Contact: <sip:3@127.0.0.1>;+sip.instance=\"<urn:uuid:2d76276d-9b37-44b8-9658-7dadd4e5cbad>\"\r\n" +
            "From: Bob <sip:3@lastbamboo.org>;tag=63366469-4\r\n";
        
        final String messageCompleter =
            "Call-ID: 3a3c60b-\r\n" +
            "Max-Forwards: 70\r\n" +
            "Expires: 7200\r\n" +
            "\r\n" +
            "v=0\r\n" +
            "o=alice 53655765 2353687637 IN IP4 pc33.atlanta.com\r\n" +
            "s=Session SDP\r\n" +
            "t=0 0\r\n" +
            "c=IN IP4 pc33.atlanta.com\r\n" +
            "m=audio 3456 RTP/AVP 0 1 3 99\r\n" +
            "a=rtpmap:0 PCMU/8000\r\n";
        
        final String fullBufferString = m_fullMessageString + partialMessage;
        final byte[] fullBufferBytes = fullBufferString.getBytes("US-ASCII");
        final ByteBuffer messageBuffer = ByteBuffer.wrap(fullBufferBytes);
        messageBuffer.rewind();
        
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final SipMessageBufferReader reader = 
            new SipMessageBufferReaderImpl(messageFactory, this);
        final ByteBuffer returned = 
            reader.readMessages(ByteBuffer.allocate(0), messageBuffer);
        //assertEquals(partialMessage.length(), returned.remaining());
        
        final Charset ascii = Charset.forName("US-ASCII");   
        final CharsetDecoder decoder = ascii.newDecoder();
        
        final CharBuffer chars = decoder.decode(returned);
        final String partial = chars.toString();
        
        assertEquals("Expected:\n"+partialMessage+"\nBut was:\n"+partial, 
            partialMessage, partial);
        
        final byte[] messageCompleterBytes = 
            messageCompleter.getBytes("US-ASCII");
        final ByteBuffer messageCompleterBuffer = 
            ByteBuffer.wrap(messageCompleterBytes);
        messageCompleterBuffer.rewind();
        returned.rewind();
        
        final ByteBuffer fullMessage = 
            reader.readMessages(returned, messageCompleterBuffer);
        
        assertEquals(0, fullMessage.remaining());
        }
    
    /**
     * Tests the case where a buffer contains several complete messages.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testManyMessagesInBuffer() throws Exception
        {
        final String threeMessages = 
            this.m_fullMessageString + this.m_fullMessageString + 
            this.m_fullMessageString;
        
        this.m_numInvitesReceived = 0;
        
        final byte[] threeMessagesBytes = threeMessages.getBytes("US-ASCII");
        final ByteBuffer buf = ByteBuffer.wrap(threeMessagesBytes);
        buf.rewind();
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final SipMessageBufferReader reader = 
            new SipMessageBufferReaderImpl(messageFactory, this);
        final ByteBuffer returned = 
            reader.readMessages(ByteBuffer.allocate(0), buf);
        assertEquals(0, returned.remaining());
        assertEquals(3, this.m_numInvitesReceived);
        }
    
    /**
     * Tests the bizarre case we've seen often in the field where the word 
     * "INVITE" is mysteriously missing from the beginning of an invite message.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testMissingInvite() throws Exception
        {
        LOG.debug("Testing missing INVITE");
        
        final String missingInviteWordInvite = 
            StringUtils.substringAfter(this.m_fullMessageString, "INVITE");
        
        final String messages = 
            this.m_fullMessageString + missingInviteWordInvite + 
            this.m_fullMessageString;
        
        this.m_numInvitesReceived = 0;
        
        final byte[] messagesBytes = messages.getBytes("US-ASCII");
        final ByteBuffer buf = ByteBuffer.wrap(messagesBytes);
        buf.rewind();
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final SipMessageBufferReader reader = 
            new SipMessageBufferReaderImpl(messageFactory, this);
        final ByteBuffer returned = 
            reader.readMessages(ByteBuffer.allocate(0), buf);
        //assertEquals(0, returned.remaining());
        //assertEquals(3, this.m_numInvitesReceived);
        }
    
    /**
     * Tests the case where a buffer contains several complete messages.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testCrlfKeepAlive() throws Exception
        {
        String messages = 
            this.m_fullMessageString + "\r\n\r\n" + 
            this.m_fullMessageString;
        
        byte[] messagesBytes = messages.getBytes("US-ASCII");
        ByteBuffer buf = ByteBuffer.wrap(messagesBytes);
        buf.rewind();
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final SipMessageBufferReader reader = 
            new SipMessageBufferReaderImpl(messageFactory, this);
        ByteBuffer returned = 
            reader.readMessages(ByteBuffer.allocate(0), buf);
        assertEquals(0, returned.remaining());
        assertEquals(1, this.m_numDoubleCrlfsReceived);
        assertEquals(2, this.m_numInvitesReceived);
        
        // Now try with a double CRLF at the end.
        this.m_numInvitesReceived = 0;
        this.m_numDoubleCrlfsReceived = 0;
        messages = 
            this.m_fullMessageString + "\r\n\r\n" + 
            this.m_fullMessageString + "\r\n\r\n";
        messagesBytes = messages.getBytes("US-ASCII");
        buf = ByteBuffer.wrap(messagesBytes);
        buf.rewind();
        
        returned = reader.readMessages(ByteBuffer.allocate(0), buf);
        assertEquals(0, returned.remaining());
        assertEquals(2, this.m_numDoubleCrlfsReceived);
        assertEquals(2, this.m_numInvitesReceived);
        
        
        // Now try with multiple double CRLFs in a row.
        this.m_numInvitesReceived = 0;
        this.m_numDoubleCrlfsReceived = 0;
        messages = 
            "\r\n\r\n" + "\r\n\r\n" + "\r\n\r\n" + this.m_fullMessageString + 
            "\r\n\r\n" + this.m_fullMessageString + "\r\n\r\n";
        messagesBytes = messages.getBytes("US-ASCII");
        buf = ByteBuffer.wrap(messagesBytes);
        buf.rewind();
        
        returned = reader.readMessages(ByteBuffer.allocate(0), buf);
        assertEquals(0, returned.remaining());
        assertEquals(5, this.m_numDoubleCrlfsReceived);
        assertEquals(2, this.m_numInvitesReceived);
        }

    public void visitOk(OkResponse response)
        {
        // TODO Auto-generated method stub
        
        }

    public void visitInvite(final Invite invite)
        {
        this.m_numInvitesReceived++;
        }

    public void visitRegister(Register register)
        {
        // TODO Auto-generated method stub
        
        }

    public void visitUnknownRequest(UnknownMessage request)
        {
        // TODO Auto-generated method stub
        
        }

    public void visitRequestTimedOut(RequestTimeoutResponse response)
        {
        // TODO Auto-generated method stub
        
        }

    public void visitDoubleCrlfKeepAlive(DoubleCrlfKeepAlive keepAlive)
        {
        this.m_numDoubleCrlfsReceived++;
        }
    }
