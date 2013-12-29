/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.birt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.model.api.DesignConfig;
import org.eclipse.birt.report.model.api.IDesignEngine;
import org.eclipse.birt.report.model.api.IDesignEngineFactory;
import org.openmrs.module.ModuleException;

/**
 * This class encapsulates the necessary logic to startup and shutdown the Birt platform
 * and obtain a ReportEngine for use in running reports
 */
public class BirtRuntime {

	private static Log log = LogFactory.getLog(BirtRuntime.class);
	private static BirtConfiguration configuration;
	private static IReportEngine reportEngine;
	private static IDesignEngine designEngine;

	/**
	 * Start up the Birt Platform.
	 */
	public static void startup(BirtConfiguration configuration) {
		try {
			BirtRuntime.configuration = configuration;
			log.debug("Starting BIRT report engine platform...");
			Platform.startup(configuration.getBirtEngineConfig());
			log.debug("Starting BIRT design platform");
			Platform.startup(new DesignConfig());
		}
		catch (Exception e) {
			throw new ModuleException("Error starting BIRT platforms", e);
		}
	}

	/**
	 * @return the BirtConfiguration used to startup Birt
	 */
	public static BirtConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Retrieves the Birt Report Engine, creating a new one if it doesn't exist,
	 * otherwise re-using the cached version
	 */
	public static synchronized IReportEngine getReportEngine() {
		if (reportEngine == null) {
			try {
				log.debug("Creating Birt report engine.");
				IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
				reportEngine = factory.createReportEngine(configuration.getBirtEngineConfig());
			}
			catch (Exception e) {
				throw new BirtReportException("Unable to create Birt report engine: " + e.getMessage(), e);
			}
		}
		return reportEngine;
	}

	/**
	 * Retrieves the Birt Design Engine, creating a new one if it doesn't exist,
	 * otherwise re-using the cached version
	 */
	public static synchronized IDesignEngine getDesignEngine() {
		if (designEngine == null) {
			try {
				log.debug("Creating Birt design engine.");
				IDesignEngineFactory factory = (IDesignEngineFactory) Platform.createFactoryObject(IDesignEngineFactory.EXTENSION_DESIGN_ENGINE_FACTORY);
				designEngine = factory.createDesignEngine(new DesignConfig());
			}
			catch (Exception e) {
				throw new BirtReportException("Unable to create Birt design engine: " + e.getMessage(), e);
			}
		}
		return designEngine;
	}

	/**
	 * Shutdown the Birt Platform
	 */
	public static void shutdown() {
		Platform.shutdown();
	}
}
