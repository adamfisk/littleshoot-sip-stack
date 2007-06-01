package org.lastbamboo.common.sip.stack.codec;

import org.lastbamboo.common.util.EnumConverter;
import org.lastbamboo.common.util.ReverseEnumMap;

/**
 * Enumeration of first words in SIP messages.  Most are request types, while
 * the SIP version indicates a response.
 */
public enum SipMessageType implements EnumConverter<String>
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
     * Unknown message type.
     */
    UNKNOWN("fjdkafj");
    

    private static ReverseEnumMap<String, SipMessageType> s_map = 
        new ReverseEnumMap<String, SipMessageType>(SipMessageType.class);
    
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
        return s_map.get(type);
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
        return s_map.contains(type);
        }
    
    private final String m_type;
    
    private SipMessageType(final String type)
        {
        this.m_type = type;
        }
    
    }
