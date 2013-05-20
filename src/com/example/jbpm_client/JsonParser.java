package com.example.jbpm_client;

import java.util.ArrayList;
import java.util.HashMap;

public interface JsonParser {
	public ArrayList<HashMap<String, String>> parseProcesses(String response);
	public ArrayList<HashMap<String, String>> parseInstances(String response);
	public ArrayList<HashMap<String, String>> parseNodes(String response);
}
