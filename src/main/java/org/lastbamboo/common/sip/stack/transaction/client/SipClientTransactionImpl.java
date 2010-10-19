package org.lastbamboo.common.sip.stack.transaction.client;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.lastbamboo.common.offer.answer.OfferAnswerMessage;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.sip.stack.message.DoubleCrlfKeepAlive;
import org.lastbamboo.common.sip.stack.message.Invite;
import org.lastbamboo.common.sip.stack.message.Register;
import org.lastbamboo.common.sip.stack.message.RequestTimeoutResponse;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipResponse;
import org.lastbamboo.common.sip.stack.message.UnknownSipRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of a SIP client transaction.
 */
public class SipClientTransactionImpl implements SipClientTransaction
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final SipMessage m_request;

    private long m_transactionTime = Long.MAX_VALUE;

    private final List<OfferAnswerTransactionListener> m_transactionListeners;

    private final long m_transactionStartTime;

    private final SipMessageFactory m_messageFactory;
    
    /**
     * Flag for whether or not timer B has already fired.
     */
    private volatile boolean m_timerBFired = false;

    private final TimerTask m_timerB;

    /**
     * Creates a new SIP client transaction.
     * 
     * @param request The request starting the transaction.
     * @param transactionListeners The listeners for transaction events.
     * @param messageFactory The factory for creating SIP messages.
     * @param timer The timer to add timer B tasks to.
     * @param t1 The value to use for T1, as specified in RFC 3261 section
     * 17.1.1.2 on page 126.  T1 is multiplied by 64 to give the 
     * timeout for transactions.  The default value is 500, but this can be 
     * modified for testing, for example.
     */
    public SipClientTransactionImpl(final SipMessage request, 
        final List<OfferAnswerTransactionListener> transactionListeners, 
        final SipMessageFactory messageFactory,
        final Timer timer, final int t1)
        {
        this.m_request = request;
        this.m_transactionListeners = transactionListeners;
        this.m_transactionStartTime = System.currentTimeMillis();
        this.m_messageFactory = messageFactory;
        
        // Start timer B, as specified for INVITE client transactions in
        // RFC 3261 section 17.1.1.2 on page 125.
        this.m_timerB = new TimerTask()
            {
            @Override
            public void run()
                {
                m_log.warn("Timer B firing!!  The client transaction timed out" +
                    " for request: " + m_request);
                m_timerBFired = true;
                final SipResponse timeout = 
                    m_messageFactory.createRequestTimeoutResponse(m_request);
                notifyListenersOfFailure(timeout);
                }
            };
            
        timer.schedule(this.m_timerB, 64 * t1);
        }
    
    public void addListener(final OfferAnswerTransactionListener listener)
        {
        this.m_transactionListeners.add(listener);
        }

    public SipMessage getRequest()
        {
        return this.m_request;
        }
    
    public long getTransactionTime()
        {
        return m_transactionTime;
        }
    
    public void visitResponse(final SipResponse response)
        {
        m_log.debug("SIP client transaction visiting response...");
        if (this.m_timerBFired)
            {
            m_log.warn("Received response after timer B fired!!");
            return;
            }
        
        m_log.debug("Canceling TIMER B");
        // Tell timer B not to fire.
        this.m_timerB.cancel();
        
        if (response.getStatusCode() == 200)
            {
            setTransactionTime();
            if (m_log.isDebugEnabled())
                {
                m_log.debug("Transaction time: "+getTransactionTime());
                }
            for (final OfferAnswerTransactionListener listener : 
                this.m_transactionListeners)
                {
                listener.onTransactionSucceeded(response);
                }
            }
        else
            {
            m_log.warn("Received non OK response: "+response.getStatusCode());
            }
        }

    public void visitRequestTimedOut(final RequestTimeoutResponse response)
        {
        if (this.m_timerBFired)
            {
            m_log.warn("Received OK after timer B fired!!");
            return;
            }
        
        // Tell timer B not to fire.
        this.m_timerB.cancel();
        notifyListenersOfFailure(response);
        }

    private void notifyListenersOfFailure(final OfferAnswerMessage response)
        {
        setTransactionTime();
        for (final OfferAnswerTransactionListener listener : 
            this.m_transactionListeners)
            {
            listener.onTransactionFailed(response);
            }
        }

    private void setTransactionTime()
        {
        this.m_transactionTime = 
            System.currentTimeMillis() - this.m_transactionStartTime;
        }

    public void visitInvite(final Invite invite)
        {
        m_log.warn("Should not receive invites on client transactions: "+invite);
        }

    public void visitRegister(final Register register)
        {
        m_log.warn("Should not receive registers on client transactions: " + 
            register);
        }

    public void visitUnknownRequest(final UnknownSipRequest request)
        {
        m_log.warn("Should not receive unknown messages on client " +
            "transactions: "+request);
        }

    public void visitDoubleCrlfKeepAlive(final DoubleCrlfKeepAlive keepAlive)
        {
        m_log.warn("Should not receive double CRLF keep alives on the client");
        }
    }
