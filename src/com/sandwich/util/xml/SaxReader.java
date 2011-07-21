package com.sandwich.util.xml;

/* Copyright 2011 Mat Bentley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.*/
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxReader extends DefaultHandler implements XmlReader {

	private Map<String, List<DeferredInvocation>> methodsByPath;
	private Params currentParams = new Params();
	private String currentPath = "";
	private String currentElementName;

	/**
	 * Decorate file access, and prepare for handling of sax parsing.
	 * Immediately invokes deferred invocations following completion of file
	 * reading.
	 * 
	 * @param xmlFile
	 *            the file this object is reading
	 * @param methods
	 *            what to invoke when encountering an expected element
	 */
	public void read(File xmlFile, List<DeferredInvocation> methods) throws Exception {
		methodsByPath = getMethodsByPathMap(methods);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(xmlFile, this);
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		List<DeferredInvocation> paramsByName = methodsByPath.get(currentPath);
		boolean hadMatchingAttributes = false;
		if(attributes.getLength() > 0){
			if(paramsByName == null){
				paramsByName = methodsByPath.get(currentPath += new StringBuilder("/").append(qName).toString());
			}
			for(DeferredInvocation w : paramsByName){
				for(String elem : w.getElements()){
					String value = attributes.getValue(elem);
					currentParams.put(elem, value);
					if(value != null){
						hadMatchingAttributes = true;
					}
				}
			}
		}
		if(paramsByName != null && paramsByNameContainsElementExpectation(paramsByName, qName)){
			currentElementName = qName;
		}else{
			currentElementName = null;
			if(!hadMatchingAttributes){
				currentPath += new StringBuilder("/").append(qName).toString();
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		currentElementName = null;
		if(currentPath.endsWith(qName)){
			List<DeferredInvocation> list = methodsByPath.get(currentPath);
			if(list != null && currentParams.size() > 0){
				for(DeferredInvocation d : list){
					List<String> elements = d.getElements();
					String[] params = new String[elements.size()];
					for(int i = 0; i < elements.size(); i++){
						String elem = elements.get(i);
						if(currentParams.containsKey(elem)){
							params[i] = currentParams.get(elem);
						}
					}
					try {
						if(params.length > 0){
							d.getMethod().invoke(d.getReceiver(), (Object[]) params);
						}
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
			currentParams.clear();
			currentPath = currentPath.substring(0, (currentPath.length() - qName.length() - 1));
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		char[] content = new char[length];
		int c = 0;
		for(int i = start; i < start + length; i++){
			content[c++] = ch[i];
		}
		String contentString = new String(content);
		if(currentElementName != null && !"".equals(contentString.trim())){
			currentParams.put(currentElementName, contentString);
		}
	}

	/**
	 * Do any of the DeferredInvocations passed in have an element equal to the passed in qName? 
	 * @param methods
	 * @param qName
	 * @return true it contains an element with that name, false - it doesnt
	 */
	private boolean paramsByNameContainsElementExpectation(List<DeferredInvocation> methods, String qName) {
		for(DeferredInvocation method : methods){
			for(String elementName : method.getElements()){
				if(qName.equals(elementName)){
					return true;
				}
			}
		}
		return false;
	}
	
	private Map<String, List<DeferredInvocation>> getMethodsByPathMap(List<DeferredInvocation> methods) {
		Map<String, List<DeferredInvocation>> methodsByPath = new LinkedHashMap<String, List<DeferredInvocation>>();
		for(DeferredInvocation method : methods){
			String pathString = constructStringPath(method.getPath());
			List<DeferredInvocation> wrappers = methodsByPath.get(pathString);
			if(wrappers == null){
				wrappers = new ArrayList<DeferredInvocation>();
				methodsByPath.put(pathString, wrappers);
			}
			wrappers.add(method);
		}
		return methodsByPath;
	}

	private String constructStringPath(List<String> path) {
		StringBuilder sb = new StringBuilder();
		for(String p : path){
			sb.append("/").append(p.replaceAll("/", ""));
		}
		return sb.toString();
	}

	private static class Params extends HashMap<String, String> {
		private static final long serialVersionUID = -7102785488635017048L;
	}
	
}
