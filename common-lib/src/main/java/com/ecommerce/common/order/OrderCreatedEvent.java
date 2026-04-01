package com.ecommerce.common.order;

import com.ecommerce.common.event.BaseEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("order.created")
public class OrderCreatedEvent extends BaseEvent {
    private UUID userId;
    private List<OrderLineItem> items;
    private BigDecimal totalAmount;
    private UUID idempotencyKey;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class OrderLineItem {
        private UUID productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
    }
}
