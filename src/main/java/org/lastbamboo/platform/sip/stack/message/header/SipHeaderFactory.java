package org.lastbamboo.platform.sip.stack.message.header;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

import org.apache.commons.id.uuid.UUID;


/**
 * Factory for creating SIP message headers.
 */
public interface SipHeaderFactory
    {

    SipHeader createHeader(final String headerString) throws IOException;

    /**
     * Creates a new Via header indicating the "sent-by" address of the
     * host processing the message.  This is specified in RFC 3261 
     * section 18.1.1.
     * 
     * @param address The address to put in the "sent-by" parameter.
     * @return A new Via header with the "sent-by" parameter.
     */
    SipHeader createSentByVia(final InetAddress address);

    SipHeader createMaxForwards(int maxForwards);

    SipHeader createTo(final URI sipUri);

    SipHeader createFrom(String displayName, URI sipUri);

    SipHeader createCallId();

    SipHeader createCSeq(String method);

    SipHeader createContact(URI contactUri, UUID instanceId);

    SipHeader createExpires(int millis);

    SipHeader createContentLength(int contentLength);

    SipHeader createTo(final SipHeader originalTo);

    /**
     * Create the header for the extensions we support.
     * 
     * @return The header for the extensions we support.
     */
    SipHeader createSupported();

    }
