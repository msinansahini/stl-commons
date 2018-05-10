package com.stella.commons.controller;

import com.stella.commons.StellaResponse;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

/**
 * All controller specific controller commons methods and definitions should be written in this class
 * @author sinan.sahin
 */
public abstract class AbstractController {

    protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    protected <T> ResponseEntity<StellaResponse> createSuccess(T payload, String ... messages) {
        StellaResponse<T> success = StellaResponse.<T>builder()
            .errorCode(null)
            .payload(payload)
            .build();
        if (ArrayUtils.isNotEmpty(messages)) {
            success.setMessages(Arrays.asList(messages));
        }
        return ResponseEntity.ok(success);
    }
}
