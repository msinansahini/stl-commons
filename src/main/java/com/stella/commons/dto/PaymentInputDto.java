package com.stella.commons.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentInputDto implements Serializable {

    private String fromAddress;
    private Long inputNo;
    private Date createdTime;

}
