package com.pp.common.core.dao;

import com.google.gson.Gson;
import com.pp.common.core.AbstractEntity;
import com.pp.common.core.Sort;
import com.pp.common.util.BeanConvertUtils;
import com.pp.common.util.UuidUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

/**
 * DAO通用实现
 * 
 * @author
 *
 * @param <T> 数据实体
 */
public abstract class AbstractGenericDao<T extends AbstractEntity> extends SqlSessionDaoSupport implements IDao<T>, IMapper {
	
	// 日志
	Logger logger = LoggerFactory.getLogger(getClass());

	// SQL 插入数据
	private static final String STATEMENT_INSERT = "insert";
	// SQL 批量插入数据
	private static final String STATEMENT_BATCH_INSERT = "batchInsert";
	// SQL 按主键更新
	private static final String STATEMENT_UPDATE_BY_PRIMARY_KEY = "updateByPrimaryKey";
	// SQL 按主键删除
	private static final String STATEMENT_DELETE_BY_PRIMARY_KEY = "deleteByPrimaryKey";
	// SQL 按主键批量删除
	private static final String STATEMENT_BATCH_DELETE_BY_PRIMARY_KEY = "batchDeleteByPrimaryKey";
	// SQL 按主键批量物理删除
	private static final String STATEMENT_BATCH_DELETE_BY_PRIMARY_KEY_PHYSICALLY = "batchDeleteByPrimaryKeyPhysically";
	// SQL 查询所有记录
	private static final String STATEMENT_SELECT_ALL = "selectAll";
	// SQL 按主键查询
	private static final String STATEMENT_SELECT_BY_PRIMARY_KEY = "selectByPrimaryKey";
	// SQL 按主键集合查询
	private static final String STATEMENT_SELECT_BY_PRIMARY_KEY_SET = "selectByPrimaryKeys";
	// SQL 按条件查询
	private static final String STATEMENT_SELECT_BY_CONDITION = "selectByCondition";
	// SQL 按条件统计记录数
	private static final String STATEMENT_COUNT_BY_CONDITION = "countByCondition";

	// 排序参数 Key
	protected static final String KEY_SORT = "_sort";
	// 分页参数 Key
	protected static final String KEY_PAGE = "_page";
	// 分片参数 Key
	protected static final String KEY_CLOVER = "_clover";
	// 数据版本参数 Key
	protected static final String KEY_VERSION = "_version";

	// 批处理操作最大数量
	protected static final int MAX_BATCH = 1000;
	// 查询结果最大数量
	protected static final int MAX_RESULT = 1000;

	// 数据实体类定义
	private final Class<T> entityClass;
	
	// MyBatis命名空间
	private final String namespace;

	// 构造函数
	public AbstractGenericDao() {
		// 取得Dao对应的数据实体类定义
		Type type = getClass().getGenericSuperclass();  
        Type[] params = ((ParameterizedType) type).getActualTypeArguments();
        @SuppressWarnings("unchecked")
		Class<T> clazz = ((Class<T>) params[0]);
        this.entityClass = clazz;
        // 取得MyBatis使用的命名空间
		Class<?>[] clazzes = this.getClass().getInterfaces();
		// 如果DAO没有继承接口
		if (clazzes == null || clazzes.length < 1) {
			throw new Error("DAO实现类需要实现接口，同时该接口名作为Mapper命名空间");
		}
		// 本系统将DAO接口作为命名空间
        this.namespace = clazzes[0].getName();
	}

	@Override
	public final Class<T> getEntityClass() {
		return this.entityClass;
	}

	@Override
	public final String getNamespace() {
		return this.namespace;
	}

	@Override
	public final String getStatement(String statementKey) {
		return this.getNamespace() + "." + statementKey;
	}

	@Override
	public final <S extends T> S insert(S entity) {
		// 执行时间
		Date exeDate = new Date();
		// 校验参数
		if (entity == null) {
			throw new IllegalArgumentException("参数异常：数据实体是null，无法插入数据库");
		} else if (StringUtils.isEmpty(entity.getCreateUser())) {
			throw new IllegalArgumentException("参数异常：数据实体的createUser属性是空值，无法插入数据库");
		}

		// 设置创建时间
		entity.setCreateTime(exeDate);
		// 设置修改时间
		entity.setUpdateTime(exeDate);
		// 设置修改人
		entity.setUpdateUser(entity.getCreateUser());
		// 设置数据版本
		entity.setVersion(UuidUtils.getUuid());
		// 设置备注
		if (entity.getRemark() == null) {
			entity.setRemark("");
		}
		// 设置删除标志
		entity.setIsDelete(AbstractEntity.NOT_DELETED);
		// 数据实体非空校验(跳过)
		// 获取插入语句
		String statement = this.getStatement(STATEMENT_INSERT);
		// 执行插入语句
		int rows = this.getSqlSession().insert(statement, entity);
		// 返回已设置主键的数据实体
		return entity;
	}

	@Override
	public final <S extends T> Iterable<S> insert(Iterable<S> entities) {
		// 执行时间
		Date exeDate = new Date();
		// 校验参数，并将参数转换成List
		List<S> entityList = null;
		if (entities == null) {
			throw new IllegalArgumentException("参数异常：集合是null，无法插入数据库");
		} else {
			entityList = new ArrayList<S>();

			for (S entity : entities) {
				if (entity == null) {
					throw new IllegalArgumentException("参数异常：集合中存在数据实体是null，无法插入数据库");
				} else if (StringUtils.isEmpty(entity.getCreateUser())) {
					throw new IllegalArgumentException("参数异常：集合中存在数据实体的createUser属性是空值，无法插入数据库");
				} else {
					// 设置创建时间
					entity.setCreateTime(exeDate);
					// 设置修改时间
					entity.setUpdateTime(exeDate);
					// 设置修改人
					entity.setUpdateUser(entity.getCreateUser());
					// 设置数据版本
					entity.setVersion(UuidUtils.getUuid());
					// 设置备注
					if (entity.getRemark() == null) {
						entity.setRemark("");
					}
					// 设置删除标志
					entity.setIsDelete(AbstractEntity.NOT_DELETED);
					// 放入列表
					entityList.add(entity);
				}
			}

			if (entityList.size() == 0) {
				throw new IllegalArgumentException("参数异常：集合中没有数据，无法插入数据库");
			}
		}
		// 数据实体非空校验（跳过）
		// 获取批量插入语句
		String statement = this.getStatement(STATEMENT_BATCH_INSERT);
		// 执行批量插入语句
		int rows = this.getSqlSession().insert(statement, entityList);
		// 记录数据变更事件
		// 返回已设置主键的数据实体集合
		return entities;
	}

	@Override
	public final <S extends T> int update(S entity) {
		// 执行时间
		Date exeDate = new Date();
		// 校验参数
		if (entity == null) {
			throw new IllegalArgumentException("参数异常：数据实体是null，无法更新数据库");
		} else if (entity.getId() == null) {
			throw new IllegalArgumentException("参数异常：数据实体的id属性是null，无法更新数据库");
		} else if (StringUtils.isEmpty(entity.getUpdateUser())) {
			throw new IllegalArgumentException("参数异常：数据实体的updateUser属性是空值，无法更新数据库");
		}
		// 数据版本参数
		String oldVersion = entity.getVersion();
		// 设置数据版本
		entity.setVersion(UuidUtils.getUuid());
		// 设置修改时间
		entity.setUpdateTime(exeDate);
		// 将查询参数包装成哈希表
		Map<String, Object> parameterMap = null;
		try {
			parameterMap = BeanConvertUtils.bean2Map(entity);
		} catch (Exception e) {
			throw new RuntimeException("参数异常：将更新数据实体转换成MAP时程序出错", e);
		}
		// 数据版本参数放入哈希表
		if (oldVersion != null) {
			parameterMap.put(KEY_VERSION, oldVersion);
		}

		// 获取更新语句
		String statement = this.getStatement(STATEMENT_UPDATE_BY_PRIMARY_KEY);
		// 执行更新语句
		int rows = this.getSqlSession().update(statement, parameterMap);
		// 返回更新记录数
		return rows;
	}

	@Override
	public final T selectOne(Long id) {
		// 如果主键不是null
		if (id != null) {
			// 获取按主键查询语句
			String statement = this.getStatement(STATEMENT_SELECT_BY_PRIMARY_KEY);
			// 执行按主键查询语句
			List<T> resultList = this.getSqlSession().selectList(statement, id);
			// 返回查询结果
			if (resultList != null && resultList.size() > 0) {
				T result = resultList.get(0);
				return result;
			}
		}
		// 如果主键是null，或者查询结果为空
		return null;
	}

	@Override
	public final T selectOne(T entity) {
		// 执行按数据实体，限定查询1条记录的查询语句
		List<T> resultList = this.selectList(entity, 0, 1);
		// 返回查询结果
		if (resultList != null && resultList.size() > 0) {
			T result = resultList.get(0);
			return result;
		}
		// 如果查询结果为空
		return null;
	}

	@Override
	public final List<T> selectAll() {
		// 获取查询全部的语句
		String statement = this.getStatement(STATEMENT_SELECT_ALL);
		// 执行查询全部的语句
		List<T> resultList = this.getSqlSession().selectList(statement);
		// 判断结果集是否过大
		if (resultList != null && resultList.size() > MAX_RESULT) {
			logger.warn("查询结果集过大：当前数量" + resultList.size() + "，建议慎用selectAll方法，改用分页排序查询[SQL:" + statement + "]");
		}
		// 返回查询结果
		return resultList;
	}

	@Override
	public final List<T> selectList(Iterable<Long> ids) {
		// 过滤null
		ArrayList<Long> idList = new ArrayList<Long>();
		if (ids != null) {
			for (Long id : ids) {
				if (id != null) {
					idList.add(id);
				}
			}
		}
		// 校验查询id的数量
		if (idList.size() == 0) {
			throw new IllegalArgumentException("参数异常：主键集合中没有值，无法按主键集合查询记录");
		} else if (idList.size() > MAX_BATCH) {
			throw new IllegalArgumentException("参数异常：主键集合允许的最大数量为1000，当前数量" + idList.size() + "，建议分批查询记录");
		}
		// 获取按主键集合查询的语句
		String statement = this.getStatement(STATEMENT_SELECT_BY_PRIMARY_KEY_SET);
		// 执行按主键集合查询的语句
		List<T> resultList = this.getSqlSession().selectList(statement, idList);
		// 返回查询结果
		return resultList;
	}

	@Override
	public final List<T> selectList(T entity) {
		return this.selectList(entity, null, null);
	}

	@Override
	public final List<T> selectList(T entity, Integer rowOffset, Integer rowLimit, Sort... sorts) {
		return this.selectList(entity, rowOffset, rowLimit, null, null, sorts);
	}

	@Override
	public final List<T> selectList(T entity, Integer rowOffset, Integer rowLimit, Integer serverCount, Integer serverId, Sort... sorts) {

		// 将查询参数包装成哈希表
		Map<String, Object> parameterMap = null;
		try {
			parameterMap = BeanConvertUtils.bean2Map(entity);
		} catch (Exception e) {
			throw new RuntimeException("将查询数据实体转换成MAP时程序出错", e);
		}
		// 设置排序参数
		if (sorts != null && sorts.length > 0) {
			parameterMap.put(KEY_SORT, Arrays.asList(sorts));
		}
		// 设置分页参数
		if (rowOffset != null && rowLimit != null) {
			if (rowOffset < 0) {
				throw new IllegalArgumentException("参数异常：参数rowOffset不能小于0");
			}
			if (rowLimit < 1) {
				throw new IllegalArgumentException("参数异常：参数rowLimit不能小于1");
			}
			RowBounds rowBounds = new RowBounds(rowOffset, rowLimit);
			parameterMap.put(KEY_PAGE, rowBounds);
		} else {
			// 当没有设置分页时，硬编码限制返回超过最大值的结果集，用于触发异常
			RowBounds rowBounds = new RowBounds(0, MAX_RESULT + 1);
			parameterMap.put(KEY_PAGE, rowBounds);
		}
		// 设置分片参数
		if (serverCount != null && serverId != null) {
			if (serverCount < 1) {
				throw new IllegalArgumentException("参数异常：参数serverCount不能小于1");
			}
			if (serverId < 0) {
				throw new IllegalArgumentException("参数异常：参数serverId不能小于0");
			}
			Clover clover = new Clover(serverCount, serverId);
			parameterMap.put(KEY_CLOVER, clover);
		}
		// 获取按条件查询的语句
		String statement = this.getStatement(STATEMENT_SELECT_BY_CONDITION);
		// 执行按条件查询的语句
		List<T> resultList = this.getSqlSession().selectList(statement, parameterMap);
		// 判断结果集是否过大
		if (resultList != null && resultList.size() > MAX_RESULT) {
			String param = new Gson().toJson(parameterMap);
			logger.warn("查询结果集过大：当前数量" + resultList.size() + "，建议设置合理的分页排序参数[SQL:" + statement + ";PARAM:" + param + "]");
		}
		// 返回查询结果
		return resultList;
	}
	
	@Override
	public final long count() {
		return this.count(null);
	}

	@Override
	public final long count(T entity) {
		// 将查询参数包装成哈希表
		Map<String, Object> parameterMap = null;
		try {
			parameterMap = BeanConvertUtils.bean2Map(entity);
		} catch (Exception e) {
			throw new RuntimeException("将查询数据实体转换成MAP时程序出错", e);
		}
		// 获取统计语句
		String statement = this.getStatement(STATEMENT_COUNT_BY_CONDITION);
		// 执行统计语句
		Long count = this.getSqlSession().selectOne(statement, parameterMap);
		// 返回结果
		if (count != null) {
			return count.longValue();
		}
		return 0;
	}

	@Override
	public final boolean exists(Long id) {
		// 如果主键不是null
		if (id != null) {
			// 执行按主键查询语句
			T result = this.selectOne(id);
			// 如果查询结果不是null，返回true
			if (result != null) {
				return true;
			}
		}
		// 如果主键是null，或者查询结果为空
		return false;
	}

	@Override
	public final boolean exists(T entity) {
		// 如果数据实体不是null
		if (entity != null) {
			// 执行按数据实体查询语句
			T result = this.selectOne(entity);
			// 如果查询结果不是null，返回true
			if (result != null) {
				return true;
			}
		}
		// 如果数据实体是null，或者查询结果为空
		return false;
	}

	@Override
	public final <S extends T> int delete(S entity) {
		// 执行时间
		Date exeDate = new Date();
		// 校验参数
		if (entity == null) {
			throw new IllegalArgumentException("参数异常：数据实体是null，无法删除记录");
		} else if (entity.getId() == null) {
			throw new IllegalArgumentException("参数异常：数据实体的id属性是null，无法删除记录");
		} else if (StringUtils.isEmpty(entity.getUpdateUser())) {
			throw new IllegalArgumentException("参数异常：数据实体的updateUser属性是空值，无法删除记录");
		}
		// 数据更新时间
		entity.setUpdateTime(exeDate);
		// 获取删除语句
		String statement = this.getStatement(STATEMENT_DELETE_BY_PRIMARY_KEY);
		// 执行删除语句
		int rows = this.getSqlSession().update(statement, entity);
		return rows;
	}

	@Override
	public final <S extends T> int delete(Iterable<S> entities) {
		// 执行时间
		Date exeDate = new Date();
		// 校验参数
		List<Long> idList = new ArrayList<Long>();
		String updateUser = null;
		String entityClassName = null;

		if (entities == null) {
			throw new IllegalArgumentException("参数异常：集合是null，无法删除记录");
		} else {
			for (S entity : entities) {
				if (entity == null) {
					throw new IllegalArgumentException("参数异常：数据实体是null，无法删除记录");
				} else if (entity.getId() == null) {
					throw new IllegalArgumentException("参数异常：数据实体的id属性是null，无法删除记录");
				} else if (StringUtils.isEmpty(entity.getUpdateUser())) {
					throw new IllegalArgumentException("参数异常：数据实体的updateUser属性是空值，无法删除记录");
				}
				idList.add(entity.getId());
				if (updateUser == null) {
					updateUser = entity.getUpdateUser();
				}
				if (entityClassName == null) {
					entityClassName = entity.getClass().getName();
				}
			}
		}

		// 校验删除id的数量
		if (idList.size() == 0) {
			throw new IllegalArgumentException("参数异常：主键集合中没有值，无法按主键集合删除记录");
		} else if (idList.size() > MAX_BATCH) {
			throw new IllegalArgumentException("参数异常：主键集合允许的最大数量为1000，当前数量" + idList.size() + "，建议分批删除记录");
		}

		// 将参数包装成哈希表
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		parameterMap.put("ids", idList);
		parameterMap.put("updateUser", updateUser);
		parameterMap.put("updateTime", exeDate);
		// 获取批量删除语句
		String statement = this.getStatement(STATEMENT_BATCH_DELETE_BY_PRIMARY_KEY);
		// 执行批量删除语句
		int rows = this.getSqlSession().update(statement, parameterMap);
		// 返回删除条数
		return rows;
	}

	@Override
	public <S extends T> int deletePhysically(Long id) {
		// 参数校验
		if (id == null) {
			throw new IllegalArgumentException("参数异常：id是null，无法物理删除记录");
		}
		// 放入id清单
		List<Long> idList = new ArrayList<Long>();
		idList.add(id);
		// 执行物理删除
		return this.deletePhysically(idList);
	}

	@Override
	public <S extends T> int deletePhysically(Iterable<Long> ids) {
		// 参数校验
		List<Long> idList = new ArrayList<Long>();
		if (ids == null) {
			throw new IllegalArgumentException("参数异常：集合是null，无法物理删除记录");
		} else {
			for (Long id : ids) {
				if (id == null) {
					throw new IllegalArgumentException("参数异常：集合中id是null，无法物理删除记录");
				}
				// 校验通过后，放入清单
				idList.add(id);
			}
		}
		// 校验删除id的数量
		if (idList.size() == 0) {
			throw new IllegalArgumentException("参数异常：主键集合中没有值，无法按主键集合物理删除记录");
		} else if (idList.size() > MAX_BATCH) {
			throw new IllegalArgumentException("参数异常：主键集合允许的最大数量为1000，当前数量" + idList.size() + "，建议分批物理删除记录");
		}
		// 获取批量物理删除语句
		String statement = this.getStatement(STATEMENT_BATCH_DELETE_BY_PRIMARY_KEY_PHYSICALLY);
		// 执行批量物理删除语句
		int rows = this.getSqlSession().delete(statement, idList);
		// 返回删除的条数
		return rows;
	}

	public <S extends Object> List<S> selectList(String statementKey, Map<String,Object> paramMap){
		// 获取语句
		String statement = this.getStatement(statementKey);
		// 执行
		List<S> resultList = this.getSqlSession().selectList(statement, paramMap);
		// 判断结果集是否过大
		if (resultList != null && resultList.size() > MAX_RESULT) {
			String param = new Gson().toJson(paramMap);
			logger.warn("查询结果集过大：当前数量" + resultList.size() + "，建议设置合理的分页排序参数[SQL:" + statement + ";PARAM:" + param + "]");
		}
		// 返回
		return resultList;
		
	}
	
	@Override
	public long count(String statementKey, Map<String,Object> paramMap){
		// 获取统计语句
		String statement = this.getStatement(statementKey);
		// 执行统计语句
		Long count = this.getSqlSession().selectOne(statement, paramMap);
		// 返回结果
		if (count != null) {
			return count.longValue();
		}
		return 0;
	}
}
