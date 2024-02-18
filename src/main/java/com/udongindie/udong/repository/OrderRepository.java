package com.udongindie.udong.repository;

import com.udongindie.udong.entity.Member;
import com.udongindie.udong.enums.OrderStatus;
import com.udongindie.udong.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

    List<Orders> findByMember(Member member);

    Page<Orders> findAll(Pageable pageable);
    Page<Orders> findByOrderStatus(Pageable pageable, OrderStatus orderStatus);

}
