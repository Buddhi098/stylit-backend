package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Long> {

    boolean existsByShopEmail(String email);
}
