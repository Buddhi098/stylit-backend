package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.CourierLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierLocationRepo extends JpaRepository<CourierLocation , Long> {
}
