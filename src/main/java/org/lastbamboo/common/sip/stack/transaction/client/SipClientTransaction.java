package org.lastbamboo.common.sip.stack.transaction.client;

import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageVisitor;

/**
 * Interface for client transactions.
 */
public interface SipClientTransaction extends SipMessageVisitor
    {

    /**
     * Accessor for the request that started the transaction.
     * 
     * @return The request that started the transaction.
     */
    SipMessage getRequest();
    
    /**
     * Accessor for the total transaction time for the transaction.
     * 
     * @return The total time the transaction took.
     */
    long getTransactionTime();

    /**
     * Adds a listener to the transaction.  This should typically be called 
     * before any message has been sent -- before the transaction has started 
     * -- to ensure events aren't missed.
     * 
     * @param listener The listener to add.
     */
    void addListener(OfferAnswerTransactionListener listener);

    }
