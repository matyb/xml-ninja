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
public class XmlNinjaWithAddMethodCallTest extends XmlNinjaTest {

	public XmlNinja createAndAddMethodCalls() {
		return new XmlNinja(this)
			.addCallMethod("config/datasources/datasource", "addDataSource", "name", "driver", "url", "databaseDialectName")
	        .addCallMethod("config/export/delimiterChar",	"setDelimiterChar")
	        .addCallMethod("config/export/encloseByChar",	"setEncloseByChar")
	        .addCallMethod("config/export/useCdata",		"setUseCdata")
	        .addCallMethod("config/export/headerColor",		"setHeaderColor")
	        .addCallMethod("config/export/borderColor",		"setBorderColor")
	        .addCallMethod("config/export/altRowColor",		"setAltRowColor")
	        .addCallMethod("config/general/lafs/laf",		"addLookAndFeel", "class", "selected")
	        .addCallMethod("config/general/history/mode",	"setHistoryMode");
	}

}
