package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.BusinessInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessInformationRepo extends JpaRepository<BusinessInformation , Long> {
}
