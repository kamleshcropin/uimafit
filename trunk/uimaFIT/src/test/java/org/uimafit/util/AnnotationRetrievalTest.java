/* 
 Copyright 2009 Regents of the University of Colorado.  
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
package org.uimafit.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.apache.uima.UIMAException;
import org.junit.Test;
import org.uimafit.Test_ImplBase;
import org.uimafit.type.Token;
/**
 * @author Steven Bethard, Philip Ogren
 */

public class AnnotationRetrievalTest extends Test_ImplBase {

	@Test
	public void testGet() throws UIMAException {
		String text = "Rot wood cheeses dew?";
		tokenBuilder.buildTokens(jCas, text);
		
		Token lastToken = AnnotationRetrieval.get(jCas, Token.class, -1);
		assertEquals("dew?", lastToken.getCoveredText());
		
		Token firstToken = AnnotationRetrieval.get(jCas, Token.class, 0);
		assertEquals("Rot", firstToken.getCoveredText());

		lastToken = AnnotationRetrieval.get(jCas, Token.class, 3);
		assertEquals("dew?", lastToken.getCoveredText());

		firstToken = AnnotationRetrieval.get(jCas, Token.class, -4);
		assertEquals("Rot", firstToken.getCoveredText());

		Token oobToken = AnnotationRetrieval.get(jCas, Token.class, -5);
		assertNull(oobToken);
		
		oobToken = AnnotationRetrieval.get(jCas, Token.class, 4);
		assertNull(oobToken);
		
	}
	
	@Test
	public void testIterator() throws Exception {
		String text = "Rot wood cheeses dew?";
		tokenBuilder.buildTokens(jCas, text);
		
		Iterator<Token> tokens = AnnotationRetrieval.get(jCas, Token.class);
		assertTrue(tokens.hasNext());
		assertEquals("Rot", tokens.next().getCoveredText());
		assertTrue(tokens.hasNext());
		assertEquals("wood", tokens.next().getCoveredText());
		assertTrue(tokens.hasNext());
		assertEquals("cheeses", tokens.next().getCoveredText());
		assertTrue(tokens.hasNext());
		assertEquals("dew?", tokens.next().getCoveredText());
		assertFalse(tokens.hasNext());
	}
}
