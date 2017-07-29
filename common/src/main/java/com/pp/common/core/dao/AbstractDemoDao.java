package com.pp.common.core.dao;

import com.pp.common.core.AbstractEntity;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 基础数据DAO基类
 * 使用注解注入SqlSessionFactory，参数名sqlSessionFactoryBaseInfo在spring-config-dao.xml定义
 * 
 * @author
 *
 * @param <T> 数据实体类型
 */
public abstract class AbstractDemoDao<T extends AbstractEntity> extends AbstractGenericDao<T> {

	@Autowired
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactoryDemo) {
    	super.setSqlSessionFactory(sqlSessionFactoryDemo);
    }
	
}
