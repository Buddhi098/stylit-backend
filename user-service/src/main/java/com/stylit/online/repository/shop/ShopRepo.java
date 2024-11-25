package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.Shop;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Long> {

    Optional<Shop> findByShopEmail(String email);
    boolean existsByShopEmail(String email);

    Optional<Shop> findById(Long id);

    @EntityGraph(attributePaths = {"shopLocation", "shopBusinessData", "shopInformation"})
    List<Shop> findAll();
}
