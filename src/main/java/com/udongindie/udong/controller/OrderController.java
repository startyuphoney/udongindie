package com.udongindie.udong.controller;

import com.udongindie.udong.repository.ItemRepository;
import com.udongindie.udong.repository.OrderRepository;
import com.udongindie.udong.service.ItemService;
import com.udongindie.udong.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@Getter
@Setter
@RequiredArgsConstructor
@RequestMapping("/order")
@Slf4j
public class OrderController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping("/orderForm")
    public String orderForm(Model model){
        log.info("now_ticket");
        model.addAttribute("item", itemService.latestItem());

        return "orderForm";
    }

    @GetMapping("/createOrder")
    public String createOrder(HttpSession session, @RequestParam(value = "itemIdx") Long itemIdx){
        log.info("/readyKakaoPay");
        if(session.getAttribute("loginMember") == null){
            log.info("redirect:/login");
            return "redirect:/login";
        }
        String approvalUrl = orderService.readyKakaoPay(session, itemIdx);

        return "redirect:" + approvalUrl;
    }

    @GetMapping("/success")
    public String orderSuccess(HttpSession session, String pg_token){
        log.info("pg_token get success");

        orderService.approveRequest(session, pg_token);

        return "redirect:/member/mypage";
    }

    @GetMapping("/cancel")
    public String orderCancel(){
        log.info("order cancel");

        return "redirect:/order/orderForm";
    }

    @PostMapping("/refund")
    @ResponseBody
    public String orderRefund(Long orderIdx) {
        log.info("order refund");

        String result = orderService.refund(orderIdx);
        if(result.equals("fail")){
            return "카카오페이 연동 이전 주문은 취소가 불가능합니다.";
        }

        return "success";
    }

    @GetMapping("/fail")
    public void orderFail(){
        log.info("order fail");
    }


}
