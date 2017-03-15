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
public class CriticalEventSubscriber implements StatementSubscriber {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(CriticalEventSubscriber.class);


    @Autowired
    private ForgiveFactor forgiveFactorModel;
    
    /**
     * If the last event in a critical sequence is this much greater than the first - issue a
     * critical alert.
     */
    private static final String CRITICAL_EVENT_MULTIPLIER = "1.5";
    
    /**
     * {@inheritDoc}
     */
    public String getStatement() {
        
        // Example using 'Match Recognize' syntax.
        String crtiticalEventExpression = "select * from ResponseTimeEvent "
                + "match_recognize ( "
                + "       measures A as temp1, B as temp2, C as temp3, D as temp4 "
                + "       pattern (A B C D) " 
                + "       define "
                + "               A as A.responseTime > " + CDataConstant.RESPONSE_TIME_THRESHOLD + ", "
                + "               B as B.responseTime > " + CDataConstant.RESPONSE_TIME_THRESHOLD + ", "
                + "               C as C.responseTime > " + CDataConstant.RESPONSE_TIME_THRESHOLD + ", "
                + "               D as D.responseTime > " + CDataConstant.RESPONSE_TIME_THRESHOLD + ") ";
        
        return crtiticalEventExpression;
    }
    
    /**
     * Listener method called when Esper has detected a pattern match.
     */
    public void update(Map<String, ResponseTimeEvent> eventMap) {

        // 1st ResponseTime in the Critical Sequence
        ResponseTimeEvent temp1 = (ResponseTimeEvent) eventMap.get("temp1");
        // 2nd ResponseTime in the Critical Sequence
        ResponseTimeEvent temp2 = (ResponseTimeEvent) eventMap.get("temp2");
        // 3rd ResponseTime in the Critical Sequence
        ResponseTimeEvent temp3 = (ResponseTimeEvent) eventMap.get("temp3");
        // 4th ResponseTime in the Critical Sequence
        ResponseTimeEvent temp4 = (ResponseTimeEvent) eventMap.get("temp4");

        StringBuilder sb = new StringBuilder();
        sb.append("***************************************");
        sb.append("\n* [ALERT] : CRITICAL VIOLATION DETECTED! ");
        sb.append("\n* " + temp1 + " > " + CDataConstant.RESPONSE_TIME_THRESHOLD);
        sb.append("\n* " + temp2 + " > " + CDataConstant.RESPONSE_TIME_THRESHOLD);
        sb.append("\n* " + temp3 + " > " + CDataConstant.RESPONSE_TIME_THRESHOLD);
        sb.append("\n* " + temp4 + " > " + CDataConstant.RESPONSE_TIME_THRESHOLD);
        sb.append("\n***************************************");
        CDataConstant.TOTAL_NUMBER_CRITICAL_EVENT ++;
        Feature responseTime = Feature.getInstance("responseTime");
		float evaluation = 0.3f;
		FeatureEvaluation evaluatedFeature = new FeatureEvaluation(responseTime, evaluation);
		forgiveFactorModel.updateTrustKnowledge(evaluatedFeature);

		
		Feature availability = Feature.getInstance("availability");
		float availabilityEvaluation = 1f;
		FeatureEvaluation availabilityEvaluatedFeature = new FeatureEvaluation(availability, availabilityEvaluation);
		forgiveFactorModel.updateTrustKnowledge(availabilityEvaluatedFeature);

		
		
        LOG.debug(sb.toString());
    }

    
}
