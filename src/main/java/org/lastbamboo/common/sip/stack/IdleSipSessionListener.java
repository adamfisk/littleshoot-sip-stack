package org.lastbamboo.common.sip.stack;

/**
 * Interface for classes that wish to be notified of idle SIP sessions.
 */
public interface IdleSipSessionListener
    {

    /**
     * Called when a session is idle.
     */
    void onIdleSession();

    }
