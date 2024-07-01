package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.BusinessDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessDetailsRepo extends JpaRepository<BusinessDetails , Long> {
}
