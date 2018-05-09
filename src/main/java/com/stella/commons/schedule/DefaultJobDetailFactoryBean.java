package com.stella.commons.schedule;

import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;


/**
 *
 * @author sinan.sahin
 *
 */
public class DefaultJobDetailFactoryBean extends JobDetailFactoryBean {

    private Environment environment;
	
	public DefaultJobDetailFactoryBean() {
		super();
	}

	@Override
	public void afterPropertiesSet() {
		setDurability(Boolean.parseBoolean(getEnvironment().getRequiredProperty("quartz.job.durability")));
		setGroup(getEnvironment().getRequiredProperty("quartz.group.name"));		
		super.afterPropertiesSet();
	}

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}
	
}
