package com.zest.elastic.html.search;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.FilterBuilders.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestHtmlSearcher {

	public final static String clusterName = "es_test";
	public final static String host = "192.168.0.105";
	public final static int port = 9300;
	public final static String indexName = "krcon";

	public Client client;

	@Before
	public void setup() {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName)
				.put("client.transport.sniff", true).build();

		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(host, port));
	}

	@After
	public void destory() {
		client.close();
	}

//	@Test
//	public void searchMatch() {
//		QueryBuilder qb = prefixQuery("text", "boat");
////		SearchResponse response = client.prepareSearch("krcon")
////				.setTypes("html")
////				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(qb) // Query
////				// .setPostFilter(FilterBuilders.rangeFilter("age").from(12).to(18))
////				// // Filter
////				// .setFrom(0).setSize(60).setExplain(true)
////				.execute().actionGet();
////		System.out.println(response.getHits().getTotalHits());
//
//		SearchResponse scrollResp = client.prepareSearch("krcon")
//				.setTypes("html")
//				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
////				.setScroll(new TimeValue(60000))
//				.setQuery(qb) .setFrom(0).setSize(10).execute().actionGet(); // 100 hits
//																	// per shard
//																	// will be
//																	// returned
//																	// for each
//																	// scroll
//		// Scroll until no hits are returned
//		int count = 0;
//		
//		while (true) {
//
//			for (SearchHit hit : scrollResp.getHits().getHits()) {
//				// Handle the hit...
////				System.out.println("getIndex====>"+hit.getIndex());
////				System.out.println(hit.getFields());
//				System.out.println(hit.getSource().get("text"));
//				System.out.println(hit.getSource().get("text.html"));
//				count++;
//				System.out.println(count);
//			}
//			scrollResp = client.prepareSearchScroll(scrollResp.getScrollId())
//					.setScroll(new TimeValue(600000)).execute().actionGet();
//			// Break condition: No hits are returned
//			if (scrollResp.getHits().getHits().length == 0) {
//				break;
//			}
//		}
//
//	}
	
//	@Test
//	public void searchAndHighlight(){
//		QueryBuilder qb = prefixQuery("text", "boat");
//		SearchResponse scrollResp = client.prepareSearch("krcon")
//				.setTypes("html")
//				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
////				.setScroll(new TimeValue(60000))
//				.addHighlightedField("text")
//				.setHighlighterFragmentSize(-1)//set it larger than the size of the field so that the only one fragment is returned and it contains the entire text of the field
////				.setHighlighterNumOfFragments(1)
//				.setHighlighterPreTags("<test class='boat'>")
//				.setHighlighterPostTags("</test>")
//				.setQuery(qb) .setFrom(0).setSize(10).execute().actionGet(); // 100 hits
//		
//		
//		SearchHit[] results = scrollResp.getHits().getHits();
//
//        System.out.println("Current results: " + results.length);
//        for (SearchHit hit : results) {
//            System.out.println("------------------------------");
//            Map<String,Object> result = hit.getSource();
//            System.out.println(result.get("categoryId"));
////            System.out.println(result);
//            System.out.println("------------------------------1111111");
//            System.out.println(hit.highlightFields().get("text"));
//        }
//	}
	
	
	
	
	
	
	
	
	
	
	@Test
	public void baseWordSearch(){

		ArrayList<String> test = getQueryParser("boats complete");
		
		System.out.println("Dddd\t"+test.toString());

	}

	public static ArrayList<String> getQueryParser(
			String queryStr)  {
		EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
		ArrayList<String> termList = new ArrayList<String>();
		try {
			
			HashSet<Term> terms = new HashSet<Term>();
			new QueryParser( "text",
					englishAnalyzer).parse(queryStr).extractTerms(terms);
					
			Iterator<Term> term =  terms.iterator();
			while(term.hasNext()){
				Term temp = term.next();
				termList.add(temp.text());
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // #B
		return termList;
	}
	
	
	
	
}
