package com.stella.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class represents response return to client
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StellaResponse<T> implements Serializable {
    private Integer errorCode;
    private List<String> messages;
    private T payload;
    @Builder.Default
    private long time = new java.util.Date().getTime();

    public List<String> getMessages() {
        if (this.messages == null) {
            this.messages = new ArrayList<>();
        }
        return this.messages;
    }

    public StellaResponse addMessage(String message) {
        getMessages().add(message);
        return this;
    }

    @JsonIgnore
    public boolean isSuccess() {
        return errorCode == null || errorCode == 0;
    }
}
