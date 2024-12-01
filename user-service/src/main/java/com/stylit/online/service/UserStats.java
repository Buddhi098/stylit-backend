package com.stylit.online.service;

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


    public ResponseEntity getUserStatistics() {
        long totalUsers = userRepo.countTotalUsers();
        LocalDateTime endDate = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime startDate = LocalDate.now().minusDays(30).atStartOfDay();
        long usersLast30Days = userRepo.countUsersCreatedBetween(startDate, endDate);
        double monthlyAvg = userRepo.monthlyAvgUsersCreated();
        double percentageIncrease = calculatePercentageIncrease(usersLast30Days, monthlyAvg);

        Map<String, Object> response = new HashMap<>();
        response.put("totalUsers", totalUsers);
        response.put("usersLast30Days", usersLast30Days);
        response.put("percentageIncrease", percentageIncrease);

        System.out.println(response);

        return ResponseEntity.ok(response);
    }

    private double calculatePercentageIncrease(long newUsers, double monthlyAvg) {
        if (monthlyAvg == 0) {
            return 0;
        }
        return ((double) newUsers / monthlyAvg) * 100;
    }
}