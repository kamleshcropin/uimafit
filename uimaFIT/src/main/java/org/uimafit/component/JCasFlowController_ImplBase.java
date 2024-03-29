/*
  Copyright 2010 Regents of the University of Colorado.
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
package org.uimafit.component;

import org.apache.uima.flow.FlowControllerContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.uimafit.component.initialize.ConfigurationParameterInitializer;
import org.uimafit.component.initialize.ExternalResourceInitializer;
import org.uimafit.util.ExtendedLogger;

/**
 * @author Philip Ogren
 */

public abstract class JCasFlowController_ImplBase extends
		org.apache.uima.flow.JCasFlowController_ImplBase {
	private ExtendedLogger logger;
	
	public ExtendedLogger getLogger() {
		if (logger == null) {
			logger = new ExtendedLogger(getContext());
		}
		return logger;
	}
	
	@Override
	public void initialize(final FlowControllerContext context) throws ResourceInitializationException {
		super.initialize(context);
		ConfigurationParameterInitializer.initialize(this, context);
		ExternalResourceInitializer.initialize(context, this);
	}
}
