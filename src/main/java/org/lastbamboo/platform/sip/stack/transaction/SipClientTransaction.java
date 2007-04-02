package org.lastbamboo.platform.sip.stack.transaction;

import org.lastbamboo.platform.sip.stack.message.SipMessage;
import org.lastbamboo.platform.sip.stack.message.SipMessageVisitor;

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

    }
