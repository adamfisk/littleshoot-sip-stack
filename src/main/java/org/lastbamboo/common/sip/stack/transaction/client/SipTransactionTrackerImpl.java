package org.lastbamboo.common.sip.stack.transaction.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.offer.answer.OfferAnswerMessage;
import org.lastbamboo.common.offer.answer.OfferAnswerTransactionListener;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class for matching requests and responses to their associated transactions.
 */
public class SipTransactionTrackerImpl implements SipTransactionTracker, 
    OfferAnswerTransactionListener
    {
    
    private final Logger m_log = LoggerFactory.getLogger(getClass());
    
    private final Map<String, SipClientTransaction> m_transactions = 
        new ConcurrentHashMap<String, SipClientTransaction>();

    public void trackTransaction(final SipClientTransaction ct)
        {
        m_log.debug("Tracking transaction...");
        final SipMessage message = ct.getRequest();
        final String key = message.getTransactionKey();//getTransactionKey(message);
        this.m_transactions.put(key, ct);
        ct.addListener(this);
        }

    public SipClientTransaction getClientTransaction(final SipMessage message)
        {
        m_log.debug("Accessing client transaction...");
        final String key = message.getTransactionKey();//getTransactionKey(message);
        m_log.debug("Using key: "+key);
        if (StringUtils.isBlank(key))
            {
            m_log.error("Blank key for message: "+message);
            throw new IllegalArgumentException("Bad message: "+message);
            }
        final SipClientTransaction ct = this.m_transactions.get(key);
        if (ct == null)
            {
            m_log.warn("Nothing known about transaction: "+key);
            m_log.warn("Known transactions: "+this.m_transactions.keySet());
            }
        return ct;
        }

    /*
    private String getTransactionKey(final SipMessage message)
        {
        final String branchId = message.getBranchId();
        final SipMethod method = message.getMethod();
        return branchId + method.toString();
        }
        */

    public void onTransactionSucceeded(final OfferAnswerMessage message)
        {
        removeTransaction(message);
        }

    public void onTransactionFailed(final OfferAnswerMessage message)
        {
        removeTransaction(message);
        }

    private void removeTransaction(final OfferAnswerMessage message)
        {
        // We now consider the transaction completed and remove the 
        // transaction.
        final String key = message.getTransactionKey();//getTransactionKey(message);
        
        m_log.debug("Removing transaction with key '" + key + "'");
        final SipClientTransaction removed = this.m_transactions.remove(key);
        if (removed == null)
            {
            m_log.warn("Could not find transaction!!");
            }
        }

    }
