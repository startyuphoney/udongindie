package com.udongindie.udong.controller;

import com.udongindie.udong.entity.*;
import com.udongindie.udong.enums.OrderStatus;
import com.udongindie.udong.repository.*;
import com.udongindie.udong.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BoardRepository boardRepository;
    private final BoardService boardService;
    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final AnswerRepository answerRepository;
    private final HistoryService historyService;

    @GetMapping("")
    public String admin(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        log.info("admin page - member_list");

        Page<Member> paging = this.memberService.getList(page);

        List<Member> members = memberRepository.findAll();
        model.addAttribute("paging", paging);
        model.addAttribute("memberCount", paging.getTotalElements());

        return "admin_memberList";
    }

    @RequestMapping("/customer_manage")
    public String customer() {
        log.info("customer_manage");
        return "redirect:/";
    }

    @RequestMapping("/item_manage")
    public String item(Model model) {
        log.info("item_manage");
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "itemManage2";
    }

    @GetMapping("/item_manage/modify")
    public String itemModify(Model model, Long idx) {
        log.info("item_manage_modify");
        Optional<Item> item = itemRepository.findById(idx);
        model.addAttribute("item", item.get());

        return "newItemForm";
    }

    @PostMapping("/item_manage/modify")
    public String itemModify(Long idx, Item item) {
        log.info("item_manage_modify_post");
        itemService.modify(idx, item);

        return "redirect:/admin/item_manage";
    }

    @GetMapping("/item_manage/add")
    public String AddItem(Model model) {
        log.info("AddItem_form");
        model.addAttribute("item", new Item());
        return "newItemForm";
    }

    @PostMapping("/item_manage/add")
    public String AddItem(@RequestParam(value = "beginTime") String beginTime,
                          Item item,
                          MultipartFile imgFile,
                          @RequestParam(value = "imgFile2") MultipartFile imgFile2) throws Exception {
        log.info("AddItem_post");
        item.setBeginDate(item.getBeginDate() + " " + beginTime);
        itemService.saveItem(item, imgFile, imgFile2);
        return "redirect:/admin/item_manage";
    }

    @RequestMapping("/order_manage")
    public String orderList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        log.info("order_manage_list");
        Page<Orders> orders = orderService.getList(page);

        model.addAttribute("paging", orders);

        return "admin_orderList";
    }

    @RequestMapping("/order_manage/option")
    public String orderListStatus(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(defaultValue = "ALL") String status) {
        log.info("order_manage_search_by_status");

        if(status.equals("ALL")){
            Page<Orders> orders = orderService.getList(page);
            model.addAttribute("paging", orders);

            return "admin_orderList";
        }
        Page<Orders> orders = orderService.getListByStatus(page, OrderStatus.valueOf(status));

        model.addAttribute("status", status);
        model.addAttribute("paging", orders);

        return "admin_orderList";
    }

    @GetMapping("/board_manage")
    public String board(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
        log.info("board_manage_list");

        Page<Board> boards = boardService.getList(page);
        model.addAttribute("paging", boards);

        return "admin_board";
    }

    @GetMapping("/board_manage/option")
    public String boardOption(Model model, @RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(defaultValue = "ALL") String status) {
        log.info("board_manage_list_by_status");

        if(status.equals("ALL")){
            Page<Board> boards = boardService.getList(page);
            model.addAttribute("paging", boards);

            return "admin_board";
        }

        Page<Board> boards = boardService.getListByStatus(page, status);
        model.addAttribute("paging", boards);
        model.addAttribute("status", status);

        return "admin_board";
    }

    @RequestMapping("/board_manage/detail")
    public String boardDetail(Model model, @RequestParam(value = "idx") Long idx) {
        log.info("board_manage_detail");

        Optional<Board> board = boardRepository.findById(idx);
        model.addAttribute("board", board.get());

        return "admin_boardDetail";
    }

    @PostMapping("/board_manage/answer/{idx}")
    public String boardAnswer(HttpSession session, Model model, @PathVariable(value = "idx") Long idx, @RequestParam(value = "answerContent") String answerContent) {
        log.info("board_manage_boardAnswer");

        Long result = boardService.createAnswer(session, model, idx, answerContent);

        return "redirect:/admin/board_manage/detail?idx=" + result;
    }

    @GetMapping("/history/add")
    public String historyForm(Model model) {
        log.info("history_form");

        model.addAttribute("history", new History());

        return "historyForm";
    }

    @PostMapping("/history/add")
    public String saveHistory(History history, MultipartFile imgFile) {
        log.info("save_history");

        historyService.saveHistory(history, imgFile);

        return "redirect:/admin/history/add";
    }

}
