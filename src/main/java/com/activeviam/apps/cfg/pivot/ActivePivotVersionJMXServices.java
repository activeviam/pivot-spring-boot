package com.activeviam.apps.cfg.pivot;

import com.qfs.jmx.JmxOperation;

import java.util.logging.Logger;

public class ActivePivotVersionJMXServices {
	private static final Logger LOGGER = Logger.getLogger(ActivePivotVersionJMXServices.class.getSimpleName());
	@JmxOperation(desc = "captureVersion", name = "Capture latest ActivePivotVersion", params = { "name" })
	public void captureVersion(final String name) {
		LOGGER.info("captureVersion");
		// TODO get the latest version and save in a map with name as key
		// Reference code:
//		protected static IActivePivotVersion getActivePivotVersion(IActivePivotManager manager, CUBE cube, String branchName) {
//			return manager.getActivePivots().get(cube.getCubeName()).getVersionHistory().getHead(branchName);
//		}
	}

	@JmxOperation(desc = "executeGetAggregateQuery", name = "Execute GetAggregateQuery", params = { "name" })
	public void executeGetAggregateQuery(final String name) {
		LOGGER.info("executeGetAggregateQuery");
		// TODO get the version from map with name as key and run GAQ to get the TRADES__NOTIONAL measure.
		//
	}
}
