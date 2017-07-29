package com.pp.web.secutiry;

import com.pp.common.core.AbstractEntity;

import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 当进行JSON序列化时，将AbstractEntity.DEFAULT_DATE修改为空串
 * 
 * @author
 *
 */
public class IgnoreDefaultValueDateFormat extends SimpleDateFormat {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 提供给配置文件的构造函数
	 * @param pattern
	 */
    public IgnoreDefaultValueDateFormat(String pattern)
    {
        super(pattern);
    }
    
	@Override
	public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
		// 如果date的值和AbstractEntity.DEFAULT_DATE相等，返回空串
		if (date != null && date.getTime() == AbstractEntity.DEFAULT_DATE.getTime()) {
			return new StringBuffer();
		} else {
			return super.format(date, toAppendTo, pos);
		}
	}

}
