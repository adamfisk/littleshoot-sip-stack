package org.lastbamboo.common.sip.stack.message;

import org.littleshoot.mina.common.IoSession;

/**
 * Factory for creating SIP message visitors.  Implementing classes might
 * include a factory for the server and a factory for the client, for example.
 */
public interface SipMessageVisitorFactory
    {

    /**
     * Creates a new visitor.
     * 
     * @param session The {@link IoSession} for reading or writing any necessary
     * data.
     * 
     * @return The new visitor.
     */
    SipMessageVisitor createVisitor(final IoSession session);

    }
