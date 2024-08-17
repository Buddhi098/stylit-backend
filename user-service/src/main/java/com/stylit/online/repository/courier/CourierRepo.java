package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepo extends JpaRepository<Courier, Long> {
    Optional<Courier> findByCourierEmail(String email);
    boolean existsByCourierEmail(String email);
}
