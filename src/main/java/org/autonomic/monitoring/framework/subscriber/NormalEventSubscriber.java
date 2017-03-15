package org.autonomic.monitoring.framework.subscriber;

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
public class NormalEventSubscriber implements StatementSubscriber {

	/** Logger */
	private static Logger LOG = LoggerFactory.getLogger(NormalEventSubscriber.class);

	@Autowired
	private ForgiveFactor forgiveFactorModel;

	/**
	 * {@inheritDoc}
	 */
	public String getStatement() {

		// Example using 'Match Recognise' syntax.
		String normalEventExpression = "select * from ResponseTimeEvent " + "match_recognize ( " + "       measures A as temp1, B as temp2"
				+ "       pattern (A B) " + "       define " + "               A as A.responseTime < " + CDataConstant.RESPONSE_TIME_THRESHOLD + ", "
				+ "               B as B.responseTime < " + CDataConstant.RESPONSE_TIME_THRESHOLD + ")";

		return normalEventExpression;
	}

	/**
	 * Listener method called when Esper has detected a pattern match.
	 */
	public void update(Map<String, ResponseTimeEvent> eventMap) {

		// 1st ResponseTime in the Normal Sequence
		ResponseTimeEvent temp1 = (ResponseTimeEvent) eventMap.get("temp1");
		// 2nd ResponseTime in the Normal Sequence
		ResponseTimeEvent temp2 = (ResponseTimeEvent) eventMap.get("temp2");

		StringBuilder sb = new StringBuilder();
		sb.append("***************************************");
		sb.append("\n* [INFO] : NORMAL NOTIFICATION ! ");
		sb.append("\n* " + temp1 + " < " + CDataConstant.RESPONSE_TIME_THRESHOLD);
		sb.append("\n* " + temp2 + " < " + CDataConstant.RESPONSE_TIME_THRESHOLD);
		sb.append("\n***************************************");

		Feature feature = Feature.getInstance("responseTime");
		float evaluation = 1f;
		FeatureEvaluation evaluatedFeature = new FeatureEvaluation(feature, evaluation);
		forgiveFactorModel.updateTrustKnowledge(evaluatedFeature);

		Feature availability = Feature.getInstance("availability");
		float availabilityEvaluation = 1f;
		FeatureEvaluation availabilityEvaluatedFeature = new FeatureEvaluation(availability, availabilityEvaluation);
		forgiveFactorModel.updateTrustKnowledge(availabilityEvaluatedFeature);

		LOG.debug(sb.toString());
	}
}
