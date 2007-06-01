package org.lastbamboo.common.sip.stack.message.header;

/**
 * Constants for all SIP header names.
 */
public final class SipHeaderNames
    {

    private SipHeaderNames() 
        {
        // Should never be constructed.
        }
    
    public final static String MAX_FORWARDS = "Max-Forwards";
    public static final String VIA = "Via";
    public static final String TO = "To";
    public static final String FROM = "From";
    public static final String CALL_ID = "Call-ID";
    public static final String CSEQ = "CSeq";
    public static final String CONTACT = "Contact";
    public static final String EXPIRES = "Expires";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String RECORD_ROUTE = "Record-Route";
    
    /**
     * Constant for the Supported header.
     */
    public static final String SUPPORTED = "Supported";
    
    }
