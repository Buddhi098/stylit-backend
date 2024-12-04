package com.stylit.online.repository;

import com.stylit.online.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepo extends JpaRepository<Request, Long> {
    List<Request> findBySenderId(Long id);
    List<Request> findByReceiverId(Long id);

    @Query("SELECT r FROM Request r WHERE (r.senderId = :shopId OR r.receiverId = :shopId) AND (r.status IN ('PENDING' , 'ACCEPTED'))")
    List<Request> findAllNotConnectableRequest(Long shopId);

    Optional<Request> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    List<Request> findByReceiverIdAndStatus(Long receiverId, Request.RequestStatus status);

    Optional<Request> findBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, Request.RequestStatus status);
}

