package org.autonomic.monitoring.framework.trustmodel;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.datetime.DateFormatter;

/**
 * @author y.mifrah
 *
 */
public class FeatureEvaluation {
	private Feature feature;
	private float evaluation;
	private Date creationDate;
	private int updateCount = 0;

	public FeatureEvaluation(Feature feature, float evaluation) {
		this.feature = feature;
		this.evaluation = evaluation;
		creationDate = new Date();
	}

	/**
	 * @return the feature
	 */
	public Feature getFeature() {
		return feature;
	}

	/**
	 * @param feature
	 *            the feature to set
	 */
	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	/**
	 * @return the evaluation
	 */
	public float getEvaluation() {
		return evaluation;
	}

	/**
	 * @param evaluation
	 *            the evaluation to set
	 */
	public void setEvaluation(float evaluation) {
		this.evaluation = evaluation;
	}

	@Override
	public String toString() {
		return (getFeature().getName() + "	[" + updateCount + "]		:	" + getEvaluation() + "         " + isValid());
	}

	public int getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	public void incrementUpdateCount() {
		updateCount++;
	}

	public boolean isValid() {
		return feature.getSLAValue() <= evaluation;
	}

	public String toStringWithDate() {
		return new DateFormatter("dd/MM/yyyy HH:mm:ss").print(creationDate, Locale.getDefault()) + "  :  " + getEvaluation();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
}
