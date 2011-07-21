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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;


public abstract class XmlNinjaTest {

	final String toString = "This Test";
	
	String addDataSourceCalls, setDelimiterCalls, setEnclosedByCharCalls, setUseCdataCalls, 
		setHeaderColorCalls, setBorderColorCalls, setAltRowColorCalls, addLookAndFeelCalls, setHistoryModeCalls;
	
	@Before
	public void setUp(){
		addDataSourceCalls = setDelimiterCalls = setEnclosedByCharCalls = setUseCdataCalls = 
		setHeaderColorCalls = setBorderColorCalls = setAltRowColorCalls = addLookAndFeelCalls = setHistoryModeCalls = "";
	}
	
	@Test
	public void testAddingMethodCalls() throws Exception {
        XmlNinja digester = createAndAddMethodCallsAndCallForAbsentNode();
		assertEquals(new StringBuilder(
				XmlNinja.class.getSimpleName()).append("[Receiver: "+toString+" [").append(
				"[DeferredInvocation [path=[config, datasources, datasource], method=addDataSource, elements=[name, driver, url, databaseDialectName]], ").append(
				 "DeferredInvocation [path=[config, export], method=setDelimiterChar, elements=[delimiterChar]], ").append(
				 "DeferredInvocation [path=[config, export], method=setEncloseByChar, elements=[encloseByChar]], ").append(
				 "DeferredInvocation [path=[config, export], method=setUseCdata, elements=[useCdata]], ").append(
				 "DeferredInvocation [path=[config, export], method=setHeaderColor, elements=[headerColor]], ").append(
				 "DeferredInvocation [path=[config, export], method=setBorderColor, elements=[borderColor]], ").append(
				 "DeferredInvocation [path=[config, export], method=setAltRowColor, elements=[altRowColor]], ").append(
				 "DeferredInvocation [path=[config, general, lafs, laf], method=addLookAndFeel, elements=[class, selected]], ").append(
				 "DeferredInvocation [path=[config, general, history], method=setHistoryMode, elements=[mode]], " +
				 "DeferredInvocation [path=[config], method=setHistoryTurtle, elements=[dove]]]]]").toString(),
				digester.toString());
	}
	
	@Test
	public void testParsing_dom() throws Exception{
		parsingTest(new DomReader(), createAndAddMethodCalls());
	}
	
	@Test
	public void testParsing_sax() throws Exception{
		parsingTest(new SaxReader(), createAndAddMethodCalls());
	}
	
	@Test
	public void testParsing_dom_addMethodCallForAbsentNode() throws Exception{
		parsingTest(new DomReader(), createAndAddMethodCallsAndCallForAbsentNode());
	}
	
	@Test
	public void testParsing_sax_addMethodCallForAbsentNode() throws Exception{
		parsingTest(new SaxReader(), createAndAddMethodCallsAndCallForAbsentNode());
	}
	
	private void parsingTest(XmlReader reader, XmlNinja digester) throws Exception {
		digester.parse(getXmlFileLocation(), reader);
		assertOnReception();
	}

	public void assertOnReception() {
		assertEquals(new StringBuilder
					 ("aTest,com.mysql.jdbc.Driver,jdbc:mysql://localhost:3306/test,com.pk.MySQLDialect").append
					 ("HsqlDataSource,org.hsqldb.jdbcDriver,jdbc:mysql://localhost:3306/test555,com.pk.OracleDialect").append
					 ("test,com.mysql.jdbc.Driver,jdbc:mysql://localhost:3306/test,com.pk.MySQLDialect").toString()
					, addDataSourceCalls);
		assertEquals(new StringBuilder
					 ("com.incors.plaf.kunststoff.KunststoffLookAndFeel,true").append
					 ("javax.swing.plaf.metal.MetalLookAndFeel,false").append
					 ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel,false").append
					 ("com.sun.java.swing.plaf.motif.MotifLookAndFeel,false").toString()
					, addLookAndFeelCalls);
		assertEquals("#b9ffb9", setAltRowColorCalls);
		assertEquals("green",	setBorderColorCalls);
		assertEquals(",",		setDelimiterCalls);
		assertEquals("\"",		setEnclosedByCharCalls);
		assertEquals("#ffff80",	setHeaderColorCalls);
		assertEquals("2",		setHistoryModeCalls);
		assertEquals("true",	setUseCdataCalls);
	}

	public abstract XmlNinja createAndAddMethodCalls();
	
	public String getXmlFileLocation(){
		return "./test/com/sandwich/util/xml/config.xml";
	}
	
	private XmlNinja createAndAddMethodCallsAndCallForAbsentNode(){
		return createAndAddMethodCalls().addCallMethod("config/dove", "setHistoryTurtle");
	}

	public void addDataSource(String name, String driver, String url, String databaseDialectName){
		addDataSourceCalls += append(name, driver, url, databaseDialectName);
	}

	public void setDelimiterChar(String delimiterChar){
		setDelimiterCalls += append(delimiterChar);
	}

	public void setEncloseByChar(String encloseByChar){
		setEnclosedByCharCalls += append(encloseByChar); 
	}
	
	public void setUseCdata(String useCdata){
		setUseCdataCalls += append(useCdata);
	}
	
	public void setHeaderColor(String headerColor){
		setHeaderColorCalls += append(headerColor);
	}
	
	public void setBorderColor(String borderColor){
		setBorderColorCalls += append(borderColor);
	}

	public void setAltRowColor(String altRowColor){
		setAltRowColorCalls += append(altRowColor);
	}
	
	public void addLookAndFeel(String clazz, String selected){
		addLookAndFeelCalls += append(clazz, selected);
	}
	
	public void setHistoryMode(String historyMode){
		setHistoryModeCalls += append(historyMode);
	}
	
	public void setHistoryTurtle(String value){
		fail("this element isn't in the xml!");
	}
	
	public String toString(){
		return toString;
	}
	
	protected String append(String...strings){
		String base = strings[0];
		for(int i = 1; i < strings.length; i++){
			base+=","+strings[i];
		}
		return base;
	}
	
}
