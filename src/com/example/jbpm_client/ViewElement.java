package com.example.jbpm_client;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewElement {
	
	public ViewElement(String type){
		setType(type);
	}
	
	private String value = null;
	private String name = null;
	private String type = null;
	private String label = null;
	private int special = 0;
	private int min = 0;
	private int max = 0;
	private ArrayList<HashMap<String, String>> list = null;
	
	
	public String getLabel(){
		return label;
	}
	
	public void setLabel(String lab){
		label = lab;
	}
	
	public int getMin(){
		return min;
	}
	
	public void setMin(int mi){
		min = mi;
	}
	
	public int getMax(){
		return max;
	}
	
	public void setMax(int ma){
		max = ma;
	}
	
	public int getSpecial(){
		return special;
	}
	
	public void setSpecial(int spec){
		special = spec;
	}
	
	public String getType(){
		return type;
	}
	
	public void setType(String typ){
		type = typ;
	}
	
	public String getValue(){
		return value;
	}
	
	public void setValue(String val){
		value = val;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String nam){
		name = nam;
	}
	
	public ArrayList<HashMap<String, String>> getArray(){
		return list;
	}
	
	public void setArray(ArrayList<HashMap<String, String>> lis){
		list = lis;
	}
}
