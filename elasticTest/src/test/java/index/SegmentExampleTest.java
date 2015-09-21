package index;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class SegmentExampleTest {

	@Test
	public void indexPDFTest(){
		Client client = ClientExample.newTransportClient();
		SegmentExample.makeSegments(client);
	}
}
