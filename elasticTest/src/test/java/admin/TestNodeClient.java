package admin;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.junit.Test;

import admin.ClientExample;
import admin.ClusterHealthExample;



public class TestNodeClient {

	public static final String host1 = "192.168.0.105";
//	@Test
//	public void stratUpShutdown(){
//		// on startup
//
//		// on startup
//
////		Client client = new TransportClient()
////		        .addTransportAddress(new InetSocketTransportAddress("192.168.0.105", 9300))
////		        .addTransportAddress(new InetSocketTransportAddress("host2", 9300));
//		
//		Client client = new TransportClient()
//        .addTransportAddress(new InetSocketTransportAddress("192.168.0.105", 9300));
//        
//
//		// on shutdown
//
//		client.close();
//	}
	// es_test
	
	@Test
	public void stratUpShutdownConnectCluster(){
		
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "es_test").build();
		
		Client client = new TransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress("192.168.0.105", 9300));
        

		// on shutdown

		client.close();

	}
	
	@Test
	public void clientExampleTest(){
//		ClientExample.connectDisconnect();
	}
	
	
	
}
