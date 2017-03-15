package org.autonomic.monitoring.framework.trustmodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author y.mifrah
 *
 */
public class Feature implements Serializable {

	private String id;
	private String name;
	private String value;
	private float SLAValue;
	private float width;
	/**
	 * 
	 */
	private static Map<String, Feature> featureMap = new HashMap<String, Feature>();

	private Feature() {
	}

	synchronized public static Feature getInstance(String featureName) {
		Feature feature = featureMap.get(featureName);
		if (feature == null) {
			feature = new Feature();
			feature.setName(featureName);
			featureMap.put(featureName, feature);
		}
		return feature;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public float getSLAValue() {
		return SLAValue;
	}

	public void setSLAValue(float sLAValue) {
		SLAValue = sLAValue;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}
}
