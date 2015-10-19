package com.zest.elastic;

import java.io.IOException;
import java.util.Date;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;








import admin.ClientExample;
import admin.IndexAdminExample;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class TestIndexDocument {

	Client client;
	@Before
	public void init(){
		Settings settings = ImmutableSettings.settingsBuilder()
		        .put("cluster.name", "es_test").build();
		
		client = new TransportClient(settings)
        .addTransportAddress(new InetSocketTransportAddress("192.168.0.105", 9300));
        

	}
	
	@After
	public void destroy(){
		// on shutdown

		client.close();

	}
	
	@Test
	public void makeIndex1() throws ElasticsearchException, IOException{
		IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
		        .setSource(jsonBuilder()
		                    .startObject()
		                        .field("user", "kimchy")
		                        .field("postDate", new Date())
		                        .field("message", "trying out Elasticsearch")
		                    .endObject()
		                  )
		        .execute()
		        .actionGet();
	}
	
	@Test
	public void makeIndex2() throws ElasticsearchException, IOException{
		String json = "{" +
		        "\"user\":\"kimchy\"," +
		        "\"postDate\":\"2013-01-30\"," +
		        "\"message\":\"trying out Elasticsearch\"" +
		    "}";
		IndexResponse response = client.prepareIndex("twitter", "tweet", "2")
		        .setSource(jsonBuilder()
		                    .startObject()
		                        .field("user", "kimchy")
		                        .field("postDate", new Date())
		                        .field("message", "trying out Elasticsearch")
		                    .endObject()
		                  )
		        .execute()
		        .actionGet();
		//report
		String _index = response.getIndex();
		// Type name
		String _type = response.getType();
		// Document ID (generated or not)
		String _id = response.getId();
		// Version (if it's the first time you index this document, you will get: 1)
		long _version = response.getVersion();
		// isCreated() is true if the document is a new one, false if it has been updated
		boolean created = response.isCreated();
		
		System.out.println("_id===>"+_id);
	}
	
	@Test
	public void GetIndexDocument(){
		GetResponse response = client.prepareGet("twitter", "tweet", "1")
		        .execute()
		        .actionGet();
		
		String _index = response.getIndex();
		// Type name
		String _type = response.getType();
		// Document ID (generated or not)
		String _id = response.getId();
		// Version (if it's the first time you index this document, you will get: 1)
		long _version = response.getVersion();
		// isCreated() is true if the document is a new one, false if it has been updated
		System.out.println("_id===>"+_id);
	}
	
	@Test
	public void getOperationTheading(){
		GetResponse response = client.prepareGet("twitter", "tweet", "1")
		        .setOperationThreaded(false) // read 할때는 default인 true를 사용. insert, update, delete 시는 false로 한다. 
		        .execute()
		        .actionGet();
	}
	
	
	
	
	
	
}
