package com.stella.commons.schedule;


import com.stella.commons.StellaException;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class QuartzTableCreatedCondition {

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzTableCreatedCondition.class);

    DataSource dataSource;

    public QuartzTableCreatedCondition(DataSource dataSource) {
        Validate.notNull(dataSource, "dataSource must be specified");
        this.dataSource = dataSource;
    }

    public boolean matches() {
        try (Connection connection = dataSource.getConnection()){
            DatabaseMetaData meta = dataSource.getConnection().getMetaData();
            ResultSet res = meta.getTables(null, null, "qrtz_job_details", new String[]{"TABLE"});
            boolean exist = false;
            while (res.next()) {
                LOGGER.info("Exists Quartz tables:" + res.getString("TABLE_CAT")
                        + ", "+res.getString("TABLE_SCHEM")
                        + ", "+res.getString("TABLE_NAME")
                        + ", "+res.getString("TABLE_TYPE")
                        + ", "+res.getString("REMARKS"));
                exist = true;
                break;
            }

            //try select
            if (!exist) {
                exist = trySelect(dataSource);
            }
            res.close();
            return exist;
        } catch (Exception e) {
            throw new StellaException(e);
        }
    }

    private boolean trySelect(DataSource dataSource) {
        try {
            dataSource.getConnection().createStatement().execute("SELECT 1 from qrtz_job_details");
            return true;
        } catch (Exception e) {
            LOGGER.warn("SELECT 1 from qrtz_job_details not worked, not quartz table created yet.");
        }
        return false;
    }

}
