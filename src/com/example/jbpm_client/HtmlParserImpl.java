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
			//node = cleaner.clean(html);
			//node = cleaner.clean("<html><body><h1>hello</h1><i>hello</i><body></html>");
			node = cleaner.clean("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1250\"><style type=\"text/css\"></style></head><body><h2>Employee evaluation</h2><hr>Please perform a self-evalutation.<br><br>Reason:a<br><br>Please fill in the following evaluation form:<form action=\"http://localhost:8080/gwt-console-server/rs/form/task/17/complete\" method=\"POST\" enctype=\"multipart/form-data\">Rate the overall performance:<input type=\"password\" name=\"aaab\"><h1>hello</h1><i>hello</i> <input type=\"radio\" name=\"aaa\" value=\"1\"> 1 <input type=\"radio\" name=\"aaa\" value=\"2\"> 2  <input type=\"number\" name=\"aaa\" min=\"1\" max=\"3\"> <select name=\"performance\">  <option value=\"outstanding\">Outstanding</option>  <option value=\"exceeding\">Exceeding expectations</option>  <option value=\"acceptable\">Acceptable</option>  <option value=\"below\">Below average</option></select><br><br>Check any that apply:<br><input type=\"checkbox\" name=\"initiative\" value=\"initiative\">Displaying initiative<br><input type=\"checkbox\" name=\"change\" value=\"change\">Thriving on change<br><input type=\"checkbox\" name=\"communication\" value=\"communication\">Good communication skills<br><br><input type=\"submit\" value=\"Complete\"></form></body></html>");
		} catch (Exception e) {
		      e.printStackTrace();
		}
		return node;
	}
	
	@Override
	public ArrayList<ViewElement> parse(TagNode tagNode){
		TagNode initNode = tagNode.findElementByName("body", true);
		ArrayList<ViewElement> viewList = new ArrayList<ViewElement>();
		HtmlCleaner cleaner = new HtmlCleaner();
		System.out.println(".........."+cleaner.getInnerHtml(initNode));
		//String title = parseTitle(tagNode);
		//ViewElement element = new ViewElement("title");
		//element.setLabel(title);
		//viewList.add(element);
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
							//System.out.println("."+tn.getText());
							//System.out.println(cleaner.getInnerHtml(tn));
							System.out.println(setElement);
							element.setName(setElement);
							viewList.add(element);
						}
						}
					
			}
		}
		return viewList;
	}
	
	public ArrayList<ViewElement> parseForm(TagNode tagnode){
		TagNode initNode = tagnode.findElementByName("form", true);
		//TagNode initNode = tagnode;
		System.out.println(initNode.getText());
		ArrayList<ViewElement> viewList = new ArrayList<ViewElement>();
		
		
		for (int i = 0; i <initNode.getAllChildren().size(); i++){
			System.out.println("new node"+initNode.getAllChildren().get(i).getClass().getName());
			
			if (initNode.getAllChildren().get(i).getClass().getName().equals(TagNode.class.getName())){
				TagNode node = (TagNode) initNode.getAllChildren().get(i);
				
					if (node.getName().equals("input")){
						if (node.getAttributeByName("type").equals("text")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							viewList.add(element);
							//
						} else
						
						if (node.getAttributeByName("type").equals("submit")){
							//batoniky
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
							HashMap<String, String> map = new HashMap<String, String>();
							//
							ViewElement element = new ViewElement("RadioGroup");
							element.setName(node.getAttributeByName("name").toString());
							element.setValue(node.getAttributeByName("value").toString());
							element.setLabel(node.getText().toString());
							viewList.add(element);
							//
						} else
												
						if (node.getAttributeByName("type").equals("date")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_DATETIME_VARIATION_DATE);
							viewList.add(element);
							//
						} else
						
						if (node.getAttributeByName("type").equals("datetime")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_DATETIME_VARIATION_NORMAL);
							viewList.add(element);
							//
						} else
							
						if (node.getAttributeByName("type").equals("datetime-local")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_DATETIME_VARIATION_NORMAL);
							viewList.add(element);
							//
						} else
							
						if (node.getAttributeByName("type").equals("email")){
							//
							ViewElement element = new ViewElement("EditText");
							element.setName(node.getAttributeByName("name").toString());
							element.setSpecial(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
							viewList.add(element);
							//
						} else
						
						if (node.getAttributeByName("type").equals("file")){
							//to tam nebude
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
							element.setSpecial(InputType.TYPE_TEXT_VARIATION_PASSWORD);
							viewList.add(element);
							//
						} else
							
						if (node.getAttributeByName("type").equals("range")){
							
/*							HashMap<String, String> map = new HashMap<String, String>();
							map.put("type", "SeekBar");
							map.put("tag", node.getAttributeByName("name").toString());
							map.put("min", node.getAttributeByName("min").toString());
							map.put("max", node.getAttributeByName("max").toString());
							list.add(map);*/
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
						element.setSpecial(InputType.TYPE_TEXT_VARIATION_PASSWORD);
						viewList.add(element);
						//
					} else
					
					if (node.getName().equals("select")){
						//
						System.out.println("spin1");
						ViewElement element = new ViewElement("Spinner");
						element.setName(node.getAttributeByName("name").toString());
						System.out.println("spin2");
						List<TagNode> selectList = node.getAllElementsList(false);
						ArrayList<HashMap<String, String>> adapter = new ArrayList<HashMap<String, String>>();
						for (TagNode select : selectList){
							HashMap<String, String> map = new HashMap<String, String>();
							map.put("item", select.getText().toString());
							map.put("value", select.getAttributeByName("value").toString());
							adapter.add(map);
							System.out.println("spin3");
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
	
	@Override
	public String parseTitle(TagNode node){
		String title = "";
		if (node.findElementByName("h2", true) != null){
			title = node.findElementByName("h2", true).getText().toString();
		}
				
		return title;
	}
	
}//end


