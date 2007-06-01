package org.lastbamboo.common.sip.stack.codec;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.commons.lang.StringUtils;
import org.apache.mina.common.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Apache MINA utility functions.
 */
public class MinaUtils
    {
    
    private static final Logger LOG = LoggerFactory.getLogger(MinaUtils.class);
    
    private static final CharsetDecoder DECODER =
        Charset.forName("US-ASCII").newDecoder();

    /**
     * Useful for debugging.  Turns the given buffer into an ASCII string.
     * 
     * @param buf The buffer to convert to a string.
     * @return The string.
     */
    public static String toAsciiString(final ByteBuffer buf)
        {
        final int position = buf.position();
        final int limit = buf.limit();
        try
            {
            final String bufString = buf.getString(DECODER);
            buf.position(position);
            buf.limit(limit);
            return bufString;
            }
        catch (final CharacterCodingException e)
            {
            LOG.error("Could not decode: "+buf, e);
            return StringUtils.EMPTY;
            }
        }

    }
