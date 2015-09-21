package com.latis.elastic;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
public class TestUpsertDocument {

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
	public void upsertDocumentTest() throws IOException, InterruptedException, ExecutionException {
		IndexRequest indexRequest = new IndexRequest("twitter", "tweet", "3")
				.source(jsonBuilder().startObject().field("user", "update1")
						.field("gender", "male1").endObject());
		UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet", "3")
				.doc(jsonBuilder().startObject().field("gender", "male3")
						.endObject()).upsert(indexRequest);
		client.update(updateRequest).get();
	}
}
