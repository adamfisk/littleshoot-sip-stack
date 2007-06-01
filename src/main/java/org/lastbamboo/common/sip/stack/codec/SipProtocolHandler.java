package org.lastbamboo.common.sip.stack.codec;

import java.io.IOException;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitor;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SipProtocolHandler implements IoHandler
    {

    private static final Logger LOG = 
        LoggerFactory.getLogger(SipProtocolHandler.class);
    
    private final SipMessageVisitorFactory m_visitorFactory;

    private static int s_messagesRead = 0;
    
    public SipProtocolHandler(final SipMessageVisitorFactory visitorFactory)
        {
        m_visitorFactory = visitorFactory;
        }

    public void exceptionCaught(final IoSession session, 
        final Throwable cause) throws Exception
        {
        if (!(cause instanceof IOException)) 
            {
            SessionLog.warn(session, "Unexpected exception:", cause);
            }
          session.close();
        }

    public void messageReceived(final IoSession session, final Object message) 
        throws Exception
        {
        s_messagesRead++;
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Now read "+s_messagesRead+" messages...");
            }
        final SipMessage sipMessage = (SipMessage) message;
        final SipMessageVisitor visitor = 
            this.m_visitorFactory.createVisitor(session);
        //final HttpMessageVisitor httpVisitor = 
          //  new HttpToSipMessageConverter(visitor, this.m_messageFactory);
        sipMessage.accept(visitor);
        }
    
    public void messageSent(final IoSession session, final Object message) 
        throws Exception
        {
        // TODO Auto-generated method stub
        
        }

    public void sessionClosed(final IoSession session) throws Exception
        {
        // TODO Auto-generated method stub
        
        }

    public void sessionCreated(final IoSession session) throws Exception
        {
        // TODO Auto-generated method stub
        
        }

    public void sessionIdle(final IoSession session, final IdleStatus idle) 
        throws Exception
        {
        // TODO Auto-generated method stub
        
        }

    public void sessionOpened(final IoSession session) throws Exception
        {
        // TODO Auto-generated method stub
        
        }

    }
