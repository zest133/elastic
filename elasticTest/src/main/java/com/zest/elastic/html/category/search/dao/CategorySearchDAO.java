package com.zest.elastic.html.category.search.dao;

import org.json.simple.JSONArray;

public interface CategorySearchDAO {
	public JSONArray getRootCategory();
	public JSONArray getSubCategory(String selectedCategoryTree);
	public String getCurrentCategoryHTML(String categoryTree, String highlightQuery);
	public String buildCategoryPath(String categoryTree);
	
}
