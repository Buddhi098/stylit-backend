package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentDetailsRepo extends JpaRepository<PaymentDetails , Long> {
}
