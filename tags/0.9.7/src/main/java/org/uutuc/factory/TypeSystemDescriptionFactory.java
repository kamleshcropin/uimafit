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

package org.uutuc.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.resource.metadata.impl.TypeSystemDescription_impl;

/**
 * @author Steven Bethard, Philip Ogren
 */
public class TypeSystemDescriptionFactory {

	
	/**
	 * Creates a TypeSystemDescription from a list of classes belonging to a type system - i.e. classes generated by JCasGen.
	 * 
	 * @param typeSystemClasses
	 *            The type system class objects.
	 * @return A TypeSystemDescription that includes all of the specified
	 *         Annotation types.
	 */
	public static TypeSystemDescription createTypeSystemDescription(Class<?>... typeSystemClasses) {
		TypeSystemDescription typeSystem = new TypeSystemDescription_impl();
		List<Import> imports = new ArrayList<Import>();
		for (Class<?> typeSystemClass : typeSystemClasses) {
			Import imprt = new Import_impl();
			imprt.setName(typeSystemClass.getName());
			imports.add(imprt);
		}
		Import[] importArray = new Import[imports.size()];
		typeSystem.setImports(imports.toArray(importArray));
		return typeSystem;
	}

	/**
	 * Creates a TypeSystemDescription from descriptor names.
	 * 
	 * @param descriptorNames
	 *            The fully qualified, Java-style, dotted descriptor names.
	 * @return A TypeSystemDescription that includes the types from all of the
	 *         specified files.
	 */
	public static TypeSystemDescription createTypeSystemDescription(String... descriptorNames) {
		TypeSystemDescription typeSystem = new TypeSystemDescription_impl();
		List<Import> imports = new ArrayList<Import>();
		for (String descriptorName : descriptorNames) {
			Import imp = new Import_impl();
			imp.setName(descriptorName);
			imports.add(imp);
		}
		Import[] importArray = new Import[imports.size()];
		typeSystem.setImports(imports.toArray(importArray));
		return typeSystem;
	}

	
	/**
	 * Creates a TypeSystemDescription from a descriptor file
	 * 
	 * @param descriptorURIs
	 *            The descriptor file paths.
	 * @return A TypeSystemDescription that includes the types from all of the
	 *         specified files.
	 */
	public static TypeSystemDescription createTypeSystemDescriptionFromPath(String... descriptorURIs) {
		TypeSystemDescription typeSystem = new TypeSystemDescription_impl();
		List<Import> imports = new ArrayList<Import>();
		for (String descriptorURI : descriptorURIs) {
			Import imp = new Import_impl();
			imp.setLocation(descriptorURI);
			imports.add(imp);
		}
		Import[] importArray = new Import[imports.size()];
		typeSystem.setImports(imports.toArray(importArray));
		return typeSystem;
	}

}