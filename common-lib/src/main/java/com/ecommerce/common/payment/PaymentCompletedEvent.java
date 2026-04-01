package com.ecommerce.common.payment;

import com.ecommerce.common.event.BaseEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("payment.completed")
public class PaymentCompletedEvent extends BaseEvent {
    private UUID orderId;           // which order this payment is for
    private UUID userId;
    private BigDecimal amount;
    private String currency;        // "EUR", "USD" — always store currency
    private String gatewayReference; // external payment gateway's transaction ID
}
