package com.stella.commons.schedule;

import com.stella.commons.Constants;
import com.stella.commons.StellaException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;


/**
 * IT is conditional configuration on property quartz.enabled
 *
 * @author sinan.sahin
 */
public abstract class AScheduleConfig {

    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    public static final String CRON_EACH_MINUTE = "0 0/1 * * * ?";
    public static final String CRON_EACH_5_MINUTES = "0 0/5 * * * ?";
    public static final String CRON_EACH_10_MINUTE = "0 0/10 * * * ?";
    public static final String CRON_END_OF_DAY = "0 0 0 1/1 * ? *";

    @Bean(name = "jobFactory")
    @DependsOn("quartzDbInitializer")
    public DefaultSchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) throws IOException {
        DefaultSchedulerFactoryBean scheduler = new DefaultSchedulerFactoryBean();
        scheduler.setJobFactory(springBeanJobFactory());
        scheduler.setQuartzProperties(quartzProperties());
        scheduler.setDataSource(dataSource);
        scheduler.setEnvironment(environment);
        List<Trigger> triggers = cronConfigurations();
        if (CollectionUtils.isNotEmpty(triggers)) {
            scheduler.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        }
        return scheduler;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        LOGGER.debug("Configuring Job factory");
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /**
     * reads /quartz.properties from classpath
     * @return
     * @throws IOException
     */
    @Bean("quartzProperties")
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }


    protected abstract List<Trigger> cronConfigurations();

    /**
     * @param clazz
     * @param jobDetailName
     * @param triggerName
     * @param cronExpression must be cron expression
     * @return
     */
    protected CronTrigger createCronTrigger(Class<? extends AJob> clazz, String jobDetailName, String triggerName, String cronExpression) {

        Validate.notNull(triggerName, "clazz must be specified");
        Validate.notBlank(triggerName, "triggerName must be specified");
        Validate.notBlank(jobDetailName, "jobDetailName must be specified");
        Validate.notBlank(cronExpression, "cronExpression must be specified");

        CronTriggerFactoryBean ctFactory = new CronTriggerFactoryBean();
        DefaultJobDetailFactoryBean jdFactory = new DefaultJobDetailFactoryBean();
        jdFactory.setEnvironment(environment);
        jdFactory.setJobClass(clazz);
        jdFactory.setName(jobDetailName);
        jdFactory.afterPropertiesSet();

        ctFactory.setJobDetail(jdFactory.getObject());
        ctFactory.setGroup("ignite-group");
        ctFactory.setName(triggerName);
        ctFactory.setCronExpression(cronExpression);

        try {
            ctFactory.afterPropertiesSet();
        } catch (ParseException e) {
            throw new StellaException(e);
        }

        return ctFactory.getObject();
    }

    @ConditionalOnProperty(name = "quartz.createFromSql")
    @Bean(name = Constants.BeanNames.QUARTZ_DB_INITIALIZER)
    public DataSourceInitializer quartzDbInitializer(DataSource dataSource,
                                                     QuartzTableCreatedCondition createdCondition) throws SQLException {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setEnabled(true);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.setContinueOnError(false);
        populator.setIgnoreFailedDrops(false);
        populator.setSqlScriptEncoding("UTF-8");

        if (!createdCondition.matches()) {
            String dbName = dataSource.getConnection().getMetaData().getDatabaseProductName().toLowerCase(Locale.ENGLISH);
            if (StringUtils.indexOf(dbName, "hsql") >= 0) {
                populator.setScripts(new ClassPathResource("/db/create-schema-hsqldb.sql"));
                populator.setScripts(new ClassPathResource("/db/quartz-init-hsqldb.sql"));
            } else if (StringUtils.indexOf(dbName, "mysql") >= 0) {
                populator.setScripts(new ClassPathResource("/db/quartz-init-mysql.sql"));
            } else if (StringUtils.indexOf(dbName, "postgres") >= 0) {
                populator.setScripts(new ClassPathResource("/db/quartz-init-postgres.sql"));
            } else if (StringUtils.indexOf(dbName, "h2") >= 0) {
                populator.setScripts(new ClassPathResource("/db/quartz-init-h2.sql"));
            } else {
                throw new RuntimeException("No quartz init sql for db: " + dbName);
            }
        }
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    @ConditionalOnProperty(name = "quartz.createFromSql", havingValue = "false")
    @Bean(name = Constants.BeanNames.QUARTZ_DB_INITIALIZER)
    public DataSourceInitializer quartzDbInitializerDummy(DataSource dataSource,
                                                     QuartzTableCreatedCondition createdCondition) throws SQLException {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setEnabled(true);
        return initializer;
    }

    @Bean
    public QuartzTableCreatedCondition quartzTableCreatedCondition(DataSource dataSource) {
        return new QuartzTableCreatedCondition(dataSource);
    }

}
