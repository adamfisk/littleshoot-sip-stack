package org.lastbamboo.common.sip.stack.message.header;

import java.util.Collection;
import java.util.List;

/**
 * Interface for a single SIP header.
 */
public interface SipHeader
    {

    /**
     * Accessor for the name of the header.
     * 
     * @return The name of the header.
     */
    String getName();

    /**
     * Accessor for the complete header value.
     * 
     * @return The header value.
     */
    //String getValue();
    
    /**
     * Accessor for the value of a parameter with the specified name.
     * 
     * @param paramName The name of the parameter to search for.
     * @return The value of the specified parameter.
     */
    //String getParamValue(final String paramName);

    /**
     * Returns whether or not this header has a parameter with the specified
     * name.
     * 
     * @param paramName The name of the parameter to look for.
     * @return <code>true</code> if the header has the parameter, otherwise
     * <code>false</code>.
     */
    //boolean hasParam(final String paramName);

    /**
     * Accessor for the header values.  This is useful for headers containing
     * multiple values.  For other headers, this {@link Collection} will 
     * contain only a single entry.
     * 
     * @return The {@link Collection} of header field values.  Each entry in
     * the collection is a header value.
     */
    List<SipHeaderValue> getValues();

    /**
     * Adds a parameter with the specified name and the specified value.
     * 
     * @param paramName The name of the paramter to add.
     * @param paramValue The value of the paramater to add.
     */
    //void addParam(final String paramName, final String paramValue);

    /**
     * Adds the value of the specified header to the value for this header.
     * This uses the rules for providing multiple header values for a single
     * header name specified in RFC 3261 section 7.3.1.
     * 
     * @param headerValue The header value to add to this header.
     */
    void addValue(final SipHeaderValue headerValue);

    /**
     * Accessor for the header value.  If this header contains multiple values,
     * this will return the first one.
     * 
     * @return The header value, or simply the first value if this header
     * contains multiple values.
     */
    SipHeaderValue getValue();

    }
