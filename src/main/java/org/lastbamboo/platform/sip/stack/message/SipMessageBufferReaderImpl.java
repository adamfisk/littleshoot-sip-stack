package org.lastbamboo.platform.sip.stack.message;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Reader for reading SIP messages from ByteBuffer data.
 */
public class SipMessageBufferReaderImpl implements SipMessageBufferReader
    {
    
    private static final Log LOG = 
        LogFactory.getLog(SipMessageBufferReaderImpl.class);
    
    private final Charset m_ascii;

    private final CharsetDecoder m_decoder;

    private final SipMessageVisitor m_messageVisitor;

    private final SipMessageFactory m_messageFactory;

    private int m_totalMessages;

    private long m_totalTime;

    private int m_factoryCalls;

    private long m_totalFactoryTime;
    
    private static final ByteBuffer EMPTY_BYTE_BUFFER = ByteBuffer.allocate(0);

    /**
     * Creates a new SIP message buffer reader.
     * 
     * @param factory The factory for creating SIP messages.
     * @param visitor The visitor for visiting messages once we've created 
     * them.
     */
    public SipMessageBufferReaderImpl(final SipMessageFactory factory, 
        final SipMessageVisitor visitor)
        {
        this.m_messageFactory = factory;
        this.m_messageVisitor = visitor;
        this.m_ascii = Charset.forName("US-ASCII");   
        this.m_decoder = this.m_ascii.newDecoder();
        }

    public ByteBuffer readMessages(final ByteBuffer partialBuffer,
        final ByteBuffer newBuffer)
        {
        LOG.debug("Reading messages...");
        if (partialBuffer == null || newBuffer == null)
            {
            LOG.error("Could not parse null buffer: "+partialBuffer+newBuffer);
            throw new NullPointerException(
                "Null buffer: "+partialBuffer+newBuffer);
            }
        
        final CharBuffer partialChars;
        final CharBuffer newChars;
        try
            {
            partialChars = this.m_decoder.decode(partialBuffer);
            newChars = this.m_decoder.decode(newBuffer);
            }
        catch (final CharacterCodingException e)
            {
            // This should never, ever happen!  If it does, we should probably
            // close the connection.
            LOG.error("Could not decode buffer!!", e);
            return null;
            }
        
        
        String messageString = partialChars.toString() + newChars.toString();
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Partial string: "+partialChars.toString());
            LOG.debug("New string: "+newChars.toString());
            }
        int beginIndex = 0;
        while (true && !(StringUtils.isEmpty(messageString)))
            {
            final long startTime = System.currentTimeMillis();
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Reading message string:\n"+messageString);
                LOG.debug("Total string length: "+messageString.length());
                }
            final SipMessage message;
            try 
                {
                final long messageStartTime = System.currentTimeMillis();
                message = this.m_messageFactory.createSipMessage(messageString);
                
                if (message == null)
                    {
                    // Read an incomplete message.  Save the partially read 
                    // portion and read it on the next pass.
                    if (LOG.isDebugEnabled())
                        {
                        LOG.debug("Could not create message from partial " +
                            "data: "+messageString);
                        }
                    final byte[] messageBytes = 
                        messageString.getBytes("US-ASCII");
                    return ByteBuffer.wrap(messageBytes);
                    }
                final long messageEndTime = System.currentTimeMillis();
                final long totalFactoryTime = messageEndTime - messageStartTime;
                this.m_factoryCalls++;
                this.m_totalFactoryTime += totalFactoryTime;
                if (LOG.isDebugEnabled())
                    {
                    LOG.debug("Created "+message.getMethod()+
                        " message with branch ID: "+message.getBranchId()+
                        " in: " + totalFactoryTime);
                    LOG.debug("Average message time: "+
                        this.m_totalFactoryTime/this.m_factoryCalls);
                    
                    LOG.debug("Read SIP message: "+message); 
                    }
                               
                message.accept(this.m_messageVisitor);
                final long endTime = System.currentTimeMillis();
                final long totalTime = endTime-startTime;
                this.m_totalMessages++;
                this.m_totalTime += totalTime;
                if (LOG.isDebugEnabled())
                    {
                    LOG.debug("Processed "+message.getMethod()+
                        " message with branch ID: "+message.getBranchId()+
                        " in: " + totalTime);
                    LOG.debug("Average processing time: "+
                        this.m_totalTime/this.m_totalMessages);
                    }
                }
            catch (final IOException e)
                {
                // Error reading the message syntax.  
                LOG.warn("Error reading message!!", e);
                // TODO: Send a message back saying we could not understand 
                // the message, and then close the connection.
                try
                    {
                    return ByteBuffer.wrap(messageString.getBytes("US-ASCII"));
                    }
                catch (final UnsupportedEncodingException uee)
                    {
                    LOG.error("Should never happen", uee);
                    return null;
                    }
                }
            
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Removing first "+beginIndex+" bytes from string...");
                }

            beginIndex = message.getLength();
            messageString = messageString.substring(beginIndex);
            if (!StringUtils.isEmpty(messageString))
                {
                LOG.debug("Moving on to next message: "+messageString);
                }
            }
        
        return EMPTY_BYTE_BUFFER;
        }
    }
