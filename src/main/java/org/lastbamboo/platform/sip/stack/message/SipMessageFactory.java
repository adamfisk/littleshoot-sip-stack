package org.lastbamboo.platform.sip.stack.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;

import org.apache.commons.id.uuid.UUID;
import org.lastbamboo.platform.sip.stack.message.header.SipHeader;

/**
 * Interface for factories for creating SIP messages.
 */
public interface SipMessageFactory
    {
    
    /**
     * Creates a new SIP message from the data in the specified reader.
     * 
     * @param reader The reader containing the message data.
     * @return A new SIP message from the reader data.
     * @throws IOException If any unexpected error occurs creating the message.
     */
    SipMessage createSipMessage(final BufferedReader reader) throws IOException;

    /**
     * Creates a new register request.
     * 
     * @param requestUri The request URI for the request start line.
     * @param displayName The display name to send the request to.
     * @param toUri The URI to send the request to.
     * @param instanceId The instance ID of the registering SIP entity.
     * @param contactUri The contact URI of the registering SIP entity.
     * @return The new SIP register request.
     */
    SipMessage createRegisterRequest(final URI requestUri, 
        final String displayName, final URI toUri, final UUID instanceId, 
        final URI contactUri);

    /**
     * Creates a new invite request outside of a dialog.  This will generate 
     * a new Call-ID for the dialog.
     * 
     * @param displayName The name to display.
     * @param toUri The SIP URI to send the message to.
     * @param fromUri The SIP URI the message is from.
     * @param instanceId The instance ID of the sending client, used in the 
     * contact header.
     * @param contactUri The contact URI.
     * @param body The body of the INVITE.  This is typcically SDP.
     * @return The new invite.
     */
    SipMessage createInviteRequest(final String displayName, 
        final URI toUri, URI fromUri, final UUID instanceId, 
        final URI contactUri, final byte[] body);
    
    /**
     * Creates a new INVITE OK message, copying data from the specified request.
     * 
     * @param request The request to respond to with an OK response.
     * @param instanceId The instance ID of the responding user.
     * @param contactUri The contact URI of the responding user.
     * @param body The body of the message.
     * @return A new INVITE OK with the specified data.
     */
    SipMessage createInviteOk(final Invite request, final UUID instanceId, 
        final URI contactUri, final byte[] body);

    /**
     * Creates a register OK message from the specified request.
     * 
     * @param register The register request to create an OK response for.
     * @return The register OK response.
     */
    SipMessage createRegisterOk(final Register register);

    /**
     * Creates a SIP message from the specified message string.
     * 
     * @param messageString The string for a complete SIP message.
     * @return The new SIP message.
     * @throws IOException If there's an IO error reading the message.
     */
    SipMessage createSipMessage(final String messageString) throws IOException;

    /**
     * Copies all the data from the original message into a new message,
     * adding the specified header.
     * 
     * @param message The original message to create a new message from.
     * @param newHeader The header to add.
     * @return A new SIP message with all the data from the original message
     * plus the specified header.
     */
    SipMessage addVia(final SipMessage message, final SipHeader newHeader);

    /**
     * Strips the topmost Via header from the response, returning a new message
     * without the topmost Via.
     * 
     * @param response The response message to strip the Via from.
     * @return The new message without the topmost Via header from the original.
     */
    SipMessage stripVia(final SipMessage response);

    /**
     * Creates a 408 Request Timeout response.
     * 
     * @param request The request that timed out.
     * @return The 408 Request Timeout response.
     */
    SipMessage createRequestTimeoutResponse(final SipMessage request);

    /**
     * Creates a new message to forward with all of the appropriate 
     * modifications to the Via header based on the remote reading of the 
     * socket address and port.
     * 
     * @param socketAddress The remote socket address the INVITE arrived from.
     * @param invite The incoming INVITE message.
     * @return A new INVITE message with appropriate modifications to the 
     * Via header.
     * @throws IOException If the message does not match the expected syntax.
     */
    SipMessage createInviteToForward(final InetSocketAddress socketAddress, 
        final SipMessage invite) throws IOException;

    }
