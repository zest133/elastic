package com.latis.krcon.html.write;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.xml.builders.SpanNearBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.SpanNearQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.FilterBuilders.*;


public class TestHtmlTotalSeacher {

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
	
	@Test
	public void andSearcher(){
		ArrayList<String> queryList = getQueryParser("boats pointed");
		/**
		 * {
			    "bool" : {
			        "must" : {
			            "term" : { "user" : "kimchy" }
			        },
			        "must_not" : {
			            "range" : {
			                "age" : { "from" : 10, "to" : 20 }
			            }
			        },
			        "should" : [
			            {
			                "term" : { "tag" : "wow" }
			            },
			            {
			                "term" : { "tag" : "elasticsearch" }
			            }
			        ],
			        "minimum_should_match" : 1,
			        "boost" : 1.0
			    }
			}
			
			
			QueryBuilder qb = boolQuery()
    .must(termQuery("content", "test1"))    
    .must(termQuery("content", "test4"))    
    .mustNot(termQuery("content", "test2")) 
    .should(termQuery("content", "test3")); 
		 */
		
		BoolQueryBuilder andBoolQuery = andQuery(queryList);
		System.out.println(andBoolQuery);
	
		queryList = getQueryParser("Certificated persons");
		//Certificated 
		BoolQueryBuilder orBoolQuery = orQuery(queryList);
		
		//suit designed
		SpanNearQueryBuilder snqb = getSpanNearQuery(getQueryParser("suit designed"));
		
		
		//must not 
		BoolQueryBuilder mustNotBoolQuery = orQuery(queryList);
		
		
		BoolQueryBuilder totalBoolQuery = boolQuery().must(andBoolQuery).must(orBoolQuery).must(snqb).mustNot(mustNotBoolQuery);
//		BoolQueryBuilder totalBoolQuery = boolQuery().must(andBoolQuery);
//		BoolQueryBuilder totalBoolQuery = boolQuery().must(snqb);
		
		
		SearchResponse scrollResp = client.prepareSearch("krcon")
				.setTypes("html")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.addHighlightedField("text")
				.setHighlighterFragmentSize(-1)//set it larger than the size of the field so that the only one fragment is returned and it contains the entire text of the field
				.setHighlighterPreTags("<test class='boat'>")
				.setHighlighterPostTags("</test>")
				.setQuery(totalBoolQuery) .setFrom(0).setSize(10).execute().actionGet(); // 100 hits
		
		
		SearchHit[] results = scrollResp.getHits().getHits();

        System.out.println("Current results: " + results.length);
        for (SearchHit hit : results) {
            System.out.println("------------------------------");
            Map<String,Object> result = hit.getSource();
            System.out.println(result.get("categoryId"));
//            System.out.println(result);
            System.out.println("------------------------------1111111");
            System.out.println(hit.highlightFields().get("text"));
        }
		
		
	}

	public SpanNearQueryBuilder getSpanNearQuery(ArrayList<String> queryList) {
		SpanNearQueryBuilder snqb = spanNearQuery();
		for(String query : queryList){
			snqb =  snqb.clause(spanTermQuery("text", "suit")).clause(spanTermQuery("text", "designed"));
		}
		snqb.slop(0).inOrder(true);
		System.out.println(snqb.toString());
		return snqb;
	}

	public BoolQueryBuilder orQuery(ArrayList<String> queryList) {
		BoolQueryBuilder orBoolQuery = null;
		for(String query : queryList){
			if(orBoolQuery == null){
				orBoolQuery = boolQuery().should(prefixQuery("text", query));
			}else{
				orBoolQuery = orBoolQuery.should(prefixQuery("text", query));
			}
		}
		return orBoolQuery;
	}

	public BoolQueryBuilder andQuery(ArrayList<String> queryList) {
		BoolQueryBuilder andBoolQuery = null;
		for(String query : queryList){
			if(andBoolQuery == null){
				andBoolQuery = boolQuery().must(prefixQuery("text", query));
			}else{
				andBoolQuery = andBoolQuery.must(prefixQuery("text", query));
			}
		}
		return andBoolQuery;
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
