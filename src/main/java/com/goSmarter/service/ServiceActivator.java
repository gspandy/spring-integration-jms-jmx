package com.goSmarter.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.integration.message.GenericMessage;

public class ServiceActivator {

	private static Log logger = LogFactory
			.getLog(ServiceActivator.class);

	public void logXml(GenericMessage<?> msg) throws Exception {

		logger.debug("### INSIDE SEND TO TRADESPHERE ###");
		String payload = (String) msg.getPayload();
		logger.debug("### PAYLOAD TO TRADESPHERE ###" + payload);
		
	}
}
