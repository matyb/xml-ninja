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

public class XmlNinjaWithMixedAttributesElementsTest extends XmlNinjaWithAttributesTest {

	@Override public String getXmlFileLocation(){
		return "./test/com/sandwich/util/xml/mixed_config.xml";
	}
	
	@Override
	public void assertOnReception() {
		assertEquals("mehTest,com.mysql.jdbc.Driver,jdbc:mysql://localhost:3306/meh,com.pk.MySQLDialect" +
					 "mehTest,com.mysql.jdbc.Driver,jdbc:mysql://localhost:3306/meh,com.pk.MySQLDialect", 
			     addDataSourceCalls);
		assertEquals("", addLookAndFeelCalls);
		assertEquals("", setAltRowColorCalls);
		assertEquals("", setBorderColorCalls);
		assertEquals("", setDelimiterCalls);
		assertEquals("", setEnclosedByCharCalls);
		assertEquals("", setHeaderColorCalls);
		assertEquals("", setHistoryModeCalls);
		assertEquals("", setUseCdataCalls);
	}
	
}
