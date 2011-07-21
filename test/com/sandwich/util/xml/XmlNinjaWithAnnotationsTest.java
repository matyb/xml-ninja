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

import org.junit.Test;

public class XmlNinjaWithAnnotationsTest extends XmlNinjaTest {

	@Test
	public void testParityWithAnnotationInferenceAndManualAddition()
			throws Exception {
		assertEquals(new XmlNinjaWithAddMethodCallTest()
				.createAndAddMethodCalls().toString(),
				new XmlNinjaWithAnnotationsTest().createAndAddMethodCalls()
						.toString());
	}

	@Override
	public XmlNinja createAndAddMethodCalls() {
		return new XmlNinja(this).mapFromAnnotations();
	}

	@Override
	@CallFor(path = "config/datasources/datasource", args = { "name", "driver",
			"url", "databaseDialectName" })
	public void addDataSource(String name, String driver, String url,
			String databaseDialectName) {
		super.addDataSource(name, driver, url, databaseDialectName);
	}

	@Override
	@CallFor(path = "config/export/delimiterChar")
	public void setDelimiterChar(String delimiterChar) {
		super.setDelimiterChar(delimiterChar);
	}

	@Override
	@CallFor(path = "config/export/encloseByChar")
	public void setEncloseByChar(String encloseByChar) {
		super.setEncloseByChar(encloseByChar);
	}

	@Override
	@CallFor(path = "config/export/useCdata")
	public void setUseCdata(String useCdata) {
		super.setUseCdata(useCdata);
	}

	@Override
	@CallFor(path = "config/export/headerColor")
	public void setHeaderColor(String headerColor) {
		super.setHeaderColor(headerColor);
	}

	@Override
	@CallFor(path = "config/export/borderColor")
	public void setBorderColor(String borderColor) {
		super.setBorderColor(borderColor);
	}

	@Override
	@CallFor(path = "config/export/altRowColor")
	public void setAltRowColor(String altRowColor) {
		super.setAltRowColor(altRowColor);
	}

	@Override
	@CallFor(path = "config/general/lafs/laf", args = { "class", "selected" })
	public void addLookAndFeel(String clazz, String selected) {
		super.addLookAndFeel(clazz, selected);
	}

	@Override
	@CallFor(path = "config/general/history/mode")
	public void setHistoryMode(String historyMode) {
		super.setHistoryMode(historyMode);
	}

}
