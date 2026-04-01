package com.ecommerce.common.event;

import com.ecommerce.common.inventory.InventoryReservationFailedEvent;
import com.ecommerce.common.inventory.InventoryReservedEvent;
import com.ecommerce.common.order.OrderCancelledEvent;
import com.ecommerce.common.order.OrderCreatedEvent;
import com.ecommerce.common.payment.PaymentCompletedEvent;
import com.ecommerce.common.payment.PaymentFailedEvent;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.time.Instant;

@Data
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "eventType",
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        visible = true
)
@JsonSubTypes({
        @Type(value = OrderCreatedEvent.class,   name = "order.created"),
        @Type(value = OrderCancelledEvent.class, name = "order.cancelled"),
        @Type(value = PaymentCompletedEvent.class, name = "payment.completed"),
        @Type(value = PaymentFailedEvent.class,  name = "payment.failed"),
        @Type(value = InventoryReservedEvent.class, name = "inventory.reserved"),
        @Type(value = InventoryReservationFailedEvent.class,
                name = "inventory.reservation-failed")
})
public abstract class BaseEvent {
    private final UUID eventId = UUID.randomUUID();
    private UUID correlationId;
    private UUID aggregateId;
    private String aggregateType;
    private String eventType;       // "order.created", "payment.completed"...
    private Instant occurredAt = Instant.now();
    private int eventVersion = 1;
}
