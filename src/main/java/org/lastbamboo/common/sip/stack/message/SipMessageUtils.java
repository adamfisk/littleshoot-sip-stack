package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderImpl;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderParamNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValueImpl;

/**
 * Collection of utility methods for handling SIP messages.
 */
public class SipMessageUtils
    {

    private static final Logger LOG = LoggerFactory.getLogger(SipMessageUtils.class);
    
    private SipMessageUtils()
        {
        // Should not be constructed.
        }

    /**
     * Creates a paramater string from the specified map of parameter names
     * and values.
     * 
     * @param paramsMap A map of parameter names to values.
     * @return The parameter names and values as a string.
     */
    public static String toParamString(final Map paramsMap)
        {
        final StringBuilder sb = new StringBuilder();
        //sb.append(value);
        final Collection params = paramsMap.entrySet();
        for (final Iterator iter = params.iterator(); iter.hasNext();)
            {
            final Map.Entry param = (Map.Entry) iter.next();
            sb.append(";");
            sb.append(param.getKey());
            sb.append("=");
            sb.append(param.getValue());
            }
        return sb.toString();
        }

    public static String extractName(final String nameValue) 
        throws IOException
        {
        return extractName(nameValue, ":");
        }

    public static String extractValue(final String nameValue) 
        throws IOException
        {
        return extractValue(nameValue, ":");
        }
    
    private static String extractName(final String nameValue, 
        final String separator) throws IOException
        {
        final String name = StringUtils.substringBefore(nameValue, separator);
        if (StringUtils.isBlank(name))
            {
            throw new IOException("Bad header: "+nameValue);
            }
        return name.trim();
        }

    private static String extractValue(final String nameValue, 
        final String separator) throws IOException
        {
        final String value = StringUtils.substringAfter(nameValue, separator);
        if (StringUtils.isBlank(value))
            {
            throw new IOException("Bad header: "+nameValue);
            }
        return value.trim();
        }
    
    public static Map<String, SipHeader> convertHeaders(
        Map<String, List<String>> headers)
        {
        final Map<String, SipHeader> newHeaders = 
            new HashMap<String, SipHeader>();
        
        final Set<Map.Entry<String, List<String>>> entries = 
            headers.entrySet();
        for (final Map.Entry<String, List<String>> entry : entries)
            {
            final List<String> values = entry.getValue();
            final List<SipHeaderValue> headerValues = createValues(values);
            final SipHeader header = 
                new SipHeaderImpl(entry.getKey(), headerValues);
            newHeaders.put(header.getName(), header);
            }
        return newHeaders;
        }
    
    private static List<SipHeaderValue> createValues(final List<String> values)
        {
        final List<SipHeaderValue> headerValues = 
            new LinkedList<SipHeaderValue>();
        
        for (final String value : values)
            {
            // We use a scanner here because the "value" from the list of
            // strings can actually contain two values separated with a comma.
            final Scanner scan = new Scanner(value);
            scan.useDelimiter(",");
            while (scan.hasNext())
                {
                final String curValue = scan.next();
                try
                    {
                    final SipHeaderValue headerValue = 
                        new SipHeaderValueImpl(curValue);
                    headerValues.add(headerValue);
                    }
                catch (final IOException e)
                    {
                    LOG.warn("Could not parse header value: "+value);
                    }
                }
            }
        return headerValues;
        }

    public static Map<String, String> extractHeaderParams(
        final String headerValue) throws IOException
        {
        final String paramsString = 
            StringUtils.substringAfter(headerValue, ";");
        final String[] paramStrings = StringUtils.split(paramsString, ";");
        final Map<String, String> paramMap = 
            new ConcurrentHashMap<String, String>();
        for (int i = 0; i < paramStrings.length; i++)
            {
            final String nameValue = paramStrings[i].trim();
            final String name = extractName(nameValue, "=");
            final String value = extractValue(nameValue, "=");
            paramMap.put(name, value);
            }
        return paramMap;
        }

    public static String extractCSeqMethod(final SipHeader cseq)
        {
        // This should probably technically allow any whitespace after the
        // sequence number and before the method, but that's unlikely to
        // arise in practice.
        return StringUtils.substringAfter(cseq.getValue().getBaseValue(), " ");
        }

    /**
     * Extracts the {@link URI} from a header value.  This method assumes the 
     * {@link URI} is within an opening "<" and a closing ">".
     * 
     * @param header The header.
     * @return A new {@link URI} from the header.
     */
    public static URI extractUri(final SipHeader header)
        {
        return extractUri(header.getValue().getBaseValue());
        }
    
    /**
     * Extracts the {@link URI} from a header value.  This method assumes the 
     * {@link URI} is within an opening "<" and a closing ">".
     * 
     * @param headerValue The header value string.
     * @return A new {@link URI} from the header.
     */
    private static URI extractUri(final String headerValue)
        {
        LOG.debug("Creating URI from header value: "+headerValue);
        final String uriString = 
            StringUtils.substringBetween(headerValue, "<", ">");
        LOG.debug("Creating URI from URI string: "+uriString);
        try
            {
            return new URI(uriString);
            }
        catch (final URISyntaxException e)
            {
            LOG.error("Could not create URI from string: "+uriString);
            throw new IllegalArgumentException(
                "Could not create URI from: "+uriString);
            }
        }

    public static int extractCSeqNumber(final SipMessage message)
        {
        final SipHeaderValue cSeqValue = 
            message.getHeader(SipHeaderNames.CSEQ).getValue();
        final String cSeqString = cSeqValue.getBaseValue();
        //final String cSeqString = message.getHeader(SipHeaderNames.CSEQ);
        //final SipHeaderValue cSeqValue = header.getValue();
        //final String cSeqString = cSeqValue.getBaseValue();
        final String sequenceString = 
            StringUtils.substringBefore(cSeqString, " ");
        if (!NumberUtils.isNumber(sequenceString))
            {
            throw new IllegalArgumentException("Bad cseq: "+cSeqString);
            }
        return Integer.parseInt(sequenceString);
        }

    /**
     * Extracts the URI from the request line of a SIP message.  The request
     * line must meet the form specified in RFC 3261, as in:<p>
     * 
     * Request-Line = Method SP Request-URI SP SIP-Version CRLF
     * 
     * @param request The SIP request.
     * @return The request URI from the request line.
     * @throws IOException If the request line does not match the expected
     * syntax.
     */
    public static URI extractUriFromRequestLine(final SipMessage request) 
        throws IOException
        {
        // Note the Request-Line requires a single space character as the
        // separator, not arbitrary whitespace.  This is specified in section
        // 7.1 of RFC 3261, on page 27:
        //
        // Request-Line = Method SP Request-URI SP SIP-Version CRLF
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Extracting URI from request line: "+
                request.getStartLine());
            }
        final String requestLine = request.getStartLine();
        final String uri = StringUtils.substringBetween(requestLine, " ");
        try
            {
            return new URI(uri);
            }
        catch (final URISyntaxException e)
            {
            LOG.warn("COuld not parse URI", e);
            throw new IOException ("Attempted to parse invalid URI in " +
                "request line: "+requestLine);
            }
        }

    /**
     * Extracts the sent-by host from a Via header.
     * 
     * @param via The Via header to extract the sent-by host from.
     * @return The address for the host.
     * @throws UnknownHostException If there's an error readin the host in
     * the expected format.
     */
    public static InetAddress extractSentByFromVia(final SipHeaderValue via) 
        throws UnknownHostException
        {
        final String baseValue = via.getBaseValue();
        final String hostString = 
            StringUtils.substringAfterLast(baseValue, " ");
        
        // Note this does
        return InetAddress.getByName(hostString);
        }

    /**
     * Utility method for extracting the host and port information from the
     * topmost Via header in a SIP message.
     * 
     * @param message The SIP message to extract the host and port from.
     * @return The host and port indicated in the topmost Via.
     * @throws IOException If there's an unexpected error reading the data.
     */
    public static InetSocketAddress extractNextHopFromVia(
        final SipMessage message) throws IOException
        {
        // Route the message to the client listed in the next Via header.
        final SipHeader via = message.getHeader(SipHeaderNames.VIA);
        if (via == null)
            {
            LOG.warn("No Via header in message: "+message);
            throw new IOException("No Via header in message!");
            }
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Accessing value for Via: "+via);
            }
        final SipHeaderValue topMostVia = via.getValue();
        final String portString = 
            topMostVia.getParamValue(SipHeaderParamNames.RPORT);
        if (StringUtils.isBlank(portString))
            {
            LOG.warn("No port in Via: "+topMostVia);
            throw new IOException("Can't handle via with no port");
            }
        final int port = Integer.parseInt(portString);
        final String hostString;
        
        // Use the received parameter if it exists.
        if (topMostVia.hasParam(SipHeaderParamNames.RECEIVED))
            {
            hostString = topMostVia.getParamValue(SipHeaderParamNames.RECEIVED);
            }
        else
            {
            LOG.debug("Not using received parameter...");
            final String baseValue = topMostVia.getBaseValue();
            hostString = StringUtils.substringAfterLast(baseValue, " ");
            LOG.debug("Using host string: "+hostString);
            }
        
        return new InetSocketAddress(hostString, port);
        }

    public static int extractContentLength(final Map<String, SipHeader> headers)
        {
        final SipHeader header = headers.get(SipHeaderNames.CONTENT_LENGTH);
        if (header == null)
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("No Content-Length header");
                }
            return 0;
            }
        final String lengthString = header.getValue().getBaseValue();
        if (!NumberUtils.isNumber(lengthString))
            {
            LOG.warn("Content-Length not a number: " + lengthString);
            return 0;
            }
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Parsing length: "+lengthString);
            }
        return Integer.parseInt(lengthString);
        }
    }
