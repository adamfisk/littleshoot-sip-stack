package org.lastbamboo.common.sip.stack.message.header;

import java.util.Map;

/**
 * The value of the header.
 */
public interface SipHeaderValue
    {

    /**
     * Accessor for the header value with no parameters.
     * 
     * @return The header value with no parameters.
     */
    String getBaseValue();

    /**
     * Determines whether this header value contains the specified parameter
     * name.
     * 
     * @param paramName The name of the specified parameter.
     * @return <code>true</code> if the value has a parameter of the specified
     * name, otherwise <code>false</code>.
     */
    boolean hasParam(final String paramName);

    /**
     * Accessor for the value of the specified parameter.  Note that param
     * values are immutable strings, so this preserved the immutability of 
     * header values.
     * 
     * @param paramName The name of the parameter to access.
     * @return The value of the parameter with the specified name.
     */
    String getParamValue(final String paramName);

    /**
     * Accessor for the map of header value parameters.  This returns a copy
     * of the parameters, preserving the immutability of the header value.
     * 
     * @return The map of header value parameters.
     */
    Map<String, String> getParams();

    }
