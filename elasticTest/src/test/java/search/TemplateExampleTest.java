package search;

import mapping.IdExample;

import org.elasticsearch.client.Client;
import org.junit.Test;

import admin.ClientExample;

public class TemplateExampleTest {

//	@Test
//	public void rawTemplateTest(){
//		Client client = ClientExample.newTransportClient();
//		TemplateExample.rawTemplate(client);;
//	}
	
	@Test
	public void sourceTemplateTest(){
		Client client = ClientExample.newTransportClient();
		TemplateExample.sourceTemplate(client);
	}
}
