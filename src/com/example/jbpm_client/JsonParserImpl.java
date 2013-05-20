package com.example.jbpm_client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParserImpl implements JsonParser {
	public static final String DEFINITIONS = "definitions";
	public static final String INSTANCES = "instances";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	
	public ArrayList<JSONObject> parseArray(JSONArray jArray){
		ArrayList<JSONObject> jList = new ArrayList<JSONObject>();
		for(int i = 0; i < jArray.length(); i++){
			try {
				JSONObject jObject = jArray.getJSONObject(i);
				jList.add(jObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return jList;
	}
	
	public HashMap<String, String> parseObject(JSONObject jObject){
		Iterator<String> keys = jObject.keys();
		HashMap<String, String> map = new HashMap<String, String>(); 
		while(keys.hasNext()){
			String key = keys.next();
			String value = jObject.optString(key);
	        map.put(key, value);
		}
		return map;
	}
	
	@Override
	public ArrayList<HashMap<String, String>> parseProcesses(String response){
		JSONArray initArray = null;
		ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
		
		try {
			JSONObject initObject = null;
			initObject = new JSONObject(response);
			initArray = initObject.optJSONArray(DEFINITIONS);
			for(JSONObject jObject : parseArray(initArray)){
				HashMap<String, String> map = parseObject(jObject);
				mapList.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mapList;
	}
	
	@Override
	public ArrayList<HashMap<String, String>> parseInstances(String response){
		JSONArray initArray = null;
		ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
		
		try {
			JSONObject initObject = null;
			initObject = new JSONObject(response);
			initArray = initObject.optJSONArray(INSTANCES);
			for(JSONObject jObject : parseArray(initArray)){
				HashMap<String, String> map = parseObject(jObject);
				map.put("currentNodeName", parseObject(jObject.optJSONObject("rootToken")).get("currentNodeName"));
				mapList.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mapList;
	}
	
	@Override
	public ArrayList<HashMap<String, String>> parseNodes(String response){
		ArrayList<HashMap<String, String>> mapList = new ArrayList<HashMap<String, String>>();
		try {
			//JSONArray initArray = null;
			JSONArray initArray = new JSONArray(response);
			System.out.println("eins");
			for(JSONObject jObject : parseArray(initArray)){
				System.out.println("zwei");
				HashMap<String, String> map = new HashMap<String, String>();
				JSONObject object = jObject.optJSONObject("activeNode");
				System.out.println("drei");
				map.put("x", object.optString("x"));
				map.put("y", object.optString("y"));
				System.out.println("vier");
				map.put(WIDTH, jObject.optString(WIDTH));
				System.out.println("funf");
				map.put(HEIGHT, jObject.optString(HEIGHT));
				System.out.println("sechs");
				mapList.add(map);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mapList;
	}
}
	 

