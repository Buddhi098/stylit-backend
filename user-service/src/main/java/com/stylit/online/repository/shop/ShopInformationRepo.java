package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.ShopInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopInformationRepo extends JpaRepository<ShopInformation , Long> {
}
