package search;

import mapping.IdExample;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class MatchAllExampleTest {

	@Test
	public void indexPDFTest(){
		Client client = ClientExample.newTransportClient();
		MatchAllExample.matchAll(client);
	}
}
