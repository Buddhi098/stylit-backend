package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.StoreFrontInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreFrontInformationRepo extends JpaRepository<StoreFrontInformation, Long> {
}
