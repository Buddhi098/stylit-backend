package com.stylit.online.repository.courier;

import com.stylit.online.model.courier.ContactPersonDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPersonDetailsRepo extends JpaRepository<ContactPersonDetails, Long> {
}
