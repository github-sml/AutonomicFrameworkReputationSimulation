package org.autonomic.monitoring.framework;

import org.autonomic.monitoring.framework.trustmodel.Feature;
import org.autonomic.monitoring.framework.trustmodel.ForgiveFactor;
import org.autonomic.monitoring.framework.util.CDataConstant;
import org.autonomic.monitoring.framework.util.RandomResponseTimeEventGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Entry point for the Demo. Run this from your IDE, or from the command line using 'mvn exec:java'.
 */
public class StartDemo {

	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(StartDemo.class);

	/**
	 * Main method - start the Demo!
	 */
	public static void main(String[] args) throws Exception {
		LOG.debug("Starting...");
		CDataConstant.PROVIDER_REPUTATION = CDataConstant.PROVIDER_REPUTATION_INITIAL;
		LOG.debug("PROVIDER REPUTATION START SIMULATION = " + CDataConstant.PROVIDER_REPUTATION);

		long noOfResponseTimeEvents = 100;

		if (args.length == 1) {
			noOfResponseTimeEvents = Long.valueOf(args[0]);
		} else {
			LOG.debug("No override of number of events detected - defaulting to " + noOfResponseTimeEvents + " events.");
		}

		// Load spring configuration
		ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext(new String[] { "application-context.xml" });
		BeanFactory factory = (BeanFactory) appContext;

		Feature responseTime = Feature.getInstance("responseTime");
		responseTime.setId("responseTime");
		responseTime.setWidth(6);
		responseTime.setSLAValue(0.8f);

		Feature availibility = Feature.getInstance("availibility");
		availibility.setId("availibility");
		availibility.setWidth(9);
		availibility.setSLAValue(0.95f);
		// Start Demo
		RandomResponseTimeEventGenerator generator = (RandomResponseTimeEventGenerator) factory.getBean("eventGenerator");
		generator.startSendingResponseTimeReadings(noOfResponseTimeEvents);

	}

}
