package com.pp.common.core.manager;

import com.pp.common.core.dao.IDao;
import com.pp.common.core.AbstractEntity;
import com.pp.common.core.Sort;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Manager通用实现
 * 
 * @author
 *
 * @param <T> 数据实体
 */
public abstract class AbstractGenericManager<T extends AbstractEntity> implements IManager<T>{
	
	@Autowired	
	private IDao<T> iDao;
	
	@Override
	public IDao<T> getDao(){
		return this.iDao;
	}

	@Override
	public Class<T> getEntityClass(){
		return this.iDao.getEntityClass();
	}
	
	@Override
	public <S extends T> S insert(S entity) {
		return this.iDao.insert(entity);
	}

	@Override
	public <S extends T> Iterable<S> insert(Iterable<S> entities) {
		return this.iDao.insert(entities);
	}

	@Override
	public <S extends T> int update(S entity) {
		return this.iDao.update(entity);
	}

	@Override
	public final T selectOne(Long id) {
		return this.iDao.selectOne(id);
	}

	@Override
	public final T selectOne(T entity) {
		return this.iDao.selectOne(entity);
	}

	@Override
	public final List<T> selectAll() {
		return this.iDao.selectAll();
	}

	@Override
	public final List<T> selectList(Iterable<Long> ids) {
		return this.iDao.selectList(ids);
	}

	@Override
	public final List<T> selectList(T entity) {
		return this.iDao.selectList(entity);
	}

	@Override
	public final List<T> selectList(T entity, Integer rowOffset, Integer rowLimit, Sort... sorts) {
		return this.iDao.selectList(entity, rowOffset, rowLimit, sorts);
	}

	@Override
	public final List<T> selectList(T entity, Integer rowOffset, Integer rowLimit, Integer serverCount, Integer serverId, Sort... sorts) {
		return this.iDao.selectList(entity, rowOffset, rowLimit, serverCount, serverId, sorts);
	}
	
	@Override
	public final long count() {
		return this.iDao.count();
	}

	@Override
	public final long count(T entity) {
		return this.iDao.count(entity);
	}
	
	@Override
	public final boolean exists(Long id) {
		return this.iDao.exists(id);
	}

	@Override
	public final boolean exists(T entity) {
		return this.iDao.exists(entity);
	}

	@Override
	public <S extends T> int delete(S entity) {
		return this.iDao.delete(entity);
	}

	@Override
	public <S extends T> int delete(Iterable<S> entities) {
		return this.iDao.delete(entities);
	}

	@Override
	public <S extends T> int deletePhysically(Long id){
		return this.iDao.deletePhysically(id);
	}

	@Override
	public <S extends T> int deletePhysically(Iterable<Long> ids){
		return this.iDao.deletePhysically(ids);
	}
}
