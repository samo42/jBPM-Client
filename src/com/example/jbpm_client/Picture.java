package com.example.jbpm_client;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private Bitmap bm = null;


	
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
	    inflater.inflate(R.menu.activity_picture, menu);
	    return true;
	}
			
	//make actions after clicking on menu items
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.picture_save:
				String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
				File file = new File(extStorageDirectory, getInstanceId()+getProcessId()+(this).toString()+".PNG");
				try {
				    FileOutputStream outStream = new FileOutputStream(file);
				    bm.compress(Bitmap.CompressFormat.PNG, 100, outStream);
				    outStream.flush();
				    outStream.close();
				} catch (Exception e) {
				    e.printStackTrace();}
				return true;
		
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
				JsonParser jp = new JsonParserImpl();
				dimensionsList = jp.parseNodes(dimensions);
				PictureEditor pe = new PictureEditorImpl();
				bitmap = pe.combineImages(bitmap, arrow, dimensionsList);
				
			}
			if (operation.equals("logout")){
				rc.logout(server);
				return null;
			}
			return bitmap;
		}
		
		protected void onPostExecute(Bitmap diagram){
			if (diagram != null){
            	Toast.makeText(getApplicationContext(), R.string.picture, Toast.LENGTH_LONG).show();
            	tv = (ImageView) findViewById(R.id.logo);
            	tv.setImageBitmap(diagram);
            	bm = diagram;
            } else {
            	sessionExpired();
            }
		}
	}
}

