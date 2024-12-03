package com.stylit.online.repository.shop;

import com.stylit.online.model.shop.Shop;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepo extends JpaRepository<Shop, Long> {

    Optional<Shop> findByShopEmail(String email);
    boolean existsByShopEmail(String email);

    Optional<Shop> findById(Long id);

    @EntityGraph(attributePaths = {"shopLocation", "shopBusinessData", "shopInformation"})
    List<Shop> findAll();

    @Query("SELECT COUNT(s) FROM Shop s WHERE s.status = 'ACTIVE'")
    long countActiveShops();

    @Query("SELECT COUNT(s) FROM Shop s WHERE s.status = 'ACTIVE' AND s.createdAt >= :startDate AND s.createdAt <= :endDate")
    long countShopsCreatedBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT AVG(shop_count) AS average_shops_per_month FROM (SELECT COUNT(s.id) AS shop_count FROM Shop s GROUP BY YEAR(s.createdAt), MONTH(s.createdAt)) AS monthly_counts")
    double monthlyAvgShopsCreated();

}
