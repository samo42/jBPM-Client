package com.example.jbpm_client;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ProcessList extends ListActivity {
	public static final String SETTING_INFOS = "SETTING_Infos";
	public static final String SERVER_INFOS = "SERVER_Infos";
	public static final String SERVER = "SERVER";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_process);
	    String[] params = new String[] {getServer(), "get"};
	    new getProcesses().execute(params);	
	 }

	//create menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_process_list, menu);
	    return true;
	}
		
	//make actions after clicking on menu items
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.refresh:
				//execute rest
				String[] params = new String[] {getServer(),"get"};
			    new getProcesses().execute(params);
				return true;
				
			case R.id.log_out:
				//remove session cookie and show log in screen
				String[] param = new String[] {getServer(),"logout"};
			    new getProcesses().execute(param);
				return true;
				}
			return false;
	}

	//get servername
	public String getServer(){
		SharedPreferences settings = getSharedPreferences(SERVER_INFOS, 0);
        String server = settings.getString(SERVER, "");
		return server;
	}
	
	//shows list
	public void populateList(ArrayList<HashMap<String, String>> visibleList){
	    SimpleAdapter adapter = new SimpleAdapter(this, visibleList,R.layout.row_process,new String[] {"name" , "version" , "id"}, new int[] {R.id.name, R.id.version, R.id.id});
		setListAdapter(adapter);
	}
	
	//do action after clicking on list item
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		HashMap<String, String> map = (HashMap<String, String>) l.getItemAtPosition(position);
		Intent intent = new Intent(this, InstancesList.class);
		intent.putExtra("hashMap", map);
		startActivity(intent);
	}
	
	//call this method when session expired
	public void sessionExpired(){
		SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
		if(settings.contains("COOKIE")) {
			SharedPreferences.Editor editor = settings.edit();
            editor.remove("COOKIE");
            editor.commit(); }
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	//background thread downloading and parsing info about processes from web through REST
	private class getProcesses extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>{
		
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			String server = params[0];
			String operation = params[1];
			System.out.println("1");
			RestClient rc = new RestClientImpl();
			SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
	        String cookie = settings.getString("COOKIE", "");
			rc.setCookie(cookie);
			System.out.println("2");
			String responseString = "ERROR";
			if (operation.equals("get")){
				String url = "http://" + server + "/gwt-console-server/rs/process/definitions";
				HttpResponse response = rc.getResponse(url);
				responseString = rc.convertResponseToString(response);
				System.out.println("3");
			}
			else if (operation.equals("logout")){
				rc.logout(server);
			}
			ArrayList<HashMap<String, String>> processList = new ArrayList<HashMap<String, String>>();
			System.out.println("4");
			if (responseString.startsWith("{")){
				JsonParser jp = new JsonParserImpl();
				processList = jp.parseProcesses(responseString);
			} else { 
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("com.example.error", "com.exaple.error");
				processList.add(map);
				}
			System.out.println("5");
			return processList;
		}
		
		protected void onPostExecute(ArrayList<HashMap<String, String>> processList){
			//if prcessList contains special expiration key, call sessionExpired
			if (processList.get(0).containsKey("com.example.error")){
				sessionExpired();
			} else {
				populateList(processList);
				Toast.makeText(getApplicationContext(), R.string.processes_loaded, Toast.LENGTH_LONG).show();
			}
		}
	}
}
