package org.lastbamboo.common.sip.stack.transaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.stack.message.SipMessage;

/**
 * Class for matching requests and responses to their associated transactions.
 */
public class SipTransactionTrackerImpl implements SipTransactionTracker, 
    SipTransactionListener
    {
    
    private static final Log LOG = 
        LogFactory.getLog(SipTransactionTrackerImpl.class);
    
    private final Map m_transactions = new ConcurrentHashMap();

    public void trackTransaction(final SipClientTransaction ct)
        {
        LOG.debug("Tracking transaction...");
        final SipMessage message = ct.getRequest();
        final String key = getTransactionKey(message);
        this.m_transactions.put(key, ct);
        }

    public SipClientTransaction getClientTransaction(final SipMessage message)
        {
        LOG.debug("Accessing client transaction...");
        final String key = getTransactionKey(message);
        LOG.debug("Using key: "+key);
        if (StringUtils.isBlank(key))
            {
            LOG.error("Blank key for message: "+message);
            throw new IllegalArgumentException("Bad message: "+message);
            }
        final SipClientTransaction ct = 
            (SipClientTransaction) this.m_transactions.get(key);
        if (ct == null)
            {
            LOG.warn("Nothing known about transaction: "+key);
            LOG.warn("Known transactions: "+this.m_transactions.keySet());
            }
        return ct;
        }

    private String getTransactionKey(final SipMessage message)
        {
        final String branchId = message.getBranchId();
        final String method = message.getMethod();
        return branchId + method;
        }

    public void onTransactionSucceeded(final SipMessage message)
        {
        removeTransaction(message);
        }

    public void onTransactionFailed(final SipMessage message)
        {
        removeTransaction(message);
        }

    private void removeTransaction(final SipMessage message)
        {
        // We now consider the transaction completed and remove the 
        // transaction.
        final String key = getTransactionKey(message);
        
        LOG.debug("Removing transaction with key '" + key + "'");
        this.m_transactions.remove(key);
        }

    }
