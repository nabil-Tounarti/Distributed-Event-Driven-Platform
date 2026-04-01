package com.ecommerce.common.order;

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
@JsonTypeName("order.cancelled")
public class OrderCancelledEvent extends BaseEvent {
    private UUID userId;
    private String reason;          // "PAYMENT_FAILED", "OUT_OF_STOCK", "USER_REQUESTED"
    private BigDecimal refundAmount; // how much to refund (0 if never charged)
}
