package org.autonomic.monitoring.framework.trustmodel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author y.mifrah
 *
 */
@Component
@Scope(value = "singleton")
public class ForgiveFactor {
	private float forgivenessFactor = 0;

	public ForgiveFactor() {
		this.forgivenessFactor = 0.7f;
	}

	@Autowired
	private TrustKnowledge trustKnowledge;

	public void updateTrustKnowledge(FeatureEvaluation evaluatedFeature) {
		List<FeatureEvaluation> passedEvaluationsOfCurrentFeature = getTrustKnowledge().getPassedExperiences().get(evaluatedFeature.getFeature().getName());
		if (passedEvaluationsOfCurrentFeature == null) {
			passedEvaluationsOfCurrentFeature = new ArrayList<FeatureEvaluation>();
			getTrustKnowledge().getPassedExperiences().put(evaluatedFeature.getFeature().getName(), passedEvaluationsOfCurrentFeature);
		}
		FeatureEvaluation currentEvaluationOfTheFeature = getTrustKnowledge().getEvaluation(evaluatedFeature.getFeature());
		float updatedEvaluation = evaluatedFeature.getEvaluation();
		int nbrPassedEval = passedEvaluationsOfCurrentFeature.size();
		for (int i = 0; i != nbrPassedEval; ++i) {
			FeatureEvaluation f = passedEvaluationsOfCurrentFeature.get(i);
			updatedEvaluation += Math.pow(forgivenessFactor, nbrPassedEval - i) * f.getEvaluation();

		}

		updatedEvaluation *= (1 - forgivenessFactor);
		updatedEvaluation /= (1 - Math.pow(forgivenessFactor, nbrPassedEval+1));
		currentEvaluationOfTheFeature.setEvaluation(updatedEvaluation);
		currentEvaluationOfTheFeature.incrementUpdateCount();;
		passedEvaluationsOfCurrentFeature.add(evaluatedFeature);
	}

	public String getTrustMetricName() {
		return "ForgiveFactor";
	}

	public TrustKnowledge getTrustKnowledge() {
		return trustKnowledge;
	}

	public void setTrustKnowledge(TrustKnowledge trustKnowledge) {
		this.trustKnowledge = trustKnowledge;
	}

}
