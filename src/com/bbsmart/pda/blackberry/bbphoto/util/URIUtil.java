package com.bbsmart.pda.blackberry.bbphoto.util;

import java.util.Hashtable;

public final class URIUtil {
	private static Hashtable lookup = new Hashtable();
	static {
		lookup.put("20", " ");
		
		lookup.put("22", "\"");
		lookup.put("3C", "<");
		lookup.put("3E", ">");
		lookup.put("23", "#");
		lookup.put("25", "%");
		
		lookup.put("7B", "{");
		lookup.put("7D", "}");
		lookup.put("7C", "|");
		lookup.put("5C", "\\");
		lookup.put("5E", "^");
		lookup.put("7E", "~");
		lookup.put("5B", "[");
		lookup.put("5D", "]");
		lookup.put("60", "`");
	}
	
	public static String decodeURI(String uri) {
		int index = uri.indexOf('%');
		while(index >= 0) {
			String code = uri.substring(index+1, index+3);
			String value = (String)lookup.get(code);
			uri = uri.substring(0, index) + value + uri.substring(index+3);
			index = uri.indexOf('%', index);
		}
		return uri;
	}
}