package com.pp.web.controller;

import com.pp.common.core.AbstractEntity;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseController {

	Logger logger = LoggerFactory.getLogger(getClass());

	protected <T extends AbstractEntity> Page<T> buildPage(int pageNum, int pageSize, String sortName, String sortOrder) {
		// 设置合理的参数
		// 页码最小是1
		if (pageNum < 1) {
			pageNum = 1;
		}
		// 每页条数最小10，最大50
		if (pageSize < 1) {
			pageSize = 10;
		} else if (pageSize > 50) {
			pageSize = 50;
		}
		// 开始页码
		int pageIndex = pageNum - 1;
		// 排序
		Sort sort = null;
		if ("desc".equalsIgnoreCase(sortOrder)) {
			sort = Sort.desc(sortName);
		} else {
			sort = Sort.asc(sortName);
		}
		// 创建分页对象
		Page<T> page = new Page<T>(pageIndex, pageSize, sort);
		return page;
	}
}
