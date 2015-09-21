package search;

import mapping.IdExample;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class SuggestExampleTest {

	@Test
	public void contextSuggestTest(){
		Client client = ClientExample.newTransportClient();
		SuggestExample.contextSuggest(client);
	}
}
