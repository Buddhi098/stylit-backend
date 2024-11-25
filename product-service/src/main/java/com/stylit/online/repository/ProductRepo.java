package com.stylit.online.repository;

import com.stylit.online.model.Product;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.isRemove <> 1")
    List<Product> findAllActiveProducts();

    Optional<Product> findByIdAndShopId(Long id , String ShopId);

    @Query("SELECT p FROM Product p WHERE p.isRemove <> 1 AND p.shopId = :shopId")
    List<Product> findAllActiveProductsByShopId(@Param("shopId") String shopId);

    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isRemove = 1 WHERE p.id = :productId AND p.shopId = :shopId")
    int markProductAsRemoved(@Param("productId") Long productId, @Param("shopId") String shopId);

    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.id = :productId AND p.shopId = :shopId AND p.isRemove <> 1")
    boolean existByProductIdAndShopId(@Param("productId") Long productId, @Param("shopId") String shopId);

    @Query("SELECT p FROM Product p WHERE p.generalInformation.gender = :gender AND p.isRemove <> 1")
    List<Product> findAllActiveProductsByGender(@Param("gender") String gender , Pageable pageable);


    Optional<Product> findById(@NotNull Long id);

}