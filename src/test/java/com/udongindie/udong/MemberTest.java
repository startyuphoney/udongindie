package com.udongindie.udong;

import com.udongindie.udong.entity.Member;
import com.udongindie.udong.enums.RoleType;
import com.udongindie.udong.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

import java.time.LocalDateTime;

@SpringBootTest
public class MemberTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Commit
    void testJpa() {
        for (int i = 1; i < 100; i ++){
            Member member = new Member();
            member.setName("연제구" + i);
            member.setUsername("연제구@" + i);
            member.setJoinDate(LocalDateTime.now());
            member.setRole(RoleType.USER);
            member.setPwd("1234");
            memberRepository.save(member);
        }
    }


}
