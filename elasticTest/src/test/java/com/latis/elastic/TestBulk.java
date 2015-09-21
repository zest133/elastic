package com.latis.elastic;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestBulk {

	Client client;

	@Before
	public void init() {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "es_test").build();

		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(
						"192.168.0.105", 9300));

	}

	@After
	public void destroy() {
		// on shutdown

		client.close();

	}
	
	@Test
	public void bulkTest1() throws IOException, InterruptedException, ExecutionException {
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		// either use client#prepare, or use Requests# to directly build index/delete requests
		bulkRequest.add(client.prepareIndex("twitter", "tweet", "4")
		        .setSource(jsonBuilder()
		                    .startObject()
		                        .field("user", "kimchy")
		                        .field("postDate", new Date())
		                        .field("message", "trying out Elasticsearch")
		                    .endObject()
		                  )
		        );

		bulkRequest.add(client.prepareIndex("twitter", "tweet", "5")
		        .setSource(jsonBuilder()
		                    .startObject()
		                        .field("user", "kimchy")
		                        .field("postDate", new Date())
		                        .field("message", "another post")
		                    .endObject()
		                  )
		        );

		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
		    // process failures by iterating through each bulk response item
		}
	}
	
	@Test
	public void bulkTest2(){
		BulkProcessor bulkProcessor = BulkProcessor.builder(
		        client,  
		        new BulkProcessor.Listener() {

					@Override
					public void beforeBulk(long executionId, BulkRequest request) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void afterBulk(long executionId,
							BulkRequest request, BulkResponse response) {
						// TODO Auto-generated method stub
						 System.out.println("Bulk execution completed ["+  executionId + "].\n" +
				                    "Took (ms): " + response.getTookInMillis() + "\n" +
				                    "Failures: " + response.hasFailures() + "\n" + 
				                    "Count: " + response.getItems().length);
					}

					@Override
					public void afterBulk(long executionId,
							BulkRequest request, Throwable failure) {
						// TODO Auto-generated method stub
						 System.out.println("Bulk execution failed ["+  executionId + "].\n" +
				                    failure.toString());
					}
		            
		        })
		        .setBulkActions(-1) 
		        .setBulkSize(new ByteSizeValue(1, ByteSizeUnit.GB)) 
		        .setFlushInterval(TimeValue.timeValueSeconds(5)) 
		        .setConcurrentRequests(1) 
		        .build();
		
		
		for (int i = 0; i < 10000; i++) {
            try {
                XContentBuilder source = XContentFactory.jsonBuilder().startObject()
                        .field("user", "this is document " + i)
                        .field("postDate", new Date())
		                        .field("message", "trying out Elasticsearch")
                    .endObject();
                
                bulkProcessor.add(Requests.indexRequest("twitter").type("tweet").source(source));
                
//                bulkProcessor.add(new IndexRequest("twitter", "tweet", "1").source(/* your doc here */));
//                bulkProcessor.add(new DeleteRequest("twitter", "tweet", "2"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		bulkProcessor.close();
	}
}
