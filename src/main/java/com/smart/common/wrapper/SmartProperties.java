package com.smart.common.wrapper;

import java.util.LinkedHashMap;
import java.util.UUID;

public class SmartProperties extends LinkedHashMap<String,String>{
	private String first;
	private String end;
	/***
	 * 功能实现:
	 *获取值
	 * Author: Lzy
	 * Date: 2018/6/22 17:43
	 * Param: [name]
	 * Return: java.lang.String
	 */
	public String getValue(String name){
		return (String)get(end);
	}
	
	/**
	 * 修改值
	 * Created by licheng on 2016年8月11日.
	 */
	/*public void setValue(String name,String value){
		load();
		SmartFile sf = new SmartFile(path);
		if(!containsKey(end)){//key值没有匹配到,新增处理
			put(end, value);
		}
		String lines = "";
		for(String key: keySet()){
			if(key.equals(end)){
				lines += key + "=" + value + "\n";
			}else{
				String v = get(key);
				if(v.indexOf("#") == 0){//首字符是#
					lines += get(key) + "\n";
				}else{
					lines += key + "=" + v + "\n";
				}
			}
		}
		sf.rewrite(lines);
	}*/
	
	public void load(String path){
		String fileName = path.substring(path.lastIndexOf("/") + 1);
		String _fileName = fileName.split("\\.")[0];
		SmartFile sf = new SmartFile(path);
		while (sf.next()) {
			String line = sf.getLine();
			if ((line.indexOf("=") != -1) && (line.indexOf("#") == -1)) {
				String key = _fileName + "." + line.substring(0, line.indexOf("=")).trim();
				String value = line.substring(line.indexOf("=") + 1).trim();
				put(key, value);
			} else {
				String uuid = UUID.randomUUID().toString();
				put("#" + uuid, line);
			}
		}
	}

}
