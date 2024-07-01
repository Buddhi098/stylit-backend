package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.ServiceDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceDetailsRepo extends JpaRepository<ServiceDetails, Long> {
}
