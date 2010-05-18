package org.lastbamboo.common.sip.stack.transaction.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.sip.stack.codec.SipMethod;
import org.lastbamboo.common.sip.stack.message.SipMessage;

/**
 * Class for matching requests and responses to their associated transactions.
 */
public class SipTransactionTrackerImpl implements SipTransactionTracker, 
    SipTransactionListener
    {
    
    private final Logger LOG = LoggerFactory.getLogger(SipTransactionTrackerImpl.class);
    
    private final Map<String, SipClientTransaction> m_transactions = 
        new ConcurrentHashMap<String, SipClientTransaction>();

    public void trackTransaction(final SipClientTransaction ct)
        {
        LOG.debug("Tracking transaction...");
        final SipMessage message = ct.getRequest();
        final String key = getTransactionKey(message);
        this.m_transactions.put(key, ct);
        ct.addListener(this);
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
        final SipClientTransaction ct = this.m_transactions.get(key);
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
        final SipMethod method = message.getMethod();
        return branchId + method.toString();
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
        final SipClientTransaction removed = this.m_transactions.remove(key);
        if (removed == null)
            {
            LOG.warn("Could not find transaction!!");
            }
        }

    }
