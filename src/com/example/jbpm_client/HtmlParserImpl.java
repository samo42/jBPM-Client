package com.example.jbpm_client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import android.text.InputType;

public class HtmlParserImpl implements HtmlParser {
	/**
	 * @author Samo
	 * 
	 * Class for parsing simple html forms into special ViewElements.
	 * It is complicated to create and manage View outside the activity
	 * so here all things are saved into VieElement and retrive later
	 * in activity.
	 * 
	 */
	
	/**
	 * Clean the given string and make it TagNode
	 * 
	 * @param  response	String response from REST call
	 * @return	html TagNode
	 */
	public TagNode cleanHtml(String html){
		// THIS IS WHERE THE HTMLCLEANER COMES IN, I INITIALIZE IT HERE
		HtmlCleaner cleaner = new HtmlCleaner();
		CleanerProperties props = cleaner.getProperties();
		props.setAllowHtmlInsideAttributes(true);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);
		props.setUseEmptyElementTags(true);
					
		TagNode node = null;

		//USE THE CLEANER TO "CLEAN" THE HTML AND RETURN IT AS A TAGNODE OBJECT
		try{
			node = cleaner.clean(html);
		} catch (Exception e) {
		      e.printStackTrace();
		}
		return node;
	}
	
	/**
	 *	Parse the whole html retrieved from cleanHtml method
	 * @param  TagNode  tagnode from HtmlCleaner
	 * @return      ArrayList of ViewElements
	 */
	@Override
	public ArrayList<ViewElement> parse(TagNode tagNode){
		TagNode initNode = tagNode.findElementByName("body", true);
		ArrayList<ViewElement> viewList = new ArrayList<ViewElement>();
		HtmlCleaner cleaner = new HtmlCleaner();
		for (int i = 0 ; i < initNode.getAllChildren().size(); i++){
			if (initNode.getAllChildren().get(i) instanceof org.htmlcleaner.ContentNode){
				ContentNode node = (ContentNode) initNode.getAllChildren().get(i);
				if (node != null){
					//
					ViewElement element = new ViewElement("TextView");
					element.setName(initNode.getAllChildren().get(i).toString());
					viewList.add(element);
					//
				}
			}
			
			if (initNode.getAllChildren().get(i) instanceof TagNode){
				TagNode tn = (TagNode) initNode.getAllChildren().get(i);
				if (tn.getName().equals("form")){
					viewList.addAll(parseForm(initNode));
					} else{
						ViewElement element = new ViewElement("HtmlTextView");
						if (cleaner.getInnerHtml(tn) != null){
							String setElement = "<"+tn.getName()+">"+cleaner.getInnerHtml(tn)+"</"+tn.getName()+">";
							element.setName(setElement);
							viewList.add(element);
						}
						}
					
			}
		}
		return viewList;
	}
	
	/**
	 * Parse Tagnode with name "form". It should have form tag in it. It is usually
	 * caled from parse() method. 
	 *
	 * @param	tagnode	cleaned html from HtmlCleaner
	 * @return	ArrayList of ViewElements
	 */
	public ArrayList<ViewElement> parseForm(TagNode tagnode){
		TagNode initNode = tagnode.findElementByName("form", true);
		ArrayList<ViewElement> viewList = new ArrayList<ViewElement>();
		
		
		for (int i = 0; i <initNode.getAllChildren().size(); i++){
			if (initNode.getAllChildren().get(i) instanceof TagNode){
				TagNode node = (TagNode) initNode.getAllChildren().get(i);
				
					if (node.getName().equals("input")){
						if (node.getAttributeByName("type").equals("text")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							viewList.add(element);
							//
						} else
						
						if (node.getAttributeByName("type").equals("checkbox")){
							//
							ViewElement element = new ViewElement("CheckBox");
							element.setName(node.getAttributeByName("name").toString());
							element.setValue(node.getAttributeByName("value").toString());
							if (initNode.getAllChildren().get(i+1) instanceof ContentNode){
								element.setValue(initNode.getAllChildren().get(i+1).toString());
								i++;
							}
							viewList.add(element);
							//
						} else
				
						if (node.getAttributeByName("type").equals("radio")){
							//
							ViewElement element = new ViewElement("RadioGroup");
							element.setName(node.getAttributeByName("name").toString());
							element.setValue(node.getAttributeByName("value").toString());
							if (initNode.getAllChildren().get(i+1) instanceof ContentNode){
								element.setValue(initNode.getAllChildren().get(i+1).toString());
								i++;
							}
							viewList.add(element);
							//
						} else
												
						if (node.getAttributeByName("type").equals("date")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_DATE);
							viewList.add(element);
							//
						} else
						
						if (node.getAttributeByName("type").equals("datetime")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_NORMAL);
							viewList.add(element);
							//
						} else
							
						if (node.getAttributeByName("type").equals("datetime-local")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_CLASS_DATETIME|InputType.TYPE_DATETIME_VARIATION_NORMAL);
							viewList.add(element);
							//
						} else
							
						if (node.getAttributeByName("type").equals("email")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
							viewList.add(element);
							//
						} else
						
						if (node.getAttributeByName("type").equals("number")){
							//
							ViewElement element = new ViewElement("NumberPicker");
							element.setName(node.getAttributeByName("name").toString());
							element.setMin(Integer.parseInt(node.getAttributeByName("min").toString()));
							element.setMax(Integer.parseInt(node.getAttributeByName("max").toString()));
							viewList.add(element);
							//
						} else
							
						if (node.getAttributeByName("type").equals("password")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
							viewList.add(element);
							//
						} else
							
						if (node.getAttributeByName("type").equals("range")){
							//
							ViewElement element = new ViewElement("NumberPicker");
							element.setName(node.getAttributeByName("name").toString());
							element.setMin(Integer.parseInt(node.getAttributeByName("min").toString()));
							element.setMax(Integer.parseInt(node.getAttributeByName("max").toString()));
							viewList.add(element);
							//
						}	else{
							ViewElement element = new ViewElement("HtmlTextView");
							if (node.getText() != null){
								HtmlCleaner cleaner = new HtmlCleaner();
								String setElement = "<"+node.getName()+">"+cleaner.getInnerHtml(node)+"</"+node.getName()+">";
								element.setName(setElement);
								viewList.add(element);
							}
						}
					} else
						
					if (node.getName().equals("textarea")){
						//
						ViewElement element = new ViewElement("EditText");
						element.setName(node.getAttributeByName("name").toString());
						element.setSpecial(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
						viewList.add(element);
						//
					} else
					
					if (node.getName().equals("select")){
						//
						ViewElement element = new ViewElement("Spinner");
						element.setName(node.getAttributeByName("name").toString());
						List<TagNode> selectList = node.getAllElementsList(false);
						ArrayList<HashMap<String, String>> adapter = new ArrayList<HashMap<String, String>>();
						for (TagNode select : selectList){
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("item", select.getText().toString());
							map.put("value", select.getAttributeByName("value").toString());
							adapter.add(map);
						}
						element.setArray(adapter);
						viewList.add(element);
						//
					}
				} else
				
				if (initNode.getAllChildren().get(i) instanceof org.htmlcleaner.ContentNode){
					ContentNode node = (ContentNode) initNode.getAllChildren().get(i);
					if (node != null){
						//
						ViewElement element = new ViewElement("TextView");
						element.setName(initNode.getAllChildren().get(i).toString());
						viewList.add(element);
						//
					}
				}	
		}
		return viewList;
	}
}

