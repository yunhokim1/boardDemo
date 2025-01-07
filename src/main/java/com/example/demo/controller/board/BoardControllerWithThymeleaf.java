package com.example.demo.controller.board;

import com.example.demo.domain.board.Board;
import com.example.demo.domain.user.User;
import com.example.demo.service.board.BoardService;
import com.example.demo.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardControllerWithThymeleaf {
    private final BoardService boardService;
    private final UserService userService;

    //게시판 목록 보기
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       Model model) {
        LoggedInUserInfo(model);

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "regDate"));
        Page<Board> boardsPage = boardService.findAllBoards(pageable);

        model.addAttribute("boards", boardsPage.getContent());
        model.addAttribute("currentPage", boardsPage.getNumber());
        model.addAttribute("totalPages", boardsPage.getTotalPages());
        model.addAttribute("totalItems", boardsPage.getTotalElements());
        return "board/list";
    }

    //게시글 작성 페이지 이동
    @GetMapping("/regist")
    public String createForm(Model model) {
        LoggedInUserInfo(model);
        model.addAttribute("board", new Board());
        return "board/write";
    }

    //게시글 저장
    @PostMapping
    public String save(@ModelAttribute Board board) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User loggedInUser = userService.findByUserId(userId);

        if (loggedInUser != null) {
            board.setRegId(userId);
            board.setNickname(loggedInUser.getNickname());
        }

        boardService.saveBoard(board);
        return "redirect:/boards";
    }

    //게시글 상세보기
    @GetMapping("/{id}")
    public String detail(@PathVariable int id, Model model, HttpServletRequest request) {
        LoggedInUserInfo(model);
        Board board = boardService.findBoardByIdWithViewCountUpdate(id, request);

        model.addAttribute("board", board);
        return "board/detail";
    }

    //게시글 수정 화면
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable int id, Model model) {
        LoggedInUserInfo(model);
        Board board = boardService.findBoardById(id);

        model.addAttribute("board", board);
        return "board/update";
    }

    @PutMapping("/{id}")
    public ResponseEntity<Board> update(@PathVariable int id, @RequestBody Board board) {
        Board findBoard = boardService.findBoardById(id);

        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User loggedInUser = userService.findByUserId(userId);

        if (loggedInUser != null) {
            findBoard.setRegId(userId);
            findBoard.setNickname(loggedInUser.getNickname());
        }
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

    private void LoggedInUserInfo(Model model) {
        // 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        User loggedInUser = userService.findByUserId(userId);

        // 로그인된 사용자가 있으면 그 정보를 model에 추가
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }
    }
}
