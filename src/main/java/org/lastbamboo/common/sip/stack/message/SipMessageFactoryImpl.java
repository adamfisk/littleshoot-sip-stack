package org.lastbamboo.common.sip.stack.message;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.SimpleByteBufferAllocator;
import org.lastbamboo.common.sip.stack.message.header.SipHeader;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;
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
    private final Logger LOG = LoggerFactory.getLogger(SipMessageFactoryImpl.class);

    private final SipHeaderFactory m_headerFactory;
    
    /**
     * If this is not set for tests or anything else, it causes massive 
     * trauma.  In particular, ByteBuffer.allocate(0) creates a 
     * ByteBuffer with a capacity of 1 -- odd behavior that causes various
     * problems.
     */
    static
        {
        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        }
    
    private static final ByteBuffer EMPTY_BODY = ByteBuffer.allocate(0);
    
    /**
     * Creates a new SIP message factory.
     * 
     * @param headerFactory The collaborator class for creating SIP headers.
     */
    public SipMessageFactoryImpl(final SipHeaderFactory headerFactory)
        {
        this.m_headerFactory = headerFactory;
        }
    
    /**
     * Creates a new SIP message factory with the default header factory.
     */
    public SipMessageFactoryImpl()
        {
        this(new SipHeaderFactoryImpl());
        }

    public Register createRegisterRequest(final URI requestUri, 
        final String displayName, final URI toUri, final UUID instanceId, 
        final URI contactUri)
        {
        // TODO: Are we even supposed to include the content length header
        // in register requests?
        final Map<String, SipHeader> headers =
            createHeaders("REGISTER", displayName, toUri, toUri, instanceId, 
            contactUri, 0);
        return new Register(requestUri, headers);
        }
    
    public Invite createInviteRequest(final String displayName, 
        final URI toUri, final URI fromUri, final UUID instanceId, 
        final URI contactUri, final ByteBuffer body)
        {
        final Map<String, SipHeader> headers = 
            createHeaders("INVITE", displayName, toUri, fromUri,
            instanceId, contactUri, body.capacity());
        return new Invite(toUri, headers, body);
        }
    
    private Map<String, SipHeader> createHeaders(final String method, 
        final String displayName, 
        final URI toUri, final URI fromUri, final UUID instanceId, 
        final URI contactUri, final int contentLength)
        {
        final Map<String, SipHeader> headers = 
            new ConcurrentHashMap<String, SipHeader>();
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
    
    public SipResponse createInviteOk(final Invite request, 
        final UUID instanceId, final URI contactUri, final ByteBuffer body)
        {
        final Map<String, SipHeader> headers = createResponseHeaders(request);
        addRecordRoute(request, headers);
        addContact(headers, instanceId, contactUri);
        addContentLength(headers, body.capacity());
        
        final SipResponse response = new SipResponse(200, "OK", headers, body);
        return response;
        }
    
    public SipMessage createErrorResponse(final SipMessage request, 
        final UUID instanceId, final URI contactUri, final int responseCode, 
        final String reasonPhrase)
        {
        final Map<String, SipHeader> headers = createResponseHeaders(request);
        addRecordRoute(request, headers);
        addContact(headers, instanceId, contactUri);
        
        // TODO: We don't currently include the warning header.
        final SipResponse response = 
            new SipResponse(responseCode, reasonPhrase, headers);
        return response;
        }
    
    public Register addVia(final Register message, final SipHeader newHeader)
        {
        final Map<String, SipHeader> headers = 
            addVia(message.getHeaders(), newHeader);
        return new Register(message.getStartLine(), headers, message.getBody());
        }

    public Invite addVia(final Invite message, final SipHeader newHeader)
        {
        final Map<String, SipHeader> headers = 
            addVia(message.getHeaders(), newHeader);
        return new Invite(message.getStartLine(), headers, message.getBody());
        }

    private Map<String, SipHeader> addVia(final Map<String, SipHeader> headers, 
        final SipHeader newHeader)
        {
        final SipHeader header = headers.get(newHeader.getName());
        if (header == null)
            {
            headers.put(newHeader.getName(), newHeader);
            }
        else
            {
            final List<SipHeaderValue> values = header.getValues();
            values.addAll(0, newHeader.getValues());
            
            final SipHeader copy = 
                new SipHeaderImpl(newHeader.getName(), values);
            headers.put(copy.getName(), copy);
            if (LOG.isDebugEnabled())
                {
                LOG.debug("Created new Via header: "+copy);
                }
            }
        return headers;
        }
    
    public SipResponse stripVia(final SipResponse response)
        {
        // Remove the top Via header.
        final Map<String, SipHeader> headers = response.getHeaders();
        final SipHeader viaHeader = headers.remove(SipHeaderNames.VIA);
        final List<SipHeaderValue> vias = viaHeader.getValues();
        final SipHeaderValue ourVia = vias.remove(0);
        
        final SipHeader strippedVia = 
            new SipHeaderImpl(SipHeaderNames.VIA, vias);
        headers.put(SipHeaderNames.VIA, strippedVia);
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Removed Via header: "+ourVia);
            }
        return new SipResponse(response.getStatusCode(), 
            response.getReasonPhrase(), headers, 
            response.getBody());
        }

    public SipResponse createRegisterOk(final Register request)
        {
        final Map<String, SipHeader> headers = 
            createResponseHeaders(request);
        
        // Add the supported header to indicated supported extensions.  
        // We support the sip outbound extension, for example.
        final SipHeader supported = this.m_headerFactory.createSupported();
        headers.put(supported.getName(), supported);
        return new SipResponse(200, "OK", headers, EMPTY_BODY);
        }

    /**
     * Add a contact header.  For responses that establish dialogs, this is
     * specified in RFC 3261 section 12.1.1, page 70.
     * 
     * @param headers The headers to copy into.
     * @param instanceId The instance ID of the user.
     * @param contactUri The contact URI of the user.
     */
    private void addContact(final Map<String, SipHeader> headers, 
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
    private void addContentLength(final Map<String, SipHeader> headers, 
        final int length)
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
    private void addRecordRoute(final SipMessage request, 
        final Map<String, SipHeader> headers)
        {
        copyHeader(headers, request, SipHeaderNames.RECORD_ROUTE);
        }
    
    public SipResponse createRequestTimeoutResponse(
        final SipMessage request)
        {
        final Map<String, SipHeader> headers = createResponseHeaders(request);
        return new RequestTimeoutResponse(headers);
        }

    private Map<String, SipHeader> createResponseHeaders(
        final SipMessage request)
        {
        // Copy the request headers into the response, as specified in 
        // RFC 3261 section 8.2.6.2, page 50.
        final Map<String, SipHeader> headers = 
            new ConcurrentHashMap<String, SipHeader>();
        copyHeader(headers, request, SipHeaderNames.FROM);
        copyHeader(headers, request, SipHeaderNames.CALL_ID);
        copyHeader(headers, request, SipHeaderNames.CSEQ);
        copyHeader(headers, request, SipHeaderNames.VIA);
        handleToHeader(headers, request);
        
        return headers;
        }

    private void handleToHeader(final Map<String, SipHeader> headers, 
        final SipMessage request)
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

    private void copyHeader(final Map<String, SipHeader> headers, 
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

    public Invite createInviteToForward(
        final InetSocketAddress socketAddress, final Invite invite) 
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
        final List<SipHeaderValue> viaValues = via.getValues();
        
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Processing "+viaValues.size()+" vias...");
            }
        final SipHeaderValue viaValue = viaValues.remove(0);
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
        
        final Map<String, String> params = viaValue.getParams();
        
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
        final Map<String, SipHeader> headers = invite.getHeaders();
        
        // We actually want to replace the old Via with the new Via 
        // containing the new parameter as opposed to adding a Via 
        // header.
        headers.put(newVia.getName(), newVia);
        return new Invite(invite.getStartLine(), headers, 
            invite.getBody());
        }

    }
