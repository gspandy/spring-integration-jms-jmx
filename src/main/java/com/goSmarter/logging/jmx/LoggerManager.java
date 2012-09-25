/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.goSmarter.logging.jmx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * @author Gary Russell
 *
 */
@ManagedResource(objectName="spring.application:type=LoggerManager,name=LoggerManager")
public class LoggerManager {
	
	private final Logger logger = Logger.getLogger(this.getClass());

	private void doSetLevel(String loggerName, String level) {
		if (LogManager.exists(loggerName) == null) {
			throw new RuntimeException("No logger named: " + loggerName);
		}
		Logger targetLogger = Logger.getLogger(loggerName);
		Level oldLevel = targetLogger.getEffectiveLevel();
		if ("trace".equalsIgnoreCase(level)) {
			targetLogger.setLevel(Level.TRACE);
		} else if ("debug".equalsIgnoreCase(level)) {
			targetLogger.setLevel(Level.DEBUG);
		} else if ("info".equalsIgnoreCase(level)) {
			targetLogger.setLevel(Level.INFO);
		} else if ("error".equalsIgnoreCase(level)) {
			targetLogger.setLevel(Level.ERROR);
		} else if ("fatal".equalsIgnoreCase(level)) {
			targetLogger.setLevel(Level.FATAL);
		} else if ("warn".equalsIgnoreCase(level)) {
			targetLogger.setLevel(Level.WARN);
		} else {
			throw new RuntimeException("Level " + level + " is not recognized");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Logger " + loggerName + " changed from "
					+ oldLevel.toString() + " to " + level);
		}
	}
	
	@ManagedOperation(description="Sets Log4j Logger Level for all loggers starting with the supplied value. " +
			"Use, with caution, to reset a complete hierarchy where lower level logger levels have been altered.")
	public void setAndCascadeLevel(String loggerPrefix, String level) {
		boolean found = false;
		List<String> loggers = getLoggers();
		for (String logger : loggers) {
			if (logger.startsWith(loggerPrefix)) {
				doSetLevel(logger, level);
				
				found = true;
			}
		}
		if (!found) {
			throw new RuntimeException("No loggers found with prefix: " + loggerPrefix);
		}
	}
	
	@ManagedOperation(description="Sets Log4j Logger Level")
	public void setLevel(String loggerName, String level) {
		if (LogManager.exists(loggerName) == null) {
			throw new RuntimeException("No logger named: " + loggerName);
		}
		doSetLevel(loggerName, level);
	}
	
	@ManagedOperation(description="Gets the current Log4j Logger Level for the specified logger")
	public String getLevel(String loggerName) {
		if (LogManager.exists(loggerName) == null) {
			return "No logger named: " + loggerName;
		}
		Logger logger = Logger.getLogger(loggerName);
		Level level = logger.getEffectiveLevel();
		if (level == null) {
			return "Logger: " + loggerName + " has no level";
		}
		return level.toString();
	}
	
	@ManagedAttribute
	public List<String> getLoggers() {
		@SuppressWarnings("rawtypes")
		Enumeration loggers = LogManager.getCurrentLoggers();
		List<String> list = new ArrayList<String>();
		while (loggers.hasMoreElements()) {
			list.add(((Logger) loggers.nextElement()).getName());
		}
		Collections.sort(list);
		return list;
	}
}
