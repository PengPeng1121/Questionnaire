package com.pp.common.core.service;

import com.pp.common.core.manager.IManager;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;

import java.util.List;

/**
 * Service接口
 *
 * @author Liu Guojian
 *
 * @param <T> 数据实体
 */
public interface IService<T> {

	/**
	 * 取得数据实体Manager实例
	 * @return
	 */
	IManager<T> getManager();

	/**
	 * 插入数据
	 *
	 * @param entityInsert 数据实体
	 * @param createUser 创建人ERP
	 * @return 数据实体
	 */
	<S extends T> S insert(S entityInsert, String createUser);

	/**
	 * 插入数据
	 *
	 * @param entitiesInsert 数据实体集合
	 * @param createUser 创建人ERP
	 * @return 数据实体集合
	 */
	<S extends T> Iterable<S> insert(Iterable<S> entitiesInsert, String createUser);

	/**
	 * 更新数据
	 *
	 * @param entityUpdate 数据实体更新对象
	 * @param updateUser 修改人
	 * @return 更新的条数
	 */
	<S extends T> int update(S entityUpdate, String updateUser);

	/**
	 * 更新指定版本的数据
	 *
	 * @param entityUpdate 数据实体更新对象
	 * @param version 数据版本
	 * @param updateUser 修改人
	 * @return 更新的条数
	 */
	<S extends T> int update(S entityUpdate, String version, String updateUser);

	/**
	 * 按主键查询
	 *
	 * @param id 主键
	 * @return 数据实体
	 */
	T selectOne(Long id);

	/**
	 * 按条件查询一条记录
	 *
	 * @param entityQuery 数据实体查询对象
	 * @return 数据实体
	 */
	T selectOne(T entityQuery);

	/**
	 * 查询所有记录
	 *
	 * @return 记录列表
	 */
	List<T> selectAll();

	/**
	 * 按主键集合查询
	 *
	 * @param ids 主键集合
	 * @return 记录列表
	 */
	List<T> selectList(Iterable<Long> ids);

	/**
	 * 按条件查询
	 *
	 * @param entityQuery 数据实体查询对象
	 * @return 记录列表
	 */
	List<T> selectList(T entityQuery);

	/**
	 * 按条件分页排序查询
	 *
	 * @param entityQuery 数据实体查询对象
	 * @param rowOffset 记录偏移
	 * @param rowLimit 记录条数
	 * @param sorts 排序数组
	 * @return 记录列表
	 */
	List<T> selectList(T entityQuery, Integer rowOffset, Integer rowLimit, Sort... sorts);

	/**
	 * 按条件分页分片排序查询
	 *
	 * @param entityQuery 数据实体查询对象
	 * @param rowOffset 记录偏移
	 * @param rowLimit 记录条数
	 * @param serverCount 分片总数
	 * @param serverId 分片序号
	 * @param sorts 排序数组
	 * @return 记录列表
	 */
	List<T> selectList(T entityQuery, Integer rowOffset, Integer rowLimit, Integer serverCount, Integer serverId, Sort... sorts);

	/**
	 * 统计记录总数
	 *
	 * @return 记录总数
	 */
	long count();

	/**
	 * 按条件统计记录总数
	 *
	 * @param entityQuery 数据实体查询对象
	 * @return 记录总数
	 */
	long count(T entityQuery);

	/**
	 * 判断某主键数据是否存在
	 *
	 * @param id 主键
	 * @return 是否存在
	 */
	boolean exists(Long id);

	/**
	 * 判断某条件数据是否存在
	 *
	 * @param entityQuery 数据实体查询对象
	 * @return 是否存在
	 */
	boolean exists(T entityQuery);

	/**
	 * 删除数据
	 *
	 * @param id 主键
	 * @param deleteUser 删除人
	 * @return 删除条数
	 */
	int delete(Long id, String deleteUser);

	/**
	 * 删除数据
	 *
	 * @param ids 主键集合
	 * @param deleteUser 删除人
	 * @return 删除条数
	 */
	int delete(Iterable<Long> ids, String deleteUser);

	/**
	 * 按主键物理删除，在归档时使用
	 * @param id 待删除数据实体的主键
	 * @return 被删除的记录数
	 * @throws IllegalArgumentException 当不满足以下约束条件时，抛出参数异常
	 * <li>id不能为null
	 */
	int deletePhysically(Long id);

	/**
	 * 按主键集合物理删除，在归档时使用
	 *
	 * @param ids 待删除数据实体的主键集合
	 * @return 被删除的记录数
	 * @throws IllegalArgumentException 当不满足以下约束条件时，抛出参数异常
	 * <li>ids不能为null
	 * <li>ids的元素数量必须大于0
	 */
	int deletePhysically(Iterable<Long> ids);

	/**
	 * 分页查询
	 *
	 * @param entityQuery 查询数据实体
	 * @param page 分页参数
	 * @return 分页结果集
	 */
	Page<T> selectPage(T entityQuery, Page<T> page);
}
