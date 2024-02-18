package com.udongindie.udong.service;

import com.udongindie.udong.entity.Answer;
import com.udongindie.udong.entity.Board;
import com.udongindie.udong.entity.Member;
import com.udongindie.udong.repository.AnswerRepository;
import com.udongindie.udong.repository.BoardRepository;
import com.udongindie.udong.repository.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final AnswerRepository answerRepository;

    /**
     * 1:1 문의 저장
     * @param member
     * @param board
     */
    public void createBoard(Member member, Board board){
        board.setMember(member);
        board.setCreateDate(LocalDateTime.now());
        board.setStatus("답변 대기");

        boardRepository.save(board);
    }

    /**
     * 1:1 문의 답변
     * @param session
     * @param model
     * @param idx
     * @param answerContent
     * @return
     */
    public Long createAnswer(HttpSession session, Model model,Long idx, String answerContent){
        Optional<Board> board = boardRepository.findById(idx);
        Member loginMember = (Member) session.getAttribute("loginMember");
        Answer answer = new Answer();
        answer.setBoard(board.get());
        answer.setMember(loginMember);
        answer.setCreateDate(LocalDateTime.now());
        answer.setContent(answerContent);

        board.get().setStatus("답변 완료");

        answerRepository.save(answer);

        return board.get().getIdx();
    }

    /**
     * 1:1 문의 전체 조회
     * @param page
     * @return
     */
    public Page<Board> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return this.boardRepository.findAll(pageable);
    }

    /**
     * 1:1 문의 상태에 따른 조회
     * @param page
     * @param status
     * @return
     */
    public Page<Board> getListByStatus(int page, String status) {
        Pageable pageable = PageRequest.of(page, 10);

        String val = "";
        if(status.equals("FINISH")) val = "답변 완료";
        if(status.equals("WAIT")) val = "답변 대기";
        if(val.isEmpty()) return this.boardRepository.findAll(pageable);

        return this.boardRepository.findByStatus(pageable, val);

    }

}
