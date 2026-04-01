package com.ecommerce.common.inventory;

import com.ecommerce.common.event.BaseEvent;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonTypeName("inventory.reservation-failed")
public class InventoryReservationFailedEvent extends BaseEvent {
    private UUID orderId;
    private List<FailedItem> failedItems; // which specific items had no stock

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class FailedItem {
        private UUID productId;
        private int requested;
        private int available; // how much stock actually exists
    }
}
