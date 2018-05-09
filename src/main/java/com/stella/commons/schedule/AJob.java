package com.stella.commons.schedule;


import com.stella.commons.StellaUtility;
import com.stella.commons.annos.NotLoggable;
import com.stella.commons.email.EmailService;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.Date;

/**
 * Base class of a quartz job
 *
 * @author sinan.sahin
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public abstract class AJob extends QuartzJobBean {

    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

    @Autowired
    EmailService emailService;

    @Override
    protected void executeInternal(JobExecutionContext context)
            throws JobExecutionException {

        LOGGER.info("JOB[" + name() + "] Started");
        Date start = new Date();
        Exception ex = null;

        try {
            Class<?> clazz = this.getClass();
            if (isJobExecutionDisabled(clazz)) {
                String message = String.format("%s disabled. System property: %s disabled", name(), getExecutionDisabledSystemProperty(clazz));
                LOGGER.warn(message);
                ex = new Exception(message);
            } else {
                //Because of quartz bean is not spring bean, the injection is made by processInjectionBasedOnCurrentContext
                SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
                run(context);
            }
        } catch (Exception e) {
            ex = e;
            LOGGER.error("Error in", e);
            if (ArrayUtils.isNotEmpty(getEmailRecipientsOnError())) {
                emailService.send(getEmailRecipientsOnError(),
                        ExceptionUtils.getStackTrace(e),
                        "Job couldn't be completed;" + name(),
                        null,
                        null);
            }
        } finally {
            logToDB(start, new Date(), ex);
        }
        LOGGER.info("JOB[{}] Done; Status:{}",
                name(),
                ex == null ? "Successful" : String.format("Error: %s", ex.toString()));
    }


    /**
     * The name of the job should be unique in terms of logging.
     * Default the name of the job is the class's simple name.
     *
     * @return The name of the job.
     */
    public String name() {
        return this.getClass().getSimpleName();
    }


    public AJob() {
        LOGGER.debug("JOB[" + name() + "] is being created");
    }

    public void executeForTest(JobExecutionContext context) throws JobExecutionException {
        this.executeInternal(context);
    }

    /**
     * // TODO job logs should be persisted, implement LogJob
     * If this class is annotated with NotLoggable, logging to DB is passed.
     *
     * @param start
     * @param end
     * @param e
     */
    private void logToDB(Date start, Date end, Exception e) {
        if (this.getClass().isAnnotationPresent(NotLoggable.class)) {
            LOGGER.debug("{} is NotLoggable, so that log to DB passed", this.getClass());
            return;
        }
        try {
			/*
			LogJob logJob = LogJob.builder()
					.jobName(this.name())
					.start(start)
					.end(end)
					.build();
			logJob.setSuccess(e == null);
			logJob.setError(e != null ? ExceptionUtils.getStackTrace(e) : null);
			logService.createLog(logJob);
			*/
        } catch (Exception e2) {
            LOGGER.error("Error", e2);
        }
    }

    /**
     * It is implemented as the work of the job.
     *
     * @param context
     * @throws JobExecutionException
     */
    public abstract void run(JobExecutionContext context) throws JobExecutionException;

    /**
     * @param clazz
     * @return If false the job is not executed
     */
    protected boolean isJobExecutionDisabled(Class<?> clazz) {
        return StellaUtility.toBool(System.getProperty(getExecutionDisabledSystemProperty(clazz)));
    }

    /**
     * Initially the job is enabled.
     *
     * @param clazz
     * @return the system property that makes job disable or enable
     */
    protected String getExecutionDisabledSystemProperty(Class<?> clazz) {
        return clazz.getSimpleName() + ".disabled";
    }

    /**
     * @return Mail addresses to which email is sent when an error occurs.
     */
    public abstract String[] getEmailRecipientsOnError();


}
