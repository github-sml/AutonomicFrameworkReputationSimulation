package org.autonomic.monitoring.framework.util;

import java.util.Date;

public class CDataConstant {
	
	 /** Used as the MAX RANDOM  */
	public static final int RESPONSE_TIME_MAX = 1500;
	
	 /** Used as the minimum starting threshold for a violation event. */
	public static final int RESPONSE_TIME_THRESHOLD = 800;
	
	
    /** If 2 consecutive responseTime events are greater than RESPONSE_TIME_THRESHOLD this - issue a notification */
	public static final int RESPONSE_TIME_THRESHOLD_NOTIFICATION = 2;
	/** If 3 consecutive responseTime events are greater than RESPONSE_TIME_THRESHOLD this - issue a warning */
	public static final int RESPONSE_TIME_THRESHOLD_WARNING = 3;
	/** If 4 consecutive responseTime events are greater than RESPONSE_TIME_THRESHOLD this - issue a critical */
	public static final int RESPONSE_TIME_THRESHOLD_CRITICAL = 4;
	
	
	public static final int TOTAL_NUMBER_NOTIFICATION_EVENT = 0;
	public static final int TOTAL_NUMBER_WARNING_EVENT = 0;
	public static int TOTAL_NUMBER_CRITICAL_EVENT = 0;
	
	public static final double PROVIDER_REPUTATION_INITIAL = 0.8;
	public static final double PROVIDER_REPUTATION_DECREASE_STEP  = 0.1;
	public static final double PROVIDER_REPUTATION_INCREASE_STEP  = 0.1;
	public static double PROVIDER_REPUTATION = 0;
	
	/** Used as the minimum starting threshold for a FAILURE SERVICE. */
	public static final int FAILURE_SERVICE_THRESHOLD = 1490;
	public static int TOTAL_DURATION_OF_FAILURES = 0;
	public static int TOTAL_NUMBER_OF_FAILURES = 0;
	 /** Used as the minimum starting threshold for a AVAILABILITY SERVICE. */
	public static final Double AVAILABILITY_SERVICE_THRESHOLD = 98.00;
	
	
	
	
}
