package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierRepo extends JpaRepository<Courier, Long> {
    boolean existsByEmail(String email);
}
