package com.udongindie.udong.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.udongindie.udong.entity.*;
import com.udongindie.udong.enums.OrderStatus;
import com.udongindie.udong.kakao.KakaoPayApproveResponse;
import com.udongindie.udong.kakao.KakaoPayReadyResponse;
import com.udongindie.udong.repository.ItemRepository;
import com.udongindie.udong.repository.MemberRepository;
import com.udongindie.udong.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final OrderRepository orderRepository;

    /**
     * 주문 생성
     * @param payApproveResponse
     */
    public void createOrder(KakaoPayApproveResponse payApproveResponse) {
        log.info("create order");

        Orders order = new Orders();
        Item item = null;
        Member member = null;

        item = itemRepository.findByName(payApproveResponse.getItem_name()).get();
        member = memberService.findMember(payApproveResponse.getPartner_user_id()).get();

        order.setItem(item);
        item.setStockQuantity(item.getStockQuantity()- payApproveResponse.getQuantity());
        order.setOrderDate(LocalDateTime.now());
        order.setCount(payApproveResponse.getQuantity());
        order.setMember(member);
        order.setOrderStatus(OrderStatus.ORDER);
        order.setTotalPrice(payApproveResponse.getQuantity().longValue() * item.getPrice());
        order.setCount(payApproveResponse.getQuantity());
        order.setTid(payApproveResponse.getTid());

        orderRepository.save(order);
    }


    /**
     * 카카오페이 결제 준비
     * @param session
     * @param itemIdx
     * @return
     */
    public String readyKakaoPay(HttpSession session, Long itemIdx) {

        String approval_url = "";
        String cancel_url = "";
        String fail_url = "";
        if(session.getAttribute("loginMember") == null){
            return "redirect:/login";
        }

        Member member = (Member) session.getAttribute("loginMember");
        Optional<Item> item = itemRepository.findById(itemIdx);

        RestTemplate rt = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", "1001");
        params.add("partner_user_id", member.getUsername());
        if(item.isPresent()){
            params.add("item_name", item.get().getName());
        }
        params.add("quantity", "1");
        params.add("total_amount", Integer.toString(item.get().getPrice()));
        params.add("tax_free_amount", "0");
        params.add("approval_url", approval_url);
        params.add("cancel_url", cancel_url);
        params.add("fail_url", fail_url);

        // HttpHeader와 HttpBody를 하나의 오브젝트에 담는다
        HttpEntity<MultiValueMap<String, String>> TIDRequest =
                new HttpEntity<>(params, headers);

        // Http 요청하기 - response 변수에 응답결과 받는다.
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/payment/ready",
                HttpMethod.POST,
                TIDRequest,
                String.class
        );

        ObjectMapper om = new ObjectMapper();
        KakaoPayReadyResponse payReadyResponse = null;
        try {
            payReadyResponse = om.readValue(response.getBody(), KakaoPayReadyResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        session.setAttribute("tid", payReadyResponse.getTid());

        return payReadyResponse.getNext_redirect_pc_url();
    }


    /**
     * 카카오페이 결제 요청
     * @param session
     * @param pg_token
     */
    public void approveRequest(HttpSession session, String pg_token) {

        log.info("approve request");
        Member member = (Member) session.getAttribute("loginMember");

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", (String) session.getAttribute("tid"));
        params.add("partner_order_id", "1001");
        params.add("partner_user_id", member.getUsername());
        params.add("pg_token", pg_token);

        HttpEntity<MultiValueMap<String, String>> payApproveRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/payment/approve",
                HttpMethod.POST,
                payApproveRequest,
                String.class
        );

        ObjectMapper om = new ObjectMapper();
        KakaoPayApproveResponse payApproveResponse = null;
        try {
            payApproveResponse = om.readValue(response.getBody(), KakaoPayApproveResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        createOrder(payApproveResponse);

    }

    /**
     * 고객 주문 목록 출력
     * @param page
     * @return
     */
    public Page<Orders> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10);

        return this.orderRepository.findAll(pageable);
    }

    public Page<Orders> getListByStatus(int page, OrderStatus status) {
        Pageable pageable = PageRequest.of(page, 10);

        return this.orderRepository.findByOrderStatus(pageable, status);
    }

    /**
     * 카카오페이 환불요청
     * @param orderIdx
     */
    @Transactional
    public String refund(Long orderIdx) {
        log.info("order refund request");

        if(orderIdx <= 9752){
            return "fail";
        }
        Optional<Orders> orders = orderRepository.findById(orderIdx);
        Orders order = orders.get();
        String tid = orders.get().getTid();
        Long amount = orders.get().getTotalPrice();

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "");
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("cid", "TC0ONETIME");
        body.add("tid", tid);
        body.add("cancel_amount", Long.toString(amount));
        body.add("cancel_tax_free_amount", "0");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v1/payment/cancel"
                , HttpMethod.POST
                , httpEntity
                , String.class
        );

        order.setOrderStatus(OrderStatus.CANCEL);
        order.getItem().setStockQuantity(order.getItem().getStockQuantity() + order.getCount());
        log.info("order refund success");

        return "success";
    }
}
