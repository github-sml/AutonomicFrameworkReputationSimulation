package org.autonomic.monitoring.framework.event;

import java.util.Date;

/**
 * Immutable ResponseTime Event class. The process control system creates these events. The
 * ResponseTimeEventHandler picks these up and processes them.
 */
public class ResponseTimeEvent {

    /** Response Time in milliseconds. */
    private int responseTime;
    
    /** Time responseTime reading was taken. */
    private Date timeOfReading;
    
    /**
     * Single value constructor.
     * @param value ResponseTime in Milliseconds.
     */
    /**
     * ResponseTime constructor.
     * @param responseTime ResponseTime in Milliseconds
     * @param timeOfReading Time of Reading
     */
    public ResponseTimeEvent(int responseTime, Date timeOfReading) {
        this.responseTime = responseTime;
        this.timeOfReading = timeOfReading;
    }

    /**
     * Get the ResponseTime.
     * @return ResponseTime in Milliseconds
     */
    public int getResponseTime() {
        return responseTime;
    }
       
    /**
     * Get time ResponseTime reading was taken.
     * @return Time of Reading
     */
    public Date getTimeOfReading() {
        return timeOfReading;
    }

    @Override
    public String toString() {
        return "ResponseTimeEvent ["+timeOfReading + ": " +responseTime + " ms]";
    }

}
