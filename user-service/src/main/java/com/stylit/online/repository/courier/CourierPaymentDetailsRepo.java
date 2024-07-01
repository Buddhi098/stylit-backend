package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.CourierPaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierPaymentDetailsRepo extends JpaRepository<CourierPaymentDetails, Long> {
}
