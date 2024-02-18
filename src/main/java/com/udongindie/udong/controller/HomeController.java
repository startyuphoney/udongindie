package com.udongindie.udong.controller;

import com.udongindie.udong.entity.History;
import com.udongindie.udong.dto.MemberDTO;
import com.udongindie.udong.repository.ItemRepository;
import com.udongindie.udong.repository.MemberRepository;
import com.udongindie.udong.service.HistoryService;
import com.udongindie.udong.service.ItemService;
import com.udongindie.udong.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final HistoryService historyService;

    @RequestMapping("/")
    public String home(Model model) {
        log.info("home controller");
        model.addAttribute("item", itemService.latestItem());

        return "home";
    }

    @RequestMapping("/about")
    public String about() {
        log.info("about");
        return "about";
    }

    @RequestMapping("/history")
    public String history(Model model) {
        log.info("history");
        List<History> list = historyService.getList();

        model.addAttribute("histories", list);

        return "history";
    }

    @GetMapping("/login")
    public String login() {
        log.info("login page");
        return "inpa_login_form";
    }

    @PostMapping("/login")
    public @ResponseBody String login(HttpServletRequest request, String id, String pwd) {
        log.info("login service");

        String result;
        result = memberService.login(request, id, pwd);

        System.out.println(result);
        return result;

    }

    @GetMapping("/login/callback")
    @Transactional
    public String kakaoCallback(String code, HttpServletRequest request) {
        log.info("login callback page");

        memberService.kakaoLogin(code, request);

        return "redirect:/";
    }

    @PostMapping("/join")
    @ResponseBody
    public String join(MemberDTO memberDTO) {
        log.info("member_join");
        String result = memberService.join(memberDTO);

        return result;
    }
}

