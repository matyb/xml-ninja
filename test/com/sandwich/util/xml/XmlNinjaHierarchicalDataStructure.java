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
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("unused")
public class XmlNinjaHierarchicalDataStructure {

	Object personReceiver = getNewPersonReceiver();
	Object stateReceiver = getNewStateReceiver();

	@Before
	public void setUp() {
		stateReceiver = getNewStateReceiver();
		personReceiver = getNewPersonReceiver();
	}

	@Test
	public void testDigestionFromMixedReceivers_sax() throws Exception {
		parse(new SaxReader());
		assertOnStateAndPersonReceiver();
	}

	@Test
	public void testDigestionFromMixedReceivers_dom() throws Exception {
		parse(new DomReader());
		assertOnStateAndPersonReceiver();
	}

	private void parse(XmlReader reader) throws Exception {
		new XmlNinja().mapFromAnnotations(personReceiver)
				.mapFromAnnotations(stateReceiver)
				.parse("./test/com/sandwich/util/xml/hierarchical.xml",reader);
	}
	
	/*
	 * TODO Not entirely sure what is correct to expect here
	 * - just let it fail to serve as a reminder
	 */
	private void assertOnStateAndPersonReceiver() {
		assertEquals("", personReceiver.toString());
		assertEquals("", stateReceiver.toString());
	}
	
	private Object getNewStateReceiver() {
		return new Object() {
			private String statePostalAbbrev = "";

			@CallFor(path = "/stuff/state")
			public void addState(String postalAbbrev) {
				statePostalAbbrev += postalAbbrev;
			}

			@Override
			public String toString() {
				return statePostalAbbrev;
			}
		};
	}

	private Object getNewPersonReceiver() {
		return new Object() {
			private String personConstructionRecollection = "";

			@CallFor(path = "/stuff/people/person", args = { "id", "name" })
			public void newPerson(String id, String name) {
				add(id, name);
			}

			@CallFor(path = "/stuff/people/person/address", args = { "street",
					"zip" })
			public void addAddress(String street, String zip) {
				add(street, zip);
			}

			@CallFor(path = "/stuff/people/person/phone")
			public void addPhone(String phone) {
				add(phone);
			}

			private void add(String... args) {
				for (String arg : args) {
					personConstructionRecollection += "," + arg;
				}
			}

			@Override
			public String toString() {
				return personConstructionRecollection;
			}
		};
	}
}
