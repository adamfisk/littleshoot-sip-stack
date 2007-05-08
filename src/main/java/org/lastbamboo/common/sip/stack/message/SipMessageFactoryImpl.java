package org.lastbamboo.common.sip.stack.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderImpl;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderParamNames;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValue;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderValueImpl;

/**
 * Factory for creating SIP messages.  This can create messages from scratch
 * or messages that arrive over the wire.  
 */
public class SipMessageFactoryImpl implements SipMessageFactory
    {
    
    /**
     * Logger for this class.
     */
    private static final Log LOG = 
        LogFactory.getLog(SipMessageFactoryImpl.class);
    
    private static final Pattern RESPONSE_CODE = 
        Pattern.compile("2.0\\s+(\\d+)");
    
    /**
     * Pattern for a SIP request method followed by whitespace.
     */
    private static final Pattern METHOD_WHITESPACE = 
        Pattern.compile("(\\w+)\\s+");

    private final SipHeaderFactory m_headerFactory;
    
    private final Map m_requestFactories = createRequstFactories();

    private final SingleSipMessageFactory m_unknownRequestFactory =
        new UnknownRequestFactory();
    
    private static final byte[] EMPTY_BODY = new byte[0];
    
    /**
     * Creates a new SIP message factory.
     * 
     * @param headerFactory The collaborator class for creating SIP headers.
     */
    public SipMessageFactoryImpl(final SipHeaderFactory headerFactory)
        {
        this.m_headerFactory = headerFactory;
        }
    
    private Map createRequstFactories()
        {
        final Map factories = new ConcurrentHashMap();
        factories.put("invite", new InviteFactory());
        factories.put("register", new RegisterFactory());
        return factories;
        }
    
    private SipMessage createSipMessage(final String requestOrResponseLine, 
        final Map headers, final byte[] body) throws IOException
        {
        if (requestOrResponseLine.startsWith("SIP/2.0"))
            {
            return createResponse(requestOrResponseLine, headers, body);
            }
        else 
            {
            return createRequest(requestOrResponseLine, headers, body);
            }
        }

    private SipMessage createRequest(final String requestLine, 
        final Map headers, final byte[] body) throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating SIP request from network with "+
                headers.size()+" headers...");
            }
        if (headers.size() < 4)
            {
            LOG.error("Bad headers in message: " + headers);
            }
        final SingleSipMessageFactory factory = toRequestFactory(requestLine);
        
        LOG.debug("Creating request with factory: "+factory);
        return factory.createSipMessage(requestLine, headers, body);
        }
    
    /**
     * Extracts the method of the request.
     * @param requestLine The request line containing the request method as the
     * first word.
     * @return The request method for the request.
     * @throws IOException If there's any error parsing the input.
     */
    public SingleSipMessageFactory toRequestFactory(final String requestLine) 
        throws IOException
        {
        final Matcher matcher = METHOD_WHITESPACE.matcher(requestLine);
        if (!matcher.find())
            {
            LOG.warn("Could not find method for request line: "+requestLine);
            throw new IOException("Invalid SIP request line:'"+requestLine+"'");
            }
        
        final String method = matcher.group(1);
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Got method: "+method+" for request line: "+requestLine);
            }
        
        if (StringUtils.isBlank(method))
            {
            LOG.warn("Blank method string!!");
            }
        if (!this.m_requestFactories.containsKey(method.toLowerCase()))
            {
            LOG.warn("Did not recognize method: "+method);
            // Handle the case where the first INVITE indicating the message
            // type mysteriously doesn't come through.  Just add the INVITE to
            // beginning as if it were there all along.
            // TODO: Obviously ugly and horrible.  We need to figure out why
            // this could ever happen and fix the problem.
            final String trimmedRequestLine = requestLine.trim(); 
            if (trimmedRequestLine.startsWith("sip:"))
                {
                LOG.debug("Treating as an INVITE");
                return toRequestFactory("INVITE "+trimmedRequestLine);
                }
            return this.m_unknownRequestFactory;
            }
   
        return (SingleSipMessageFactory) this.m_requestFactories.get(
            method.toLowerCase());
        }

    private SipMessage createResponse(final String responseLine, 
        final Map headers, final byte[] body) throws IOException
        {
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Creating SIP response from response line: " + 
                responseLine);
            }
        
        final Matcher matcher = RESPONSE_CODE.matcher(responseLine);
        if (!matcher.find())
            {
            throw new IOException(
                "Could not parse response line: "+responseLine);
            }
        
        final String responseCodeString = matcher.group(1);
        final int responseCode = Integer.parseInt(responseCodeString);
        return createResponse(responseCode, responseLine, headers, body);
        }

    private SipMessage createResponse(final int responseCode, 
        final String responseLine, final Map headers, final byte[] body) 
        throws IOException
        {
        switch (responseCode)
            {
            case SipResponseCode.OK:
                LOG.trace("Processing 200 OK!!!");
                return new OkResponse(responseLine, headers, body);
            case SipResponseCode.REQUEST_TIMEOUT:
                LOG.trace("Processing 200 OK!!!");
                return new RequestTimeoutResponse(responseLine, headers);
            default:
                LOG.warn("Did not understand response: "+responseLine);
                throw new IOException(
                    "Did not understand response: "+responseLine);
            }
        }

    public SipMessage createRegisterRequest(final URI requestUri, 
        final String displayName, final URI toUri, final UUID instanceId, 
        final URI contactUri)
        {
        final String requestLine = 
            "REGISTER "+requestUri.toASCIIString()+" SIP/2.0";
        
        // TODO: Are we even supposed to include the content length header
        // in register requests?
        final Map headers = createHeaders("REGISTER", displayName, toUri, 
            toUri, instanceId, contactUri, 0);
        return new Register(requestLine, headers);
        }
    
    public SipMessage createInviteRequest(final String displayName, 
        final URI toUri, final URI fromUri, final UUID instanceId, 
        final URI contactUri, final byte[] body)
        {
        final String requestLine = "INVITE " + toUri + " SIP/2.0";
        final Map headers = createHeaders("INVITE", displayName, toUri, fromUri,
            instanceId, contactUri, body.length);
        return new Invite(requestLine, headers, body);
        }
    
    private Map createHeaders(final String method, final String displayName, 
        final URI toUri, final URI fromUri, final UUID instanceId, 
        final URI contactUri, final int contentLength)
        {
        final Map headers = new ConcurrentHashMap();
        SipHeader curHeader = this.m_headerFactory.createMaxForwards(70);
        headers.put(curHeader.getName(), curHeader);
        curHeader = this.m_headerFactory.createTo(toUri);
        headers.put(curHeader.getName(), curHeader);
        curHeader = this.m_headerFactory.createFrom(displayName, fromUri);
        headers.put(curHeader.getName(), curHeader);
        curHeader = this.m_headerFactory.createCallId();
        headers.put(curHeader.getName(), curHeader);
        curHeader = this.m_headerFactory.createCSeq(method);
        headers.put(curHeader.getName(), curHeader);
        curHeader = this.m_headerFactory.createContact(contactUri, instanceId);
        headers.put(curHeader.getName(), curHeader);
        curHeader = this.m_headerFactory.createExpires(7200);
        headers.put(curHeader.getName(), curHeader);
        curHeader = this.m_headerFactory.createContentLength(contentLength);
        headers.put(curHeader.getName(), curHeader);
        return headers;
        }
    
    public SipMessage createInviteOk(final Invite request, 
        final UUID instanceId, final URI contactUri, final byte[] body)
        {
        if (ArrayUtils.isEmpty(body))
            {
            LOG.error("Sending INVITE OK with no body!!!");
            }
        final Map headers = createResponseHeaders(request);
        addRecordRoute(request, headers);
        addContact(request, headers, instanceId, contactUri);
        addContentLength(headers, body.length);
        
        final SipMessage msg = 
            new SipMessageImpl("SIP/2.0 200 OK", headers, body);
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Returning OK to INVITE:" + request);
            LOG.debug("OK:                    " + msg);
            }
        return msg;
        }
    
    public SipMessage addVia(final SipMessage message, 
        final SipHeader newHeader)
        {
        // Note this is actually a copy of the headers.
        final Map headers = message.getHeaders();
        final SipHeader header = (SipHeader) headers.get(newHeader.getName());
        if (header == null)
            {
            headers.put(newHeader.getName(), newHeader);
            }
        else
            {
            final List values = header.getValues();
            values.addAll(0, newHeader.getValues());
            
            final SipHeader copy = 
                new SipHeaderImpl(newHeader.getName(), values);
            headers.put(copy.getName(), copy);
            }
        
        return new SipMessageImpl(message.getStartLine(), headers, 
            message.getBody());
        }
    
    public SipMessage stripVia(final SipMessage response)
        {
        // Remove the top Via header.
        final Map headers = response.getHeaders();
        final SipHeader viaHeader = 
            (SipHeader) headers.remove(SipHeaderNames.VIA);
        final List vias = viaHeader.getValues();
        final SipHeaderValue ourVia = (SipHeaderValue) vias.remove(0);
        
        final SipHeader strippedVia = 
            new SipHeaderImpl(SipHeaderNames.VIA, vias);
        headers.put(SipHeaderNames.VIA, strippedVia);
        LOG.debug("Removed Via header: "+ourVia);
        return new SipMessageImpl(response.getStartLine(), headers, 
            response.getBody());
        }

    public SipMessage createRegisterOk(final Register request)
        {
        final Map headers = createResponseHeaders(request);
        
        // Add the supported header to indicated supported extensions.  
        // We support the sip outbound extension, for example.
        final SipHeader supported = this.m_headerFactory.createSupported();
        headers.put(supported.getName(), supported);
        return new SipMessageImpl("SIP/2.0 200 OK", headers);
        }

    /**
     * Add a contact header.  For responses that establish dialogs, this is
     * specified in RFC 3261 section 12.1.1, page 70.
     * 
     * @param request The request to copy the header from.
     * @param headers The headers to copy into.
     * @param instanceId The instance ID of the user.
     * @param contactUri The contact URI of the user.
     */
    private void addContact(final Invite request, final Map headers, 
        final UUID instanceId, final URI contactUri)
        {
        final SipHeader contact = 
            this.m_headerFactory.createContact(contactUri, instanceId);
        headers.put(contact.getName(), contact);
        }
    
    /**
     * Adds the content length header.
     * 
     * @param headers The group of headers to add the Content-Length header to.
     * @param length The length of the content in bytes.
     */
    private void addContentLength(final Map headers, final int length)
        {
        final SipHeader contentLength =
            this.m_headerFactory.createContentLength(length);
        headers.put(contentLength.getName(), contentLength);
        }

    /**
     * Copy all the Record-Route headers into the response for responses that
     * create dialogs.  See RFC 3261 section 12.1.1, page 70.
     * 
     * @param request The request to copy the header from.
     * @param headers The headers to copy into.
     */
    private void addRecordRoute(final Invite request, final Map headers)
        {
        copyHeader(headers, request, SipHeaderNames.RECORD_ROUTE);
        }
    
    public SipMessage createRequestTimeoutResponse(final SipMessage request)
        {
        final Map headers = createResponseHeaders(request);
        return new SipMessageImpl("SIP/2.0 408 Request Timeout", headers);
        }

    private Map createResponseHeaders(final SipMessage request)
        {
        // Copy the request headers into the response, as specified in 
        // RFC 3261 section 8.2.6.2, page 50.
        final Map headers = new ConcurrentHashMap();
        copyHeader(headers, request, SipHeaderNames.FROM);
        copyHeader(headers, request, SipHeaderNames.CALL_ID);
        copyHeader(headers, request, SipHeaderNames.CSEQ);
        copyHeader(headers, request, SipHeaderNames.VIA);
        handleToHeader(headers, request);
        
        return headers;
        }

    private void handleToHeader(final Map headers, final SipMessage request)
        {
        final SipHeader to = request.getHeader(SipHeaderNames.TO);
        if (to.getValue().hasParam(SipHeaderParamNames.TAG))
            {
            copyHeader(headers, request, SipHeaderNames.TO);
            }
        else 
            {
            final SipHeader toWithTag = this.m_headerFactory.createTo(to);
            headers.put(toWithTag.getName(), toWithTag);
            }
        }

    private void copyHeader(final Map headers, 
        final SipMessage request, final String headerName)
        {
        final SipHeader header = request.getHeader(headerName);
        if (header == null)
            {
            // There are headers we copy only if they exist in the request,
            // so this certainly can happen.
            if (LOG.isDebugEnabled())
                {
                LOG.debug("No header for name: "+headerName);
                }
            return;
            }
        headers.put(header.getName(), header);
        }

    public SipMessage createSipMessage(final String messageString) 
        throws IOException  
        {
        // Check for the double CRLF keep alive outside of the normal message
        // reading since the BufferedReader readLine method eats the keep
        // alive message.
        if (messageString.startsWith("\r\n\r\n"))
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Got double CRLF keep alive");
                }
            return new DoubleCrlfKeepAlive();
            }
        final BufferedReader reader = 
            new BufferedReader(new StringReader(messageString));
        
        return createSipMessage(reader);
        
        }

    public SipMessage createSipMessage(final BufferedReader reader) 
        throws IOException
        {
        int bytesRead = 0;
        final Map headers = new ConcurrentHashMap();
        final String requestOrResponseLine = reader.readLine();
        
        if (requestOrResponseLine == null)
            {
            LOG.debug("Could not read request or response line...");
            return null;
            }
        bytesRead += requestOrResponseLine.length() + 2;
        LOG.debug("Received first line: "+requestOrResponseLine);
        
        String curLine = reader.readLine();
        if (curLine == null)
            {
            LOG.debug("Could not read first header...");
            return null;
            }
        
        // The 2 extra bytes are for the "\r\n".
        bytesRead += curLine.length() + 2;
        
        // Now read all the headers.  The headers are terminated with a CRLF.
        while (!StringUtils.isBlank(curLine))
            {
            LOG.debug(curLine);
            addHeader(curLine, headers);
            curLine = reader.readLine();
            if (curLine == null)
                {
                LOG.debug("Could not read current header...");
                return null;
                }
            
            // The 2 extra bytes are for the "\r\n".
            bytesRead += curLine.length() + 2;
            }

        if (LOG.isDebugEnabled())
            {
            LOG.debug("Finished reading message headers: "+headers);
            }
        final SipHeader contentLength = 
            (SipHeader) headers.get(SipHeaderNames.CONTENT_LENGTH);
        if (contentLength == null)
            {
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Creating empty SIP message for: "+
                    requestOrResponseLine+" " + 
                    headers.get(SipHeaderNames.CSEQ));
                }
            return createSipMessage(requestOrResponseLine, headers, EMPTY_BODY);
            }
        final int length = 
            Integer.parseInt(contentLength.getValue().getBaseValue());
        final char[] bodyChars = new char[length];
        
        int bytesInBodyRead = 0;
        while (bytesInBodyRead < length)
            {
            final int newBytesRead;
            try 
                {
                newBytesRead = reader.read(bodyChars);
                }
            catch (final IOException e)
                {
                LOG.warn("Exception reading body!!", e);
                throw e;
                }
            if (newBytesRead == -1)
                {
                LOG.debug("Reached end of stream!!");
                return null;
                }
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Adding bytes read: "+newBytesRead);
                }
            bytesInBodyRead += newBytesRead;
            }
        
        final byte[] body;
        try
            {
            final String bodyString = new String(bodyChars);
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Using body string: "+bodyString);
                }
            body = bodyString.getBytes("US-ASCII");
            }
        catch (final UnsupportedEncodingException e)
            {
            LOG.error("Should NEVER happen", e);
            throw new IOException("Encoding error");
            }
        
        return createSipMessage(requestOrResponseLine, headers, body);
        }

    private void addHeader(final String headerString, final Map headers) 
        throws IOException 
        {
        final SipHeader header = 
            this.m_headerFactory.createHeader(headerString);
        headers.put(header.getName(), header);        
        }

    public SipMessage createInviteToForward(
        final InetSocketAddress socketAddress, final SipMessage invite) 
        throws IOException
        {
        final SipHeader via = invite.getHeader(SipHeaderNames.VIA);
        if (via == null)
            {
            LOG.warn("No Via in message: "+invite);
            throw new IOException("No Via in message: "+invite);
            }
        // As specified in section 18.2.1 of RFC 3261, we must add the 
        // 'received' parameter to the Via header if the sent-by parameter
        // of the Via differs from the address we see.
        final List viaValues = via.getValues();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Processing "+viaValues.size()+" vias...");
            }
        final SipHeaderValue viaValue = (SipHeaderValue) viaValues.remove(0);
        final String fullSentByString = 
            StringUtils.substringAfter(viaValue.getBaseValue(), " ");
        if (StringUtils.isBlank(fullSentByString))
            {
            LOG.warn("Blank sent-by in Via: "+via);
            // TODO: Should return a client error message to the client.
            return null;
            }
        final String sentByHost;
        if (StringUtils.contains(fullSentByString, ":"))
            {
            sentByHost = StringUtils.substringBefore(fullSentByString, ":");
            }
        else
            {
            sentByHost = fullSentByString;
            }
        
        final String host = socketAddress.getAddress().getHostAddress();
        final int rport = socketAddress.getPort();        
        
        final Map params = viaValue.getParams();
        
        // Only add the received parameter if the hosts differ.
        if (!host.equals(sentByHost))
            {
            params.put(SipHeaderParamNames.RECEIVED, host);
            }
        
        // Always add the rport parameter, since the sending host is not
        // even supposed to have its local port in the via.  The rport 
        // parameter is non-standard.  We use it because there's 
        // no other way to match responses to the client connections that
        // are only mapped according to ephemeral ports.  See the discussion
        // on this point on the SIP implementors list and section 18 of 
        // RFC 3261.
        params.put(SipHeaderParamNames.RPORT, Integer.toString(rport));
        
        final SipHeaderValue viaValueCopy = 
            new SipHeaderValueImpl(viaValue.getBaseValue(), params);
        
        viaValues.add(0, viaValueCopy);
        final SipHeader newVia = 
            new SipHeaderImpl(SipHeaderNames.VIA, viaValues);
        
        // Note this returns a full copy of the headers.
        final Map headers = invite.getHeaders();
        
        // We actually want to replace the old Via with the new Via 
        // containing the new parameter as opposed to adding a Via 
        // header.
        headers.put(newVia.getName(), newVia);
        return new SipMessageImpl(invite.getStartLine(), headers, 
            invite.getBody());
        }
    }
