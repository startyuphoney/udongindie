package com.udongindie.udong.controller;

import com.udongindie.udong.entity.Board;
import com.udongindie.udong.entity.Member;
import com.udongindie.udong.entity.Orders;
import com.udongindie.udong.repository.BoardRepository;
import com.udongindie.udong.repository.ItemRepository;
import com.udongindie.udong.repository.MemberRepository;
import com.udongindie.udong.repository.OrderRepository;
import com.udongindie.udong.service.BoardService;
import com.udongindie.udong.service.ItemService;
import com.udongindie.udong.service.MemberService;
import com.udongindie.udong.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@Getter
@Setter
@RequiredArgsConstructor
@RequestMapping("/member")
@Slf4j
public class MemberController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @GetMapping("/test")
    public String loginTest(HttpServletRequest request){
        log.info("kakaoLoginTest");
        HttpSession session = request.getSession(false);
        Member loginMember = (Member) session.getAttribute("loginMember");
        return "loginTest";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        log.info("LOGOUT");
        //세션을 삭제
        HttpSession session = request.getSession(false);
        // session이 null이 아니라는건 기존에 세션이 존재했었다는 뜻이므로
        // 세션이 null이 아니라면 session.invalidate()로 세션 삭제해주기.
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }

    @GetMapping("/mypage")
    public String myPage(HttpSession session, Model model) {
        log.info("myPage");

        Member member = (Member) session.getAttribute("loginMember");

//        List<Orders> orders = orderRepository.findByMember(member);
//        List<Board> boards = boardRepository.findByMember(member);
        List<Orders> orders = orderRepository.findAll();
        List<Board> boards = boardRepository.findAll();

        model.addAttribute("orders", orders);
        model.addAttribute("boards", boards);


        return "myPage";
    }

    @GetMapping("/board")
    public String boardForm() {
        log.info("1:1 boardForm");

        return "boardForm";
    }

    @PostMapping("/board")
    public String createBoard(HttpServletRequest request, Board board) {
        log.info("1:1 board created");
        Member member = (Member) request.getSession().getAttribute("loginMember");
        boardService.createBoard(member, board);

        return "redirect:/member/mypage";
    }

    @GetMapping("/board/detail")
    public String boardDetail(@RequestParam(value = "idx") Long idx, Model model) {
        log.info("1:1 board detail");
        Optional<Board> board = boardRepository.findById(idx);
        model.addAttribute("board", board.get());

        return "boardDetail";
    }

}
