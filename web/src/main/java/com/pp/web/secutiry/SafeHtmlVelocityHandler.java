package com.pp.web.secutiry;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.RuntimeServicesAware;

/**
 * 当使用VELOCITY表达式输出时，进行HTML字符转义
 * 仅限于$!{}指令，当不需要转义时，使用${}
 * @author
 *
 */
public class SafeHtmlVelocityHandler implements ReferenceInsertionEventHandler, RuntimeServicesAware {

	private static final String DIRECTIVE_PREFIX = "$!{";
	
	private RuntimeServices rs;

	@Override
	public void setRuntimeServices(RuntimeServices rs) {
		this.rs = rs;
	}

	
	public RuntimeServices getRuntimeServices() {
		return this.rs;
	}

	@Override
	public Object referenceInsert(String reference, Object value) {
		if(value instanceof String && reference.startsWith(DIRECTIVE_PREFIX)){
			return StringEscapeUtils.escapeHtml4((String)value);
		} else {
			return value;			
		}
	}

}
