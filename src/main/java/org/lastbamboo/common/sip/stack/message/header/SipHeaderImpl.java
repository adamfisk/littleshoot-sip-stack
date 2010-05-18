package org.lastbamboo.common.sip.stack.message.header;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean for a single SIP header.
 */
public class SipHeaderImpl implements SipHeader
    {

    private static final Logger LOG = LoggerFactory.getLogger(SipHeaderImpl.class);
    
    private final String m_headerName;
    private final List<SipHeaderValue> m_headerValues;

    /**
     * Creates a new SIP header with the specified name and values.
     * 
     * @param headerName The name of the header.  
     * @param headerValues The values of the header.
     */
    public SipHeaderImpl(final String headerName, 
        final List<SipHeaderValue> headerValues)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating header with "+headerValues.size()+" values...");
            }
        this.m_headerName = headerName;
        this.m_headerValues = headerValues;
        }

    /**
     * Creates a new SIP header with the specified name and value.
     * 
     * @param headerName The name of the header.  
     * @param headerValue The value of the header.
     */
    public SipHeaderImpl(final String headerName, 
        final SipHeaderValue headerValue)
        {
        this(headerName, createValues(headerValue));
        }

    private static List<SipHeaderValue> createValues(
        final SipHeaderValue headerValue)
        {
        final List<SipHeaderValue> values = new ArrayList<SipHeaderValue>();
        values.add(headerValue);
        return values;
        }

    public String getName()
        {
        return this.m_headerName;
        }
    
    public List<SipHeaderValue> getValues()
        {
        synchronized(this.m_headerValues)
            {
            return new ArrayList<SipHeaderValue>(this.m_headerValues);
            }
        }
    
    public SipHeaderValue getValue()
        {
        return this.m_headerValues.get(0);
        }
    
    public void addValue(final SipHeaderValue headerValue)
        {
        this.m_headerValues.add(0, headerValue);
        }
    
    public String toString()
        {
        return this.m_headerName + ": " + getValues();
        }
    }
