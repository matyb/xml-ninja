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
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class XmlNinja {

	private static final String NULL_RECEIVER_MSG = "a default receiver is required if adding method call with no receiver";
	private final Object receiver;
	private final List<DeferredInvocation> methodCalls;
	
	public XmlNinja(){
		this(null);
	}
	
	public XmlNinja(Object receiver){
		this.receiver = receiver;
		methodCalls = new ArrayList<DeferredInvocation>();
	}

	public XmlNinja addCallMethod(String...params) {
		methodCalls.add(DeferredInvocation.getInstance(params[0], params[1],
				getReceiver(), Arrays.asList(params).subList(2, params.length).toArray(new String[params.length - 2])));
		return this;
	}

	public XmlNinja addCallMethod(Object receiver, String...params) {
		methodCalls.add(DeferredInvocation.getInstance(params[0], params[1],
				require(NULL_RECEIVER_MSG, receiver), Arrays.asList(params).subList(2, params.length).toArray(new String[params.length - 2])));
		return this;
	}
	
	/**
	 * !IMPORTANT
	 * this getter throws a NullPointerException if receiver is null
	 * @return the receiver if it exists, or an NPE thrown if it doesn't
	 */
	private Object getReceiver() {
		return require(NULL_RECEIVER_MSG, receiver);
	}
	
	private <T> T require(String msg, T receiver) {
		if(receiver == null){
			throw new NullPointerException(msg);
		}
		return receiver;
	}

	public XmlNinja mapFromAnnotations(){
		return mapFromAnnotations(getReceiver());
	}
	
	public XmlNinja mapFromAnnotations(Object receiver) {
		for(Method m : receiver.getClass().getMethods()){
			DeferredInvocation deferredInvocation = DeferredInvocation.getInstance(m, receiver);
			if(deferredInvocation != null){
				methodCalls.add(deferredInvocation);
			}
		}
		return this;
	}
	
	public void parse(String fileName) throws Exception {
		File file = new File(fileName);
		parse(file, new SaxReader());
	}
	
	public void parse(String fileName, XmlReader reader) throws Exception{
		parse(new File(fileName), reader);
	}

	private void parse(File file, XmlReader reader) throws Exception {
		if(file.exists()){
			reader.read(file, methodCalls);
		}else{
			throw new RuntimeException(new FileNotFoundException(file.getAbsolutePath()+" was not found"));
		}
	}
	
	public String toString(){
		return getClass().getSimpleName() + "[" + "Receiver: "+receiver + " [" + methodCalls.toString() + "]]";
	}
	
}
