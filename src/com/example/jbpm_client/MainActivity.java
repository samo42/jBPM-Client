package com.example.jbpm_client;

import java.util.HashMap;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Main Activity is the first activity user
 * sees when using application. 
 *
 */
public class MainActivity extends Activity {
	public static final String SETTING_INFOS = "SETTING_Infos";
	public static final String SERVER_INFOS = "SERVER_Infos";
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";
	public static final String SERVER = "SERVER";
	public static String KEY_USERNAME = "j_username";
	public static String KEY_PASSWORD = "j_password";		
	private EditText fieldName;
	private EditText fieldPass;
	private CheckBox cb;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //initialize EditText fields
        fieldName = (EditText) findViewById(R.id.username);
        fieldPass = (EditText) findViewById(R.id.password);
        System.out.println("3");
                
        //initialize checkBox
        cb=(CheckBox)findViewById(R.id.rememberBox);
        fillUsernameAndPassword();
     }
	
	/**
	 * When user presses login button, authentization is executed. 
	 *
	 */
	public void login(View view) {
    	if (cb.isChecked()) {
    		rememberUsernameAndPassword();
    		}
    	if (getServer().length()<1){
    		Toast.makeText(getApplicationContext(), R.string.configure_server, Toast.LENGTH_LONG).show();
    	} else {
    		String params[] = new String[] {getUsername(), getPassword(), getServer()};
    		new logInAsyncTask().execute(params);
    	}
	}
    
	/**
	 * if log in was succesful, show process instances 
	 */
	public void resume(){
		Intent intent = new Intent(this, ProcessList.class);
    	startActivity(intent);
	}
	
	/**
	 * create menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
	    return true;
	}
	
	/**
	 * clicking on them
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.set_up_server:
				//starts SetUpServer Activity
				startActivity(new Intent(this, SetUpServer.class));
				return true;
			
			case R.id.clear_user_data:
				//remove name, password and servername from shared prefs
				SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);
				settings.edit().clear().commit();
				SharedPreferences server = getSharedPreferences(SERVER_INFOS, 0);
				server.edit().clear().commit();
				fieldName.setText(null);
				fieldPass.setText(null);
				return true;
			}
		return false;
	}
	
	/**
	 * fill in username and password previously remembered from shared prefs
	 */
    public void fillUsernameAndPassword(){
        SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);
        String username = settings.getString(USERNAME, "");
        String password = settings.getString(PASSWORD, "");
        fieldName.setText(username);
        fieldPass.setText(password);
    }
    
    /**
     * save new username and password to shared preferences
     */
    public void rememberUsernameAndPassword(){
    	SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);
        settings.edit()
          .putString(USERNAME, getUsername())
          .putString(PASSWORD, getUsername())
          .commit();

	}
    
    //get username
    public String getUsername(){
    	String username = fieldName.getText().toString();
    	return username;
    }
    
    //get password
    public String getPassword(){
    	String password = fieldPass.getText().toString();
    	return password;
    }
    
    /**
     * get servername from shared prefs
     */
    public String getServer(){
		SharedPreferences settings = getSharedPreferences(SERVER_INFOS, 0);
        String server = settings.getString(SERVER, "");
        return server;
	}
    

    /**
     * starts new thread, authentificates in user and saves session cookie into shared prefs
     */
    private class logInAsyncTask extends AsyncTask<String, Boolean, Boolean>{
		
		@Override
		protected Boolean doInBackground(String... params) {
			//get parameters needed to log in
			String username = params[0];
			String password = params[1];
			String server = params[2];
			//put username and password into hashmap
			HashMap<String, String> dataSet = new HashMap<String, String>();
			dataSet.put(KEY_USERNAME,username);
			dataSet.put(KEY_PASSWORD,password);
			//make URL's
			String sidUrl = "http://"+server+"/gwt-console-server/rs/identity/secure/sid";
			String authUrl = "http://"+server+"/gwt-console-server/rs/process/j_security_check";
			//execute rest calls
			RestClient rc = new RestClientImpl();
			HttpResponse authResponse = rc.getResponse(sidUrl);
			String cookie = rc.initCookie(authResponse);
			rc.convertResponseToString(authResponse);
			HttpResponse response = rc.postData(authUrl, dataSet);
			String SID = rc.convertResponseToString(response);
			//save SID cookie into shared preferences
			SharedPreferences settings = getSharedPreferences(SETTING_INFOS, 0);
	        settings.edit()
	          .putString("COOKIE", cookie)
	          .commit();
			
	        if (SID.equals(cookie)){
	        	System.out.println("5");
	        	return true;
		    }
		    return false;
		}
		
		protected void onPostExecute(Boolean succes){
			if ((succes != null) && (succes == true)){
				Toast.makeText(getApplicationContext(), R.string.log_in_success, Toast.LENGTH_LONG).show();
				resume();
			} else {
				Toast.makeText(getApplicationContext(), R.string.log_in_unsuccess, Toast.LENGTH_LONG).show();
			}
		}
    }
}
