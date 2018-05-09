package com.stella.commons.email;

public interface EmailService {

    /**
     * Sends async
     * @param recipients mandatory
     * @param subject mandatory
     * @param content mandatory
     */
    void send(String[] recipients, String subject, String content, String objectId, String type);

    /**
     *
     * @param recipients mandatory
     * @param subject mandatory
     * @param content mandatory
     * @param sync true if async send
     */
    void send(String[] recipients, String subject, String content, String objectId, String type, boolean sync);
}
