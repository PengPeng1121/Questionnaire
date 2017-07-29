package com.pp.common.core.service;

import com.pp.common.core.manager.IManager;
import com.pp.common.core.AbstractEntity;
import com.pp.common.core.Page;
import com.pp.common.core.Sort;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Service通用实现
 * 
 * @author
 *
 * @param <T> 数据实体
 */
public abstract class AbstractGenericService<T extends AbstractEntity> implements IService<T> {

	@Autowired
	private IManager<T> iManager;
	
	@Override
	public IManager<T> getManager(){
		return this.iManager;
	}

	@Override
	public <S extends T> S insert(S entityInsert, String createUser) {
		if (entityInsert == null) {
			throw new IllegalArgumentException("参数entityInsert不能为null");
		}
		if (createUser == null) {
			throw new IllegalArgumentException("参数createUser不能为null");
		}
		entityInsert.setCreateUser(createUser);
		return this.iManager.insert(entityInsert);
	}

	@Override
	public <S extends T> Iterable<S> insert(Iterable<S> entitiesInsert, String createUser) {
		if (entitiesInsert == null) {
			throw new IllegalArgumentException("参数entitiesInsert不能为null");
		}
		if (createUser == null) {
			throw new IllegalArgumentException("参数createUser不能为null");
		}
		for (S entity : entitiesInsert) {
			entity.setCreateUser(createUser);
		}
		return this.iManager.insert(entitiesInsert);
	}

	@Override
	public <S extends T> int update(S entityUpdate, String updateUser) {
		if (entityUpdate == null) {
			throw new IllegalArgumentException("参数entityUpdate不能为null");
		} else if (entityUpdate.getId() == null) {
			throw new IllegalArgumentException("参数entityUpdate的id不能为null");
		}
		if (updateUser == null) {
			throw new IllegalArgumentException("参数updateUser不能为null");
		}
		entityUpdate.setUpdateUser(updateUser);
		entityUpdate.setVersion(null);
		return this.iManager.update(entityUpdate);
	}

	@Override
	public <S extends T> int update(S entityUpdate, String version, String updateUser) {
		if (entityUpdate == null) {
			throw new IllegalArgumentException("参数entityUpdate不能为null");
		} else if (entityUpdate.getId() == null) {
			throw new IllegalArgumentException("参数entityUpdate的id不能为null");
		}
		if (version == null) {
			throw new IllegalArgumentException("参数version不能为null");
		}
		if (updateUser == null) {
			throw new IllegalArgumentException("参数updateUser不能为null");
		}
		entityUpdate.setUpdateUser(updateUser);
		entityUpdate.setVersion(version);
		return this.iManager.update(entityUpdate);
	}

	@Override
	public T selectOne(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("参数id不能为null");
		}
		return this.iManager.selectOne(id);
	}

	@Override
	public T selectOne(T entityQuery) {
		return this.iManager.selectOne(entityQuery);
	}

	@Override
	public List<T> selectAll() {
		return this.iManager.selectAll();
	}

	@Override
	public List<T> selectList(Iterable<Long> ids) {
		if (ids == null) {
			throw new IllegalArgumentException("参数ids不能为null");
		}
		return this.iManager.selectList(ids);
	}

	@Override
	public List<T> selectList(T entityQuery) {
		return this.iManager.selectList(entityQuery);
	}

	@Override
	public List<T> selectList(T entityQuery, Integer rowOffset, Integer rowLimit, Sort... sorts) {
		if (rowOffset == null || rowOffset < 0) {
			throw new IllegalArgumentException("参数rowOffset不能小于0");
		}
		if (rowLimit == null || rowLimit < 1) {
			throw new IllegalArgumentException("参数rowLimit不能小于1");
		}
		return this.iManager.selectList(entityQuery, rowOffset, rowLimit, sorts);
	}

	@Override
	public List<T> selectList(T entityQuery, Integer rowOffset, Integer rowLimit, Integer serverCount, Integer serverId, Sort... sorts) {
		if (rowOffset == null || rowOffset < 0) {
			throw new IllegalArgumentException("参数rowOffset不能小于0");
		}
		if (rowLimit == null || rowLimit < 1) {
			throw new IllegalArgumentException("参数rowLimit不能小于1");
		}
		if (serverCount == null || serverCount < 1) {
			throw new IllegalArgumentException("参数serverCount不能小于1");
		}
		if (serverId == null || serverId < 0) {
			throw new IllegalArgumentException("参数serverId不能小于0");
		}
		return this.iManager.selectList(entityQuery, rowOffset, rowLimit, serverCount, serverId, sorts);
	}

	@Override
	public long count() {
		return this.iManager.count();
	}

	@Override
	public long count(T entityQuery) {
		return this.iManager.count(entityQuery);
	}

	@Override
	public boolean exists(Long id) {
		if (id == null) {
			throw new IllegalArgumentException("参数id不能为null");
		}
		return this.iManager.exists(id);
	}

	@Override
	public boolean exists(T entityQuery) {
		return this.iManager.exists(entityQuery);
	}

	@Override
	public int delete(Long id, String deleteUser) {
		if (id == null) {
			throw new IllegalArgumentException("参数id不能为null");
		}
		if (deleteUser == null) {
			throw new IllegalArgumentException("参数deleteUser不能为null");
		}
		T entity = this.createEntity();
		entity.setId(id);
		entity.setUpdateUser(deleteUser);
		return this.iManager.delete(entity);
	}

	@Override
	public int delete(Iterable<Long> ids, String deleteUser) {
		if (ids == null) {
			throw new IllegalArgumentException("参数id不能为null");
		}
		if (deleteUser == null) {
			throw new IllegalArgumentException("参数deleteUser不能为null");
		}
		List<T> entities = new ArrayList<T>();
		for (Long id : ids) {
			T entity = this.createEntity();
			entity.setId(id);
			entity.setUpdateUser(deleteUser);
			entities.add(entity);
		}
		return this.iManager.delete(entities);
	}

	@Override
	public int deletePhysically(Long id) {
		return this.iManager.deletePhysically(id);
	}

	@Override
	public int deletePhysically(Iterable<Long> ids) {
		return this.iManager.deletePhysically(ids);
	}
	
	@Override
	public Page<T> selectPage(T entityQuery, Page<T> page) {

		if (page == null) {
			throw new IllegalArgumentException("参数page不能为null");
		}

		int pageIndex = page.getPageIndex();
		int pageSize = page.getPageSize();

		int rowOffset = pageIndex * pageSize;
		int rowLimit = pageSize;
		Sort[] sorts = page.getSorts().toArray(new Sort[0]);

		long count = this.iManager.count(entityQuery);
		List<T> resultList = this.iManager.selectList(entityQuery, rowOffset, rowLimit, sorts);

		return new Page<T>(resultList, count, pageIndex, pageSize, sorts);
	}

	/**
	 * 根据泛型创建一个数据实体
	 * 
	 * @return 数据实体
	 */
	private T createEntity() {
		try {
			Class<T> clazz = this.iManager.getEntityClass();
			T entity = clazz.newInstance();
			return entity;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
}
