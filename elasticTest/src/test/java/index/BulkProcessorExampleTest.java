package index;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class BulkProcessorExampleTest {

//	@Test
//	public void BulkIndexBySizeTest(){
//		Client client = ClientExample.newTransportClient();
//		BulkProcessorExample.bulkIndexBySize(client);
//	}
	
	@Test
	public void BulkIndexByActionsTest(){
		Client client = ClientExample.newTransportClient();
		BulkProcessorExample.bulkIndexByActions(client);
	}
}
