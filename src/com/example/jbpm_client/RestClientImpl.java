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
	/**
	 * @author Samo
	 * Simple restclient allows user to authentifiate, send and retrieve data
	 * and convert data to String format. It also alows user to set
	 * session cookie, so user dont have to login every time.
	 * 	
	 */
	
	private final HttpClient Client = new DefaultHttpClient();
	private String cookie = null;
	
	public String getCookie(){
		return cookie;
	}
	
	@Override
	public void setCookie(String cookie){
		this.cookie = cookie;
	}
	
	/**
	 * basic GET REST call
	 * @param	url	full url
	 * @return HttpResponse
	 */
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
	
	/**
	 * basic POST REST call
	 * allow user to post key-value pairs
	 * @param url	full url
	 * @param	dataSet	HashMap<String, String> containing data to send
	 * @return HttpResponse
	 */
	@Override
	public HttpResponse postData(String url, HashMap<String, String> dataSet){
		HttpPost request = new HttpPost(url);
		HttpResponse response = null;
		try {
			//set Session Id
			request.setHeader("Cookie", "JSESSIONID="+getCookie());
			List<NameValuePair> formsParam=new ArrayList<NameValuePair>();
	    	//save all values to list
	    	if (dataSet != null){
				for (Map.Entry<String, String> entry : dataSet.entrySet()) {
	    			formsParam.add(new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString()));
	    		}
	    	}
	    	//set list as entity
	    	request.setEntity(new UrlEncodedFormEntity(formsParam));
	        //get the response
	    	response = Client.execute(request);
	    	   } catch (IOException e) {
	        	e.printStackTrace();
	    }
		return response;
	}
	
	/**
	 * POST REST call with sending data in MIME-text form
	 * @param	url	full url
	 * @param	dataSet	HashMap<String, String> containing data to send
	 * @return HttpResponse
	 */
	@Override
	public HttpResponse postMultipart(String url, HashMap<String, String> dataSet){
		HttpPost request = new HttpPost(url);
		HttpResponse response = null;
		try {
			//set Session Id
			request.setHeader("Cookie", "JSESSIONID="+getCookie());
			MultipartEntity multipart = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
	    	//save all values to list
	    	if (dataSet != null){
	    		for (Map.Entry<String, String> entry : dataSet.entrySet()) {
	    			multipart.addPart(entry.getKey().toString(), new StringBody(entry.getValue().toString()));
	    		}
	    	}
	    	//set list as entity
	    	request.setEntity(multipart);
	        //get the response
	    	response = Client.execute(request);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    }
		return response;
	}
	
	/**
	 *	converting HttpResponse to String. 
	 *	@param HttpResponse
	 *	@return	String representation 
	 */
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
	
	/**
	 * method wrote for diagram, converting HttpResponse to bitmap
	 * @param	HttpResponse
	 * @return	Bitmap
	 */
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
	
	/**
	 * Catch cookie from head of first HTTP request and save it for later
	 * @param	HttpResponse
	 * @return	String representation
	 */
	@Override
	public String initCookie(HttpResponse response){ 
		Header[] cookie = response.getHeaders("Set-Cookie");
		String cookieString = null;
		//String cookieString = "";
		if (cookie.length == 1){
			String wholeString = cookie[0].toString();
		Pattern pattern = Pattern.compile("(Set-Cookie: JSESSIONID=)(.+)(;)(.+)");
		Matcher regexMatcher = pattern.matcher(wholeString);
		while (regexMatcher.find()) {
			cookieString = regexMatcher.group(2);
		}
		}
		return cookieString;
	}
	
	/**
	 * unvalidate session id, cookie we store will not be valid after this POST call
	 * @param servername/domain
	 */
	@Override
	public void logout(String server){
		HttpPost request = new HttpPost("http://"+server+"/gwt-console-server/rs/identity/sid/invalidate");
		HttpResponse response = null;
		try {
			//set Session Id
			request.setHeader("Cookie", "JSESSIONID="+getCookie());
			//get the response
	    	response = Client.execute(request);
	    	   } catch (IOException e) {
	        	e.printStackTrace();
	    }
		convertResponseToString(response);
	}

}
