package org.lastbamboo.common.sip.stack.codec;

import java.io.IOException;

import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.util.SessionUtil;
import org.lastbamboo.common.sip.stack.IdleSipSessionListener;
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

    private final Logger m_log = 
        LoggerFactory.getLogger(SipIoHandler.class);
    
    private final SipMessageVisitorFactory m_visitorFactory;

    private static int s_messagesRead = 0;

    private final IdleSipSessionListener m_idleSipSessionListener;
    
    /**
     * Creates a new protocol handler.
     * 
     * @param visitorFactory The factory for creating visitors for read 
     * messages.  Factories might create specialized visitors for clients or
     * servers, for example.
     */
    public SipIoHandler(final SipMessageVisitorFactory visitorFactory)
        {
        this.m_visitorFactory = visitorFactory;
        this.m_idleSipSessionListener = null;
        }

    /**
     * Creates a new protocol handler.
     * 
     * @param visitorFactory The factory for creating visitors for read 
     * messages.  Factories might create specialized visitors for clients or
     * servers, for example.
     * @param idleSipSessionListener Listener for idle sessions.
     */
    public SipIoHandler(final SipMessageVisitorFactory visitorFactory,
        final IdleSipSessionListener idleSipSessionListener)
        {
        m_visitorFactory = visitorFactory;
        this.m_idleSipSessionListener = idleSipSessionListener;
        }
    
    public void exceptionCaught(final IoSession session, 
        final Throwable cause) throws Exception
        {
        if (!(cause instanceof IOException)) 
            {
            m_log.warn("Unexpected exception:", cause);
            }
        session.close();
        }

    public final void messageReceived(final IoSession session, 
        final Object message) throws Exception
        {
        s_messagesRead++;
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Received message.  Now read: {} ", s_messagesRead);
            }
        final SipMessage sipMessage = (SipMessage) message;
        final SipMessageVisitor visitor = 
            this.m_visitorFactory.createVisitor(session);
        sipMessage.accept(visitor);
        if (m_log.isDebugEnabled())
            {
            m_log.debug("Message processing complete for: {}", sipMessage);
            }
        }
    
    public void messageSent(final IoSession session, final Object message) 
        throws Exception
        {
        m_log.debug("Message sent to: {}", session.getRemoteAddress());
        }

    public void sessionOpened(final IoSession session) throws Exception
        {
        m_log.debug("Session opened!!!");
        }
    
    public void sessionClosed(final IoSession session) throws Exception
        {
        m_log.debug("Session closed!!!");
        }

    public void sessionCreated(final IoSession session) throws Exception
        {
        SessionUtil.initialize(session);
        
        // The idle time is in seconds.  If there's been no traffic in either
        // direction for awhile, we free the connection to limit load on the
        // server.
        
        // Even though SIP governs online status, clients still send traffic to 
        // keep connections open.  If they don't, we should close them.
        
        // Note the CRLF keep alive time is 120 or less (it's randomized), so 
        // we tolerate 2 failures as well as slightly inaccurate timers.
        session.setIdleTime(IdleStatus.BOTH_IDLE, 260);
        }

    public void sessionIdle(final IoSession session, final IdleStatus status)
        {
        // This can happen, for example, if a user puts their laptop to sleep.
        // When it wakes up, the session will be seen as idle.
        m_log.debug("Killing idle session: {}", session);
        
        // Kill idle sessions.
        session.close();
        
        if (this.m_idleSipSessionListener != null)
            {
            this.m_idleSipSessionListener.onIdleSession();
            }
        }
    }
