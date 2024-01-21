package com.qkwl.admin.layui.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

@SuppressWarnings("rawtypes")
public class PageData extends HashMap implements Map{
	
	private static final long serialVersionUID = 1L;
	
	Map map = null;
	HttpServletRequest request;
	
	@SuppressWarnings("unchecked")
	public PageData(HttpServletRequest request){
		this.request = request;
		Map properties = request.getParameterMap();
		Map returnMap = new HashMap(); 
		Iterator entries = properties.entrySet().iterator(); 
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry) entries.next(); 
			String name = (String) entry.getKey(); 
			String value = "";  
			Object valueObj = entry.getValue(); 
			if(null == valueObj){ 
				value = "";
			}else if(valueObj instanceof String[]){ 
				String[] values = (String[])valueObj;
				for(int i=0;i<values.length;i++){ 
					 value += values[i] + ",";
				}
				value = value.substring(0, value.length()-1); 
			}else{
				value = valueObj.toString(); 
			}
			returnMap.put(name, value); 
		}
		map = returnMap;
	}
	
	public PageData() {
		map = new HashMap();
	}
	
	@Override
	public Object get(Object key) {
		Object obj = null;
		if(map.get(key) instanceof Object[]) {
			Object[] arr = (Object[])map.get(key);
			obj = request == null ? arr:(request.getParameter((String)key) == null ? arr:arr[0]);
		} else {
			obj = map.get(key);
		}
		return obj;
	}
	
	public String getString(Object key) {
		Object value = get(key);
		return value == null ? "" : value.toString();
	}
	
	public Integer getInteger(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		}
		if (value instanceof Integer) {
			return (Integer) value;
		}
		return Integer.valueOf(value.toString());
	}
	
	public JSONObject getJSONObject(String key) {
		String value = getString(key);
		return JSONObject.parseObject(value);
	}
	
	@SuppressWarnings("unchecked")
	public void setJSONString(String key, Map<String, Object> obj) {
		map.put(key, JSONObject.toJSONString(obj));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object put(Object key, Object value) {
		//String formatedKey=Tools.toCamelhump(key.toString());
		return map.put(key, value);
	}
	
	@Override
	public Object remove(Object key) {
		return map.remove(key);
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set entrySet() {
		return map.entrySet();
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set keySet() {
		return map.keySet();
	}

	@SuppressWarnings("unchecked")
	public void putAll(Map t) {
		map.putAll(t);
	}

	public int size() {
		return map.size();
	}

	public Collection values() {
		return map.values();
	}
	
}
