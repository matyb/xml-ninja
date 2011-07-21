package com.sandwich.util.xml;

/* Copyright 2011 Mat Bentley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.*/
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

import org.junit.Test;

public class DeferredInvocationTest {

	@Test(expected = RuntimeException.class)
	public void testConstructionNullPath() throws Exception {
		DeferredInvocation.getInstance(null, "someMethodThatTakesString", this);
	}

	@Test(expected = RuntimeException.class)
	public void testConstructionNullMethodName() throws Exception {
		DeferredInvocation.getInstance("/1/2", null, this);
	}

	@Test(expected = RuntimeException.class)
	public void testConstructionBadMethodName() throws Exception {
		DeferredInvocation.getInstance("/1/2", "meh", this);
	}

	@Test(expected = RuntimeException.class)
	public void testConstructionNullReceiver() throws Exception {
		DeferredInvocation.getInstance("/1/2", "someMethodThatTakesString",
				null);
	}

	@Test
	public void testConstructionReceiverAndTwoParams() throws Exception {
		Method method = getClass().getMethod("someMethodThatTakesString",
				String.class);
		DeferredInvocation i = DeferredInvocation.getInstance("1",
				method.getName(), this);
		assertEquals(Arrays.asList("1"), i.getElements());
		assertEquals(method, i.getMethod());
		Iterator<String> iter = i.getPath().iterator();
		assertEquals("1", iter.next());
		assertFalse(iter.hasNext());
		assertSame(this, i.getReceiver());
	}

	@Test
	public void testConstructionReceiverAndTwoParams_wSeparatorFirst()
			throws Exception {
		Method method = getClass().getMethod("someMethodThatTakesString",
				String.class);
		DeferredInvocation i = DeferredInvocation.getInstance("/1",
				method.getName(), this);
		assertEquals(Arrays.asList("1"), i.getElements());
		assertEquals(method, i.getMethod());
		Iterator<String> iter = i.getPath().iterator();
		assertEquals("1", iter.next());
		assertFalse(iter.hasNext());
		assertSame(this, i.getReceiver());
	}

	@Test
	public void testConstructionReceiverAndTwoParams_wSeparatorBeweenPathComponents_noParams()
			throws Exception {
		Method method = getClass().getMethod("someMethodThatTakesString",
				String.class);
		DeferredInvocation i = DeferredInvocation.getInstance("/1/2",
				method.getName(), this);
		assertEquals(Arrays.asList("2"), i.getElements());
		assertEquals(method, i.getMethod());
		Iterator<String> iter = i.getPath().iterator();
		assertEquals("1", iter.next());
		assertFalse(iter.hasNext());
		assertSame(this, i.getReceiver());
	}

	@Test
	public void testConstruction_oneParam() throws Exception {
		Method method = getClass().getMethod("someMethodThatTakesString",
				String.class);
		DeferredInvocation i = DeferredInvocation.getInstance("/1/2",
				method.getName(), this, "3");
		assertEquals(Arrays.asList("3"), i.getElements());
		assertEquals(method, i.getMethod());
		Iterator<String> iter = i.getPath().iterator();
		assertEquals("1", iter.next());
		assertEquals("2", iter.next());
		assertFalse(iter.hasNext());
		assertSame(this, i.getReceiver());
	}

	@Test
	public void testConstruction_twoParams() throws Exception {
		Method method = getClass().getMethod("someMethodThatTakesStrings",
				String.class, String.class);
		DeferredInvocation i = DeferredInvocation.getInstance("/1/2",
				method.getName(), this, "3", "4");
		assertEquals(Arrays.asList("3", "4"), i.getElements());
		assertEquals(method, i.getMethod());
		Iterator<String> iter = i.getPath().iterator();
		assertEquals("1", iter.next());
		assertEquals("2", iter.next());
		assertFalse(iter.hasNext());
		assertSame(this, i.getReceiver());
	}

	public void someMethodThatTakesString(String s) {
	}

	public void someMethodThatTakesStrings(String s, String s2) {
	}

}
