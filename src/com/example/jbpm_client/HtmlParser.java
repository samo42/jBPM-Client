package com.example.jbpm_client;

import java.util.ArrayList;

import org.htmlcleaner.TagNode;

public interface HtmlParser {
	//public View parseForm(org.htmlcleaner.TagNode tagnode, Context cont);
	public ArrayList<ViewElement> parseForm(org.htmlcleaner.TagNode tagnode);
	public TagNode cleanHtml(String is);
	public ArrayList<ViewElement> parse(TagNode tagNode);
	public String parseTitle(TagNode node);
}
