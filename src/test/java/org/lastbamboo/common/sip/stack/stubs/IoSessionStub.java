package org.lastbamboo.common.sip.stack.stubs;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Set;

import org.littleshoot.mina.common.CloseFuture;
import org.littleshoot.mina.common.IdleStatus;
import org.littleshoot.mina.common.IoFilterChain;
import org.littleshoot.mina.common.IoHandler;
import org.littleshoot.mina.common.IoService;
import org.littleshoot.mina.common.IoServiceConfig;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.IoSessionConfig;
import org.littleshoot.mina.common.TrafficMask;
import org.littleshoot.mina.common.TransportType;
import org.littleshoot.mina.common.WriteFuture;
import org.littleshoot.mina.common.support.DefaultWriteFuture;

/**
 * Stub class for an IO session.
 */
public class IoSessionStub implements IoSession
    {

    public CloseFuture close()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public boolean containsAttribute(String key)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public Object getAttachment()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Object getAttribute(String key)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Set<String> getAttributeKeys()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public CloseFuture getCloseFuture()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public IoSessionConfig getConfig()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public long getCreationTime()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public IoFilterChain getFilterChain()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public IoHandler getHandler()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public int getIdleCount(IdleStatus status)
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public int getIdleTime(IdleStatus status)
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getIdleTimeInMillis(IdleStatus status)
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getLastIdleTime(IdleStatus status)
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getLastIoTime()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getLastReadTime()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getLastWriteTime()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public SocketAddress getLocalAddress()
        {
        return new InetSocketAddress("208.54.95.129", 5394);
        }

    public long getReadBytes()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getReadMessages()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public SocketAddress getRemoteAddress()
        {
        return new InetSocketAddress("208.54.95.129", 1178);
        }

    public int getScheduledWriteBytes()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public IoService getService()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public SocketAddress getServiceAddress()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public TrafficMask getTrafficMask()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public TransportType getTransportType()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public int getWriteTimeout()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getWriteTimeoutInMillis()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getWrittenBytes()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public long getWrittenMessages()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public boolean isClosing()
        {
        // TODO Auto-generated method stub
        return false;
        }

    public boolean isConnected()
        {
        // TODO Auto-generated method stub
        return false;
        }

    public boolean isIdle(IdleStatus status)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public Object removeAttribute(String key)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void resumeRead()
        {
        // TODO Auto-generated method stub

        }

    public void resumeWrite()
        {
        // TODO Auto-generated method stub

        }

    public Object setAttachment(Object attachment)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Object setAttribute(String key)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public Object setAttribute(String key, Object value)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public void setIdleTime(IdleStatus status, int idleTime)
        {
        // TODO Auto-generated method stub

        }

    public void setTrafficMask(TrafficMask trafficMask)
        {
        // TODO Auto-generated method stub

        }

    public void setWriteTimeout(int writeTimeout)
        {
        // TODO Auto-generated method stub

        }

    public void suspendRead()
        {
        // TODO Auto-generated method stub

        }

    public void suspendWrite()
        {
        // TODO Auto-generated method stub

        }

    public WriteFuture write(Object message)
        {
        return new DefaultWriteFuture(this);
        }

    public Object getAttribute(String arg0, Object arg1)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public int getScheduledWriteMessages()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public boolean removeAttribute(String arg0, Object arg1)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public boolean replaceAttribute(String arg0, Object arg1, Object arg2)
        {
        // TODO Auto-generated method stub
        return false;
        }

    public Object setAttributeIfAbsent(String arg0, Object arg1)
        {
        // TODO Auto-generated method stub
        return null;
        }

    public int getScheduledWriteRequests()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    public IoServiceConfig getServiceConfig()
        {
        // TODO Auto-generated method stub
        return null;
        }

    public long getWrittenWriteRequests()
        {
        // TODO Auto-generated method stub
        return 0;
        }

    }
