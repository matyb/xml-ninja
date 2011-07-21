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
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

class DeferredInvocation {
	private final Object receiver;
	private final List<String> path;
	private final Method method;
	private final List<String> elements;

	public DeferredInvocation(Object receiver, Class<?>[] args, String methodName, 
			List<String> path, String... elements) {
		this.receiver = receiver;
		try {
			this.method = receiver.getClass().getMethod(methodName, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		this.elements = Arrays.asList(elements);
		this.path = path;
	}
	
	public static DeferredInvocation getInstance(Method m, Object lReceiver) {
		CallFor annotation = m.getAnnotation(CallFor.class);
		if(annotation != null){
			return getInstance(annotation.path(), m.getName(), lReceiver, (String[])annotation.args());
		}
		return null;
	}
	
	public static DeferredInvocation getInstance(String path, String methodName, Object receiver, String... params) {
		if(path == null){
			throw new IllegalArgumentException("path is required");
		}else if(methodName == null){
			throw new IllegalArgumentException("methodName is required");
		}else if(receiver == null || receiver instanceof Boolean){
			throw new IllegalArgumentException("receiver is required");
		}else{
			path = path.replace("/", " ");
			path = path.trim();
			List<String> pathList = Arrays.asList(path.contains(" ") ? path.split(" ") : new String[]{path});
			if(params.length == 0){
				Class<?>[] args = new Class[1];
				Arrays.fill(args, String.class);
				return new DeferredInvocation(receiver, args, methodName,
						pathList.size() > 1 ? pathList.subList(0, pathList.size() - 1) : pathList,
						path.substring(path.lastIndexOf(" ") + 1));
			}else{
				Class<?>[] args = new Class[params.length];
				Arrays.fill(args, String.class);
				return new DeferredInvocation(receiver, args, methodName, pathList, 
					Arrays.asList(params).toArray(new String[params.length]));
			}
		}
	}
	
	Object getReceiver() {
		return receiver;
	}

	List<String> getPath() {
		return path;
	}

	Method getMethod() {
		return method;
	}

	List<String> getElements() {
		return elements;
	}
	
	@Override
	public String toString() {
		return "DeferredInvocation [path=" + path + ", method=" + (method == null ? null : method.getName())  
				+ ", elements=" + elements + "]";
	}
}
