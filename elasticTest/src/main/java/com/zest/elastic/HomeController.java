package com.zest.elastic;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zest.elastic.html.search.dao.HtmlSearchDAO;


/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Autowired
	public HtmlSearchDAO htmlSearchDAO;

	

	@RequestMapping(value = { "/", "/main.do" }, method = RequestMethod.GET)
	public String home(Model model) {
		
		model.addAttribute("filters", htmlSearchDAO.getSearchFilterOption());
		
		return "content";
	}
	
}
