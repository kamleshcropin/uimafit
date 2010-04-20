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
package org.uimafit.factory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.uima.UIMA_IllegalArgumentException;
import org.apache.uima.resource.ResourceCreationSpecifier;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.impl.ConfigurationParameter_impl;
import org.uimafit.util.ReflectionUtil;

/**
 * @author Philip Ogren
 */

public class ConfigurationParameterFactory {

	/**
	 * A mapping from Java class names to UIMA configuration parameter type
	 * names. Used by setConfigurationParameters().
	 */
	public static final Map<String, String> javaUimaTypeMap = new HashMap<String, String>();
	static {
		javaUimaTypeMap.put(Boolean.class.getName(), ConfigurationParameter.TYPE_BOOLEAN);
		javaUimaTypeMap.put(Float.class.getName(), ConfigurationParameter.TYPE_FLOAT);
		javaUimaTypeMap.put(Double.class.getName(), ConfigurationParameter.TYPE_FLOAT);
		javaUimaTypeMap.put(Integer.class.getName(), ConfigurationParameter.TYPE_INTEGER);
		javaUimaTypeMap.put(String.class.getName(), ConfigurationParameter.TYPE_STRING);
		javaUimaTypeMap.put("boolean", ConfigurationParameter.TYPE_BOOLEAN);
		javaUimaTypeMap.put("float", ConfigurationParameter.TYPE_FLOAT);
		javaUimaTypeMap.put("double", ConfigurationParameter.TYPE_FLOAT);
		javaUimaTypeMap.put("int", ConfigurationParameter.TYPE_INTEGER);

	};

	public static boolean isConfigurationParameterField(Field field) {
		return field.isAnnotationPresent(org.uimafit.descriptor.ConfigurationParameter.class);
	}

	public static Object getDefaultValue(Field field) {
		if (isConfigurationParameterField(field)) {
			org.uimafit.descriptor.ConfigurationParameter annotation = field
					.getAnnotation(org.uimafit.descriptor.ConfigurationParameter.class);

			String[] stringValue = annotation.defaultValue();
			if (stringValue.length == 1
					&& stringValue[0].equals(org.uimafit.descriptor.ConfigurationParameter.NO_DEFAULT_VALUE)) return null;

			String valueType = getConfigurationParameterType(field);
			boolean isMultiValued = isMultiValued(field);

			if (!isMultiValued) {
				if (ConfigurationParameter.TYPE_BOOLEAN.equals(valueType)) {
					return Boolean.parseBoolean(stringValue[0]);
				}
				else if (ConfigurationParameter.TYPE_FLOAT.equals(valueType)) {
					return Float.parseFloat(stringValue[0]);
				}
				else if (ConfigurationParameter.TYPE_INTEGER.equals(valueType)) {
					return Integer.parseInt(stringValue[0]);
				}
				else if (ConfigurationParameter.TYPE_STRING.equals(valueType)) {
					return stringValue[0];
				}
				throw new UIMA_IllegalArgumentException(UIMA_IllegalArgumentException.METADATA_ATTRIBUTE_TYPE_MISMATCH,
						new Object[] { valueType, "type" });
			}
			else {
				if (ConfigurationParameter.TYPE_BOOLEAN.equals(valueType)) {
					Boolean[] returnValues = new Boolean[stringValue.length];
					for (int i = 0; i < stringValue.length; i++) {
						returnValues[i] = Boolean.parseBoolean(stringValue[i]);
					}
					return returnValues;
				}
				else if (ConfigurationParameter.TYPE_FLOAT.equals(valueType)) {
					Float[] returnValues = new Float[stringValue.length];
					for (int i = 0; i < stringValue.length; i++) {
						returnValues[i] = Float.parseFloat(stringValue[i]);
					}
					return returnValues;
				}
				else if (ConfigurationParameter.TYPE_INTEGER.equals(valueType)) {
					Integer[] returnValues = new Integer[stringValue.length];
					for (int i = 0; i < stringValue.length; i++) {
						returnValues[i] = Integer.parseInt(stringValue[i]);
					}
					return returnValues;
				}
				else if (ConfigurationParameter.TYPE_STRING.equals(valueType)) {
					return stringValue;
				}
				throw new UIMA_IllegalArgumentException(UIMA_IllegalArgumentException.METADATA_ATTRIBUTE_TYPE_MISMATCH,
						new Object[] { valueType, "type" });

			}

		}
		else {
			throw new IllegalArgumentException("field is not annotated with annotation of type "
					+ org.uimafit.descriptor.ConfigurationParameter.class.getName());
		}
	}

	private static String getConfigurationParameterType(Field field) {
		Class<?> parameterClass = field.getType();
		String parameterClassName;
		if (parameterClass.isArray()) {
			parameterClassName = parameterClass.getComponentType().getName();
		}
		else if (Collection.class.isAssignableFrom(parameterClass)) {
			ParameterizedType collectionType = (ParameterizedType) field.getGenericType();
			parameterClassName = ((Class<?>) (collectionType.getActualTypeArguments()[0])).getName();
		}
		else {
			parameterClassName = parameterClass.getName();
		}
		String parameterType = javaUimaTypeMap.get(parameterClassName);
		if (parameterType == null) {
			return ConfigurationParameter.TYPE_STRING;
		}
		return parameterType;
	}

	private static boolean isMultiValued(Field field) {
		Class<?> parameterClass = field.getType();
		if (parameterClass.isArray()) {
			return true;
		}
		else if (Collection.class.isAssignableFrom(parameterClass)) {
			return true;
		}
		return false;
	}

	public static String getConfigurationParameterName(Field field) {
		if (isConfigurationParameterField(field)) {
			org.uimafit.descriptor.ConfigurationParameter annotation = field
					.getAnnotation(org.uimafit.descriptor.ConfigurationParameter.class);
			String name = annotation.name();
			if (name.equals(org.uimafit.descriptor.ConfigurationParameter.USE_FIELD_NAME)) {
				name = field.getDeclaringClass().getName() + "." + field.getName();
			}
			return name;
		}
		return null;
	}

	public static String createConfigurationParameterName(Class<?> clazz, String fieldName) throws RuntimeException {
		try {
			return ConfigurationParameterFactory.getConfigurationParameterName(clazz.getDeclaredField(fieldName));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static ConfigurationParameter createPrimitiveParameter(Field field) {
		if (isConfigurationParameterField(field)) {
			org.uimafit.descriptor.ConfigurationParameter annotation = field
					.getAnnotation(org.uimafit.descriptor.ConfigurationParameter.class);
			String name = getConfigurationParameterName(field);
			boolean multiValued = isMultiValued(field);
			String parameterType = getConfigurationParameterType(field);
			return createPrimitiveParameter(name, parameterType, annotation.description(), multiValued, annotation
					.mandatory());
		}
		else {
			throw new IllegalArgumentException("field is not annotated with annotation of type "
					+ org.uimafit.descriptor.ConfigurationParameter.class.getName());
		}
	}

	/*
	 * The UIMA_IllegalArgumentException statement was copied from
	 * org.apache.uima
	 * .resource.metadata.impl.ConfigurationParameter_impl.setType
	 */
	public static ConfigurationParameter createPrimitiveParameter(String name, Class<?> parameterClass,
			String parameterDescription, boolean isMandatory) {
		String parameterClassName;
		if (parameterClass.isArray()) parameterClassName = parameterClass.getComponentType().getName();
		else parameterClassName = parameterClass.getName();

		String parameterType = javaUimaTypeMap.get(parameterClassName);
		if (parameterType == null) {
			throw new UIMA_IllegalArgumentException(UIMA_IllegalArgumentException.METADATA_ATTRIBUTE_TYPE_MISMATCH,
					new Object[] { parameterClassName, "type" });
		}
		return createPrimitiveParameter(name, parameterType, parameterDescription, parameterClass.isArray(),
				isMandatory);
	}

	public static ConfigurationParameter createPrimitiveParameter(String name, String parameterType,
			String parameterDescription, boolean isMultiValued, boolean isMandatory) {
		ConfigurationParameter param = new ConfigurationParameter_impl();
		param.setName(name);
		param.setType(parameterType);
		param.setDescription(parameterDescription);
		param.setMultiValued(isMultiValued);
		param.setMandatory(isMandatory);
		return param;
	}

	public static ConfigurationData createConfigurationData(Object... configurationData) {
		if (configurationData == null) {
			return new ConfigurationData(new ConfigurationParameter[0], new Object[0]);
		}
		if (configurationData.length % 2 != 0) {
			String message = "a value must be specified for each parameter name: an odd number of values passed in ("
					+ configurationData.length + ")";
			throw new IllegalArgumentException(message);
		}

		
		int numberOfParameters = configurationData.length / 2;
		List<ConfigurationParameter> configurationParameters = new ArrayList<ConfigurationParameter>();
		List<Object> configurationValues = new ArrayList<Object>();
		
		for (int i = 0; i < numberOfParameters; i++) {
			String name = (String) configurationData[i * 2];
			Object value = configurationData[i * 2 + 1];

			if (value == null) {
				continue;
			}
			
			if (value.getClass().isArray() && value.getClass().getComponentType().getName().equals("boolean")) {
				value = ArrayUtils.toObject((boolean[]) value);
			}
			else if (value.getClass().isArray() && value.getClass().getComponentType().getName().equals("int")) {
				value = ArrayUtils.toObject((int[]) value);
			}
			else if (value.getClass().isArray() && value.getClass().getComponentType().getName().equals("float")) {
				value = ArrayUtils.toObject((float[]) value);
			}

			ConfigurationParameter param = ConfigurationParameterFactory.createPrimitiveParameter(name, value
					.getClass(), null, false);
			configurationParameters.add(param);
			configurationValues.add(value);
		}
		return new ConfigurationData(configurationParameters.toArray(new ConfigurationParameter[configurationParameters.size()]), configurationValues.toArray());
	}

	public static ConfigurationData createConfigurationData(Class<?> componentClass) {
		List<ConfigurationParameter> configurationParameters = new ArrayList<ConfigurationParameter>();
		List<Object> configurationValues = new ArrayList<Object>();

		for (Field field : ReflectionUtil.getFields(componentClass)) {
			if (ConfigurationParameterFactory.isConfigurationParameterField(field)) {
				configurationParameters.add(ConfigurationParameterFactory.createPrimitiveParameter(field));
				configurationValues.add(ConfigurationParameterFactory.getDefaultValue(field));
			}
		}

		return new ConfigurationData(configurationParameters.toArray(new ConfigurationParameter[configurationParameters
				.size()]), configurationValues.toArray(new Object[configurationValues.size()]));
	}

	public static class ConfigurationData {
		public ConfigurationParameter[] configurationParameters;

		public Object[] configurationValues;

		public ConfigurationData(ConfigurationParameter[] configurationParameters, Object[] configurationValues) {
			this.configurationParameters = configurationParameters;
			this.configurationValues = configurationValues;
		}

	}

	public static void addConfigurationParameters(ResourceCreationSpecifier specifier, Object... configurationData) {
		ConfigurationData cdata = ConfigurationParameterFactory.createConfigurationData(configurationData);
		ResourceCreationSpecifierFactory.setConfigurationParameters(specifier, cdata.configurationParameters,
				cdata.configurationValues);
	}

	public static void addConfigurationParameters(ResourceCreationSpecifier specifier,
			List<Class<?>> dynamicallyLoadedClasses) {
		for (Class<?> dynamicallyLoadedClass : dynamicallyLoadedClasses) {
			ConfigurationData reflectedConfigurationData = ConfigurationParameterFactory
					.createConfigurationData(dynamicallyLoadedClass);
			ResourceCreationSpecifierFactory.setConfigurationParameters(specifier,
					reflectedConfigurationData.configurationParameters, reflectedConfigurationData.configurationValues);
		}

	}

	public static void addConfigurationParameters(ResourceCreationSpecifier specifier,
			Class<?>... dynamicallyLoadedClasses) {
		for (Class<?> dynamicallyLoadedClass : dynamicallyLoadedClasses) {
			ConfigurationData reflectedConfigurationData = ConfigurationParameterFactory
					.createConfigurationData(dynamicallyLoadedClass);
			ResourceCreationSpecifierFactory.setConfigurationParameters(specifier,
					reflectedConfigurationData.configurationParameters, reflectedConfigurationData.configurationValues);
		}
	}

	public static void addConfigurationParameter(ResourceCreationSpecifier specifier, String name, Object value) {
		ConfigurationData cdata = ConfigurationParameterFactory.createConfigurationData(name, value);
		ResourceCreationSpecifierFactory.setConfigurationParameters(specifier, cdata.configurationParameters,
				cdata.configurationValues);

	}

}
