package com.example.jbpm_client;

import java.util.ArrayList;

import org.htmlcleaner.TagNode;

/**
 * @author Samo
 */
public interface HtmlParser {
	public ArrayList<ViewElement> parseForm(TagNode tagnode);
	public TagNode cleanHtml(String response);
	public ArrayList<ViewElement> parse(TagNode tagNode);
}
