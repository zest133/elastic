package mapping;

import index.AttachmentExample;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class IdExampleTest {

	@Test
	public void idPathTest(){
		Client client = ClientExample.newTransportClient();
		IdExample.idPath(client);
	}
}
