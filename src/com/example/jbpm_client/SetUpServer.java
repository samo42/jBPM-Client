package com.example.jbpm_client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SetUpServer extends Activity {
	public final static String SERVER_INTENT = "com.example.jbpm_client.SERVER";
	public static final String SERVER_INFOS = "SERVER_Infos";
	public static final String SERVER = "SERVER";
	
	private EditText field_server;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_server);
        field_server = (EditText) findViewById(R.id.server);
        fillServer();
    }

    public void ok(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
        
        EditText editText = (EditText) findViewById(R.id.server);
        String serverName = editText.getText().toString();
        intent.putExtra(SERVER_INTENT, serverName);
        startActivity(intent);
    	rememberServer();
    }
    
    public void clear(View view) {
    	field_server.setText(null);
    	if (forgetServer() == false) {
    		Toast.makeText(getApplicationContext(), "Error deleting data", Toast.LENGTH_LONG).show();
    	} else {
    		Toast.makeText(getApplicationContext(), "Servername deleted", Toast.LENGTH_LONG).show();
    	}
    }
    
    public String getServer(){
    	String server = field_server.getText().toString();
    	return server;
    }
    
    //fill in servername previously remembered
    public void fillServer(){
        SharedPreferences server = getSharedPreferences(SERVER_INFOS, 0);
        String serverName = server.getString(SERVER, "");
        field_server.setText(serverName);
    }
    
    //save new server name to shared preferences
    public boolean rememberServer(){
    	SharedPreferences settings = getSharedPreferences(SERVER_INFOS, 0);
        return settings.edit()
          .putString(SERVER, getServer())
          .commit();
    }
    
    public boolean forgetServer(){
    	SharedPreferences server = getSharedPreferences(SERVER_INFOS, 0);
    	return server.edit().clear().commit();
    	
    }
}
