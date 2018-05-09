/**
 * 
 */
package com.stella.commons.annos;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotate with this class that you do not want to log
 * @author sinan.sahin
 *
 */
@Documented
@Retention(value=java.lang.annotation.RetentionPolicy.RUNTIME)
@Target (value= {ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface NotLoggable {

}
