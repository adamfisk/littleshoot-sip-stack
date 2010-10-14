package org.lastbamboo.common.sip.stack.transaction.client;

import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.sip.stack.message.SipMessage;


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
    SipClientTransaction createClientTransaction(SipMessage request, 
        OfferAnswerTransactionListener transactionListener);

    }
