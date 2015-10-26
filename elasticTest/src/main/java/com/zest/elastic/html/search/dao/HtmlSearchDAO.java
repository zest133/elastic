package com.zest.elastic.html.search.dao;

import java.util.ArrayList;

import com.zest.elastic.filter.dto.FilterDTO;
import com.zest.elastic.html.search.dto.SearchDTO;
import com.zest.elastic.html.search.dto.SearchResultDTO;


public interface HtmlSearchDAO {
	
	public FilterDTO getSearchFilterOption();
	public ArrayList<SearchResultDTO> advSearch(SearchDTO searchDTO,int currentStartFrom);
	public String getQueryString(SearchDTO searchDTO);
	public long totalHitLength();
	public ArrayList<String> compareStopWord(SearchDTO searchDTO);
	public void destory();
}
