package admin;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;
import admin.ClusterHealthExample;
import admin.ClusterStateExample;

public class TestClusterCheck {

	@Test
	public void clusterHealthExampleTest(){
		Client client = ClientExample.newTransportClient();
		ClusterHealthExample.clusterHealth(client);
	}
	
	@Test
	public void clusterHealthAsyncExampleTest(){
		Client client = ClientExample.newTransportClient();
		ClusterHealthExample.clusterHealthAsync(client);
	}
	
	@Test
	public void clusterStateExampleTest(){
		Client client = ClientExample.newTransportClient();
		ClusterStateExample.indexList(client);
	}
	
	
}
