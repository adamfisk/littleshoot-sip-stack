package org.lastbamboo.common.sip.stack.transaction;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;

/**
 * Class for creating SIP transactions.
 */
public class SipTransactionFactoryImpl implements SipTransactionFactory
    {

    private final SipTransactionTracker m_transactionTracker;
    private final SipMessageFactory m_messageFactory;
    private final Timer m_timerBThread;
    private final int m_t1;

    /**
     * Creates a new transaction factory with the specified tracker.
     * 
     * @param tracker The class that keeps track of transactions.
     * @param messageFactory The factory for creating SIP messages.
     * @param t1 The value to use for T1, as speficied in RFC 3261 s
     * ection 17.1.1.2 on page 126.  T1 is multiplied by 64 to give the 
     * timeout for transactions.
     */
    public SipTransactionFactoryImpl(final SipTransactionTracker tracker,
        final SipMessageFactory messageFactory, final int t1)
        {
        this.m_transactionTracker = tracker;
        this.m_messageFactory = messageFactory;
        this.m_timerBThread = new Timer(true);
        this.m_t1 = t1;
        }
    
    public SipClientTransaction createClientTransaction(
        final SipMessage request, final SipTransactionListener listener)
        {
        final List<SipTransactionListener> transactionListeners = 
            new LinkedList<SipTransactionListener>();
        transactionListeners.add(listener);
        
        final SipClientTransaction ct = 
            new SipClientTransactionImpl(request, transactionListeners, 
                this.m_messageFactory, this.m_timerBThread, this.m_t1);
        this.m_transactionTracker.trackTransaction(ct);
        return ct;
        }

    }
