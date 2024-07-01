package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.CourierAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierAddressRepo extends JpaRepository<CourierAddress, Long> {
}
