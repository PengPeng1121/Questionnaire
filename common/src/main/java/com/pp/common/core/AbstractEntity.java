package com.pp.common.core;

import com.pp.common.annotation.Transient;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据实体基类
 * 
 * @author Liu Guojian
 *
 */
public abstract class AbstractEntity implements Serializable {

	/**
	 * 数据未删除
	 */
	public static final int NOT_DELETED = 0;
	/**
	 * 数据已删除
	 */
	public static final int DELETED = 1;
	/**
	 * 数据启用
	 */
	public static final int ENABLE = 1;
	/**
	 * 数据停用
	 */
	public static final int NOT_ENABLE = 0;
	
	/**
	 * 时间默认值 因为数据库规范要求字段非空 所以当时间类型字段没有值时，使用该值
	 */
	public static final Date DEFAULT_DATE = new Date(0L);
	
	/**
	 * 整型默认值
	 */
	public static final Integer DEFAULT_INTEGER = 0;
	
	/**
	 * 长整型默认值
	 */
	public static final Long DEFAULT_LONG = 0L;
	
	/**
	 * 大数字型默认值
	 */
	public static final BigDecimal DEFAULT_BIGDECIMAL = new BigDecimal(0);
	
	/**
	 * 字符串默认值
	 */
	public static final String DEFAULT_STRING = "";

	// 序列化
	private static final long serialVersionUID = 1L;

	// 主键
	private Long id;

	// 主键最大值（用于查询）
	@Transient
	private Long idMax;

	// 主键最小值（用于查询）
	@Transient
	private Long idMin;

	// 备注
	private String remark;

	// 创建人
	private String createUser;

	// 创建时间
	private Date createTime;

	// 创建时间/起(虚拟字段：用于时间段查询)
	@Transient
	private Date createTimeBegin;

	// 创建时间/止(虚拟字段：用于时间段查询)
	@Transient
	private Date createTimeEnd;

	// 修改人
	private String updateUser;

	// 修改时间
	private Date updateTime;

	// 修改时间/起(虚拟字段：用于时间段查询)
	@Transient
	private Date updateTimeBegin;

	// 修改时间/止(虚拟字段：用于时间段查询)
	@Transient
	private Date updateTimeEnd;

	// 数据版本
	private String version;

	// 逻辑删除标志
	private Integer isDelete;

	// 最后一次修改时间
	private Date ts;

	/**
	 * 取得主键
	 * @return 主键
	 */
	public Long getId() {
		return id;
	}

	/**
	 * 设置主键
	 * @param id 主键
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * 取得主键最大值查询条件（不包括该值）
	 * @return 主键最大值查询条件（不包括该值）
	 */
	public Long getIdMax() {
		return idMax;
	}

	/**
	 * 设置主键最大值查询条件（不包括该值）
	 * @param idMax 主键最大值查询条件（不包括该值）
	 */
	public void setIdMax(Long idMax) {
		this.idMax = idMax;
	}

	/**
	 * 取得主键最小值查询条件（不包括该值）
	 * @return 主键最小值查询条件（不包括该值）
	 */
	public Long getIdMin() {
		return idMin;
	}

	/**
	 * 设置主键最小值查询条件（不包括该值）
	 * @param idMin 主键最小值查询条件（不包括该值）
	 */
	public void setIdMin(Long idMin) {
		this.idMin = idMin;
	}

	/**
	 * 取得备注
	 * @return 备注
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * 设置备注
	 * @param remark 备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 取得创建人
	 * @return 创建人
	 */
	public String getCreateUser() {
		return createUser;
	}

	/**
	 * 设置创建人
	 * @param createUser 创建人
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	/**
	 * 取得创建时间
	 * @return 创建时间
	 */
	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置创建时间
	 * @param createTime 创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	/**
	 * 取得修改人
	 * @return 修改人
	 */
	public String getUpdateUser() {
		return updateUser;
	}

	/**
	 * 设置修改人
	 * @param updateUser
	 */
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	/**
	 * 取得修改时间
	 * @return 修改时间
	 */
	public Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * 设置修改时间
	 * @param updateTime 修改时间
	 */
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	/**
	 * 取得创建日期开始查询条件（包括该时间）
	 * @return 创建日期开始查询条件（包括该时间）
	 */
	public Date getCreateTimeBegin() {
		return createTimeBegin;
	}

	/**
	 * 设置创建日期开始查询条件（包括该时间）
	 * @param createTimeBegin 创建日期开始查询条件（包括该时间）
	 */
	public void setCreateTimeBegin(Date createTimeBegin) {
		this.createTimeBegin = createTimeBegin;
	}

	/**
	 * 取得创建日期结束查询条件（包括该时间）
	 * @return 创建日期结束查询条件（包括该时间）
	 */
	public Date getCreateTimeEnd() {
		return createTimeEnd;
	}

	/**
	 * 设置创建日期结束查询条件（包括该时间）
	 * @param createTimeEnd 创建日期结束查询条件（包括该时间）
	 */
	public void setCreateTimeEnd(Date createTimeEnd) {
		this.createTimeEnd = createTimeEnd;
	}

	/**
	 * 取得修改日期开始查询条件（包括该时间）
	 * @return 修改日期开始查询条件（包括该时间）
	 */
	public Date getUpdateTimeBegin() {
		return updateTimeBegin;
	}

	/**
	 * 设置修改日期开始查询条件（包括该时间）
	 * @param updateTimeBegin 修改日期开始查询条件（包括该时间）
	 */
	public void setUpdateTimeBegin(Date updateTimeBegin) {
		this.updateTimeBegin = updateTimeBegin;
	}

	/**
	 * 取得修改日期结束查询条件（包括该时间）
	 * @return 修改日期结束查询条件（包括该时间）
	 */
	public Date getUpdateTimeEnd() {
		return updateTimeEnd;
	}

	/**
	 * 设置修改日期结束查询条件（包括该时间）
	 * @param updateTimeEnd 修改日期结束查询条件（包括该时间）
	 */
	public void setUpdateTimeEnd(Date updateTimeEnd) {
		this.updateTimeEnd = updateTimeEnd;
	}

	/**
	 * 取得数据版本
	 * @return 数据版本
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * 设置数据版本
	 * @param version 数据版本
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * 取得是否删除
	 * @return 是否删除
	 */
	public Integer getIsDelete() {
		return isDelete;
	}

	/**
	 * 设置是否删除
	 * @param isDelete 是否删除
	 */
	public void setIsDelete(Integer isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * 取得TS
	 * @return TS
	 */
	public Date getTs() {
		return ts;
	}

	/**
	 * 设置TS
	 * @param ts
	 */
	public void setTs(Date ts) {
		this.ts = ts;
	}

	/**
	 * 赋默认值
	 */
	public void setDefaultValue(){
		Class<? extends Object> clazz = getClass();
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(clazz);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
		PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
		if (propertyDescriptors != null) {
			for (PropertyDescriptor descriptor : propertyDescriptors) {
				if (descriptor != null) {
					String propertyName = descriptor.getName();
					if (propertyName != null && !propertyName.equalsIgnoreCase("class") && !propertyName.equalsIgnoreCase("id") && !propertyName.equalsIgnoreCase("ts")) {
						// 跳过@Transient注解
						if (this.isTransient(clazz, propertyName)) {
							continue;
						}
						// 取得写方法
						Method writeMethod = descriptor.getWriteMethod();
						// 初始值
						Object value = null;
						Class<?> typeClass = descriptor.getPropertyType();
						// 根据不同类型赋值
						if(typeClass.equals(String.class)){
							value = DEFAULT_STRING;
						} else if (typeClass.equals(Integer.class)) {
							value = DEFAULT_INTEGER;
						} else if (typeClass.equals(Long.class)) {
							value = DEFAULT_LONG;
						} else if (typeClass.equals(Date.class)) {
							value = DEFAULT_DATE;
						} else if (typeClass.equals(BigDecimal.class)) {
							value = DEFAULT_BIGDECIMAL;
						}
						try {
							writeMethod.invoke(this, new Object[]{value});
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch (InvocationTargetException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		}
	}

	/**
	 * 判断属性是否有@Transient注解
	 *
	 * @param clazz
	 * @param propertyName
	 * @return
	 */
	private boolean isTransient(Class<?> clazz, String propertyName) {
		// 取得基类Field
		Field[] fidldsOfSuperClazz = AbstractEntity.class.getDeclaredFields();
		// 取得子类Field
		Field[] fidldsOfClazz = clazz.getDeclaredFields();
		// 首先在基类中判断属性是否有@Transient注解
		for (Field field : fidldsOfSuperClazz) {
			if (field.getName().equals(propertyName) && field.isAnnotationPresent(Transient.class)) {
				return true;
			}
		}
		// 其次在子类中判断属性是否有@Transient注解
		for (Field field : fidldsOfClazz) {
			if (field.getName().equals(propertyName) && field.isAnnotationPresent(Transient.class)) {
				return true;
			}
		}
		return false;
	}

}
