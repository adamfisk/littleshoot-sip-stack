package org.lastbamboo.platform.sip.stack.message;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RequestMethod
    {

    private static final Map s_methods = new ConcurrentHashMap();
    
    public RequestMethod(final String methodName)
        {
        s_methods.put(methodName, this);
        }

    public static final RequestMethod INVITE = new RequestMethod("invite");
    public static final RequestMethod REGISTER = new RequestMethod("register");

    public static RequestMethod getMethod(final String method)
        {
        return (RequestMethod) s_methods.get(method.toLowerCase());
        }
    }
