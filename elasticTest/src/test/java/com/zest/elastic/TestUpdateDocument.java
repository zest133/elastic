package com.zest.elastic;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.script.ScriptService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.xcontent.XContentFactory.*;

public class TestUpdateDocument {

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
	public void upateTest1() throws IOException, InterruptedException,
			ExecutionException {
		UpdateRequest updateRequest = new UpdateRequest();
		updateRequest.index("twitter");
		updateRequest.type("tweet");
		updateRequest.id("1");
		updateRequest.doc(jsonBuilder().startObject().field("user", "update")
				.endObject());
		client.update(updateRequest).get();
	}

	
	/**
	 * 새로운 field가 추가된 경우. 
	 */
	@Test
	public void upateTest2() throws IOException, InterruptedException,
			ExecutionException {
		UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet", "1")
				.doc(jsonBuilder().startObject().field("gender", "male")
						.endObject());
		client.update(updateRequest).get();
	}

	// @Test
	// public void upateTest2() throws IOException, InterruptedException,
	// ExecutionException {
	// /**
	// * curl -XPUT localhost:9200/_cluster/settings -d '{
	// "persistent" : {
	// "script.engine.groovy.inline.aggs": true
	// }
	// }
	// 위에 것처럼 셋팅이 되야 된다고 함.
	// */
	// client.prepareUpdate("twitter", "tweet", "1")
	// .setScript("ctx._source.user = \"update1\"",
	// ScriptService.ScriptType.INLINE).get();
	//
	// client.prepareUpdate("twitter", "tweet", "1")
	// .setDoc(jsonBuilder().startObject().field("user", "update")
	// .endObject()).get();
	// }
	// @Test
	// public void upateTest2() throws IOException, InterruptedException,
	// ExecutionException {
	// UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet", "1")
	// .script("ctx._source.user = \"male\"");
	// client.update(updateRequest).get();
	// }
}
