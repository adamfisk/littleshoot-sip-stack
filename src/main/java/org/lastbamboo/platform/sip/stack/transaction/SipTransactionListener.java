package org.lastbamboo.platform.sip.stack.transaction;

import org.lastbamboo.platform.sip.stack.message.SipMessage;

/**
 * Interface for "transaction user" (TU) classes wishing to listen for 
 * transtaction events.
 */
public interface SipTransactionListener
    {

    /**
     * Called when the transaction completed normally with a 200 OK response.
     * 
     * @param message The message that transitioned the transaction to a
     * successful state.
     */
    void onTransactionSucceeded(final SipMessage message);

    /**
     * Called when the transaction failed with an error response, a timeout,
     * or for any other reason.  This is called really when we receive
     * anything but a 200 OK response.
     * 
     * @param message The message that transitioned the transaction to a
     * failed state.
     */
    void onTransactionFailed(final SipMessage message);

    }
