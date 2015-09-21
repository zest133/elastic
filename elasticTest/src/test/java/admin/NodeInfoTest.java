package admin;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;
import admin.ClusterStateExample;
import admin.NodesInfoExample;

public class NodeInfoTest {

	@Test
	public void getNodesTest(){
		Client client = ClientExample.newTransportClient();
		NodesInfoExample.Nodes(client);
	}
	
	@Test
	public void getIndexesTest(){
		Client client = ClientExample.newTransportClient();
		NodesInfoExample.Indices(client);
	}
}
