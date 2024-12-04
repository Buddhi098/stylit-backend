package com.stylit.online.repository.order;

import com.stylit.online.model.order.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long shopperId);

    List<Order> findByOrderItems_ShopId(Long shopId);
}
