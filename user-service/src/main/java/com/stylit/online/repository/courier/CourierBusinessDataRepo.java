package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.CourierBusinessData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierBusinessDataRepo extends JpaRepository<CourierBusinessData , Long> {
}
