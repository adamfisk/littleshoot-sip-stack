package org.lastbamboo.platform.sip.stack.message;

import java.nio.ByteBuffer;

/**
 * Class for reading available SIP messages from a message buffer.
 */
public interface SipMessageBufferReader
    {

    /**
     * Reads SIP message from the specified partial buffer and the newly
     * received message buffer.  The partial buffer will frequently be emtpy,
     * as in the case where we read only a complete message on the previous
     * pass.  If there are multiple messages in the buffer, this will read 
     * and handle all of them.
     * 
     * @param partialBuffer The buffer containing part of a message.  If this
     * is not empty, it should always contain the beginning of the message 
     * up to the point where we stopped receiving data.
     * 
     * @param newBuffer The new buffer received from the network.
     * 
     * @return Any left over bytes from a partial message we could not read.
     * This will frequently a an empty buffer, but is guaranteed not to be
     * <code>null</code>.
     */
    ByteBuffer readMessages(final ByteBuffer partialBuffer, 
        final ByteBuffer newBuffer);

    }
