package org.lastbamboo.common.sip.stack.transport;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import junit.framework.TestCase;

import org.apache.mina.common.IoSession;
import org.lastbamboo.common.sip.stack.message.SipMessageFactory;
import org.lastbamboo.common.sip.stack.message.SipMessageFactoryImpl;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactory;
import org.lastbamboo.common.sip.stack.message.header.SipHeaderFactoryImpl;
import org.lastbamboo.common.sip.stack.stubs.IoSessionStub;
import org.lastbamboo.common.sip.stack.stubs.SipResponseStub;
import org.lastbamboo.common.sip.stack.transaction.SipTransactionFactory;
import org.lastbamboo.common.sip.stack.transaction.SipTransactionFactoryImpl;
import org.lastbamboo.common.sip.stack.transaction.SipTransactionTracker;
import org.lastbamboo.common.sip.stack.transaction.SipTransactionTrackerImpl;

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
        
        //final ReaderWriter rw = new ReaderWriterStub();
        final IoSession io = new IoSessionStub();
        final InetSocketAddress sa = (InetSocketAddress) io.getRemoteAddress();
        transport.addConnection(io);
        
        final InetSocketAddress sa2 = 
            new InetSocketAddress("208.54.95.129", 1178);
        
        assertTrue(transport.writeResponse(sa2, new SipResponseStub()));
        
        final InetSocketAddress sa3 = 
            new InetSocketAddress(InetAddress.getByName("208.54.95.129"), 1178);
        
        assertTrue(transport.writeResponse(sa2, new SipResponseStub()));
        
        assertEquals(sa.getAddress(), sa3.getAddress());
        }
    }
