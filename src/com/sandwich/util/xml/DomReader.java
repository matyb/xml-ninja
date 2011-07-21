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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomReader implements XmlReader {

	public void read(File xmlFile, List<DeferredInvocation> methodCalls) {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			for(DeferredInvocation method : methodCalls){
				List<String[]> params = getChildNodesParams(doc.getDocumentElement(), method);
				for(String[] paramsArray : params){
					if(paramsArray.length > 0){ // may not have read any elements ***
						method.getMethod().invoke(method.getReceiver(), (Object[])paramsArray);
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private List<String[]> getChildNodesParams(Element element, DeferredInvocation method) {
		List<List<String>> allParams = new ArrayList<List<String>>();
		for(String path : method.getPath()){
			String nodeName = element.getNodeName();
			if(element.getNodeType() == Element.ELEMENT_NODE && nodeName.equals(path)){
				if(method.getPath().indexOf(path) != (method.getPath().size() - 1)){
					element = getNextElement(element, method, path);
				}else{
					addAllParamsFromElementAndSiblings(element, method, allParams, path);
				}
			}
		}
		List<String[]> params = new ArrayList<String[]>();
		for(List<String> listOfParams : allParams){
			params.add(listOfParams.toArray(new String[listOfParams.size()]));
		}
		return params;
	}

	private void addAllParamsFromElementAndSiblings(Element element,
			DeferredInvocation method, List<List<String>> allParams, String path) {
		Node n = element;
		do{
			if(n.getNodeType() == Element.ELEMENT_NODE){
				Map<String, String> params = new LinkedHashMap<String, String>();
				NodeList childNodes = n.getChildNodes();
				NamedNodeMap attributes = n.getAttributes();
				if(attributes != null && attributes.getLength() > 0){
					for(String elementName : method.getElements()){
						Node namedItem = attributes.getNamedItem(elementName);
						String content = null;
						if(namedItem != null){
							content = namedItem.getTextContent();
						}
						params.put(elementName, content);
					}
				}
				for(int i = 0; i < childNodes.getLength(); i++){
					Node no = childNodes.item(i);
					if(no.getNodeType() == Element.ELEMENT_NODE){
						for(String elementName : method.getElements()){
							if(elementName.equals(no.getNodeName())){
								String content = no.getTextContent();
								if(content != null){
									params.put(elementName, content);
									break;
								}
							}
						}
					}
				}
				allParams.add(new ArrayList<String>(params.values())); //ordered via linked hashmap impl
			}
			n = n.getNextSibling();
		}while(n != null && (n.getNodeType() != Element.ELEMENT_NODE || path.equals(n.getNodeName())));
	}

	private Element getNextElement(Element element, DeferredInvocation method,
			String path) {
		String nodeName;
		NodeList nodeList = element.getElementsByTagName(method.getPath().get(method.getPath().indexOf(path) + 1));
		for(int i = 0; i < nodeList.getLength(); i++){
			Element n = (Element)nodeList.item(i);
			nodeName = n.getNodeName();
			if(n.getNodeType() == Element.ELEMENT_NODE && nodeName.equals(method.getPath().get(method.getPath().indexOf(path) + 1))){
				element = n;
				break;
			}
		}
		return element;
	}
	
}
