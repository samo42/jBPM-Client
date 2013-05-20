package com.example.jbpm_client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class RestClientImpl implements RestClient {
	
	private final HttpClient Client = new DefaultHttpClient();
	private String cookie = null;
	
	public String getCookie(){
		return cookie;
	}
	
	@Override
	public void setCookie(String cookie){
		this.cookie = cookie;
	}
	
	@Override
	public HttpResponse getResponse(String url){
		HttpGet request = new HttpGet(url);
		HttpResponse response = null;
		
		try {
			//set Session Id
			request.setHeader("Cookie", "JSESSIONID="+getCookie());
			//execute REST and get the response
			response = Client.execute(request);
		} catch (Exception e) {
	      e.printStackTrace();
		}
		return response; 
	}
	
	@Override
	public HttpResponse postData(String url, HashMap<String, String> dataSet){
		HttpPost post = new HttpPost(url);
		HttpResponse response = null;
		try {
	    	List<NameValuePair> formsParam=new ArrayList<NameValuePair>();
	    	//save all values to list
	    	for (Map.Entry<String, String> entry : dataSet.entrySet()) {
	        	formsParam.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
	        }
	    	//set list as entity
	    	post.setEntity(new UrlEncodedFormEntity(formsParam));
	        //get the response
	    	response = Client.execute(post);
	    	   } catch (IOException e) {
	        	e.printStackTrace();
	    }
		return response;
	}
	
	@Override
	public HttpResponse postMultipart(String url, HashMap<String, String> dataSet){
		HttpPost post = new HttpPost(url);
		HttpResponse response = null;
		try {
			MultipartEntity multipart = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
	    	//save all values to list
	    	for (Map.Entry<String, String> entry : dataSet.entrySet()) {
	        	multipart.addPart(entry.getKey().toString(), new StringBody(entry.getValue().toString()));
	        }
	    	//set list as entity
	    	post.setEntity(multipart);
	        //get the response
	    	response = Client.execute(post);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    }
		return response;
	}
	
	@Override
	public String convertResponseToString(HttpResponse response){
		StringBuilder sb = new StringBuilder();
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader (response.getEntity().getContent()));
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();	
	}
	
	@Override
	public Bitmap getPictureFromResponse(HttpResponse response){
		Bitmap bitmap = null;
		try{
			byte[] bytes = EntityUtils.toByteArray(response.getEntity());
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	@Override
	public String initCookie(HttpResponse response){ 
		Header[] cookie = response.getHeaders("Set-Cookie");
		String cookieString = null;
		//String cookieString = "";
		if (cookie.length == 1){
			//cookieString = cookie[0].getValue().toString();
			String wholeString = cookie[0].toString();
		Pattern pattern = Pattern.compile("(Set-Cookie: JSESSIONID=)(.+)(;)(.+)");
		Matcher regexMatcher = pattern.matcher(wholeString);
		while (regexMatcher.find()) {
			cookieString = regexMatcher.group(2);
		}
		}
		return cookieString;
	}
	
	@Override
	public void logout(String server){
		HttpPost post = new HttpPost("http://"+server+"/gwt-console-server/rs/identity/sid/invalidate");//TODO
		HttpResponse response = null;
		try {
	    	//get the response
	    	response = Client.execute(post);
	    	   } catch (IOException e) {
	        	e.printStackTrace();
	    }
		convertResponseToString(response);
	}

}
