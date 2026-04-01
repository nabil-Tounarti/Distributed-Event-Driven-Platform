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
@JsonTypeName("inventory.reserved")
public class InventoryReservedEvent extends BaseEvent {
    private UUID orderId;
    private List<ReservedItem> reservedItems;

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class ReservedItem {
        private UUID productId;
        private int quantity;
        private int stockRemaining; // useful for low-stock alerts
    }
}
