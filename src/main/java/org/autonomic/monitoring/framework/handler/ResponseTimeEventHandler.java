package org.autonomic.monitoring.framework.handler;

import org.autonomic.monitoring.framework.event.ResponseTimeEvent;
import org.autonomic.monitoring.framework.subscriber.StatementSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * This class handles incoming ResponseTime Events. It processes them through the EPService, to which it has attached the 3 queries.
 */
@Component
@Scope(value = "singleton")
public class ResponseTimeEventHandler implements InitializingBean {

	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(ResponseTimeEventHandler.class);

	/** Esper service */
	private EPServiceProvider epService;
	private EPStatement criticalEventStatement;
	private EPStatement warningEventStatement;
	private EPStatement normalEventStatement;
	private EPStatement notificationEventStatement;
	private EPStatement monitorEventStatement;

	@Autowired
	@Qualifier("criticalEventSubscriber")
	private StatementSubscriber criticalEventSubscriber;

	@Autowired
	@Qualifier("warningEventSubscriber")
	private StatementSubscriber warningEventSubscriber;

	@Autowired
	@Qualifier("notificationEventSubscriber")
	private StatementSubscriber notificationEventSubscriber;

	@Autowired
	@Qualifier("normalEventSubscriber")
	private StatementSubscriber normalEventSubscriber;

	@Autowired
	@Qualifier("failureEventSubscriber")
	private StatementSubscriber failureEventSubscriber;

	/**
	 * Configure Esper Statement(s).
	 */
	public void initService() {

		LOG.debug("Initializing Service ..");
		Configuration config = new Configuration();
		config.addEventTypeAutoName("org.autonomic.monitoring.framework.event");
		epService = EPServiceProviderManager.getDefaultProvider(config);

		createCriticalResponseTimeCheckExpression();
		createWarningResponseTimeCheckExpression();
		createNotificationResponseTimeCheckExpression();
		createFailureCheckExpression();
		createNormalResponseTimeCheckExpression();
	}

	/**
	 * EPL to check for a sudden critical rise across 4 events, where the last event is 1.5x greater than the first event. This is checking for a sudden,
	 * sustained escalating rise in the responseTime
	 */
	private void createCriticalResponseTimeCheckExpression() {

		LOG.debug("create Critical Response Time Check Expression");
		criticalEventStatement = epService.getEPAdministrator().createEPL(criticalEventSubscriber.getStatement());
		criticalEventStatement.setSubscriber(criticalEventSubscriber);
	}

	/**
	 * EPL to check for 2 consecutive ResponseTime events over the threshold - if matched, will alert listener.
	 */
	private void createWarningResponseTimeCheckExpression() {

		LOG.debug("create Warning Response Time Check Expression");
		warningEventStatement = epService.getEPAdministrator().createEPL(warningEventSubscriber.getStatement());
		warningEventStatement.setSubscriber(warningEventSubscriber);
	}

	/**
	 * EPL to check for a sudden notification rise across 2 events, where the last event is 1.5x greater than the first event. This is checking for a sudden,
	 * sustained escalating rise in the responseTime
	 */
	private void createNotificationResponseTimeCheckExpression() {

		LOG.debug("create Notification Response Time Check Expression");
		notificationEventStatement = epService.getEPAdministrator().createEPL(notificationEventSubscriber.getStatement());
		notificationEventStatement.setSubscriber(notificationEventSubscriber);
	}

	/**
	 * EPL to check for a normal notification rise across 2 events, where the behavoir is normal responseTime
	 */
	private void createNormalResponseTimeCheckExpression() {

		LOG.debug("create Normal Response Time Check Expression");
		normalEventStatement = epService.getEPAdministrator().createEPL(normalEventSubscriber.getStatement());
		normalEventStatement.setSubscriber(normalEventSubscriber);
	}

	/**
	 * EPL to check for a sudden notification rise across 2 events, where the last event is 1.5x greater than the first event. This is checking for a sudden,
	 * sustained escalating rise in the responseTime
	 */
	private void createFailureCheckExpression() {

		LOG.debug("create Failure Check Expression");
		notificationEventStatement = epService.getEPAdministrator().createEPL(failureEventSubscriber.getStatement());
		notificationEventStatement.setSubscriber(failureEventSubscriber);
	}

	/**
	 * Handle the incoming ResponseTimeEvent.
	 */
	public void handle(ResponseTimeEvent event) {

		LOG.debug(event.toString());
		epService.getEPRuntime().sendEvent(event);

	}

	@Override
	// invoked by beanFactory (in StartDemo.java) after setting all bean properties
	public void afterPropertiesSet() {

		LOG.debug("Configuring..");
		initService();
	}
}
