package com.zest.elastic.html.category.search.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.elasticsearch.search.SearchHit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.zest.elastic.html.category.search.CategorySearch;

public class CategorySearchDAOImpl implements CategorySearchDAO {

	private static final Logger logger = LoggerFactory
			.getLogger(CategorySearchDAOImpl.class);

	@Value("${rootCategoryTreeName}")
	private String rootCategoryTreeName;

	@Value("${categoryTreeField}")
	private String categoryTreeField;

	@Autowired
	CategorySearch categorySearch;

	public CategorySearchDAOImpl() {
	}

	@Override
	public JSONArray getRootCategory() {
		JSONArray returnArray = null;

		SearchHit[] searchHit = categorySearch
				.getRootSearchData(rootCategoryTreeName);

		returnArray = convertJsonArray(searchHit);

		return returnArray;
	}

	@Override
	public JSONArray getSubCategory(String selectedCategoryTree) {
		JSONArray returnArray = null;

		SearchHit[] searchHit = categorySearch
				.getSubSearchData(selectedCategoryTree);

		returnArray = convertJsonArray(searchHit);

		// try {
		// categorySearch.init();
		// categorySearch.setSearchWord(selectedCategoryTree);
		// ArrayList<Document> list = categorySearch
		// .categorySubTreeSearchData();
		// returnArray = convertJsonArray(list);
		// } catch (Exception e) {
		// logger.error(e.getMessage());
		// }

		return returnArray;
	}

	// @Override
	// public String getCurrentCategoryHTML(String selectedCategoryTree,
	// String highlightQuery) {
	// String returnVal = "";
	// try {
	// categorySearch.init();
	// ArrayList<Document> list = categorySearch
	// .currentSearch(selectedCategoryTree);
	//
	// returnVal = convertHtmlText(list, highlightQuery);
	// } catch (IOException e) {
	// logger.error(e.getMessage());
	// }
	//
	// return returnVal;
	// }

	@SuppressWarnings("unchecked")
	public JSONArray convertJsonArray(SearchHit[] searchHit) {
		JSONArray array = new JSONArray();
		try {

			for (SearchHit hit : searchHit) {

				JSONObject jsonObject = new JSONObject();
				Map<String, Object> result = hit.getSource();
				String categoryTree = result.get("categoryTree").toString();
				jsonObject.put("key", categoryTree);
				jsonObject.put("categoryTree", categoryTree);
				jsonObject.put("title", result.get("categoryTitle").toString());
				categorySearch.checkSubCategory(categoryTree, jsonObject);

				array.add(jsonObject);

			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return array;
	}

	@Override
	public String buildCategoryPath(String categoryTreeId) {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		buffer.append(rootCategoryTreeName);

		String categoryTree = "";

		if (!categoryTreeId.equals(rootCategoryTreeName)) {
			String solasId = categoryTreeId.substring(rootCategoryTreeName
					.length() + 1);
			String[] ids = solasId.split("\\.");

			String subCategory = "";

			for (String id : ids) {
				if (subCategory.equals("")) {
					subCategory = subCategory + id;
				} else {
					subCategory = subCategory + "." + id;
				}

				buffer.append("/").append(rootCategoryTreeName).append(".")
						.append(subCategory);
			}

			categoryTree = buffer.toString();
		} else {
			categoryTree = rootCategoryTreeName;
		}
		return categoryTree;
	}
	

	@Override
	public String getCurrentCategoryHTML(String categoryTree,
			String highlightQuery) {
		// TODO Auto-generated method stub
		return categorySearch
				.getCurrentCategoryHTML(categoryTree,highlightQuery);
	}

	// public String convertHtmlText(ArrayList<Document> list,
	// String highlightQuery) {
	// String html = list.get(0).get(htmlField).trim();
	// // return html;
	// if (highlightQuery.equals("")) {
	// return html;
	// } else {
	// return htmlHighlight.htmlHighlight(html,
	// htmlHighlight.buildPatternString(highlightQuery));
	// }
	// }

	// @Override
	// public String buildCategoryPath(String categoryTree) {
	// return englishHtmlSearch.buildCategoryTreePath(categoryTree);
	// }
}
