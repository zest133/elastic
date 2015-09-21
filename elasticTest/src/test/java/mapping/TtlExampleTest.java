package mapping;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class TtlExampleTest {

	@Test
	public void ttlTest(){
		Client client = ClientExample.newTransportClient();
		TtlExample.ttl(client);
	}
}
