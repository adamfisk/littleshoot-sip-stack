package org.lastbamboo.common.sip.stack.codec.decoder.support;

/**
 * Skips tabs and spaces.  Taken from AsyncWeb.
 */
public abstract class SpaceSkippingState extends SkippingState 
    {

    /**
     * State that skips a single space.
     */
    public SpaceSkippingState()
        {
        super((byte)32);
        }
    }
