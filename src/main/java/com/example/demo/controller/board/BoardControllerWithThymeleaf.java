package com.example.demo.controller.board;

import com.example.demo.domain.board.Board;
import com.example.demo.service.board.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/boards")
public class BoardControllerWithThymeleaf {
    private final BoardService boardService;

    public BoardControllerWithThymeleaf(BoardService boardService) {
        this.boardService = boardService;
    }

    //게시판 목록 보기
    @GetMapping
    public String list(Model model) {
        model.addAttribute("boards", boardService.findAllBoards());
        return "board/list";
    }

    //게시글 작성 페이지 이동
    @GetMapping("/regist")
    public String createForm(Model model) {
        model.addAttribute("board", new Board());
        return "board/regist";
    }

    //게시글 저장
    @PostMapping
    public String save(@ModelAttribute Board board) {
        boardService.saveBoard(board);
        return "redirect:/boards";
    }

    //게시글 상세보기
    @GetMapping("/{id}")
    public String detail(@PathVariable int id, Model model, HttpServletRequest request) {
        Board board = boardService.findBoardByIdWithViewCountUpdate(id, request);

        model.addAttribute("board", board);
        return "board/detail";
    }

    //게시글 수정 화면
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable int id, Model model) {
        Board board = boardService.findBoardById(id);

        model.addAttribute("board", board);
        return "board/update";
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable int id, @RequestBody Board board) {
        Board findBoard = boardService.findBoardById(id);

        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());
        findBoard.setWriter(board.getWriter());
        boardService.saveBoard(findBoard);

        return ResponseEntity.ok(findBoard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        Board board = boardService.findBoardById(id);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }

        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
