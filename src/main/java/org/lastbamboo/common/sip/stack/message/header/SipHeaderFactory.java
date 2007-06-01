package org.lastbamboo.common.sip.stack.message.header;

import java.net.InetAddress;
import java.net.URI;

import org.apache.commons.id.uuid.UUID;


/**
 * Factory for creating SIP message headers.
 */
public interface SipHeaderFactory
    {

    SipHeader createHeader(String name, String value);

    /**
     * Creates a new Via header indicating the "sent-by" address of the
     * host processing the message.  This is specified in RFC 3261 
     * section 18.1.1.
     * 
     * @param address The address to put in the "sent-by" parameter.
     * @return A new Via header with the "sent-by" parameter.
     */
    SipHeader createSentByVia(InetAddress address);

    SipHeader createMaxForwards(int maxForwards);

    SipHeader createTo(URI sipUri);

    SipHeader createFrom(String displayName, URI sipUri);

    SipHeader createCallId();

    SipHeader createCSeq(String method);

    SipHeader createContact(URI contactUri, UUID instanceId);

    SipHeader createExpires(int millis);

    SipHeader createContentLength(int contentLength);

    SipHeader createTo(SipHeader originalTo);

    /**
     * Create the header for the extensions we support.
     * 
     * @return The header for the extensions we support.
     */
    SipHeader createSupported();

    }
