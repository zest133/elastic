package com.latis.elastic;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.script.ScriptService;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class TestClient {

	private static final String INDEX_NAME = "test_index";

	@Test
	public void conntection() {
		// single
		Client client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						"192.168.0.105", 9300));
		// .addTransportAddress(
		// new InetSocketTransportAddress("host2", 9300));
		// cluster
		// Client client = new TransportClient().addTransportAddress(
		// new InetSocketTransportAddress("192.168.0.105", 9300));
		// .addTransportAddress(
		// new InetSocketTransportAddress("host2", 9300));
		// on shutdown

		// client.close();
	}

	@Test
	public void setClusterName() {
		// Client client = new TransportClient().addTransportAddress(
		// new InetSocketTransportAddress("192.168.0.105", 9300));
		// .addTransportAddress(
		// new InetSocketTransportAddress("host2", 9300));

		// on shutdown
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", "myClusterName").build();
		Client client = new TransportClient(settings);
		client.close();

	}

	@Test
	public void dataBindingExample() {
		// 1
		String json = "{" + "\"user\":\"kimchy\","
				+ "\"postDate\":\"2013-01-30\","
				+ "\"message\":\"trying out Elasticsearch\"" + "}";

		// 2
		Map<String, Object> json2 = new HashMap<String, Object>();
		json2.put("user", "kimchy");
		json2.put("postDate", new Date());
		json2.put("message", "trying out Elasticsearch");

		// 3
		ObjectMapper mapper = new ObjectMapper(); // create once, reuse

		// generate json
		// byte[] json3 = mapper.writeValueAsBytes(yourbeaninstance);

		// 4
		try {
			XContentBuilder builder = jsonBuilder().startObject()
					.field("user", "kimchy").field("postDate", new Date())
					.field("message", "trying out Elasticsearch").endObject();

			String json4 = builder.string();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void indexDocument() throws ElasticsearchException, IOException {

		Client client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						"192.168.0.105", 9300));

		IndexResponse response = client
				.prepareIndex("twitter", "tweet", "1")
				//
				.setSource(
						jsonBuilder().startObject().field("user", "kimchy")
								.field("postDate", new Date())
								.field("message", "trying out Elasticsearch")
								.endObject()).execute().actionGet();
		String _index = response.getIndex();
		String _type = response.getType();
		// Document ID (generated or not)
		String _id = response.getId();
		// Version (if it's the first time you index this document, you will
		// get: 1)
		long _version = response.getVersion();
		// isCreated() is true if the document is a new one, false if it has
		// been updated
		boolean created = response.isCreated();
		client.close();
	}

	@Test
	public void getIndex() {
		Client client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						"192.168.0.105", 9300));
		GetResponse response = client.prepareGet("twitter", "tweet", "1")
				.execute().actionGet();
		String _id = response.getId();
		client.close();
	}

	// 이건 자세히는 모르겠음.
	/**
	 * GET API는 API의 실제 실행은 (API가 같은 서버에 할당 된 샤드에서 실행되는)와 같은 노드에서 실행 된 경우 동작이
	 * 실행되는 스레드 모델을 설정할 수 합니다.
	 * 
	 * 옵션은 (API 아직 비동기임을주의하십시오) 다른 스레드에서 작업을 수행하거나 호출 스레드에서 실행하는 것입니다. 기본적으로
	 * operationThreaded 작업이 다른 스레드에서 실행되는 것을 의미하고 true로 설정되어 있습니다. 여기에서는 false로
	 * 설정하는 방법을 보여줍니다.
	 */
	@Test
	public void getIndexOpterationThread() {
		Client client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						"192.168.0.105", 9300));
		GetResponse response = client.prepareGet("twitter", "tweet", "1")
				.setOperationThreaded(false).execute().actionGet();
		String _id = response.getId();
		client.close();
	}

	// @Test
	// public void deleteApi(){
	// Client client = new TransportClient().addTransportAddress(
	// new InetSocketTransportAddress("192.168.0.105", 9300));
	// DeleteResponse response = client.prepareDelete("twitter", "tweet", "1")
	// .execute()
	// .actionGet();
	//
	// String _id = response.getId();
	// client.close();
	// }

	// @Test
	// public void deleteApiOpterationThread(){
	// Client client = new TransportClient().addTransportAddress(
	// new InetSocketTransportAddress("192.168.0.105", 9300));
	// DeleteResponse response = client.prepareDelete("twitter", "tweet", "1")
	// .setOperationThreaded(false)
	// .execute()
	// .actionGet();
	//
	// String _id = response.getId();
	// client.close();
	// }
	@Test
	public void updateRequest() throws IOException, InterruptedException,
			ExecutionException {
		Client client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						"192.168.0.105", 9300));
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index("twitter");
		updateRequest.type("tweet");
		updateRequest.id("1");
		updateRequest.doc(jsonBuilder().startObject().field("gender", "male")
				.endObject());
		client.update(updateRequest).get();
		
		client.close();
	}

	@Test
	public void updateRequest2() throws IOException, InterruptedException,
			ExecutionException {
		Client client = new TransportClient()
				.addTransportAddress(new InetSocketTransportAddress(
						"192.168.0.105", 9300));
//		client.prepareUpdate("twitter", "tweet", "1")
//				.setScript("ctx._source.gender = \"male1\"",
//						ScriptService.ScriptType.INLINE).get();

		 client.prepareUpdate("twitter", "tweet", "1")
		 .setDoc(jsonBuilder().startObject().field("gender", "male")
		 .endObject()).get();
		client.close();
	}

	@Test
	public void upsertRequest() throws IOException, InterruptedException, ExecutionException {
		Client client = new TransportClient()
		.addTransportAddress(new InetSocketTransportAddress(
				"192.168.0.105", 9300));
		IndexRequest indexRequest = new IndexRequest("index", "type", "1")
				.source(jsonBuilder().startObject().field("name", "Joe Smith")
						.field("gender", "male").endObject());
		UpdateRequest updateRequest = new UpdateRequest("index", "type", "1")
				.doc(jsonBuilder().startObject().field("gender", "male")
						.endObject()).upsert(indexRequest);
		client.update(updateRequest).get();
	}
	
	@Test
	public void bulkRequest() throws IOException{
		Client client = new TransportClient()
		.addTransportAddress(new InetSocketTransportAddress(
				"192.168.0.105", 9300));
		BulkRequestBuilder bulkRequest = client.prepareBulk();

		// either use client#prepare, or use Requests# to directly build index/delete requests
		bulkRequest.add(client.prepareIndex("twitter", "tweet", "1")
		        .setSource(jsonBuilder()
		                    .startObject()
		                        .field("user", "kimchy")
		                        .field("postDate", new Date())
		                        .field("message", "trying out Elasticsearch")
		                    .endObject()
		                  )
		        );

		bulkRequest.add(client.prepareIndex("twitter", "tweet", "2")
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
		client.close();
	}

	@Test
	public void searchAPI(){
		Client client = new TransportClient()
		.addTransportAddress(new InetSocketTransportAddress(
				"192.168.0.105", 9300));
		SearchResponse response = client.prepareSearch("index1", "index2")
		        .setTypes("type1", "type2")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("multi", "test"))             // Query
		        .setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))   // Filter
		        .setFrom(0).setSize(60).setExplain(true)
		        .execute()
		        .actionGet();
		client.close();
	}
	
	@Test
	public void multiSearch(){
		Client client = new TransportClient()
		.addTransportAddress(new InetSocketTransportAddress(
				"192.168.0.105", 9300));
		SearchRequestBuilder srb1 = client
			    .prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
			SearchRequestBuilder srb2 = client
			    .prepareSearch().setQuery(QueryBuilders.matchQuery("name", "kimchy")).setSize(1);

			MultiSearchResponse sr = client.prepareMultiSearch()
			        .add(srb1)
			        .add(srb2)
			        .execute().actionGet();

			// You will get all individual responses from MultiSearchResponse#getResponses()
			long nbHits = 0;
			for (MultiSearchResponse.Item item : sr.getResponses()) {
			    SearchResponse response = item.getResponse();
			    nbHits += response.getHits().getTotalHits();
			}
	}
	
	
}
