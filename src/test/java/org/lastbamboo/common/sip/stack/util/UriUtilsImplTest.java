package org.lastbamboo.common.sip.stack.util;

import java.net.URI;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test for the UriUtils class.
 */
public class UriUtilsImplTest extends TestCase
    {

    private static final Logger LOG = LoggerFactory.getLogger(UriUtilsImplTest.class);
    
    /**
     * Tests the method for extracting the port from a SIP URI.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testGetPortInSipUri() throws Exception 
        {
        final UriUtils uriUtils = new UriUtilsImpl();
        final URI uri1 = uriUtils.getSipUri("24.239.168.239", 5060, "TCP");
        
        final URI uri2 = uriUtils.getSipUri("24.239.168.239", 5061, "TCP");
        assertEquals(5060, uriUtils.getPortInSipUri(uri1));
        assertEquals(5061, uriUtils.getPortInSipUri(uri2));
        }
    
    /**
     * Test for extracting the host froma a SIP URI.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testGetHostFromSipUri() throws Exception
        {
        String uriString = "sip:72.3.139.235:5060;transport=tcp";
        URI uri = new URI(uriString);
        LOG.debug("Using URI: "+uri);
        final UriUtils utils = new UriUtilsImpl();
        String host = utils.getHostInSipUri(uri);
        assertEquals("Received: "+host, "72.3.139.235", host);
        
        // Now test with URIs of the form 'sip:2@lastbamboo.org'.
        uriString = "sip:2@lastbamboo.org";
        uri = new URI(uriString);
        host = utils.getHostInSipUri(uri);
        assertEquals("Received: "+host, "lastbamboo.org", host);
        }
    
    /**
     * Tests the method for getting the user from a SIP URI.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testGetUserInSipUri() throws Exception
        {
        final String uriString = "sip:255@lastbamboo.org";
        final URI uri = new URI(uriString);
        LOG.debug("Using URI: "+uri);
        final UriUtils utils = new UriUtilsImpl();
        final String user = utils.getUserInSipUri(uri);
        assertEquals("Received: "+user, "255", user);
        }
    }
