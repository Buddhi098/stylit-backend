package com.stylit.online.repository;

import com.stylit.online.model.VariantBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantBoxRepo extends JpaRepository<VariantBox , Long> {
}
