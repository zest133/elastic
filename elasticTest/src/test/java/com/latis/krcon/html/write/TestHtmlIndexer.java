package com.latis.krcon.html.write;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.tika.exception.TikaException;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.latis.krcon.html.dto.HtmlDTO;
import com.latis.krcon.html.parser.HtmlWithTikaParser;

import static org.elasticsearch.common.xcontent.XContentFactory.*;
public class TestHtmlIndexer {

	public final static String clusterName = "es_test";
	public final static String host = "192.168.0.105";
	public final static int port = 9300;
	public final static String indexName="krcon";
	
	
	public Client client;
	private HtmlWithTikaParser htmlParser;
	
	@Before
	public void setup(){
		Settings settings = ImmutableSettings.settingsBuilder()
	            .put("cluster.name", clusterName)
	            .put("client.transport.sniff", true)
	            .build();
	              
	    client = new TransportClient(settings).addTransportAddress(new InetSocketTransportAddress(host, port));
	    htmlParser = new HtmlWithTikaParser();
	}
	
	@After
	public void destory(){
		client.close();
	}
	
	
	public void settings() throws ElasticsearchException, IOException{
		//delete index if exists
        if (client.admin().indices().prepareExists(indexName).execute().actionGet().isExists())
            client.admin().indices().prepareDelete(indexName).execute().actionGet();
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("number_of_shards", 5)
                .put("number_of_replicas", 5)
                .build();
	
        /**
         * {
    "settings": {
        "analysis": {
            "char_filter": {
                "&_to_and": {
                    "type":       "mapping",
                    "mappings": [ "&=> and "]
            }},
            "filter": {
                "my_stopwords": {
                    "type":       "stop",
                    "stopwords": [ "the", "a" ]
            }},
            "analyzer": {
                "my_analyzer": {
                    "type":         "custom",
                    "char_filter":  [ "html_strip", "&_to_and" ],
                    "tokenizer":    "standard",
                    "filter":       [ "lowercase", "my_stopwords" ],
                 "test_2" : {
	               "filter" : [
	                  "standard",
	                  "lowercase",
	                  "stop",
	                  "asciifolding"
	               ],
	               "char_filter" : [
	                  "html_strip"
	               ],
	               "tokenizer" : "standard"
	            }
                    
            }}
}}}
         */
        
        
		client.admin().indices().prepareCreate(indexName)
        .setSettings(ImmutableSettings.settingsBuilder().loadFromSource(jsonBuilder()
            .startObject()
                .startObject("analysis")
                    .startObject("analyzer")
                        .startObject("custom_analyzer")
                            .field("type", "custom")
                            .field("tokenizer", "standard")
                            .field("filter", new String[]{ "lowercase"})
                        .endObject()
//                         .startObject("custom_analyzer2")
//                            .field("type", "custom")
//                            .field("tokenizer", "standard")
//                            .field("filter", new String[]{ "lowercase"})
//                            .field("char_filter", new String[]{ "html_strip"})
//                        .endObject()
                    .endObject()
                .endObject()
            .endObject().string()))
        .execute().actionGet();
	}
	
	
	@Test
	public void testMappings(){
		createIndexWithSettingsMappings(client);
		addDocument();
	}
	
	public static void createIndexWithSettingsMappings (Client client) {
//        String index = "krcon";

        //delete index if exists
        if (client.admin().indices().prepareExists(indexName).execute().actionGet().isExists())
            client.admin().indices().prepareDelete(indexName).execute().actionGet();
        
        
        try {
        	XContentBuilder mapping = setMappingAndSettings();
            
            CreateIndexResponse response = client.admin().indices().prepareCreate(indexName)
                .setSource(mapping)
                .execute().actionGet();
            
            System.out.println("Index created: " + response.isAcknowledged());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

	public static XContentBuilder setMappingAndSettings() throws IOException {
		System.out.println(Integer.MAX_VALUE);
		XContentBuilder mapping = XContentFactory.jsonBuilder().startObject()
		        .startObject("settings")
//		            .startObject("index")
//		                .field("number_of_shards", 1)
//		                .field("number_of_replicas", 0)
//	                .endObject()
		                .startObject("analysis")
		                    .startObject("analyzer")
		                        .startObject("custom_analyzer")
		                        	.field("type", "standard")
		                        .endObject()
		                        .startObject("custom_analyzer2")
		                           // .field("type", "custom")
		                            .field("tokenizer", "standard")
		                            .field("filter", new String[]{ "lowercase","trim"})
		                            .field("char_filter", new String[]{ "html_strip"})
		                        .endObject()
		                    .endObject()
		                .endObject()
		        .endObject()
		        .startObject("mappings")//mapping 정의 
		            .startObject("html")// type name
		                .startObject("properties")// properties 선언. 
		                    .startObject("breadcrumb") // field name
		                        .field("type", "string") // field type
		                        .field("index", "analyzed") // field analyzer
		                        .field("index_analyzer", "custom_analyzer") // field analyzer
		                    .endObject()
		                    .startObject("categoryDesc")
		                    	.field("type", "string") // field type
		                        .field("index", "analyzed") // field analyzer
		                        .field("index_analyzer", "custom_analyzer") // field analyzer
		                    .endObject()
		                    .startObject("categoryId")
		                    	.field("type", "integer") // field type
		                        .field("index", "analyzed") // field analyzer
		                    	.field("index_analyzer", "custom_analyzer") // field analyzer
		                    .endObject()
		                    .startObject("categoryTextId")
		                    	.field("type", "integer") // field type
                                    .field("index", "analyzed") // field analyzer
		                    .endObject()
		                    .startObject("categoryTitle")
		                    	.field("type", "string") // field type
		                        .field("index", "analyzed") // field analyzer
		                        .field("index_analyzer", "custom_analyzer") // field analyzer
		                    .endObject()
		                    .startObject("categoryTree")
		                    	.field("type", "string") // field type
		                        .field("index", "analyzed") // field analyzer
		                    .endObject()
		                    .startObject("localKey")
		                    	.field("type", "string") // field type
		                        .field("index", "not_analyzed") // field analyzer
		                    .endObject()
		                    .startObject("text")
		                    	.field("type", "string") // field type
//		                        .field("term_vector", "with_positions_offsets_payloads") // field type
//		                        .field("ignore_above", 256) // field type
		                        .field("index", "analyzed") // field analyzer
		                        .field("analyzer", "custom_analyzer2") // field analyzer
		                        .field("index_analyzer", "custom_analyzer2") // field analyzer
		                        
		                        
//								html strips filter로 해결 가능. 		                        
//		                        .startObject("fields")
//		                        	.startObject("html")
//		                        		.field("type", "string") // field type
//		                        		.field("ignore_above", 256) // field type
//		                        		.field("index", "not_analyzed") // field analyzer
//		                        		.field("analyzer", "custom_analyzer2") // field analyzer
//		                        		.field("index_analyzer", "custom_analyzer2") // field analyzer
//		                            .endObject()
//		                        .endObject()
		                    .endObject()
//		                    .startObject("html")
//                        		.field("type", "string") // field type
//                        		//.field("ignore_above", 256) // field type
//                        		.field("index", "not_analyzed") // field analyzer
//                        		.field("analyzer", "custom_analyzer") // field analyzer
//                        		.field("index_analyzer", "custom_analyzer") // field analyzer
//                            .endObject()
		                .endObject()
		            .endObject()
		        .endObject()
		    .endObject();
		
		return mapping;
	}
	
	public void addDocument(){
		
		BulkProcessor bp = BulkProcessor.builder(client, new BulkProcessor.Listener() {
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
            }
            
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                System.out.println("Bulk execution failed ["+  executionId + "].\n" +
                    failure.toString());
            }
            
            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                System.out.println("Bulk execution completed ["+  executionId + "].\n" +
                    "Took (ms): " + response.getTookInMillis() + "\n" +
                    "Failures: " + response.hasFailures() + "\n" + 
                    "Count: " + response.getItems().length);
            }
        })
        .setConcurrentRequests(4)
        .setBulkActions(100)//최대 1000개까지만 허용 한다. 
        .setBulkSize(new ByteSizeValue(-1))
        .build();
        
		
		
		
		
		URL url = this.getClass().getClassLoader().getResource("html/kr-con.json"); // 이부분 수정. 
		String path = url.getPath();
		File file = new File(path);
		
		
		JSONParser parser = new JSONParser();
		try {
			 
			Object obj = parser.parse(new FileReader(path));
	 
			JSONObject jsonObject = (JSONObject) obj;
	 
			
			Iterator<String> keys =  jsonObject.keySet().iterator();
			while(keys.hasNext()){
				String key = keys.next();
				JSONObject valueObj =  (JSONObject) jsonObject.get(key);
				String filepath = valueObj.get("FilePath").toString();
				String CATEGORY_TEXT_ID = valueObj.get("CATEGORY_TEXT_ID").toString();
				String breadcrumb = valueObj.get("Breadcrumb").toString();
				String CATEGORY_TREE = valueObj.get("CATEGORY_TREE").toString();
				String CATEGORY_ID = valueObj.get("CATEGORY_ID").toString();
				String LOCALE_KEY = valueObj.get("LOCALE_KEY").toString();
				String CATEGORY_TITLE = valueObj.get("CATEGORY_TITLE").toString();
				String CATEGORY_DESC = valueObj.get("CATEGORY_DESC").toString();
//				String filepath = valueObj.get("FilePath").toString();
//				String filepath = valueObj.get("FilePath").toString();
				
				
				
				
				
				url = this.getClass().getClassLoader().getResource("html/"+filepath); // 이부분 수정. 
				ArrayList<String> list = htmlParser.htmlParser(url.getPath());
				
				
				XContentBuilder source = XContentFactory.jsonBuilder().startObject()
                        .field("breadcrumb", breadcrumb)
                        .field("categoryDesc", CATEGORY_DESC)
						.field("categoryId", CATEGORY_ID)
						.field("categoryTextId", CATEGORY_TEXT_ID)
						.field("categoryTitle", CATEGORY_TITLE)
						.field("categoryTree", CATEGORY_TREE)
						.field("text", list.get(1))
//						.field("text.html", list.get(1))
						.field("localKey", LOCALE_KEY)
                    .endObject();
				bp.add(Requests.indexRequest(indexName).type("html").source(source));
				
			}
			
			
			
			
	 
		} catch (FileNotFoundException e) {
//			logger.error(e.getMessage());
		} catch (IOException e) {
//			logger.error(e.getMessage());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bp.close();
		
	}

}
