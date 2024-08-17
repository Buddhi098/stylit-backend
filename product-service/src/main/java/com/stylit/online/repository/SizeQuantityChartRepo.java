package com.stylit.online.repository;

import com.stylit.online.model.SizeQuantityChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SizeQuantityChartRepo extends JpaRepository<SizeQuantityChart, Long> {
}
