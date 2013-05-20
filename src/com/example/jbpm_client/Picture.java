package com.example.jbpm_client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Picture extends Activity {
	public static final String SERVER_INFOS = "SERVER_Infos";
	public static final String SERVER = "SERVER";
	public static final String SETTING_INFOS = "SETTING_Infos";
	private ImageView tv = null;
	private String instanceId = "";
	private String processId = "";
	private String nodes = "";
	private boolean showImage;


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		readIntent();
		TextView tv = new TextView(this);
		tv = (TextView) findViewById(R.id.activeNode);
		tv.setText(getNodes());
		
		if (getImage() == true){
			String[] params = new String[] {getServer(), getInstanceId(), getProcessId(), "get"};
			new showPicture().execute(params);
		}
	}
	
	public void readIntent(){
		Intent intent= getIntent();
		HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra("hashMap");
	    if (map.containsKey("id"))
	    	setInstanceId(map.get("id"));
	    if (map.containsKey("definitionId"))
	    	setProcessId(map.get("definitionId"));
	    if (map.containsKey("diagram")){
	    	setImage(true);
	    } else{
	    	setImage(false);
	    }
	    System.out.println(map.containsKey("currentNodeName"));
	    if (map.containsKey("currentNodeName")){
	    	setNodes(map.get("currentNodeName"));
	    }
	}
	
	public String getServer(){
		SharedPreferences settings = getSharedPreferences(SERVER_INFOS, 0);
        String server = settings.getString(SERVER, "");
		return server;
	}
	
	public String getProcessId(){
		return processId;
	}
	
	public void setProcessId(String procId){
		processId = procId;
	}
	
	public String getInstanceId(){
		return instanceId;
	}
		
	public void setInstanceId(String inId){
		instanceId = inId;
	}
		
	public void setImage(boolean b){
		showImage = b;
	}
	
	public boolean getImage(){
		return showImage;
	}
	
	public void setNodes(String node){
		nodes = node;
	}
	
	public String getNodes(){
		return nodes;
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
				if (getImage() == true){
					String[] params = new String[] {getServer(), getInstanceId(), getProcessId(), "get"};
					new showPicture().execute(params);
				}
				return true;
					
			case R.id.log_out:
				//remove session cookie and show log in screen
				String[] params = new String[] {getServer(), "", "", "logout"};
				new showPicture().execute(params);
				return true;
				}
				return false;
		}

	
	private class showPicture extends AsyncTask<String, Void, Bitmap>{
		
		@Override
		protected Bitmap doInBackground(String... params) {
			String server = params[0];
			String instanceId = params[1];
			String processId = params[2];
			String operation = params[3];
			String diagramUrl = "http://"+ server +"/gwt-console-server/rs/process/definition/"+ processId +"/image";
			String nodesUrl = "http://"+ server +"/gwt-console-server/rs/process/instance/"+ instanceId +"/activeNodeInfo";
			Bitmap bitmap = null;

			
			RestClient rc = new RestClientImpl();
			SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
	        String cookie = settings.getString("COOKIE", "");
			rc.setCookie(cookie);
			
			if (operation.equals("get")){
				ArrayList<HashMap<String, String>> dimensionsList = new ArrayList<HashMap<String, String>>();
				Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
				
				bitmap = rc.getPictureFromResponse(rc.getResponse(diagramUrl));
				HttpResponse response = rc.getResponse(nodesUrl);
				String dimensions = rc.convertResponseToString(response);
				System.out.println("one");
				JsonParser jp = new JsonParserImpl();
				dimensionsList = jp.parseNodes(dimensions);
				System.out.println("twho");
				PictureEditor pe = new PictureEditorImpl();
				System.out.println("3");
				bitmap = pe.combineImages(bitmap, arrow, dimensionsList);
				System.out.println("for");

			}
			if (operation.equals("logout")){
				rc.logout(server);
			}
			return bitmap;

		}
		
		protected void onPostExecute(Bitmap diagram){
			Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
            if (diagram != null){
            	tv = (ImageView) findViewById(R.id.logo);
            	tv.setImageBitmap(diagram);
            }
		}
		
		public ArrayList<ArrayList<String>> parse(String jSONString){
			ArrayList<ArrayList<String>> dimensionList = new ArrayList<ArrayList<String>>();
			
			JSONArray dimensions = null;
			try {
				dimensions = new JSONArray(jSONString);
				
				for(int i = 0; i < dimensions.length(); i++){
					JSONObject jObject = dimensions.getJSONObject(i);
					ArrayList<String> list = new ArrayList<String>();
					
					String width = jObject.getString("width");
					System.out.println(width);
					
					String height = jObject.getString("height");
					System.out.println(height);

					JSONObject nodeObject = jObject.getJSONObject("activeNode");
						
					String nodeWidth = nodeObject.getString("x");
					System.out.println(nodeWidth);
						
					String nodeHeight = nodeObject.getString("y");
					System.out.println(nodeHeight);
					list.add(width);
					list.add(height);
					list.add(nodeWidth);
					list.add(nodeHeight);
					dimensionList.add(list);
				}
					
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return dimensionList;
		}
		
		
		public Bitmap combineImages(Bitmap diagram, ArrayList<ArrayList<String>> dimensionsList){
			Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
			arrow = arrow.createScaledBitmap(arrow, 23, 26, false);
			
			Bitmap combined = null;
				
			int width, height, nodeWidth, nodeHeight = 0; 
			
			ArrayList<String> getSize = dimensionsList.get(0);
		    width = Integer.parseInt(getSize.get( 0 ));
			height = Integer.parseInt(getSize.get( 1 ));
			Bitmap croppedDiagram = Bitmap.createBitmap(diagram, 0, 0, width, height);
			combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		    Canvas comboImage = new Canvas(combined);
			comboImage.drawBitmap(croppedDiagram, 0f, 0f, null);
			for( ArrayList<String> inList : dimensionsList ) {
				nodeWidth = Integer.parseInt(inList.get( 2 ));
				nodeHeight = Integer.parseInt(inList.get( 3 ));
				comboImage.drawBitmap(arrow, nodeWidth-12, nodeHeight-12, null);
			}
			
		    return combined;
		}
	}
}

