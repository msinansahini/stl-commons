package com.stella.commons.dto;

import com.stella.commons.Coin;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Data
@Builder
public class PaymentDto implements Serializable {

    private Coin coin;
    private String toAddress;
    private String txId;
    private BigDecimal amount;
    private Long blockNo;
    private Date createdTime;
    private Set<PaymentInputDto> inputs;

}
