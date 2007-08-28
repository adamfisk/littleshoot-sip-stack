package org.lastbamboo.common.sip.stack.codec;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration of first words in SIP messages.  Most are request types, while
 * the SIP version indicates a response.
 */
public enum SipMessageType
    {
  
    /**
     * REGISTER method.
     */
    REGISTER("REGISTER"), 
  
    /**
     * INVITE method.
     */
    INVITE("INVITE"),
    
    /**
     * SIP/2.0 version string.
     */
    SIP_2_0("SIP/2.0"),
    
    /**
     * The double CRLF keep-alive message from SIP outbound.
     */
    DOUBLE_CRLF("\r\n\r\n"),
    
    /**
     * Unknown message type.
     */
    UNKNOWN("UNKNOWN"), 
    ;
    
    
    private static final Map<String, SipMessageType> s_stringsToEnums =
        new HashMap<String, SipMessageType>();
    
    
    static
        {
        for (final SipMessageType type : values())
            {
            s_stringsToEnums.put(type.convert(), type);
            }
        }
    
    /**
     * Converts the enum to a {@link String}.
     * 
     * @return The {@link String} for the enum.
     */
    public String convert()
        {
        return this.m_type;
        }
    
    
    /**
     * Converts from the int type representation to the enum equivalent.
     * 
     * @param type The int type.
     * @return The corresponding enum value.
     */
    public static SipMessageType convert(final String type)
        {
        return s_stringsToEnums.get(type);
        }
    
    /**
     * Returns whether or not the specified string matches one of the message
     * types.
     * 
     * @param type The type to match.
     * @return <code>true</code> if there's a matching type, otherwise
     * <code>false</code>.
     */
    public static boolean contains(final String type)
        {
        return s_stringsToEnums.containsKey(type);
        }
    
    private final String m_type;
    
    private SipMessageType()
        {
        this.m_type = StringUtils.EMPTY;
        }
    
    private SipMessageType(final String type)
        {
        this.m_type = type;
        }
    
    }
