package com.stylit.online.service;

import com.stylit.online.repository.shop.ShopRepo;
import com.stylit.online.repository.shopper.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserStats {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ShopRepo shopRepo;

    public ResponseEntity<Map<String, Object>> getUserStatistics() {
        long totalUsers = userRepo.countTotalUsers();
        LocalDateTime endDate = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime startDate = LocalDate.now().minusDays(30).atStartOfDay();
        long usersLast30Days = userRepo.countUsersCreatedBetween(startDate, endDate);
        double monthlyAvgUsers = userRepo.monthlyAvgUsersCreated();
        double percentageIncreaseUsers = calculatePercentageIncrease(usersLast30Days, monthlyAvgUsers);

        long activeShops = shopRepo.countActiveShops();
        long shopsLast30Days = shopRepo.countShopsCreatedBetween(startDate, endDate);
        double monthlyAvgShops = shopRepo.monthlyAvgShopsCreated();
        double percentageIncreaseShops = calculatePercentageIncrease(shopsLast30Days, monthlyAvgShops);

        LocalDateTime yesterday = LocalDate.now().minusDays(1).atStartOfDay();
        long usersYesterday = userRepo.countUsersCreatedBetween(yesterday, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", totalUsers);
        response.put("usersLast30Days", usersLast30Days);
        response.put("percentageIncreaseUsers", percentageIncreaseUsers);
        response.put("activeShops", activeShops);
        response.put("shopsLast30Days", shopsLast30Days);
        response.put("percentageIncreaseShops", percentageIncreaseShops);
        response.put("usersYesterday", usersYesterday);

        System.out.println(response);

        return ResponseEntity.ok(response);
    }

    private double calculatePercentageIncrease(long newCount, double monthlyAvg) {
        if (monthlyAvg == 0) {
            return 0;
        }
        return ((double) newCount / monthlyAvg) * 100;
    }
}