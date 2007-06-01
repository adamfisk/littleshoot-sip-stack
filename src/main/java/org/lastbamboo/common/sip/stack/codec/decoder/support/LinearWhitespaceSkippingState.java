package org.lastbamboo.common.sip.stack.codec.decoder.support;

/**
 * Skips tabs and spaces.  Taken from AsyncWeb.
 */
public abstract class LinearWhitespaceSkippingState extends DynamicSkippingState 
    {

    @Override
    protected boolean canSkip(byte b)
        {
        return (b == 32 || b == 9);
        }
    }
