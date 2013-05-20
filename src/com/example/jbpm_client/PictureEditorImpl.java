package com.example.jbpm_client;

import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class PictureEditorImpl implements PictureEditor {
	
	@Override
	public Bitmap combineImages(Bitmap diagram, Bitmap arrow, ArrayList<HashMap<String, String>> dimensionsList){
		//Bitmap arrow = BitmapFactory.decodeResource(getResources(), R.drawable.arrow);
		arrow = arrow.createScaledBitmap(arrow, 23, 26, false);
		Bitmap combined = null;
		
		int width, height, nodeWidth, nodeHeight = 0; 
		width = Integer.parseInt(dimensionsList.get(0).get("width"));
		height = Integer.parseInt(dimensionsList.get(0).get("height"));
		
		Bitmap croppedDiagram = Bitmap.createBitmap(diagram, 0, 0, width, height);
		combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas comboImage = new Canvas(combined);
		comboImage.drawBitmap(croppedDiagram, 0f, 0f, null);
		
		for (HashMap<String, String> map : dimensionsList){
			nodeWidth = Integer.parseInt(map.get("x"));
			nodeHeight = Integer.parseInt(map.get("y"));
			comboImage.drawBitmap(arrow, nodeWidth-12, nodeHeight-12, null);
		}
		
		return combined;
	}
}
