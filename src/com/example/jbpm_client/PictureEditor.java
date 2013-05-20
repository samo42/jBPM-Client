package com.example.jbpm_client;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;

public interface PictureEditor {
	public Bitmap combineImages(Bitmap diagram, Bitmap arrow, ArrayList<HashMap<String, String>> dimensionsList);
}
