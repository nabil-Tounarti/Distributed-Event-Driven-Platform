package com.ecommerce.common.payment;

import com.ecommerce.common.event.BaseEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("payment.failed")
public class PaymentFailedEvent extends BaseEvent {
    private UUID orderId;
    private String failureReason;   // "INSUFFICIENT_FUNDS", "CARD_DECLINED", "TIMEOUT"
    private boolean retryable;      // true = retry later, false = cancel order now
}
