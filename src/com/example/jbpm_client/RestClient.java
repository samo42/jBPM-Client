package com.example.jbpm_client;

import java.util.HashMap;

import org.apache.http.HttpResponse;

import android.graphics.Bitmap;

public interface RestClient {
	public void setCookie(String cookie);
	public HttpResponse getResponse(String url);
	public String convertResponseToString(HttpResponse response);
	public Bitmap getPictureFromResponse(HttpResponse response);
	public HttpResponse postMultipart(String url, HashMap<String, String> dataSet);
	public HttpResponse postData(String url, HashMap<String, String> dataSet);
	public String initCookie(HttpResponse response);
	public void logout(String server);
}
