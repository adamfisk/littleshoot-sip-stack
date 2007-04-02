package org.lastbamboo.platform.sip.stack.transaction;

import org.lastbamboo.platform.sip.stack.message.SipMessage;


/**
 * Interface for factories for creating transactions.
 */
public interface SipTransactionFactory
    {

    /**
     * Creates a new client transaction.
     * 
     * @param request The SIP request creating the client transaction.
     * @param transactionListener The listener for transaction events.
     * @return The new client transaction.
     */
    SipClientTransaction createClientTransaction(final SipMessage request, 
        final SipTransactionListener transactionListener);

    }
