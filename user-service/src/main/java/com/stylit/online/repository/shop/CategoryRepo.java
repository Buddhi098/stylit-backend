package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category , Long> {
}
