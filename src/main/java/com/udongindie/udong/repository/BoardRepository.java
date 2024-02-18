package com.udongindie.udong.repository;

import com.udongindie.udong.entity.Board;
import com.udongindie.udong.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findByMember(Member member);

    Page<Board> findAll(Pageable pageable);
    Page<Board> findByStatus(Pageable pageable,String status);
}
