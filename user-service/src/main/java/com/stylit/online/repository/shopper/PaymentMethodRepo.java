package com.stylit.online.repository.shopper;

import com.stylit.online.model.shopper.PaymentMethod;
import com.stylit.online.model.shopper.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepo extends JpaRepository<PaymentMethod, Long> {

}
