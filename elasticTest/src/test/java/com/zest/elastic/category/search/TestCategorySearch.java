package com.zest.elastic.category.search;

import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zest.elastic.filter.dto.FilterDTO;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.FilterBuilders.*;


@ContextConfiguration(locations={
"file:src/main/webapp/WEB-INF/spring/root-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class TestCategorySearch {

	
	@Value("${clusterName}")
	private String clusterName;
	
	@Value("${host}")
	private String host;
	
	@Value("${port}")
	private int port;
	
	@Value("${indexName}")
	private String indexName;
	
	
	@Autowired
	private FilterDTO filterDTO;
	
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
//	public void searchFilter(){
//		//search
//        QueryBuilder q = QueryBuilders.matchAllQuery();
//		SearchResponse scrollResp = client.prepareSearch("krcon")
//				.setTypes("html")
//				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//				.setQuery(q).addSort("categoryTree", SortOrder.ASC) .setFrom(0).setSize(10).execute().actionGet(); // 100 hits
//		
//		
//		SearchHit[] results = scrollResp.getHits().getHits();
//
//        System.out.println("Current results: " + results.length);
//        ArrayList<String> titleList =  filterDTO.getTitleFilter();
//        ArrayList<String> categoryList =  filterDTO.getBreadcrumbsFilter();
//        ArrayList<String> localeList =  filterDTO.getLocaleFilter();
//        for (SearchHit hit : results) {
//            System.out.println("------------------------------");
//            Map<String,Object> result = hit.getSource();
//            System.out.println(result.get("breadcrumb"));
//            categoryList.add(result.get("breadcrumb").toString());
//            titleList.add(result.get("categoryTitle").toString());
//            localeList.add(result.get("localKey").toString());
//            System.out.println("------------------------------1111111");
//        }
//	}
	
	@Test
	public void subSearchCategory(){
		//search
        QueryBuilder q = wildcardQuery("categoryTree", "0000.00e0.1530.????");
        
		SearchResponse scrollResp = client.prepareSearch("krcon")
				.setTypes("html")
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(q).addSort("categoryTree", SortOrder.ASC).setSize(100) .execute().actionGet(); // 100 hits
		
		
		SearchHit[] results = scrollResp.getHits().getHits();

        System.out.println("Current results: " + results.length);
        ArrayList<String> titleList =  filterDTO.getTitleFilter();
        ArrayList<String> categoryList =  filterDTO.getBreadcrumbsFilter();
        ArrayList<String> localeList =  filterDTO.getLocaleFilter();
        for (SearchHit hit : results) {
            System.out.println("------------------------------");
            Map<String,Object> result = hit.getSource();
            System.out.println(result.get("categoryTitle"));
            System.out.println("------------------------------1111111");
        }
	}
	
//	@Test
//	public void rootSearchCategory(){
//		//search
//        QueryBuilder q = matchQuery("categoryTree", "0000.00e0.1530");
//        
//		SearchResponse scrollResp = client.prepareSearch("krcon")
//				.setTypes("html")
//				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//				.setQuery(q).addSort("categoryTree", SortOrder.ASC) .setFrom(0).setSize(10).execute().actionGet(); // 100 hits
//		
//		
//		SearchHit[] results = scrollResp.getHits().getHits();
//
//        System.out.println("Current results: " + results.length);
//        ArrayList<String> titleList =  filterDTO.getTitleFilter();
//        ArrayList<String> categoryList =  filterDTO.getBreadcrumbsFilter();
//        ArrayList<String> localeList =  filterDTO.getLocaleFilter();
//        for (SearchHit hit : results) {
//            System.out.println("------------------------------");
//            Map<String,Object> result = hit.getSource();
//            System.out.println(result.get("categoryTitle"));
//            System.out.println("------------------------------1111111");
//        }
//	}

	
}
