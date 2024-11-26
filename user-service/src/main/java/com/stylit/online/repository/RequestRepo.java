package com.stylit.online.repository;

import com.stylit.online.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepo extends JpaRepository<Request, Long> {
    List<Request> findBySenderId(Long id);
    List<Request> findByReceiverId(Long id);
}

