package admin;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;
import admin.IndexAdminExample;

public class TestIndexAdminExample {

//	@Test
//	public void createIndexeTest(){
//		Client client = ClientExample.newTransportClient();
//		IndexAdminExample.createIndex(client);
//	}
	
//	@Test
//	public void createIndexFullMappingTest(){
//		Client client = ClientExample.newTransportClient();
//		IndexAdminExample.createIndexFullMapping(client);
//	}
	
	@Test
	public void createIndexWithSettingsMappingsTest(){
		Client client = ClientExample.newTransportClient();
		IndexAdminExample.createIndexWithSettingsMappings(client);
	}
}
