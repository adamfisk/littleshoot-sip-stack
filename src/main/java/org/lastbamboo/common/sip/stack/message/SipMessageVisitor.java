package org.lastbamboo.common.sip.stack.message;

/**
 * Interface for classes that visit SIP messages using the visitor pattern.
 */
public interface SipMessageVisitor
    {

    /**
     * Visits a 200 OK response.  This can be for any type of request, such
     * as REGISTER or INVITE.
     * 
     * @param response The response message.
     */
    void visitOk(final OkResponse response);
    
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
    void visitUnknownRequest(final UnknownMessage request);
    }
