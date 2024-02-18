package com.udongindie.udong.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udongindie.udong.dto.MemberDTO;
import com.udongindie.udong.entity.*;
import com.udongindie.udong.enums.RoleType;
import com.udongindie.udong.kakao.KakaoProfile;
import com.udongindie.udong.kakao.OAuthToken;
import com.udongindie.udong.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    @Value("${cos.key}")
    private String cosKey;

    private final MemberRepository memberRepository;
//    private final MemberService memberService;

    @Transactional(readOnly = true)
    public Optional<Member> findMember(String username){

        return memberRepository.findByUsername(username);
    }

    /**
     * 회원가입
     * @param memberDTO
     * @return
     */
    public String join(MemberDTO memberDTO) {

        Optional<Member> findMember = memberRepository.findByUsername(memberDTO.getEmail());
        if(findMember.isEmpty()) {
            Member member = new Member();
            member.setUsername(memberDTO.getEmail());
            member.setName(memberDTO.getUsername());
            if(!memberDTO.getPwd().equals(memberDTO.getPwd2())){
                return "비밀번호를 확인하세요";
            }
            member.setPwd(memberDTO.getPwd());
            member.setJoinDate(LocalDateTime.now());
            member.setRole(RoleType.USER);

            memberRepository.save(member);

            return "회원가입이 완료되었습니다";
        }
        return "이미 존재하는 회원입니다";

    }

    /**
     * 카카오 정보로 자동 회원가입
     * @param kakaoProfile
     * @return
     */
    @Transactional
    public Member kakaoJoin(KakaoProfile kakaoProfile) {

        Member kakaoMember = new Member();
        kakaoMember.setName(kakaoProfile.getProperties().getNickname());
        kakaoMember.setUsername(kakaoProfile.getKakao_account().getEmail());
        kakaoMember.setPwd(cosKey);
        kakaoMember.setJoinDate(LocalDateTime.now());

        memberRepository.save(kakaoMember);

        return kakaoMember;
    }

    /**
     * 카카오 회원정보 요청 및 자동회원가입
     * @param code
     * @param request
     */
    public void kakaoLogin(String code, HttpServletRequest request) {

        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "");
        params.add("redirect_uri", "");
        params.add("code", code);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        // 이렇게 하면 header, body 데이터를 가지고 있는 Entity가 된다.
        // rt.exchange가 파라미터로 HttpEntity를 필요로 하기때문
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - response 변수에 응답결과 받는다.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper obMapper = new ObjectMapper();
        OAuthToken oauthToken = null;

        try {
            oauthToken = obMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        System.out.println(oauthToken.getAccess_token());

        // 사용자 정보 요청하기
        RestTemplate rt2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "");
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담는다
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 =
                new HttpEntity<>(headers2);

        // Http 요청하기 - response 변수에 응답결과 받는다.
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );

        ObjectMapper obMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = obMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Member member = null;

        // 가입자 혹은 비가입자 체크 해서 처리
        Optional<Member> originMember = findMember(kakaoProfile.getKakao_account().getEmail());
        if(originMember.isEmpty()){
            member = kakaoJoin(kakaoProfile);
        } else {
            member = originMember.get();
        }

        log.info("session set user");
        HttpSession session = request.getSession();
        session.setAttribute("loginMember", member);
        log.info("session login success! username >>> " + member.getName());
    }


    /**
     * 기본 로그인
     * @param request
     * @param id
     * @param pwd
     */
    public String login(HttpServletRequest request, @RequestParam(value = "id") String id, @RequestParam(value = "pwd") String pwd) {
        Optional<Member> member = memberRepository.findByUsername(id);

        if (member.isPresent()){
            if(member.get().getPwd().equals(pwd)){
                log.info("session set user");
                HttpSession session = request.getSession();
                session.setAttribute("loginMember", member.get());
                log.info("session login success! username >>> " + member.get().getName());

                return "success";
            }
        }

        log.info("login fail");
        return "fail";

    }

    public Page<Member> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.memberRepository.findAll(pageable);
    }

}
