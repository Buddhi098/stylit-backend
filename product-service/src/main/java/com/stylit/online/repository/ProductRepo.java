package com.stylit.online.repository;

import com.stylit.online.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product , Long> {
    List<Product> findAllByShopId(String id);
}
