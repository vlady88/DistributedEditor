package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import comm.CentralServerCommunicator;
import comm.CommunicatorI;
import editor.DistributedEditor;
import server.BroadcastThread;
import server.CentralServer;
import struct.CommandQueue;

/**
 * Class with unit tests and integration tests
 * 
 * @author Vladimir
 *
 */
public class EditorTest {
	
	private static int CLIENT_PORT_1 = 2001;
	private static int CLIENT_PORT_2 = 2002;
	private static int SERVER_PORT = 2000;
	private static String SERVER_IP = "127.0.0.1";
	
	private static CentralServer server;

	/**
	 * start server before each run 
	 */
	@BeforeClass
	public static void init(){
		server = new CentralServer(SERVER_PORT);
		server.start();
	    System.out.println("Server initialized");
	}

	/**
	 * stop server after each run
	 */
	@AfterClass
	public static void clean(){
		server.stopServer();
		System.out.println("Server stopped");
	}
	
	/**
	 * wait for network operations to complete before each test
	 */
	@Before
	public void beforeEach(){
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * create an editor instance connected to a fake GUI object (we don't need GUI for testing)
	 */
	private DistributedEditor createEditor(int port) {
		CommunicatorI communicator = new CentralServerCommunicator(port, SERVER_IP, SERVER_PORT);
		FakeGui gui = new FakeGui();
		DistributedEditor de = new DistributedEditor(gui, communicator);
		gui.setEditor(de);
		communicator.setEditor(de);
		
		return de;
	}
	
	/**
	 * create a dummy server thread
	 */
	private Thread createDummyServer(int port) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
					serverSocketChannel.socket().bind(new InetSocketAddress(port));
					serverSocketChannel.accept();
					serverSocketChannel.close();
				} catch (IOException e) {
					fail(e.getMessage());
				}
			}
		});
	}
	
	/**
	 * test BroadcastThread.addClient() method by adding a fake client (unit test)
	 */
	@Test
	public void testBroadcastThreadAddClient() {
		try {
			createDummyServer(CLIENT_PORT_1).start();

			BroadcastThread bt = new BroadcastThread(new CommandQueue());
			String client = InetAddress.getLocalHost().getHostAddress() + ":" + CLIENT_PORT_1;
			bt.addClient(client);

			assertNotNull(bt.getClients().get(client));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * test BroadcastThread.removeClient() method by adding a fake client and then removing it (unit test)
	 */
	@Test
	public void testBroadcastThreadRemoveClient() {
		try {
			createDummyServer(CLIENT_PORT_1).start();
			
			BroadcastThread bt = new BroadcastThread(new CommandQueue());
			String client = InetAddress.getLocalHost().getHostAddress() + ":" + CLIENT_PORT_1;
			bt.addClient(client);
			Thread.sleep(200);
			bt.removeClient(client);
			
			assertNull(bt.getClients().get(client));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * test initialization of CentralServerCommunicator object that is used by editors to communicate with the server (unit test)
	 */
	@Test
	public void testCommunicatorConnect() {
		CommunicatorI communicator = null;
		
		try {
			communicator = new CentralServerCommunicator(CLIENT_PORT_1, SERVER_IP, SERVER_PORT);
			Thread.sleep(200);
			communicator.init();
			Thread.sleep(200);
			
			assertNotNull(server.getBroadcastThread().getClients().get(InetAddress.getLocalHost().getHostAddress() + ":" + CLIENT_PORT_1));
		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			if(communicator != null) {
				communicator.close();
			}
		}
	}
	
	/**
	 * test uninitialization of CentralServerCommunicator object that is used by editors to communicate with the server (unit test)
	 */
	@Test
	public void testCommunicatorDisconnect() {
		try {
			CommunicatorI communicator = new CentralServerCommunicator(CLIENT_PORT_1, SERVER_IP, SERVER_PORT);
			Thread.sleep(200);
			communicator.init();
			Thread.sleep(200);
			communicator.close();
			Thread.sleep(200);
			
			assertNull(server.getBroadcastThread().getClients().get(InetAddress.getLocalHost().getHostAddress() + ":" + CLIENT_PORT_1));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * test insertion of characters in one editor by checking the other editor (integration test)
	 */
	@Test
	public void testInsert() {
		try {
			// setup first editor
			DistributedEditor de1 = createEditor(CLIENT_PORT_1);
			de1.start();

			// setup second editor
			DistributedEditor de2 = createEditor(CLIENT_PORT_2);
			de2.start();
			
			Thread.sleep(200);
			de1.sendInsertChar('a', 0);
			Thread.sleep(200);
			de1.sendInsertChar('b', 1);
			Thread.sleep(200);
			de1.sendInsertChar('c', 2);
			Thread.sleep(200);
			de1.close();
			de2.close();

			assertEquals(((FakeGui)de2.getGui()).getText(), "abc");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * test deletion of characters in one editor by checking the other editor (integration test)
	 */
	@Test
	public void testDelete() {
		try {
			// setup first editor
			DistributedEditor de1 = createEditor(CLIENT_PORT_1);
			de1.start();
			
			// setup second editor
			DistributedEditor de2 = createEditor(CLIENT_PORT_2);
			de2.start();
			
			Thread.sleep(200);
			de1.sendInsertChar('a', 0);
			Thread.sleep(200);
			de1.sendInsertChar('b', 1);
			Thread.sleep(200);
			de1.sendInsertChar('c', 2);
			Thread.sleep(200);
			de1.sendDeleteChar(1);
			Thread.sleep(200);
			de1.close();
			de2.close();
			
			assertEquals(((FakeGui)de2.getGui()).getText(), "ac");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * test editing of text in both editors (integration test)
	 */
	@Test
	public void testCrossEditing() {
		try {
			// setup first editor
			DistributedEditor de1 = createEditor(CLIENT_PORT_1);
			de1.start();
			
			// setup second editor
			DistributedEditor de2 = createEditor(CLIENT_PORT_2);
			de2.start();
			
			Thread.sleep(200);
			de1.sendInsertChar('a', 0);
			Thread.sleep(200);
			de1.sendInsertChar('b', 1);
			Thread.sleep(200);
			de2.sendInsertChar('c', 2);
			Thread.sleep(200);
			de2.sendDeleteChar(0);
			Thread.sleep(200);
			de1.close();
			de2.close();
			
			assertEquals(((FakeGui)de1.getGui()).getText(), "bc");
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
}
