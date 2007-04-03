package org.lastbamboo.platform.sip.stack.message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.protocol.ProtocolHandler;

/**
 * Abstract class for reading SIP messages from byte buffers.
 */
public final class SipProtocolHandler implements ProtocolHandler
    {

    private static final Log LOG = 
        LogFactory.getLog(SipProtocolHandler.class);
    
    private final SipMessageFactory m_messageFactory;
    
    private final SipMessageVisitor m_messageVisitor;

    private final SipMessageBufferReader m_sipMessageBufferReader;
    
    /**
     * Buffer for any partial reads from previous passes.  We initialize this
     * to a zero length buffer because we initially have no partial data.
     */
    private ByteBuffer m_partialBuffer = ByteBuffer.allocate(0);
    
    /**
     * Creates a new protocol handler for processing SIP messages.  Different
     * SIP entities can use this protocol handler through implementing custom
     * SIP message visitors.  A SIP proxy may use a different message visitor
     * than a SIP client, for example.
     * 
     * @param factory The factory for creating SIP messages.
     * @param visitor The class for visiting SIP messages.
     */
    public SipProtocolHandler(final SipMessageFactory factory, 
        final SipMessageVisitor visitor)
        {
        this.m_messageFactory = factory;
        this.m_messageVisitor = visitor;
        this.m_sipMessageBufferReader = 
            new SipMessageBufferReaderImpl(this.m_messageFactory, 
                this.m_messageVisitor);
        }

    public void handleMessages(final ByteBuffer messageBuffer, 
        final InetSocketAddress remoteHost) throws IOException
        {
        LOG.debug("Received byte buffer...");
        messageBuffer.flip();
        LOG.trace("Handling message buffer with remaining: " + 
            messageBuffer.remaining());
        
        // The reader takes care of dispatching any complete messages to the
        // appropriate handlers.
        this.m_partialBuffer = this.m_sipMessageBufferReader.readMessages(
            this.m_partialBuffer, messageBuffer);
        LOG.trace("Finished reading all data in the buffer...");
        }

    }
