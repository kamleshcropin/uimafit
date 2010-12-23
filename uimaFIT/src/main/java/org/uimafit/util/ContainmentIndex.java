/*
 Copyright 2009-2010
 Ubiquitous Knowledge Processing (UKP) Lab
 Technische Universitaet Darmstadt
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

import static org.uimafit.util.JCasUtil.iterate;
import static org.uimafit.util.JCasUtil.selectCovered;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.jcas.JCas;

/**
 * Create a fast way of repeatedly checking whether instances of one type are contained within the
 * boundaries on another type.
 * 
 * @author Richard Eckart de Castilho
 *
 * @param <S> covering type.
 * @param <U> covered type.
 */
public class ContainmentIndex<S extends AnnotationFS, U extends AnnotationFS>
{
	public static enum Type { DIRECT, REVERSE, BOTH }

	private Map<S, Collection<U>> data = new LinkedHashMap<S, Collection<U>>();
	private Map<U, Collection<S>> dataRev = new LinkedHashMap<U, Collection<S>>();

	/**
	 * Create a new index on the given JCas using the specified two types. The last argument
	 * indicates in which directions lookups to the index will be made.
	 * 
	 * @param aJcas the working JCas.
	 * @param aSuper the covering type.
	 * @param aUnder the covered type.
	 * @param aType the indexing strategy.
	 */
	public ContainmentIndex(JCas aJcas, Class<? extends S> aSuper, Class<? extends U> aUnder,
			Type aType)
	{
		for (S s : iterate(aJcas, aSuper)) {
			for (U u : selectCovered(aUnder, s)) {
				switch (aType) {
				case DIRECT: {
					put(data, s, u);
					break;
				}
				case REVERSE:
					put(dataRev, u, s);
					break;
				case BOTH:
					put(data, s, u);
					put(dataRev, u, s);
					break;
				}
			}
		}
	}

	private <X, Y> void put(Map<X, Collection<Y>> map, X key, Y value)
	{
		Collection<Y> c = map.get(key);
		if (c == null) {
			c = new LinkedList<Y>();
			map.put(key, c);
		}
		c.add(value);
	}

	/**
	 * Get all instances of the covered type contained within the boundaries of the specified 
	 * instance of the covering type.
	 * 
	 * @param aSuper a covering type instance.
	 * @return a collection of covered type instances.
	 */
	public Collection<U> containedIn(S aSuper)
	{
		Collection<U> c = data.get(aSuper);
		if (c == null) {
			return Collections.emptySet();
		}
		return c;
	}

	/**
	 * Get all instances of the covering type containing the the specified 
	 * instance of the covered type.
	 * 
	 * @param aUnder a covered type instance.
	 * @return a collection of covering type instances.
	 */
	public Collection<S> containing(U aUnder)
	{
		Collection<S> c = dataRev.get(aUnder);
		if (c == null) {
			return Collections.emptySet();
		}
		return c;
	}

	/**
	 * Checks if the given covered type is contained in the specified covering type.
	 * 
	 * @param aSuper the covering type instance.
	 * @param aUnder the covered type instance.
	 * @return whether the covered instance is contained in the covering instance.
	 */
	public boolean isContainedIn(S aSuper, U aUnder)
	{
		return containedIn(aSuper).contains(aUnder);
	}

	/**
	 * Checks if the given covered type is contained in any instance of the covering type.
	 * 
	 * @param aUnder the covered type instance.
	 * @return whether the covered instance is contained in any instance of the covering type.
	 */
	public boolean isContainedInAny(U aUnder)
	{
		Collection<S> containers = containing(aUnder);
		return containers != null && !containers.isEmpty();
	}

	/**
	 * Factory method to create an index instead of using the constructor. This makes used of Java's
	 * type inference capabilities and results in less verbose code.
	 * 
	 * @param <A> covering type.
	 * @param <B> covered type.
	 * @param aJcas the working JCas.
	 * @param aSuper the covering type.
	 * @param aUnder the covered type.
	 * @param aType the indexing strategy.
	 * @return the index instance.
	 */
	public static <A extends AnnotationFS, B extends AnnotationFS> ContainmentIndex<A, B> create(
			JCas aJcas, Class<A> aSuper, Class<B> aUnder, Type aType)
	{
		return new ContainmentIndex<A, B>(aJcas, aSuper, aUnder, aType);
	}
}