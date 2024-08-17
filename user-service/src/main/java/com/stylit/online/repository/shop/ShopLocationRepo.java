package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.ShopLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopLocationRepo extends JpaRepository<ShopLocation , Long> {
}
