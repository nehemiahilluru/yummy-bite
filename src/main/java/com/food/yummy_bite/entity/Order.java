package com.food.yummy_bite.entity;

import com.food.yummy_bite.enums.OrderStatus;
import com.food.yummy_bite.enums.OrderType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(name = "order_reference", unique = true, nullable = false, length = 30)
    private String orderReference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30, name = "order_status")
    private OrderStatus orderStatus = OrderStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20, name = "order_type")
    private OrderType orderType = OrderType.DELIVERY;

    @Column(name = "sub_total", precision = 10, scale = 2)
    private BigDecimal subTotal;

    @Column(name = "delivery_fee", precision = 10, scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "tip_amount", precision = 10, scale = 2)
    private BigDecimal tipAmount = BigDecimal.ZERO;

    @Column(name = "discount_applied", precision = 10, scale = 2)
    private BigDecimal discountApplied = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "delivery_latitude"
          //  , precision = 10, scale = 8
    )
    private Double deliveryLatitude;

    @Column(name = "delivery_longitude"
            //, precision = 11, scale = 8
    )
    private Double deliveryLongitude;

    @Column(name = "delivery_instructions", columnDefinition = "TEXT")
    private String deliveryInstructions;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Column(name = "actual_delivery_time")
    private LocalDateTime actualDeliveryTime;

    @Column(name = "preparation_started_at")
    private LocalDateTime preparationStartedAt;

    @Column(name = "ready_at")
    private LocalDateTime readyAt;

    @Column(name = "picked_up_at")
    private LocalDateTime pickedUpAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Version
    private Long version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();


    public void addItem(OrderItem item){
        items.add(item);
        item.setOrder(this);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.subTotal = items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO , BigDecimal::add);

        this.totalAmount = subTotal
                .add(deliveryFee!=null?deliveryFee:BigDecimal.ZERO)
                .add(tax!=null?tax:BigDecimal.ZERO)
                .add(tipAmount!=null?tipAmount:BigDecimal.ZERO)
                .subtract(discountApplied!=null?discountApplied:BigDecimal.ZERO);
        if(totalAmount.compareTo(BigDecimal.ZERO)<0) {
            totalAmount = BigDecimal.ZERO;
        }
    }

    public boolean canTransitionTo(OrderStatus newStatus){
        return switch(this.orderStatus){
            case PENDING -> newStatus == OrderStatus.ACCEPTED || newStatus == OrderStatus.CANCELLED;
            case ACCEPTED -> newStatus == OrderStatus.PREPARING || newStatus == OrderStatus.CANCELLED;
            case PREPARING -> newStatus == OrderStatus.READY ;
            case READY -> newStatus == OrderStatus.ASSIGNED || newStatus == OrderStatus.CANCELLED ;
            case ASSIGNED -> newStatus == OrderStatus.PICKED_UP || newStatus == OrderStatus.CANCELLED;
            case PICKED_UP -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED , CANCELLED -> false;
        };
    }
    public  boolean isCancellable(){
        return switch (this.orderStatus){
            case PENDING , ACCEPTED , READY ,ASSIGNED -> true;
            default -> false;
        };
    }

}