package com.stella.commons.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class PaymentInputDto implements Serializable {

    private String fromAddress;
    private Long inputNo;
    private Date createdTime;

}
