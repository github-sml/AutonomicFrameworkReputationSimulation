package org.autonomic.monitoring.framework.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.autonomic.monitoring.framework.event.ResponseTimeEvent;
import org.autonomic.monitoring.framework.handler.ResponseTimeEventHandler;
import org.autonomic.monitoring.framework.trustmodel.ForgiveFactor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Just a simple class to create a number of Random ResponseTimeEvents and pass them off to the
 * ResponseTimeEventHandler.
 */
@Component
public class RandomResponseTimeEventGenerator {

    /** Logger */
    private static Logger LOG = LoggerFactory.getLogger(RandomResponseTimeEventGenerator.class);

    /** The ResponseTimeEventHandler - wraps the Esper engine and processes the Events  */
    @Autowired
    private ResponseTimeEventHandler responseTimeEventHandler;

    @Autowired
    private ForgiveFactor forgiveFactorModel;
    /**
     * Creates simple random ResponseTime events and lets the implementation class handle them.
     */
    public void startSendingResponseTimeReadings(final long noOfResponseTimeEvents) {

        ExecutorService xrayExecutor = Executors.newSingleThreadExecutor();

        xrayExecutor.submit(new Runnable() {
            public void run() {

                LOG.debug(getStartingMessage());
                
                int count = 0;
                Date oStartDateOfTreatment = new Date();
                while (count < noOfResponseTimeEvents) {
                	int iResponseTime = new Random().nextInt(CDataConstant.RESPONSE_TIME_MAX);
                    ResponseTimeEvent ve = new ResponseTimeEvent(iResponseTime, new Date());
                    responseTimeEventHandler.handle(ve);
                    count++;
                    try {
                        Thread.sleep(iResponseTime);
//                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        LOG.error("Thread Interrupted", e);
                    }
                }
                Date oEndDateOfTreatment = new Date();
                LOG.debug("TOTAL NUMBER OF CRITICAL VIOLATION EVENT  = " + CDataConstant.TOTAL_NUMBER_CRITICAL_EVENT);
                if (CDataConstant.TOTAL_NUMBER_CRITICAL_EVENT == 0){
                	// increase reputation
                	CDataConstant.PROVIDER_REPUTATION = CDataConstant.PROVIDER_REPUTATION + CDataConstant.PROVIDER_REPUTATION_INCREASE_STEP;
                }else{
                	// decrease reputation
                	CDataConstant.PROVIDER_REPUTATION = CDataConstant.PROVIDER_REPUTATION - (CDataConstant.TOTAL_NUMBER_CRITICAL_EVENT * CDataConstant.PROVIDER_REPUTATION_DECREASE_STEP);
                }
//                double dProviderReputation = CDataConstant.PROVIDER_REPUTATION < 0.0 ? 0: CDataConstant.PROVIDER_REPUTATION ;
                double dProviderReputation =forgiveFactorModel.getTrustKnowledge().getTotalEvaluation();
                LOG.debug("PROVIDER REPUTATION = " + dProviderReputation +"\n");
                LOG.debug(forgiveFactorModel.getTrustKnowledge().toString());

                //nombre de d�faillances
                LOG.debug("TOTAL NUMBER OF FAILURES  = " + CDataConstant.TOTAL_NUMBER_OF_FAILURES);
                // somme des temps de d�faillance
                LOG.debug("SUM OF FAILURE TIMES  = " + CDataConstant.TOTAL_DURATION_OF_FAILURES);
                // Somme des Temps de Bon Fonctionnement = temps d'execution - somme des temps de d�faillance
                double iSumOfOperatingTimes =    (Math.abs(oEndDateOfTreatment.getTime() - oStartDateOfTreatment.getTime()) - CDataConstant.TOTAL_DURATION_OF_FAILURES);
                LOG.debug("SUM OF OPERATING TIMES = " + iSumOfOperatingTimes);
                // MTBF = Somme des Temps de Bon Fonctionnement / nombre de d�faillances 	(http://chohmann.free.fr/maintenance/mtbf_mttr.htm)
                double iMTBF = iSumOfOperatingTimes/CDataConstant.TOTAL_NUMBER_OF_FAILURES;
                LOG.debug("MTBF = " + iMTBF);
                
                // MTTR = Temps d'arr�t Total / nombre d'arr�ts
                double iMTTR  = CDataConstant.TOTAL_DURATION_OF_FAILURES/CDataConstant.TOTAL_NUMBER_OF_FAILURES;
                LOG.debug("MTTR = " + iMTTR);
                
                // Disponibilit� = MTBF / ( MTTR + MTBF )
                Double oAvailability = (iMTBF / (iMTTR+iMTBF))*100;
                Double oAvailabilityTruncated = BigDecimal.valueOf(oAvailability).setScale(2, RoundingMode.HALF_UP).doubleValue();
                LOG.debug("AVAILABILITY  = " +oAvailabilityTruncated +"%");
                
                // check if Availability is respected
                if (CDataConstant.AVAILABILITY_SERVICE_THRESHOLD > oAvailabilityTruncated){
                	LOG.debug("AVAILABILITY IS NOT RESPECTED = " +CDataConstant.AVAILABILITY_SERVICE_THRESHOLD +"% > "+ oAvailabilityTruncated +"%");
                }else{
                	LOG.debug("AVAILABILITY IS RESPECTED = " +CDataConstant.AVAILABILITY_SERVICE_THRESHOLD +"% <= "+ oAvailabilityTruncated +"%");
                }

            }
        });
    }

    
    private String getStartingMessage(){
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n************************************************************");
        sb.append("\n* STARTING - ");
        sb.append("\n* PLEASE WAIT - RESPONSE TIME ARE RANDOM SO MAY TAKE");
        sb.append("\n* A WHILE TO SEE NOTIFICATION, WARNING AND CRITICAL VIOLATION EVENTS!");
        sb.append("\n************************************************************\n");
        return sb.toString();
    }
}
