package com.zest.elastic;


import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zest.elastic.html.search.dao.HtmlSearchDAO;
import com.zest.elastic.html.search.dto.SearchDTO;
import com.zest.elastic.html.search.dto.SearchResultDTO;


@Controller
public class SearchController {
	
	@Autowired
	public HtmlSearchDAO htmlSearchDAO;
	
//	@Autowired
//	public SynonymSearchDAO synonumSearchDAO;
	
	@RequestMapping(value = "/search.do", method = RequestMethod.POST)
	public String search(
			Model model,
			@RequestParam String searchAND,
			@RequestParam String searchOR, 
			@RequestParam String searchExact, 
			@RequestParam String searchNON,
			@RequestParam String filterBreradcrumbsList,
			@RequestParam String filterTitleList,
			@RequestParam String filterLocaleList,
			@RequestParam String pageNum,
			@RequestParam String totalCount
			){
		
		SearchDTO dto = new SearchDTO();
		
		dto.setAndWordSearch(searchAND);
		dto.setOrWordSearch(searchOR);
		
//		if(!searchExact.equals("")){
//			searchExact = "\"" + searchExact + "\"";
//		}
		
		dto.setExactWordSearch(searchExact);
		dto.setNotWordSearch(searchNON);		
		dto.setBreadcrumb(filterBreradcrumbsList);
		dto.setCategoryTitle(filterTitleList);
		dto.setLocale(filterLocaleList);		
		dto.setPageNum(Integer.parseInt(pageNum));		
		dto.setTotalCount(Integer.parseInt(totalCount));
		ArrayList<String> stopList = htmlSearchDAO.compareStopWord(dto);
		
		
		int page = Integer.parseInt(pageNum);
		if(page >0){
//			page = page -1 ;
			page = page * 12;
		}
		
		ArrayList<SearchResultDTO> searchResult = htmlSearchDAO.advSearch(dto,page);
		
		model.addAttribute("searchKeyword", htmlSearchDAO.getQueryString(dto));
		if(Integer.parseInt(pageNum) == 0){
			model.addAttribute("resultSize", htmlSearchDAO.totalHitLength());
		}else{
			model.addAttribute("resultSize", searchResult.size());
		}
		model.addAttribute("searchResult", searchResult);
		model.addAttribute("stopWord", stopList);
//		model.addAttribute("synonym", synonumSearchDAO.checkSynonymWord(searchAND));
		model.addAttribute("pageNum", pageNum);
//		model.addAttribute("highlightQuery", htmlSearchDAO.highlightQuery());
				
		return "searchResult";
		
	}
	
}
