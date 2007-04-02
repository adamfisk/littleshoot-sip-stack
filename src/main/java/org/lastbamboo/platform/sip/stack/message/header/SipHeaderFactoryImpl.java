package org.lastbamboo.platform.sip.stack.message.header;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.id.uuid.UUID;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.lastbamboo.platform.sip.stack.message.SipMessageUtils;

/**
 * Factory for creating SIP headers.
 */
public class SipHeaderFactoryImpl implements SipHeaderFactory
    {

    private static final Log LOG = 
        LogFactory.getLog(SipHeaderFactoryImpl.class);
    
    private static int sequenceNumber = 1;
    
    public SipHeader createHeader(final String headerString) throws IOException
        {
        LOG.debug("Creating header from string: "+headerString);
        final String headerName = 
            SipMessageUtils.extractName(headerString);
        final String headerValueWithParams = 
            SipMessageUtils.extractValue(headerString);
        
        final List headerValues = createHeaderValues(headerValueWithParams);
        return new SipHeaderImpl(headerName, headerValues);
        }

    /**
     * Creates a list of header values.
     * 
     * @param headerValueString The header value string.
     * @return A list of header value instances.
     * @throws IOException If the header values don't match the expected 
     * syntax.
     */
    private List createHeaderValues(final String headerValueString) 
        throws IOException
        {
        final List valuesList = new ArrayList();
        if (!StringUtils.contains(headerValueString, ","))
            {
            final SipHeaderValue value = 
                new SipHeaderValueImpl(headerValueString);
            valuesList.add(value);
            return valuesList;
            }
        final String[] values = StringUtils.split(headerValueString, ",");
        
        for (int i = 0; i < values.length; i++)
            {
            final SipHeaderValue value = 
                new SipHeaderValueImpl(values[i].trim());
            valuesList.add(value);
            }
        
        return valuesList;
        }

    public SipHeader createSentByVia(final InetAddress address)
        {
        final String baseValue = 
            "SIP/2.0/TCP " + address.getHostAddress();
        final Map params = createParams(SipHeaderParamNames.BRANCH, 
            createBranchId());
        
        return new SipHeaderImpl(SipHeaderNames.VIA, 
            new SipHeaderValueImpl(baseValue, params));
        }
    
    public SipHeader createMaxForwards(final int maxForwards)
        {
        final String valueString = Integer.toString(maxForwards);
        final SipHeaderValue value = 
            new SipHeaderValueImpl(valueString, Collections.EMPTY_MAP);
        return new SipHeaderImpl(SipHeaderNames.MAX_FORWARDS, value);
        }
    
    public SipHeader createSupported()
        {
        final String valueString = "outbound";
        final SipHeaderValue value = 
            new SipHeaderValueImpl(valueString, Collections.EMPTY_MAP);     
        return new SipHeaderImpl(SipHeaderNames.SUPPORTED, value);
        }
    
    public SipHeader createTo(final URI sipUri)
        {
        final String valueString = "Anonymous <"+sipUri+">";
        final SipHeaderValue value = 
            new SipHeaderValueImpl(valueString, Collections.EMPTY_MAP);
        return new SipHeaderImpl(SipHeaderNames.TO, value);
        }
    
    public SipHeader createTo(final SipHeader originalTo)
        {
        final SipHeaderValue value = originalTo.getValue();
        final Map params = value.getParams();
        params.put(SipHeaderParamNames.TAG, createTagValue());
        final SipHeaderValue copy = 
            new SipHeaderValueImpl(value.getBaseValue(), params);
        return new SipHeaderImpl(SipHeaderNames.TO, copy);
        }

    public SipHeader createFrom(final String displayName, final URI sipUri)
        {
        final String baseValue = displayName + " <"+sipUri+">";
        final Map params = createParams(SipHeaderParamNames.TAG, 
            createTagValue());
        final SipHeaderValue value = new SipHeaderValueImpl(baseValue, params);
        return new SipHeaderImpl(SipHeaderNames.FROM, value);
        }

    public SipHeader createCallId()
        {
        final String valueString = createCallIdValue();
        final SipHeaderValue value = 
            new SipHeaderValueImpl(valueString, Collections.EMPTY_MAP);
        return new SipHeaderImpl(SipHeaderNames.CALL_ID, value);
        }
    
    public SipHeader createCSeq(final String method)
        {
        final String valueString = createCSeqValue(method);
        final SipHeaderValue value = 
            new SipHeaderValueImpl(valueString, Collections.EMPTY_MAP);
        return new SipHeaderImpl(SipHeaderNames.CSEQ, value);
        }

    public SipHeader createContact(final URI contactUri, 
        final UUID instanceId)
        {
        final String baseValue = "<"+contactUri+">";
        final String sipInstanceValue = "\"<"+instanceId.toUrn()+">\"";
        final Map params = 
            createParams(SipHeaderParamNames.SIP_INSTANCE, sipInstanceValue);
        final SipHeaderValue value = new SipHeaderValueImpl(baseValue, params);
        return new SipHeaderImpl(SipHeaderNames.CONTACT, value);
        }

    public SipHeader createExpires(final int millis)
        {
        final String valueString = Integer.toString(millis);
        final SipHeaderValue value = 
            new SipHeaderValueImpl(valueString, Collections.EMPTY_MAP);
        return new SipHeaderImpl(SipHeaderNames.EXPIRES, value);
        }

    public SipHeader createContentLength(final int contentLength)
        {
        final String valueString = Integer.toString(contentLength);
        final SipHeaderValue value = 
            new SipHeaderValueImpl(valueString, Collections.EMPTY_MAP);
        return new SipHeaderImpl(SipHeaderNames.CONTENT_LENGTH, value);
        }
    
    
    private String createTagValue()
        {
        final UUID id = UUID.randomUUID();
        final String urn = id.toUrn();
        return urn.substring(9, 19);
        }
    
    private String createCallIdValue()
        {
        final UUID id = UUID.randomUUID();
        return id.toUrn().substring(10, 18);
        }
    
    private String createBranchId()
        {
        final UUID id = UUID.randomUUID();
        return "z9hG4bK"+id.toUrn().substring(10, 17);
        }
    
    private String createCSeqValue(final String method)
        {
        sequenceNumber++;
        return sequenceNumber + " " + method;
        }
    
    /**
     * Generates the parameters map.  This is the complete parameters for the
     * common case where a header only has a single parameter.  Otherwise,
     * calling methods can add additional parameters to the map.
     * 
     * @param name The name of the first parameter to add.
     * @param value The value of the first parameter to add.
     * @return The map mapping parameter names to parameter values.
     */
    private Map createParams(final String name, final String value)
        {
        final Map params = new HashMap();
        params.put(name, value);
        return params;
        }
    }
