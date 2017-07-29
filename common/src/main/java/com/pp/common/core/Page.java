package com.pp.common.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class Page<T> implements Serializable{

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;
	
	// 页码，从0开始
	private final int pageIndex;
	// 每页条数
	private final int pageSize;
	// 记录总数
	private final long total;
	// 排序列表
	private final List<Sort> sorts = new ArrayList<Sort>();
	// 分页结果集
	private final List<T> content = new ArrayList<T>();
	
	/**
	 * 供JSF反射使用的构造函数
	 */
	public Page(){
		this.pageIndex = 0;
		this.pageSize = 0;
		this.total = 0;
	}

	/**
	 * 业务代码中使用的构造函数
	 * @param pageIndex 页码，从0开始
	 * @param pageSize 每页条数
	 * @param sorts 排序
	 */
	public Page(int pageIndex, int pageSize, Sort... sorts) {
		this(null, 0L, pageIndex, pageSize, sorts);
	}

	/**
	 * 业务代码中使用的构造函数
	 * @param content 分页结果集
	 * @param total 记录总数
	 * @param pageIndex 页码，从0开始
	 * @param pageSize 每页条数
	 * @param sorts 排序
	 */
	public Page(List<T> content, long total, int pageIndex, int pageSize, Sort... sorts) {
		// 结果集
		if (content != null && content.size() > 0) {
			this.content.addAll(content);
		}
		// 记录总数
		this.total = total;
		// 分页
		if (pageIndex < 0) {
			throw new IllegalArgumentException("参数pageIndex不能小于0");
		}
		if (pageSize < 1) {
			throw new IllegalArgumentException("参数pageSize不能小于1");
		}
		this.pageIndex = pageIndex;
		this.pageSize = pageSize;
		// 排序
		if (sorts != null && sorts.length > 0) {
			this.sorts.addAll(Arrays.asList(sorts));
		}
	}

	/**
	 * 获取当前页码，从0开始
	 * 
	 * @return 当前页码
	 */
	public int getPageIndex() {
		return this.pageIndex;
	}

	/**
	 * 获取当前页码，从1开始
	 * @return 当前页码
	 */
	public int getPageNum(){
		return this.pageIndex + 1;
	}
	/**
	 * 获取每页记录数
	 * 
	 * @return 每页记录数
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * 获取排序列表
	 * 
	 * @return 排序列表
	 */
	public List<Sort> getSorts() {
		return sorts;
	}

	/**
	 * 获取总记录数
	 * 
	 * @return 总记录数
	 */
	public long getTotalElements() {
		return this.total;
	}

	/**
	 * 获取分页结果集
	 * 
	 * @return 分页结果集
	 */
	public List<T> getContent() {
		return Collections.unmodifiableList(this.content);
	}

	/**
	 * 获取总页数
	 * 
	 * @return 总页数
	 */
	public int getTotalPages() {
		return (int) Math.ceil((double) total / (double) getPageSize());
	}

	/**
	 * 获取当前结果集条数
	 * 
	 * @return 当前结果集条数
	 */
	public int getNumberOfContent() {
		return this.content.size();
	}

	/**
	 * 是否有上一页
	 * 
	 * @return 是否有上一页
	 */
	public boolean hasPrevious() {
		return getPageIndex() > 0;
	}

	/**
	 * 是否有下一页
	 * 
	 * @return 是否有下一页
	 */
	public boolean hasNext() {
		return getPageIndex() < getTotalPages() - 1;
	}

	/**
	 * 是否是第一页
	 * 
	 * @return 是否是第一页
	 */
	public boolean isFirst() {
		return !hasPrevious();
	}

	/**
	 * 是否是最后一页
	 * 
	 * @return 是否是最后一页
	 */
	public boolean isLast() {
		return !hasNext();
	}
}
