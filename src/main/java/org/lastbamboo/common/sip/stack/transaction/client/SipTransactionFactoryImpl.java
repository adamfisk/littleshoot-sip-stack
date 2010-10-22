package org.lastbamboo.common.sip.stack.transaction.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
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
     * @param t1 The value to use for T1, as specified in RFC 3261 section 
     * 17.1.1.2 on page 126.  T1 is multiplied by 64 to give the 
     * timeout for transactions.
     */
    public SipTransactionFactoryImpl(final SipTransactionTracker tracker,
        final SipMessageFactory messageFactory, final int t1)
        {
        this.m_transactionTracker = tracker;
        this.m_messageFactory = messageFactory;
        this.m_timerBThread = new Timer("SIP-TimerB-Thread", true);
        this.m_t1 = t1;
        }
    
    public SipClientTransaction createClientTransaction(
        final SipMessage request, final OfferAnswerTransactionListener listener)
        {
        final List<OfferAnswerTransactionListener> listeners =
            new ArrayList<OfferAnswerTransactionListener>();
        listeners.add(listener);
        final SipClientTransaction ct = 
            new SipClientTransactionImpl(request, listeners, 
                this.m_messageFactory, this.m_timerBThread, this.m_t1);
        this.m_transactionTracker.trackTransaction(ct);
        return ct;
        }

    }
