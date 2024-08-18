package com.stylit.online.repository;

import com.stylit.online.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product , Long> {
    @Query("SELECT p FROM Product p WHERE p.isRemove <> 1")
    List<Product> findAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.isRemove <> 1 AND p.shopId = :shopId")
    List<Product> findAllActiveProductsByShopId(@Param("shopId") String shopId);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isRemove = 1 WHERE p.id = :productId AND p.shopId = :shopId")
    void markProductAsRemoved(@Param("productId") Long productId, @Param("shopId") String shopId);
}
