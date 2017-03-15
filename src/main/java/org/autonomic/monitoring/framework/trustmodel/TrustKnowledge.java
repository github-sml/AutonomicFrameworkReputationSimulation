package org.autonomic.monitoring.framework.trustmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author y.mifrah
 *
 */
@Component
@Scope(value = "singleton")
public class TrustKnowledge {
	private List<FeatureEvaluation> featureEvaluations = new ArrayList<FeatureEvaluation>();
	private Map<String, List<FeatureEvaluation>> passedExperiences = new HashMap<String, List<FeatureEvaluation>>();

	public FeatureEvaluation getEvaluation(Feature feature) {
		for (FeatureEvaluation fe : featureEvaluations) {
			if (fe.getFeature().equals(feature))
				return fe;
		}
		FeatureEvaluation fe = new FeatureEvaluation(feature, 0f);
		featureEvaluations.add(fe);
		return fe;
	}

	/**
	 * @return the featureEvaluations
	 */
	public List<FeatureEvaluation> getFeatureEvaluations() {
		return featureEvaluations;
	}

	/**
	 * @param featureEvaluations
	 *            the featureEvaluations to set
	 */
	public void setFeatureEvaluations(List<FeatureEvaluation> featureEvaluations) {
		this.featureEvaluations = featureEvaluations;
	}

	@Override
	public String toString() {
		String str = "Total Evaluation  :  " + getTotalEvaluation() + "\n";
		str += "featureName  		|   evaluation   |	   SLA Status   |\n";
		for (FeatureEvaluation featureEvaluation : featureEvaluations) {
			str += featureEvaluation.toString() + "\n";

			str += "Passed feature's experiences :\n";
			for (FeatureEvaluation f : passedExperiences.get(featureEvaluation.getFeature().getName())) {
				str += f.toStringWithDate() + "\n";

			}
		}
		return str;
	}

	public int getNbrExperience() {

		int totalNbrExp = 0;
		for (FeatureEvaluation f : featureEvaluations) {
			totalNbrExp += f.getUpdateCount();
		}
		return totalNbrExp;
	}

	public Map<String, List<FeatureEvaluation>> getPassedExperiences() {
		return passedExperiences;
	}

	public void setPassedExperiences(Map<String, List<FeatureEvaluation>> passedExperiences) {
		this.passedExperiences = passedExperiences;
	}

	public float getTotalEvaluation() {
		float total = 0;
		float totalWidth = 0;
		for (FeatureEvaluation f : featureEvaluations) {
			total += f.getFeature().getWidth() * f.getEvaluation();
			totalWidth += f.getFeature().getWidth();
		}

		total /= totalWidth;

		return total;
	}
}
