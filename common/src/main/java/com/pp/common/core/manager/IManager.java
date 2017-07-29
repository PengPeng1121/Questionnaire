package com.pp.common.core.manager;

import com.pp.common.core.dao.IDao;
import com.pp.common.core.Sort;

import java.util.List;

/**
 * Manager通用接口
 * 接口方法和IDao一致，在插入、更新和删除方法上增加事务控制
 * 
 * @author LiuGuojian
 *
 * @param <T> 数据实体
 */
public interface IManager<T> {
	
	/**
	 * 取得当前DAO实例
	 * @return
	 */
	public IDao<T> getDao();
	
	/**
	 * (non-Javadoc)
	 */
	Class<T> getEntityClass();

	/**
	 * (non-Javadoc)
	 */
	<S extends T> S insert(S entity);

	/**
	 * (non-Javadoc)
	 */
	<S extends T> Iterable<S> insert(Iterable<S> entities);

	/**
	 * (non-Javadoc)
	 */
	<S extends T> int update(S entity);

	/**
	 * (non-Javadoc)
	 */
	T selectOne(Long id);

	/**
	 * (non-Javadoc)
	 */
	T selectOne(T entity);

	/**
	 * (non-Javadoc)
	 */
	List<T> selectAll();

	/**
	 * (non-Javadoc)
	 */
	List<T> selectList(Iterable<Long> ids);

	/**
	 * (non-Javadoc)
	 */
	List<T> selectList(T entity);

	/**
	 * (non-Javadoc)
	 */
	List<T> selectList(T entity, Integer rowOffset, Integer rowLimit, Sort... sorts);

	/**
	 * (non-Javadoc)
	 */
	List<T> selectList(T entity, Integer rowOffset, Integer rowLimit, Integer serverCount, Integer serverId, Sort... sorts);

	/**
	 * (non-Javadoc)
	 */
	long count();

	/**
	 * (non-Javadoc)
	 */
	long count(T entity);
	
	/**
	 * (non-Javadoc)
	 */
	boolean exists(Long id);

	/**
	 * (non-Javadoc)
	 */
	boolean exists(T entity);

	/**
	 * (non-Javadoc)
	 */
	<S extends T> int delete(S entity);

	/**
	 * (non-Javadoc)
	 */
	<S extends T> int delete(Iterable<S> entities);
	
	/**
	 * (non-Javadoc)
	 */
	<S extends T> int deletePhysically(Long id);

	/**
	 * (non-Javadoc)
	 */
	<S extends T> int deletePhysically(Iterable<Long> ids);
}
