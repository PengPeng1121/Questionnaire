package com.pp.web.controller.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("")
public class IndexController {

	Logger log = LoggerFactory.getLogger(IndexController.class.getName());

	/**
	 * 系统首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(
			HttpServletRequest request, HttpServletResponse response,Model model) {
		return "login";
	}

	/**
	 * 系统首页
	 *
	 * @return
	 */
	@RequestMapping(value = "", method = RequestMethod.GET)
	public String index1(
			HttpServletRequest request, HttpServletResponse response,Model model) {
		return "login";
	}

}
