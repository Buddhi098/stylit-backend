package com.stylit.online.repository.order;

import com.stylit.online.model.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem , Long> {
    List<OrderItem> findByShopId(Long shopId);

    List<OrderItem> findByCourierId(Long courierId);
}
