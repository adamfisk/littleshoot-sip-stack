package org.lastbamboo.common.sip.stack.message.header;

/**
 * Contant for SIP header parameter names.
 */
public final class SipHeaderParamNames
    {

    private SipHeaderParamNames() 
        {
        // Should never be constructed.
        }
    
    /**
     * Constant for the "tag" param in To and From headers.
     */
    public static final String TAG = "tag";
    
    /**
     * Constant for the branch parameter.
     */
    public static final String BRANCH = "branch";

    public static final String SIP_INSTANCE = "+sip.instance";
    
    /**
     * Constant for the "received" parameter, used to the specify the IP 
     * address requests were received on.
     */
    public static final String RECEIVED = "received";

    /**
     * Constant for the "rport" Via header parameter.  This is normally only
     * used in the symmetric response routing extension.  RFC 3261 fails to 
     * properly address routing of SIP responses for stateless proxies, however,
     * so we also use this to specify the local, typically ephemeral port of
     * a client so that routing using the Via header works.
     */
    public static final String RPORT = "rport";
    }
