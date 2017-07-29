package com.pp.common.core.dao;

/**
 * Mapper映射接口
 * 
 * @author
 *
 */
public interface IMapper {

	/**
	 * 获取映射文件命名空间 ，默认使用DAO实例的类名
	 * 
	 * @return 映射文件命名空间
	 */
	String getNamespace();

	/**
	 * 获取SQL语句在映射文件中的id
	 * 
	 * @param statementKey 映射文件命名空间
	 * @return SQL语句在映射文件中的id
	 */
	String getStatement(String statementKey);

}
