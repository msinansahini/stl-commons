package com.stella.commons.schedule;

import com.stella.commons.StellaUtility;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author sinan.sahin
 */
public class DefaultSchedulerFactoryBean extends org.springframework.scheduling.quartz.SchedulerFactoryBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSchedulerFactoryBean.class);

    private Environment environment;

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
    }


    /**
     * Triggers are added into scheduler by hand
     *
     */
    @Override
    public void setTriggers(Trigger... triggers) {
        if (isInsertAllQuartzTasks()) {
            super.setTriggers(triggers);
        } else {
            List<Trigger> additives = new ArrayList<>();
            for (Trigger trigger : triggers) {
                String triggerJobName = trigger.getJobKey().getName();
                if (StellaUtility.toBool(getEnvironment().getRequiredProperty(triggerJobName + ".enabled"))) {
                    additives.add(trigger);
                    LOGGER.info("Will be added to triggers: {}", triggerJobName);
                }
            }
            super.setTriggers(additives.toArray(new Trigger[]{}));
        }
    }

    /**
     * The additive jobs can be added into scheduler in the way that jobname.enabled=[true,false]
     * If insert.all.quartz.tasks is true, jobname.enabled is omitted.
     *
     * @return
     */
    public boolean isInsertAllQuartzTasks() {
        return StellaUtility.toBool(getEnvironment().getRequiredProperty("insert.all.quartz.task"));
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
