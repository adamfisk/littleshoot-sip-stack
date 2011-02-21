package org.lastbamboo.common.sip.stack.codec.encoder;

import org.littleshoot.mina.common.ByteBuffer;
import org.littleshoot.mina.common.IoFuture;
import org.littleshoot.mina.common.IoFutureListener;
import org.littleshoot.mina.common.IoSession;
import org.littleshoot.mina.common.WriteFuture;
import org.littleshoot.mina.filter.codec.ProtocolEncoder;
import org.littleshoot.mina.filter.codec.ProtocolEncoderOutput;
import org.lastbamboo.common.sip.stack.message.SipMessage;
import org.littleshoot.util.mina.MinaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ProtocolEncoder} for SIP messages.  The actual encoding takes place
 * in a delegate class for easier testing.
 */
public class SipMessageProtocolEncoder implements ProtocolEncoder, 
    IoFutureListener 
    {
    
    private final Logger LOG = 
        LoggerFactory.getLogger(SipMessageProtocolEncoder.class);
    
    private final SipMessageEncoder m_encoder = new SipMessageEncoderImpl();

    private volatile static int s_encodesCompleted = 0;

    private volatile static int s_encodeAttempts = 0;
    
    public void encode(final IoSession session, final Object message,
        final ProtocolEncoderOutput out) throws Exception
        {
        s_encodeAttempts++;
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Attempted encodes: "+s_encodeAttempts);
            }
        final ByteBuffer buffer = this.m_encoder.encode((SipMessage) message);
        //LOG.debug("Writing buffer: \n{}", MinaUtils.toAsciiString(buffer));
        
        out.write(buffer);
        }

    public void dispose(IoSession session) throws Exception
        {
        }

    public void operationComplete(final IoFuture future)
        {
        s_encodesCompleted++;
        if (LOG.isDebugEnabled())
            {
            LOG.debug("Completed encodes: "+s_encodesCompleted);
            }
        }
    }
