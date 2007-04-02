package org.lastbamboo.platform.sip.stack.transport;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.lastbamboo.platform.sip.stack.message.SipMessageFactory;
import org.lastbamboo.platform.sip.stack.message.SipMessageFactoryImpl;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.platform.sip.stack.message.header.SipHeaderFactoryImpl;
import org.lastbamboo.platform.sip.stack.stubs.ReaderWriterStub;
import org.lastbamboo.platform.sip.stack.stubs.SipMessageStub;
import org.lastbamboo.platform.sip.stack.transaction.SipTransactionFactory;
import org.lastbamboo.platform.sip.stack.transaction.SipTransactionFactoryImpl;
import org.lastbamboo.platform.sip.stack.transaction.SipTransactionTracker;
import org.lastbamboo.platform.sip.stack.transaction.SipTransactionTrackerImpl;
import org.lastbamboo.shoot.protocol.ReaderWriter;

import junit.framework.TestCase;

/**
 * Test for the TCP transport layer.
 */
public class SipTcpTransportLayerImplTest extends TestCase
    {

    /**
     * Test to make sure the connection mappings operate as we expect them to.
     * 
     * @throws Exception If any unexpected error occurs.
     */
    public void testConnectionMapping() throws Exception 
        {
        final SipHeaderFactory headerFactory = new SipHeaderFactoryImpl();
        final SipMessageFactory messageFactory = 
            new SipMessageFactoryImpl(headerFactory);
        final SipTransactionTracker transactionTracker = 
            new SipTransactionTrackerImpl();
        final SipTransactionFactory transactionFactory = 
            new SipTransactionFactoryImpl(transactionTracker, messageFactory,
                500);
        final SipTcpTransportLayerImpl transport = 
            new SipTcpTransportLayerImpl(transactionFactory, headerFactory, 
                messageFactory);
        
        //final InetSocketAddress sa = 
            
        final ReaderWriter rw = new ReaderWriterStub();
        final InetSocketAddress sa = rw.getRemoteSocketAddress();
        transport.addConnection(rw);
        
        final InetSocketAddress sa2 = 
            new InetSocketAddress("208.54.95.129", 1178);
        
        assertTrue(transport.writeResponse(sa2, new SipMessageStub()));
        
        final InetSocketAddress sa3 = 
            new InetSocketAddress(InetAddress.getByName("208.54.95.129"), 1178);
        
        assertTrue(transport.writeResponse(sa2, new SipMessageStub()));
        
        assertEquals(sa.getAddress(), sa3.getAddress());
        }
    }
