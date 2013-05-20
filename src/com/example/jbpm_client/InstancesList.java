package com.example.jbpm_client;
/**
 * @author Samo
 * 
 * in this activity processes are shown
 */

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.htmlcleaner.TagNode;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * special method to parse JSONObject
 * containing informations about processes 
 *
 * @param	response	String response from REST call
 * @return	ArrayList of HashMaps<String, String>
 */
public class InstancesList extends ListActivity {
	public static final String SETTING_INFOS = "SETTING_Infos";
	public static final String SERVER_INFOS = "SERVER_Infos";
	public static final String SERVER = "SERVER";
	public static String KEY_EMPLOYEE = "employee";
	public static String KEY_REASON = "reason";		
	final Context context = this;
	
	private String processId;
	private boolean form;
	private String name;
	private boolean image;
	
	private int lastPosition = 0;
	private ArrayList<HashMap<String, String>> visibleList = new ArrayList<HashMap<String, String>>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_process);
	    readIntent();
	    //enables context menu on longClick on list item
	    registerForContextMenu(getListView());
	    String[] params = new String[] {getServer(),getprocessId(), "", "get"};
	    new GetInstances().execute(params);
	}

	/**
	 * retrieve data from previous instance, in this case ProcessList as serializable hashmap
	 */
	public void readIntent(){
		Intent intent= getIntent();
	    //String procID = intent.getStringExtra("process"); // will return "FirstKeyValue"
		HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra("hashMap");
		//setprocessId(intent.getStringExtra("com.example.jbpm_client.id"));
	    if (map.containsKey("formUrl")){
	    	setform(true);
	    } else {
	    	setform(false);
	    }
	    if (map.containsKey("diagramUrl")){
	    	setImage(true);
	    } else {
	    	setImage(false);
	    }
	    if (map.containsKey("name"))
	    	setname(map.get("name"));
	    if (map.containsKey("id"))
	    	setprocessId(map.get("id"));
	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {  
		AdapterView.AdapterContextMenuInfo info = 
		        (AdapterView.AdapterContextMenuInfo) menuInfo;
		    lastPosition = info.position;
		
		super.onCreateContextMenu(menu, v, menuInfo);  
		    menu.setHeaderTitle(getprocessId());  
		    menu.add(0, v.getId(), 0, R.string.delete);  
		    menu.add(0, v.getId(), 0, R.string.terminate);  
		}  

	@Override  
	public boolean onContextItemSelected(MenuItem item) {  
		if(item.getTitle().equals(getResources().getString(R.string.delete))){
			String instanceId = visibleList.get(lastPosition).get("id");
			String[] newParams = new String[] {getServer(),getprocessId(), instanceId, "delete"};
			new GetInstances().execute(newParams);
			return true;
			} 
		if(item.getTitle().equals(getResources().getString(R.string.terminate))){
	       String instanceId = visibleList.get(lastPosition).get("id");
			String[] newParams = new String[] {getServer(),getprocessId(), instanceId, "terminate"};
			new GetInstances().execute(newParams);
			return true;
	       	}  
		return false; 
	}  
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_instances_list, menu);
	    return true;
	}
		
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()){
			case R.id.refresh:
				//execute rest
				String[] refreshParams = new String[] {getServer(), getprocessId(), "", "get"};
			    new GetInstances().execute(refreshParams);
					return true;
				
			case R.id.new_instance:
				//start new instance
				if (hasform() == true){
					String[] params = new String[] {getServer(), getprocessId()};
					new GetForm().execute(params);
				} else {
					String[] newParams = new String[] {getServer(), getprocessId(), "", "new1"};
					new GetInstances().execute(newParams);
				}
				return true;
			
			case R.id.log_out:
				//remove session cookie and show log in screen
				String[] logoutParams = new String[] {getServer(), getprocessId(), "", "logout"};
				new GetInstances().execute(logoutParams);
				return true;
				}
			return false;
	}

	/**
	 * Shows new popup window. Here are also ViewElements converting to View,
	 * and inflating. Then it processes the info. It is also happening here
	 * because of not so easy work with View and Context.
	 * @param	list	ArrayList of ViewElements
	 */
	public void fillForm(ArrayList<ViewElement> list){
		
		LayoutInflater li = LayoutInflater.from(context);
		final View promptsView = li.inflate(R.layout.new_instance_form, null);
		
		//needs to be here
		for (ViewElement element : list){
			if (element.getType().equals("EditText")){
				EditText et = new EditText(this);
				if (element.getName() != null){
					et.setTag(element.getName());
					et.setHint(element.getName());
				}
				if (element.getSpecial() != 0){
					et.setInputType(element.getSpecial());
				}
				((ViewGroup) promptsView).addView(et);
			} else
			
			if (element.getType().equals("CheckBox")){
				CheckBox cb = new CheckBox(this);
				if (element.getName() != null)
					cb.setTag(element.getName());
				if (element.getValue() != null)
					cb.setHint(element.getValue());
				if (element.getLabel() != null)
					cb.setText(element.getLabel());
				((ViewGroup) promptsView).addView(cb);
			} else
			
			if (element.getType().equals("RadioGroup")){
				RadioGroup rg = new RadioGroup(this);
				RadioButton rb = new RadioButton(this);
				if (element.getValue() != null)
					rb.setHint(element.getValue());
				if (element.getLabel() != null)
					rb.setText(element.getLabel());
								
				if (element.getName() != null){
					if ((promptsView).findViewWithTag(element.getName()) != null){
						rg = (RadioGroup) (promptsView).findViewWithTag(element.getName());
						rg.addView(rb);
					} else {
						rg.setTag((String) element.getName());
						rg.addView(rb);
						((ViewGroup) promptsView).addView(rg);
					}
				}
			} else
			
			if (element.getType().equals("NumberPicker")){
				NumberPicker np = new NumberPicker(this);
				if (element.getName() != null)
					np.setTag(element.getName());
				
				np.setMinValue(element.getMin());
				np.setMaxValue(element.getMax());
				((ViewGroup) promptsView).addView(np);
			} else
			
			if (element.getType().equals("Spinner")){
				Spinner sp = new Spinner(this);
				
				if (element.getName() != null)
					sp.setTag(element.getName());
				
				if (element.getArray() != null){
					SimpleAdapter spinnerAdapter = new SimpleAdapter(this, element.getArray(),R.layout.select,new String[] {"item", "value"}, new int[] {R.id.select, R.id.hidden});
					sp.setAdapter(spinnerAdapter);
				}
				((ViewGroup) promptsView).addView(sp);
			} else
			
			if (element.getType().equals("TextView")){
				TextView tv = new TextView(this);
				if (element.getName() != null){
				tv.setText(element.getName());
				((ViewGroup) promptsView).addView(tv);
				}
			} else
			
			if (element.getType().equals("HtmlTextView")){
				TextView tv = new TextView(this);
				if (element.getName() != null){
					tv.setText(Html.fromHtml(element.getName()));
				((ViewGroup) promptsView).addView(tv);
				}
			}
			
				
		}
		ScrollView sv = new ScrollView(this);
		sv.addView(promptsView);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		
		// set view to alertdialog builder
		alertDialogBuilder.setView(sv);
		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("OK",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    
			    //GET INFO FROM USER IN LIST
			    ArrayList<String> attributes = new ArrayList<String>();
				    
				for (int i = 0; i < ((ViewGroup)promptsView).getChildCount(); i++){
					View view = ((ViewGroup)promptsView).getChildAt(i);

				    
				    if (view instanceof EditText) {
				    	attributes.add(((EditText) view).getText().toString());
				    	attributes.add(((EditText) view).getHint().toString());
					}
				    else if (view instanceof RadioGroup) {
				    	int button = ((RadioGroup) view).getCheckedRadioButtonId();
				    	attributes.add(((RadioButton)((RadioGroup) view).findViewById(button)).getHint().toString());
				    	attributes.add(((RadioGroup) view).getTag().toString());
					}
				    else if (view instanceof NumberPicker) {
				    	attributes.add(((Integer)((NumberPicker) view).getValue()).toString());
				    	attributes.add(((NumberPicker) view).getTag().toString());
				    }
				    else if (view instanceof CheckBox) {
				    	if (((CheckBox)view).isChecked()){
				    		attributes.add(((CheckBox) view).getHint().toString());
				    		attributes.add(((CheckBox) view).getTag().toString());
				    	}
				    } 
				    else if (view instanceof Spinner) {
				    	attributes.add(((HashMap<String, String>)((Spinner) view).getSelectedItem()).get("value").toString());
				    	attributes.add(view.getTag().toString());
				    }
				 }
			    //SAVE IT TO ARRAY
			    String[] newParams = new String[4+attributes.size()];
			    newParams[0]=getServer();
			    newParams[1]=getprocessId();
			    newParams[2]="";
			    newParams[3]="new";
			    for (int i = 0; i<attributes.size(); i++){
			    	newParams[i+4]=attributes.get(i);
			    }
			    new GetInstances().execute(newParams);
			    }
			  })
			.setNegativeButton("Cancel",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}
	
	
	/**
	 * fills listactivity with data about processes from server
	 * 
	 * @param	list	ArrayList of ViewElements
	 */
	public void populateList(ArrayList<HashMap<String, String>> visibleList){
		for (HashMap<String, String> map : visibleList){
			if (map.containsKey("suspended")){
				if (map.get("suspended").equals("true")){
					map.put("suspended", "Suspended");
				} else
					if (map.get("suspended").equals("false")){
						map.put("suspended", "Running");
					}
			}
		}
		SimpleAdapter adapter = new SimpleAdapter(this, visibleList,R.layout.row_instances,new String[] {"id" , "startDate" , "suspended"}, new int[] {R.id.instance_id, R.id.startdate, R.id.state});
		setListAdapter(adapter);
		setVisibleList(visibleList);
	}
	
	/**
	 * when user clicks on process, mapping of that process is saved and 
	 * new activity is started
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		HashMap<String, String> map = (HashMap<String, String>) l.getItemAtPosition(position);
		if (hasImage() == true)
			map.put("diagram", "diagram");
		Intent intent = new Intent(this, Picture.class);
		intent.putExtra("hashMap", map);
		startActivity(intent);
	}
	
	public void sessionExpired(){
		SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
		if(settings.contains("COOKIE")) {
			SharedPreferences.Editor editor = settings.edit();
            editor.remove("COOKIE");
            editor.commit(); }
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	
	//getry and setry
	/**
	 * get serverName from shared prefs
	 * @return	server	server for connecting to rest interface
	 */
	public String getServer(){
		SharedPreferences settings = getSharedPreferences(SERVER_INFOS, 0);
        String server = settings.getString(SERVER, "");
		return server;
	}
	
	public boolean hasImage(){
		return image;
	}
	
	public void setImage(boolean id){
		image = id;
	}
	
	public void setprocessId(String id){
		processId = id;
	}
	
	public String getprocessId(){
		return processId;
	}
	
	public void setform(boolean id){
		form = id;
	}
	
	public boolean hasform(){
		return form;
	}
	
	public String getname(){
		return name;
	}
	
	public void setname(String id){
		name = id;
	}
	
	public void setVisibleList(ArrayList<HashMap<String, String>> list){
		visibleList = list;
	}
	
	public ArrayList<HashMap<String, String>> getVisibleList(){
		return visibleList;
	}
		
	/**
	 * In this class asynchronious operations on background thread are running.
	 * Mostly REST calls but also json parsing. 
	 */
	private class GetInstances extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>>{
		
		@Override
		protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
			ArrayList<HashMap<String, String>> instancesList = new ArrayList<HashMap<String, String>>(); //novinka
			
			String server = params[0];
			String processId = params[1];
			String instanceId = params[2];
			String operation = params[3];
			String url = "";
			
			RestClient rc = new RestClientImpl();
			SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
	        String cookie = settings.getString("COOKIE", "");
			rc.setCookie(cookie);
			
			if (operation == "terminate") {
				url = "http://"+server+"/gwt-console-server/rs/process/instance/"+ instanceId +"/end/OBSOLETE";
				rc.postData(url, null);
			}
			else if (operation == "delete") {
				url = "http://"+server+"/gwt-console-server/rs/process/instance/" + instanceId + "/delete";
				HashMap<String, String> dataSet = new HashMap<String, String>();
				dataSet.put("delete", "android");
				rc.postData(url, dataSet);
			}
			else if (operation == "new") {
				url = "http://"+server+"/gwt-console-server/rs/form/process/"+ processId +"/complete";
				HashMap<String, String> dataSet = new HashMap<String, String>();
				if (params.length>3){
					for (int i = 4; i<params.length; i++){
						dataSet.put(params[i+1], params[i]);
						i++;
						System.out.println(dataSet.keySet().size());
					}
					rc.postMultipart(url, dataSet);
				}
			}
			else if (operation == "new1"){
				url = "http://"+server+"/gwt-console-server/rs/process/definition/" + processId + "/new_instance";
				rc.postData(url, null);
			}
			
			else if (operation == "logout"){
				rc.logout(server);
				return null;
			}
			
			url = "http://" + server + "/gwt-console-server/rs/process/definition/" + processId + "/instances";
			HttpResponse response = rc.getResponse(url);
			String responseString = rc.convertResponseToString(response);
			if (responseString.startsWith("{")){
				JsonParser jp = new JsonParserImpl();
				instancesList = jp.parseInstances(responseString);
				} else { 
					rc.logout(server);
					return null;
					}
			
				return instancesList;
		}
		
		protected void onPostExecute(ArrayList<HashMap<String, String>> instancesList){
			if(instancesList == null){
				sessionExpired();
			} else {
				populateList(instancesList);
				Toast.makeText(getApplicationContext(), "Proesses loaded", Toast.LENGTH_LONG).show();
			}
		}
		
	}//end asynctask

	/**
	 * asynctask to download html form via REST.
	 * In onPostExecute, fillform is called
	 * 
	 */
	private class GetForm extends AsyncTask<String, Void, ArrayList<ViewElement>>{
		
		protected ArrayList<ViewElement> doInBackground(String... params) {
			String server = params[0];
			String id = params[1];
			String url = "http://" + server + "/gwt-console-server/rs/form/process/" + id + "/render";

			ArrayList<ViewElement> list = new ArrayList<ViewElement>();
			
			
			RestClient rc = new RestClientImpl();
			SharedPreferences settings = getSharedPreferences("SETTING_Infos", 0);
	        String cookie = settings.getString("COOKIE", "");
			rc.setCookie(cookie);
			HttpResponse response = rc.getResponse(url);
			String str = rc.convertResponseToString(response);
			HtmlParser hp = new HtmlParserImpl();
			try{
				TagNode tn = hp.cleanHtml(str);
				list = hp.parse(tn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return list;
		}
		protected void onPostExecute(ArrayList<ViewElement> list){
			fillForm(list);
		}
	}
}
	
	