package org.lastbamboo.common.sip.stack.message;

/**
 * Interface for classes that visit SIP messages using the visitor pattern.
 */
public interface SipMessageVisitor
    {
    
    /**
     * Visits a 408 Request Timeout response.
     * @param response The 408 Request Timeout response.
     */
    void visitRequestTimedOut(final RequestTimeoutResponse response);

    /**
     * Visits an INVITE request.
     * 
     * @param invite The INVITE message.
     */
    void visitInvite(final Invite invite);

    /**
     * Visits a REGISTER request.
     * 
     * @param register The REGISTER request.
     */
    void visitRegister(final Register register);

    /**
     * Visits a request we do not recognize.
     * 
     * @param request A request we do not recognize.
     */
    void visitUnknownRequest(UnknownSipRequest request);

    /**
     * Visits a double CRLF keep alive message, as specified at:<p>
     * 
     * http://www.ietf.org/internet-drafts/draft-ietf-sip-outbound-08.txt<p>
     * 
     * @param keepAlive The keep alive message.
     */
    void visitDoubleCrlfKeepAlive(DoubleCrlfKeepAlive keepAlive);

    /**
     * Visits a response message.
     * 
     * @param response The response.
     */
    void visitResponse(SipResponse response);
    }
