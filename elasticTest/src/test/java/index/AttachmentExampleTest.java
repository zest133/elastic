package index;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class AttachmentExampleTest {

	@Test
	public void indexPDFTest(){
		Client client = ClientExample.newTransportClient();
		AttachmentExample.indexPdf(client);
	}
}
