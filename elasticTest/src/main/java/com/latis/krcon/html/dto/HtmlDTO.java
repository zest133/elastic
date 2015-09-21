package com.latis.krcon.html.dto;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.TermVector;

public class HtmlDTO {

	/**
	 *  "0000.00e0.1530.0005": {
               // "DepthLevel": "2",
               // "ChildList": {},
                "FilePath": "7733.en.7733.html",
                "CATEGORY_TEXT_ID": "7733",
                //"CATEGORY_TREE": "0000.00e0.1530.0005",
                "CATEGORY_ID": "4271",
                "LOCALE_KEY": "en",
                "CATEGORY_TITLE": "Amendment Status",
                "CATEGORY_DESC": "SOLAS 1974",
                "DATA_XML": "",
                "DATA_XML_TRANSFORM": null,
                "DATA_TEXT": null
            },
	 */
	
	
	
	private String categoryTree; // category tree struct
	private int categoryTextId;	
	private int categoryId;	
	private String localeKey;	//locale info
	private String categoryTitle;	//breadcrumbs
	private String categoryDesc;	//desc
	
	private String html;			//html
	private String text;			//html text
	private String breadcrumb;
	
	



	public String getBreadcrumb() {
		return breadcrumb;
	}



	public void setBreadcrumb(String breadcrumb) {
		this.breadcrumb = breadcrumb;
	}



	public String getCategoryTree() {
		return categoryTree;
	}



	public void setCategoryTree(String categoryTree) {
		this.categoryTree = categoryTree;
	}



	public int getCategoryTextId() {
		return categoryTextId;
	}



	public void setCategoryTextId(int categoryTextId) {
		this.categoryTextId = categoryTextId;
	}



	public int getCategoryId() {
		return categoryId;
	}



	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}



	public String getLocaleKey() {
		return localeKey;
	}



	public void setLocaleKey(String localeKey) {
		this.localeKey = localeKey;
	}



	public String getCategoryTitle() {
		return categoryTitle;
	}



	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}



	public String getCategoryDesc() {
		return categoryDesc;
	}



	public void setCategoryDesc(String categoryDesc) {
		this.categoryDesc = categoryDesc;
	}






	public String getHtml() {
		return html;
	}



	public void setHtml(String html) {
		this.html = html;
	}



	public String getText() {
		return text;
	}



	public void setText(String text) {
		this.text = text;
	}



	
	
	
}
