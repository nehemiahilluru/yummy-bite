package com.food.yummy_bite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "order_item")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
   private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id" , nullable = false)
   private  Order order;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id" , nullable = false)
   private MenuItem menuItem;
    @Column(name = "item_name" , nullable = false , length = 100)
   private  String itemName;
    @Column(nullable = false)
   private Integer quantity;
    @Column(name = "unit_price" , nullable = false , precision = 10 , scale = 2)
   private BigDecimal unitPrice;
    @Column(name = "total_price" , nullable = false , precision = 10 , scale = 2)
   private BigDecimal totalPrice;
    @Column(name = "special_instructions" , columnDefinition = "TEXT")
   private String specialInstructions;
    @CreationTimestamp
    @Column(name = "created_at" , nullable = false , updatable = false)
   private LocalDateTime createdAt;

    @PrePersist
    @PreUpdate
    private void calculateTotal(){
        if(this.quantity!=null && this.unitPrice!=null){
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }
}
