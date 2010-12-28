/*
 Copyright 2009-2010	Regents of the University of Colorado.
 All rights reserved.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.uimafit.factory;

import org.junit.Test;

/**
 * @author Steven Bethard, Philip Ogren
 * @author Richard Eckart de Castilho
 */
public class TypeSystemDescriptionFactoryTest
{
	@Test
	public void testFromPath()
		throws Exception
	{
		TypeSystemDescriptionFactory.createTypeSystemDescriptionFromPath(
				"src/test/resources/org/uimafit/type/AnalyzedText.xml",
				"src/test/resources/org/uimafit/type/Sentence.xml",
				"src/test/resources/org/uimafit/type/Token.xml").resolveImports();
	}
}
