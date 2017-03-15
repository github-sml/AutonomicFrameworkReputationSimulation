package org.autonomic.monitoring.framework.subscriber;

import java.util.Date;
import java.util.Map;

import org.autonomic.monitoring.framework.event.ResponseTimeEvent;
import org.autonomic.monitoring.framework.trustmodel.Feature;
import org.autonomic.monitoring.framework.trustmodel.FeatureEvaluation;
import org.autonomic.monitoring.framework.trustmodel.ForgiveFactor;
import org.autonomic.monitoring.framework.util.CDataConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Wraps Esper Statement and Listener. No dependency on Esper libraries.
 */
@Component
public class FailureEventSubscriber implements StatementSubscriber {

	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(FailureEventSubscriber.class);

	@Autowired
	private ForgiveFactor forgiveFactorModel;

	/**
	 * {@inheritDoc}
	 */
	public String getStatement() {

		// Example using 'Match Recognise' syntax.
		String warningEventExpression = "select * from ResponseTimeEvent " + "match_recognize ( " + "       measures A as temp1" + "       pattern (A) "
				+ "       define " + "               A as A.responseTime > " + CDataConstant.FAILURE_SERVICE_THRESHOLD + ")";

		return warningEventExpression;
	}

	/**
	 * Listener method called when Esper has detected a pattern match.
	 */
	public void update(Map<String, ResponseTimeEvent> eventMap) {

		// 1st ResponseTime in the Warning Sequence
		ResponseTimeEvent temp1 = (ResponseTimeEvent) eventMap.get("temp1");

		StringBuilder sb = new StringBuilder();
		sb.append("***************************************");
		sb.append("\n* [SYSTEM STAT] : FAILURE SERVICE DETECTED! ");
		sb.append("\n* " + temp1 + " > " + CDataConstant.FAILURE_SERVICE_THRESHOLD);
		sb.append("\n***************************************");
		CDataConstant.TOTAL_NUMBER_OF_FAILURES++;
		CDataConstant.TOTAL_DURATION_OF_FAILURES = CDataConstant.TOTAL_DURATION_OF_FAILURES + temp1.getResponseTime();

		Feature feature = Feature.getInstance("responseTime");
		float evaluation = 0.1f;
		FeatureEvaluation evaluatedFeature = new FeatureEvaluation(feature, evaluation);
		forgiveFactorModel.updateTrustKnowledge(evaluatedFeature);

		Feature availability = Feature.getInstance("availability");
		float availabilityEvaluation = 0.01f;
		FeatureEvaluation availabilityEvaluatedFeature = new FeatureEvaluation(availability, availabilityEvaluation);
		forgiveFactorModel.updateTrustKnowledge(availabilityEvaluatedFeature);

		LOG.debug(sb.toString());
	}
}
