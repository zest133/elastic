package com.zest.elastic.html.category.search;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.wildcardQuery;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.zest.elastic.filter.dto.FilterDTO;

import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.index.query.FilterBuilders.*;

public class CategorySearch {
	private static final Logger logger = LoggerFactory
			.getLogger(CategorySearch.class);

	@Value("${clusterName}")
	private String clusterName;

	@Value("${host}")
	private String host;

	@Value("${port}")
	private int port;

	@Value("${indexName}")
	private String indexName;

	@Value("${typeName}")
	private String typeName;

	@Autowired
	private FilterDTO filterDTO;

	public Client client;

	private String searchWord;

	@Value("${categoryTreeField}")
	private String categoryTreeField;

	@Value("${anonymousData}")
	private String anonymousData;

	
//	highlightStartTag=<span class=\"highlight\">
//			highlightEndTag=</span>
	
	@Value("${highlightStartTag}")
	private String highlightStartTag;
	
	@Value("${highlightEndTag}")
	private String highlightEndTag;
	
	
	public CategorySearch() {

	}

	public void init() {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName)
				.put("client.transport.sniff", true).build();

		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(host, port));
	}

	// public ArrayList<Document> search() {
	// return categorySearchData(categoryTreeField, searchWord);
	// }
	//
	// public ArrayList<Document> currentSearch(String currentCategoryTree) {
	// return categorySearchData(categoryTreeField, currentCategoryTree);
	// }

	// public ArrayList<Document> categoryAllSearchData() {
	// Query allCategoryQuery = new MatchAllDocsQuery();
	// htmlSort.addSortList(new SortField(categoryTreeField, SortField.STRING));
	//
	// ArrayList<Document> list = null;
	// try {
	// TopDocs hits = searcher.search(allCategoryQuery, searcher.maxDoc(),
	// htmlSort.getSort());
	// list = getDocumentList(searcher, hits, categoryTreeField);
	//
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// logger.error(e.getMessage());
	// }
	// return list;
	// }

	// public ArrayList<Document> categorySubTreeSearchData() {
	// String searchWord = this.searchWord + anonymousData;
	// return categorySearchData(categoryTreeField, searchWord);
	// }

	public SearchHit[] getRootSearchData( String searchWord) {

		init();

		QueryBuilder q = matchQuery(categoryTreeField, searchWord);

		SearchResponse scrollResp = client.prepareSearch(indexName)
				.setTypes(typeName)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(q)
				.addSort("categoryTree", SortOrder.ASC).setSize(100).execute()
				.actionGet(); // 100 hits

		SearchHit[] results = scrollResp.getHits().getHits();

		client.close();

		return results;
	}

	// @SuppressWarnings("unchecked")
	public void checkSubCategory(String categoryTree, JSONObject jsonObject) {
		// init();

		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName)
				.put("client.transport.sniff", true).build();

		Client checkSubCategoryClient = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(host, port));

		QueryBuilder q = wildcardQuery("categoryTree", categoryTree
				+ anonymousData);

		SearchResponse scrollResp = checkSubCategoryClient
				.prepareSearch(indexName).setTypes(typeName)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(q)
				.addSort("categoryTree", SortOrder.ASC).setFrom(0).setSize(1)
				.execute().actionGet(); // 100 hits

		// SearchHit[] results = scrollResp.getHits().getHits();
		if (scrollResp.getHits().getTotalHits() > 0) {
			jsonObject.put("isFolder", "true");
			jsonObject.put("isLazy", "true");
		}
		checkSubCategoryClient.close();
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public SearchHit[] getSubSearchData(String selectedCategoryTree) {
		// TODO Auto-generated method stub

		init();

		QueryBuilder q = wildcardQuery(categoryTreeField, selectedCategoryTree+anonymousData);

		SearchResponse scrollResp = client.prepareSearch(indexName)
				.setTypes(typeName)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(q)
				.addSort("categoryTree", SortOrder.ASC).setSize(100).execute()
				.actionGet(); // 100 hits

		SearchHit[] results = scrollResp.getHits().getHits();

		client.close();

		return results;

	}

	public String getCurrentCategoryHTML(String categoryTree,
			String highlightQuery) {
		// TODO Auto-generated method stub
		init();

		QueryBuilder q = matchQuery(categoryTreeField, categoryTree);
		SearchResponse scrollResp =  null;
		if(highlightQuery.equals("")){
			scrollResp = client.prepareSearch(indexName)
					.setTypes(typeName)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(q)
					.setSize(1).execute()
					.actionGet(); // 100 hits
			SearchHit[] results = scrollResp.getHits().getHits();
			if(results.length >0){
				return results[0].getSource().get("text.html").toString();
				
			}
		}else{
			scrollResp = client.prepareSearch(indexName)
					.setTypes(typeName)
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(q)
					.addHighlightedField("text")
					.setHighlighterFragmentSize(-1)
					.setHighlighterPreTags(highlightStartTag)
					.setHighlighterPostTags(highlightEndTag)
					.setSize(1).execute()
					.actionGet(); // 100 hits
			SearchHit[] results = scrollResp.getHits().getHits();
			if(results.length >0){
				return results[0].getSource().get("text").toString();
				
			}
		}
		client.close();
		return null;
	}

	// public void destroy(){
	// client.close();
	// }
}
