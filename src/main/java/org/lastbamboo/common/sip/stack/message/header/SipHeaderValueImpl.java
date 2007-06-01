package org.lastbamboo.common.sip.stack.message.header;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.lastbamboo.common.sip.stack.message.SipMessageUtils;

/**
 * Class representing a single SIP header value.  If a SIP header contains
 * multiple values, it will contain multiple instances of this class.
 */
public class SipHeaderValueImpl implements SipHeaderValue
    {

    private final Map<String, String> m_params;
    private final String m_baseValue;

    /**
     * Creates a new SIP header value with the specified string.
     * 
     * @param valueString The header value as a string.
     * @throws IOException If the header value does not match the expected
     * syntax.
     */
    public SipHeaderValueImpl(final String valueString) throws IOException
        {
        this.m_baseValue = createBaseValue(valueString);
        this.m_params = SipMessageUtils.extractHeaderParams(valueString);
        }

    /**
     * Creates a new header value with the specified base value and the
     * specified parameters.
     * 
     * @param baseValue The base value of the header with no parameters.
     * @param params The header value parameters.
     */
    public SipHeaderValueImpl(final String baseValue, 
        final Map<String, String> params)
        {
        this.m_baseValue = baseValue;
        this.m_params = params;
        }
    
    private String createBaseValue(final String valueString)
        {
        if (StringUtils.contains(valueString, ";"))
            {
            return StringUtils.substringBefore(valueString, ";");
            }
        return valueString;
        }
    
    public Map<String, String> getParams()
        {
        // We favor immutability here.
        synchronized (this.m_params)
            {
            return new ConcurrentHashMap<String, String>(this.m_params);
            }
        }

    public String getBaseValue()
        {
        return this.m_baseValue;
        }

    public boolean hasParam(final String paramName)
        {
        return this.m_params.containsKey(paramName);
        }

    public String getParamValue(final String paramName)
        {
        return this.m_params.get(paramName);
        }

    public boolean equals(final Object obj)
        {
        if (!(obj instanceof SipHeaderValueImpl))
            {
            return false;
            }
        final SipHeaderValueImpl value = (SipHeaderValueImpl) obj;
        if (this.m_baseValue.equals(value.getBaseValue()))
            {
            final Map params = value.getParams();
            synchronized (this.m_params)
                {
                synchronized (params)
                    {
                    return this.m_params.equals(params);
                    }
                }
            }
        return false;
        }
    
    public int hashCode()
        {
        synchronized (this.m_params)
            {
            return 17 * this.m_params.hashCode() * this.m_baseValue.hashCode();
            }
        }
    
    public String toString()
        {
        return this.m_baseValue + " " + this.m_params;
        }
    }
