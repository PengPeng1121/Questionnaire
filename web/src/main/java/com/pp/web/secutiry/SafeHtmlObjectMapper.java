package com.pp.web.secutiry;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 * 当进行JSON转换时，进行HTML字符转义
 * 
 * @author
 *
 */
public class SafeHtmlObjectMapper extends ObjectMapper {

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;
	
	public SafeHtmlObjectMapper(){
		super();
		this.getFactory().setCharacterEscapes(new HTMLCharacterEscapes());
	}

}

class HTMLCharacterEscapes extends CharacterEscapes{

	/**
	 * 序列化
	 */
	private static final long serialVersionUID = 1L;
	private final int[] htmlEscapes;
	
	public HTMLCharacterEscapes(){
		htmlEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
		htmlEscapes['<'] = CharacterEscapes.ESCAPE_CUSTOM;
		htmlEscapes['>'] = CharacterEscapes.ESCAPE_CUSTOM;
		htmlEscapes['&'] = CharacterEscapes.ESCAPE_CUSTOM;
		htmlEscapes['"'] = CharacterEscapes.ESCAPE_CUSTOM;
		htmlEscapes['\''] = CharacterEscapes.ESCAPE_CUSTOM;
	}
	@Override
	public int[] getEscapeCodesForAscii() {
		return this.htmlEscapes;
	}

	@Override
	public SerializableString getEscapeSequence(int ch) {
		return new SerializedString(StringEscapeUtils.escapeHtml4(Character.toString((char) ch)));
	}
	
}
