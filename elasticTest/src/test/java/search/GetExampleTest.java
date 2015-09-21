package search;

import mapping.IdExample;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class GetExampleTest {

	@Test
	public void getDocTest(){
		Client client = ClientExample.newTransportClient();
		GetExample.getDoc(client);
	}
}
