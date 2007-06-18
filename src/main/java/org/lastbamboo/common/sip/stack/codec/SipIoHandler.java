package org.lastbamboo.common.sip.stack.codec;

import java.io.IOException;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitor;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Protocol handler for SIP messages.
 */
public class SipIoHandler implements IoHandler
    {

    private final Logger LOG = 
        LoggerFactory.getLogger(SipIoHandler.class);
    
    private final SipMessageVisitorFactory m_visitorFactory;

    private static int s_messagesRead = 0;
    
    /**
     * Creates a new protocol handler.
     * 
     * @param visitorFactory The factory for creating visitors for read 
     * messages.  Factories might create specialized visitors for clients or
     * servers, for example.
     */
    public SipIoHandler(final SipMessageVisitorFactory visitorFactory)
        {
        m_visitorFactory = visitorFactory;
        }

    public void exceptionCaught(final IoSession session, 
        final Throwable cause) throws Exception
        {
        if (!(cause instanceof IOException)) 
            {
            LOG.warn("Unexpected exception:", cause);
            }
          session.close();
        }

    public final void messageReceived(final IoSession session, 
        final Object message) throws Exception
        {
        s_messagesRead++;
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Messages read:{} ", s_messagesRead);
            }
        final SipMessage sipMessage = (SipMessage) message;
        final SipMessageVisitor visitor = 
            this.m_visitorFactory.createVisitor(session);
        sipMessage.accept(visitor);
        }
    
    public void messageSent(final IoSession session, final Object message) 
        throws Exception
        {
        }

    public void sessionClosed(final IoSession session) throws Exception
        {
        }

    public void sessionCreated(final IoSession session) throws Exception
        {
        }

    public void sessionIdle(final IoSession session, final IdleStatus idle) 
        throws Exception
        {
        }

    public void sessionOpened(final IoSession session) throws Exception
        {
        }

    }
