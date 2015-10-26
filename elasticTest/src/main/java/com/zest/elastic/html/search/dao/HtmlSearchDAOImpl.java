package com.zest.elastic.html.search.dao;

import static org.elasticsearch.index.query.FilterBuilders.andFilter;
import static org.elasticsearch.index.query.FilterBuilders.prefixFilter;
import static org.elasticsearch.index.query.FilterBuilders.termFilter;
import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.prefixQuery;
import static org.elasticsearch.index.query.QueryBuilders.spanNearQuery;
import static org.elasticsearch.index.query.QueryBuilders.spanTermQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SpanNearQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.zest.elastic.filter.dto.FilterDTO;
import com.zest.elastic.html.search.dto.SearchDTO;
import com.zest.elastic.html.search.dto.SearchResultDTO;

public class HtmlSearchDAOImpl implements HtmlSearchDAO {

	private static final Logger logger = LoggerFactory
			.getLogger(HtmlSearchDAOImpl.class);

	@Value("${clusterName}")
	private String clusterName;
	
	@Value("${host}")
	private String host;
	
	@Value("${typeName}")
	private String typeName;
	
	@Value("${port}")
	private int port;
	
	@Value("${indexName}")
	private String indexName;
	
	@Value("${categoryTitleField}")
	private String categoryTitleField;
	
	@Value("${localeField}")
	private String localeField;
	
	@Value("${breadcrumbField}")
	private String breadcrumbField;
	
	@Value("${textField}")
	private String textFieldName;
	
	@Value("${categoryTreeField}")
	private String categoryTreeField;
	
	
	@Value("${highlightStartTag}")
	private String highlightStartTag;
	
	@Value("${highlightEndTag}")
	private String highlightEndTag;
	
	
	
	@Autowired
	private FilterDTO filterDTO;
	
	public Client client;

	
	private long totalhits;
	private ArrayList<String> stopList;
	
	
	public HtmlSearchDAOImpl(){
		stopList = new ArrayList<String>();
	}
	
	
	public void init() {
		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", clusterName)
				.put("client.transport.sniff", true).build();

		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(host, port));
	}
	
	
	@Override
	public ArrayList<SearchResultDTO> advSearch(SearchDTO searchDTO,int currentStartFrom) {
		// TODO Auto-generated method stub
		ArrayList<SearchResultDTO> returnList = new ArrayList<SearchResultDTO>();
		
		ArrayList<String> queryList = new ArrayList<String>();
		BoolQueryBuilder andBoolQuery = null;
		BoolQueryBuilder orBoolQuery = null;
		SpanNearQueryBuilder snqb = null;
		BoolQueryBuilder mustNotBoolQuery = null;
		
		BoolQueryBuilder totalBoolQuery = null;
		
		if(searchDTO.getAndWordSearch() != null && !searchDTO.getAndWordSearch().equals("") ){
			queryList = getQueryList(searchDTO.getAndWordSearch(), queryList);
			andBoolQuery = andQuery(queryList);
			totalBoolQuery = boolQuery().must(andBoolQuery);
			
		}
		
		if(searchDTO.getOrWordSearch() != null && !searchDTO.getOrWordSearch().equals("") ){
			queryList = getQueryList(searchDTO.getOrWordSearch(), queryList);
			orBoolQuery = orQuery(queryList);
			totalBoolQuery = totalBoolQuery.must(orBoolQuery);
		}
		
		if(searchDTO.getExactWordSearch() != null && !searchDTO.getExactWordSearch().equals("") ){
			snqb = getSpanNearQuery(searchDTO.getExactWordSearch().toLowerCase().split(" "));
			totalBoolQuery = totalBoolQuery.must(snqb);
		}
	
		if(searchDTO.getNotWordSearch() != null && !searchDTO.getNotWordSearch().equals("") ){
			queryList = getQueryList(searchDTO.getNotWordSearch(), queryList);
			mustNotBoolQuery = orQuery(queryList);
			totalBoolQuery = totalBoolQuery.mustNot(mustNotBoolQuery);
		}
		
		
		if(totalBoolQuery != null){
			
			//breadcrumb
			AndFilterBuilder fb = this.getFilter(searchDTO.getCategoryTitle(), searchDTO.getBreadcrumb(),searchDTO.getLocale());
			
			init();
			
			SearchResponse scrollResp = client.prepareSearch("krcon2")
					.setTypes("html")
					.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//				.setScroll(new TimeValue(60000))
					.addHighlightedField("text")
					.setHighlighterFragmentSize(200)//set it larger than the size of the field so that the only one fragment is returned and it contains the entire text of the field
					.setHighlighterNumOfFragments(1)
					.setHighlighterPreTags(this.highlightStartTag)
					.setHighlighterPostTags(highlightEndTag)
					.setQuery(totalBoolQuery)
					.setPostFilter(fb)
					.setFrom(currentStartFrom).setSize(12).execute().actionGet(); // 100 hits
			
			
			
			
			SearchHit[] results = scrollResp.getHits().getHits();
			this.totalhits = scrollResp.getHits().totalHits();
			System.out.println("Total Hit===>"+scrollResp.getHits().getTotalHits());
			System.out.println("Current results: " + results.length);
			for (SearchHit hit : results) {
				Map<String,Object> result = hit.getSource();
				SearchResultDTO dto = new SearchResultDTO();
				dto.setCategoryTree(result.get("categoryTree").toString());
				dto.setTitle(result.get("categoryTitle").toString());
				
				Text[] text = hit.highlightFields().get("text").getFragments();
				System.out.println(text[0].string());
				dto.setHtmlText(text[0].string());
				dto.setBreadcrumbs(result.get("breadcrumb").toString());
				
				
				Random random = new Random();
				
				dto.setRank(random.nextInt(100));
				dto.setView(random.nextInt(50));
				
				returnList.add(dto);
			}
			
			destory();
		}
		
		
		
		return returnList;
	}


	public ArrayList<String> getQueryList(String query,
			ArrayList<String> queryList) {
		queryList.addAll(getQueryParser(query.toLowerCase()));
		return queryList;
	}
	
	
	
	@Override
	public FilterDTO getSearchFilterOption() {
		// TODO Auto-generated method stub
		// String breadcrumbs = doc.get(breadcrumbField);
		//
		// breadcrumbs = breadcrumbs.replace("KRCON/KR-CON (English)/", "");

		int totalCount  = (int) getTotalCount();
		
		init();
		
		QueryBuilder q = QueryBuilders.matchAllQuery();
		SearchResponse scrollResp = client
				.prepareSearch(indexName)
				.setTypes(typeName)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(q)
				.addSort(categoryTreeField, SortOrder.ASC).setSize(totalCount)
				.execute().actionGet(); // 100 hits
		
		
		SearchHit[] results = scrollResp.getHits().getHits();

		ArrayList<String> titleList = new ArrayList<String>();
		ArrayList<String> categoryList = new ArrayList<String>();
		ArrayList<String> localeList = new ArrayList<String>();
		for (SearchHit hit : results) {
			Map<String, Object> result = hit.getSource();
			
			String breadcrumbs = result.get(breadcrumbField).toString();
			breadcrumbs = breadcrumbs.replace("KRCON/KR-CON (English)/", "");
			categoryList.add(breadcrumbs);
			titleList.add(result.get(categoryTitleField).toString());
			localeList.add("EN");
		}

		filterDTO.setBreadcrumbsFilter(titleList);
		filterDTO.setTitleFilter(categoryList);
		filterDTO.setLocaleFilter(localeList);
		destory();
		
		return filterDTO;
	}



	public long getTotalCount() {
		init();
		
		QueryBuilder q = QueryBuilders.matchAllQuery();
		SearchResponse scrollResp = client
				.prepareSearch(indexName)
				.setTypes(typeName)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH).setQuery(q)
				.addSort(categoryTreeField, SortOrder.ASC).setSize(1000)
				.execute().actionGet(); // 100 hits
		destory();
		return scrollResp.getHits().getTotalHits();
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub
		client.close();
	}
	
	
	public AndFilterBuilder getFilter( String categoryTitleSearchWord, 
			 String breadcrumbSearchWord,  String localKeySearchWord) {
		AndFilterBuilder fb = null;
		if(categoryTitleSearchWord != null && !categoryTitleSearchWord.equals("")){
			fb = andFilter(termFilter(categoryTitleField, categoryTitleSearchWord));
			if(breadcrumbSearchWord != null && !breadcrumbSearchWord.equals("")){
				fb = fb.add(prefixFilter(breadcrumbField, breadcrumbSearchWord));
			}
			if(localKeySearchWord != null && !localKeySearchWord.equals("")){
				fb = fb.add(termFilter(localeField, localKeySearchWord));
			}
			
		}
		return fb;
	}

	public SpanNearQueryBuilder getSpanNearQuery(String[] queryArray) {
		SpanNearQueryBuilder snqb = spanNearQuery();
		for(String query : queryArray){
			snqb =  snqb.clause(spanTermQuery(textFieldName, query));
		}
		snqb.slop(0).inOrder(true);
//		System.out.println(snqb.toString());
		return snqb;
	}

	public BoolQueryBuilder orQuery(ArrayList<String> queryList) {
		BoolQueryBuilder orBoolQuery = null;
		for(String query : queryList){
			if(orBoolQuery == null){
				orBoolQuery = boolQuery().should(prefixQuery(textFieldName, query));
			}else{
				orBoolQuery = orBoolQuery.should(prefixQuery(textFieldName, query));
			}
		}
		return orBoolQuery;
	}

	public BoolQueryBuilder andQuery(ArrayList<String> queryList) {
		BoolQueryBuilder andBoolQuery = null;
		for(String query : queryList){
			if(andBoolQuery == null){
				andBoolQuery = boolQuery().must(prefixQuery(textFieldName, query));
			}else{
				andBoolQuery = andBoolQuery.must(prefixQuery(textFieldName, query));
			}
		}
		return andBoolQuery;
	}
	
	public ArrayList<String> getQueryParser(
			String queryStr)  {
		EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
		ArrayList<String> termList = new ArrayList<String>();
		try {
			
			HashSet<Term> terms = new HashSet<Term>();
			new QueryParser( textFieldName,
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
	
	public String getQueryString(SearchDTO searchDTO) {
		StringBuilder builder = new StringBuilder();

		if (searchDTO.getAndWordSearch() != null
				&& !searchDTO.getAndWordSearch().equals("")) {

			builder.append("(");
			String[] words = searchDTO.getAndWordSearch().split(" ");
			for (int i = 0; i < words.length; i++) {
				String word = words[i];
				builder.append(word);
				if (i != words.length - 1) {
					builder.append(" <span class='queryRegex'>AND</span> ");
				}
			}
			builder.append(")");
		}

		if (searchDTO.getOrWordSearch() != null
				&& !searchDTO.getOrWordSearch().equals("")) {

			if (!builder.toString().equals("")) {
				builder.append(" <span class='queryRegex'>AND</span>");
			}

			builder.append("(");
			String[] words = searchDTO.getOrWordSearch().split(" ");
			for (int i = 0; i < words.length; i++) {
				String word = words[i];
				builder.append(word);
				if (i != words.length - 1) {
					builder.append(" <span class='queryRegex'>OR</span> ");
				}
			}
			builder.append(")");
		}

		if (searchDTO.getExactWordSearch() != null
				&& !searchDTO.getExactWordSearch().equals("")) {

			if (!builder.toString().equals("")) {
				builder.append(" <span class='queryRegex'>AND</span>");
			}

			builder.append("(\"");
			builder.append(searchDTO.getExactWordSearch());
			builder.append("\")");
		}

		if (searchDTO.getNotWordSearch() != null
				&& !searchDTO.getNotWordSearch().equals("")) {
			builder.append(" <span class='queryRegex'>NOT</span>(");
			String[] words = searchDTO.getNotWordSearch().split(" ");
			for (int i = 0; i < words.length; i++) {
				String word = words[i];
				builder.append(word);
				if (i != words.length - 1) {
					builder.append(" <span class='queryRegex'>AND</span> ");
				}
			}
			builder.append(")");
		}

		return builder.toString();
	}


	@Override
	public long totalHitLength() {
		// TODO Auto-generated method stub
		return this.totalhits;
	}
	
	
	public ArrayList<String> compareStopWord(SearchDTO searchDTO) {
		ArrayList<String> stopList = new ArrayList<String>();

		CharArraySet temp = (CharArraySet) StopAnalyzer.ENGLISH_STOP_WORDS_SET;
		
		if (searchDTO.getAndWordSearch() != null
				&& !searchDTO.getAndWordSearch().equals("")) {
			StringBuilder buffer = new StringBuilder();
			String[] queryArr = searchDTO.getAndWordSearch().split("\\ ");
			for (String str : queryArr) {
				if (temp.contains(str.toLowerCase())) {
					if (!stopList.contains(str)) {
						stopList.add(str);
					}
				}else{
					buffer.append(str).append(" ");
				}
			}
			searchDTO.setAndWordSearch(buffer.toString().trim());

		}

		if (searchDTO.getOrWordSearch() != null
				&& !searchDTO.getOrWordSearch().equals("")) {
			String[] queryArr = searchDTO.getOrWordSearch().split("\\ ");
			StringBuilder buffer = new StringBuilder();
			for (String str : queryArr) {
				if (temp.contains(str.toLowerCase())) {
					searchDTO.setOrWordSearch(searchDTO.getOrWordSearch().replaceAll(str, "").trim());
					if (!stopList.contains(str)) {
						stopList.add(str);
					}
				}else{
					buffer.append(str).append(" ");
				}
			}
			searchDTO.setOrWordSearch(buffer.toString().trim());
		}

		if (searchDTO.getExactWordSearch() != null
				&& !searchDTO.getExactWordSearch().equals("")) {
			String[] queryArr = searchDTO.getExactWordSearch().split("\\ ");
			StringBuilder buffer = new StringBuilder();
			for (String str : queryArr) {
				if (temp.contains(str.toLowerCase())) {
					searchDTO.setExactWordSearch(searchDTO.getExactWordSearch().replaceAll(str, "").trim());
					if (!stopList.contains(str)) {
						stopList.add(str);
					}
				}else{
					buffer.append(str).append(" ");
				}
			}
			searchDTO.setExactWordSearch(buffer.toString().trim());
		}

		if (searchDTO.getNotWordSearch() != null
				&& !searchDTO.getNotWordSearch().equals("")) {
			String[] queryArr = searchDTO.getNotWordSearch().split("\\ ");
			StringBuilder buffer = new StringBuilder();
			for (String str : queryArr) {
				if (temp.contains(str.toLowerCase())) {
					searchDTO.setNotWordSearch(searchDTO.getNotWordSearch().replaceAll(str, "").trim());
					if (!stopList.contains(str)) {
						stopList.add(str);
					}
				}else{
					buffer.append(str).append(" ");
				}
			}
			searchDTO.setNotWordSearch(buffer.toString().trim());
		}

		if (stopList.size() == 0) {
			stopList = null;
		}

		return stopList;
	}
	
	


	public ArrayList<String> getStopList() {
		return stopList;
	}

}
