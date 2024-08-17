package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.ShopBusinessData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopBusinessDataRepo extends JpaRepository<ShopBusinessData , Long> {
}
