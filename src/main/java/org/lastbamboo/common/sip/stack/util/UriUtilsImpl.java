package org.lastbamboo.common.sip.stack.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the URI utilities interface.
 */
public class UriUtilsImpl implements UriUtils
    {
    /**
     * The log for this class.
     */
    private static final Logger LOG = LoggerFactory.getLogger (UriUtilsImpl.class);
    
    private final String m_sipUriPortRegex = 
        "[sips:|sip:]\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:(\\d+)";
    private final Pattern m_sipUriPortPattern = 
        Pattern.compile(m_sipUriPortRegex);
    
    public java.net.URI getUri (final String uriString)
        {
        try
            {
            return (new java.net.URI (uriString));
            }
        catch (final URISyntaxException uriSyntaxException)
            {
            throw (new RuntimeException (uriSyntaxException));
            }
        }

    public java.net.URI getUriForHost (final String host)
        {
        try
            {
            return (new java.net.URI (null, host, null, null));
            }
        catch (final URISyntaxException uriSyntaxException)
            {
            throw (new RuntimeException (uriSyntaxException));
            }
        }

    public java.net.URI getSipUri (final String host)
        {
        final StringBuilder buffer = new StringBuilder ();

        buffer.append ("sip:");
        buffer.append (host);

        try
            {
            return (new java.net.URI (buffer.toString ()));
            }
        catch (final URISyntaxException uriSyntaxException)
            {
            throw (new RuntimeException (uriSyntaxException));
            }
        }

    public java.net.URI getSipUri (final String host, final int port)
        {
        final StringBuilder buffer = new StringBuilder ();

        buffer.append ("sip:");
        buffer.append (host);
        buffer.append (':');
        buffer.append (port);

        try
            {
            return (new java.net.URI (buffer.toString ()));
            }
        catch (final URISyntaxException uriSyntaxException)
            {
            throw (new RuntimeException (uriSyntaxException));
            }
        }

    public java.net.URI getSipUri (final String host, final int port,
        final String transport)
        {
        final StringBuilder buffer = new StringBuilder ();

        buffer.append ("sip:");
        buffer.append (host);
        buffer.append (':');
        buffer.append (port);
        buffer.append (';');
        buffer.append ("transport=");
        buffer.append (transport);

        try
            {
            return (new java.net.URI (buffer.toString ()));
            }
        catch (final URISyntaxException uriSyntaxException)
            {
            throw (new RuntimeException (uriSyntaxException));
            }
        }
    
    public java.net.URI getUriWithPort(final java.net.URI uri, final int port)
        {
        try
            {
            return (new java.net.URI (uri.getScheme (),
                                      uri.getUserInfo (),
                                      uri.getHost (),
                                      port,
                                      uri.getPath (),
                                      uri.getQuery (),
                                      uri.getFragment ()));
            }
        catch (final URISyntaxException uriSyntaxException)
            {
            throw (new RuntimeException (uriSyntaxException));
            }
        }
    
    public String getUserInSipUri(final java.net.URI sipUri)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Getting user for URI: "+sipUri);
            }
        final String sipUriString = sipUri.toASCIIString();
        return StringUtils.substringBetween(sipUriString, "sip:", "@");
        }
    
    public String getHostInSipUri(final java.net.URI sipUri)
        {
        final String uri = sipUri.toASCIIString();
        LOG.debug("Parsing URI string: "+uri);
        
        // Look for the host before the port, as in the URI: 
        // 'sip:72.3.139.235:5060;transport=tcp'
        final String candidate1 = 
            StringUtils.substringBetween(uri, "sip:", ":");
        LOG.debug("Candidate 1: "+candidate1);
        if (!StringUtils.isBlank(candidate1))
            {
            return candidate1;
            }
        
        // There might be no port, but only a transport, as in the URI: 
        // 'sip:72.3.139.235;transport=tcp'
        final String candidate2 = 
            StringUtils.substringBetween(uri, "sip:", ";");
        LOG.debug("Cadidate 2: "+candidate2);
        if (!StringUtils.isBlank(candidate2)) 
            {
            return candidate2;
            }
        
        // Another possibility is a URI of the form 'sip:user@domain.org'.
        return StringUtils.substringAfterLast(uri, "@");
        }

    public int getPersonIdInSipUri(final java.net.URI sipUri)
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Getting person ID for URI: "+sipUri);
            }
        return (Integer.parseInt (getUserInSipUri (sipUri)));
        }

    public int getPortInSipUri(final URI uri)
        {
        final String uriString = uri.toASCIIString();
        LOG.debug("Getting port from URI: "+uriString);
        
        final Matcher matcher = this.m_sipUriPortPattern.matcher(uriString);
        if (matcher.find())
            {
            final String port = matcher.group(1);
            LOG.debug("Found port: "+port);
            return Integer.parseInt(port);
            }
        else if (uriString.startsWith("sips:"))
            {
            return 5061;
            }
        else 
            {
            return 5060;
            }
        }
    }
