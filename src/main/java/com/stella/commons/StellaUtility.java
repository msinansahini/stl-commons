package com.stella.commons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class StellaUtility {
    private static final Logger logger = LoggerFactory.getLogger(StellaUtility.class);

    public static void sleep(long milliSecond) {
        try {
            logger.debug("Waiting for {} ms", milliSecond);
            Thread.sleep(milliSecond);
        } catch (InterruptedException e) {
            throw new StellaException(e);
        }
    }


    /**
     * @param obj
     * @return true If obj "true" or "True" etc.
     */
    public static boolean toBool(Object obj) {
        try {
            return Boolean.valueOf(Objects.toString(obj == null ? "false": obj));
        } catch (Exception e) {}
        return false;
    }
}
