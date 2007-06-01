package org.lastbamboo.common.sip.stack.codec;

/**
 * Enumeration of standard SIP methods.
 */
public enum SipMethod 
    {
  
    /**
     * REGISTER method.
     */
    REGISTER, 
  
    /**
     * INVITE method.
     */
    INVITE, 
    
    /**
     * Keep alive message.  This isn't really a "method" in the traditional
     * sense. 
     */
    DOUBLE_CRLF_KEEP_ALIVE, 
    
    /**
     * An unknown request method.
     */
    UNKNOWN,
    ;
    }
