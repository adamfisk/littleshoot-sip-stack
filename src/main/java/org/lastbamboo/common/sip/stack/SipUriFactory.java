package org.lastbamboo.common.sip.stack;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Factory for creating SIP URIs.
 */
public final class SipUriFactory {

    public static URI createSipUri(final String id) {
        final String sipUriString = "sip:" + id + "@littleshoot.org";
        try {
            return new URI(sipUriString);
        } catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Invalid request string: "+id);
        }
    }

    public static URI createSipUri(final long id) {
        return createSipUri(Long.toString(id));
    }

}
