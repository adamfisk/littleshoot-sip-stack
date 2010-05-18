package org.lastbamboo.common.sip.stack.util;

import java.net.URI;

/**
 * Some utilities for working with URIs and SIP URIs.
 */
public interface UriUtils
    {
    /**
     * Returns a URI from a string.
     *
     * @param uriString The string containing a URI-formatting string.
     *
     * @return The URI represented by the passed string.
     */
    java.net.URI getUri (String uriString);

    /**
     * Returns a URI from a host.
     *
     * @param host The host.
     *
     * @return The URI representing the given host.
     */
    java.net.URI getUriForHost (String host);

    /**
     * Returns a SIP URI for a given host of the form "sip:host".
     *
     * @param host The host.
     *
     * @return A URI for the given scheme, host, and port.
     */
    java.net.URI getSipUri (String host);

    /**
     * Returns a SIP URI for a given host and port of the form "sip:host:port".
     *
     * @param host The host.
     * @param port The port.
     *
     * @return A URI for the given scheme, host, and port.
     */
    java.net.URI getSipUri (String host, int port);

    /**
     * Returns a SIP URI for a given host and port of the form "sip:host:port".
     *
     * @param host The host.
     * @param port The port.
     * @param transport The transport to be used to connect.
     * @return A URI for the given scheme, host, and port.
     */
    java.net.URI getSipUri (String host, int port, String transport);

    /**
     * Returns a URI with a modified port.
     *
     * @param uri The original URI.
     * @param port The new port.
     *
     * @return The URI with the new port.
     */
    java.net.URI getUriWithPort (java.net.URI uri, int port);

    /**
     * Returns the user portion of a SIP URI (in the form of an opaque
     * java.net.URI).
     *
     * @param sipUri The URI.
     *
     * @return The user portion of the SIP URI.
     */
    String getUserInSipUri (java.net.URI sipUri);

    /**
     * Returns the host portion of a SIP URI (in the form of an opaque
     * java.net.URI).
     *
     * @param sipUri The URI.
     *
     * @return The host portion of the SIP URI.
     */
    String getHostInSipUri (java.net.URI sipUri);
    
    /**
     * Returns the user portion of a SIP URI (in the form of an opaque
     * java.net.URI) as a Little Shoot person identifier.
     *
     * @param sipUri The URI.
     *
     * @return The user portion of the SIP URI as a Little Shoot person
     * identifier..
     */
    int getPersonIdInSipUri (java.net.URI sipUri);

    /**
     * Extracts the port from a SIP URI.  If the URI does not specify a port,
     * this returns the default port, such as 5060 for sip: URIs and 5061 for
     * sips: URIs.
     * 
     * @param uri The URI to extract the port from.
     * @return The port in the SIP URI, or the default port if it is not 
     * specified.
     */
    int getPortInSipUri(URI uri);

    }
