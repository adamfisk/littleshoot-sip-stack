package org.lastbamboo.common.sip.stack.message;

/**
 * Type-safe enumeration of SIP response codes.
 */
public final class SipResponseCode
    {

    private SipResponseCode()
        {
        // Should never be constructed.
        }
    
    public static final int TRYING = 100;
    public static final int OK = 200;
    
    /**
     * 408 Response Timeout.
     */
    public static final int REQUEST_TIMEOUT = 408;
    }
