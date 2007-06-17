package org.lastbamboo.common.sip.stack;

import java.net.URI;

/**
 * Factory for creating SIP <code>URI</code>s from user IDs.
 */
public interface SipUriFactory
    {

    /**
     * Creates a SIP URI for the user with the specified ID.
     * 
     * @param id The ID of the user to create a SIP URI for.
     * @return The URI for the user.
     */
    URI createSipUri(final String id);

    /**
     * Creates a SIP URI for the user with the specified ID.
     * 
     * @param id The ID of the user to create a SIP URI for.
     * @return The URI for the user.
     */
    URI createSipUri(final long id);

    }
