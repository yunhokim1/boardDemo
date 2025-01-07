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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        // 현재 로그인된 사용자 정보 가져오기
        User loggedInUser = userService.loggedInUserInfo();

        // 로그인된 사용자가 있으면 그 정보를 model에 추가
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
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

        // 현재 로그인된 사용자 정보 가져오기
        User loggedInUser = userService.loggedInUserInfo();

        // 로그인된 사용자가 있으면 그 정보를 model에 추가
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }

        model.addAttribute("board", new Board());
        return "board/write";
    }

    //게시글 저장
    @PostMapping
    public String save(@ModelAttribute Board board) {

        // 현재 로그인된 사용자 정보 가져오기
        User loggedInUser = userService.loggedInUserInfo();

        if (loggedInUser != null) {
            board.setRegId(loggedInUser.getUserId());
            board.setNickname(loggedInUser.getNickname());
        }

        boardService.saveBoard(board);
        return "redirect:/boards";
    }

    //게시글 상세보기
    @GetMapping("/{id}")
    public String detail(@PathVariable int id,
                         @RequestParam(defaultValue = "0") int page,
                         Model model, HttpServletRequest request) {

        // 현재 로그인된 사용자 정보 가져오기
        User loggedInUser = userService.loggedInUserInfo();

        // 로그인된 사용자가 있으면 그 정보를 model에 추가
        if (loggedInUser != null) {
            model.addAttribute("loggedInUser", loggedInUser);
        }

        Board board = boardService.findBoardByIdWithViewCountUpdate(id, request);

        model.addAttribute("board", board);
        model.addAttribute("currentPage", page);
        return "board/detail";
    }

    //게시글 수정 화면
    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable int id,
                             @RequestParam(defaultValue = "0") int page,
                             Model model) {
        Board board = boardService.findBoardById(id);
        User loggedInUser = userService.loggedInUserInfo();

        // 로그인된 사용자가 있으면 그 정보를 model에 추가
        if (loggedInUser != null) {

            model.addAttribute("loggedInUser", loggedInUser);


            // 로그인된 사용자와 게시글 작성자 검증
            if (!(loggedInUser.getRole().equals("ADMIN") || loggedInUser.getUserId().equals(board.getRegId()))) {
                return "error/authorization"; // 권한 없음 페이지로 리다이렉트
            }

            model.addAttribute("board", board);
            model.addAttribute("currentPage", page);
            return "board/update";

        } else {
            return "login";
        }

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody Board board) {
        Board findBoard = boardService.findBoardById(id);
        User loggedInUser = userService.loggedInUserInfo();

        if (loggedInUser == null || !(loggedInUser.getRole().equals("ADMIN") || loggedInUser.getUserId().equals(findBoard.getRegId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수정 권한이 없습니다.");
        }

        findBoard.setTitle(board.getTitle());
        findBoard.setContent(board.getContent());
        if(loggedInUser.getRole().equals("ADMIN")){
            findBoard.setRegId(findBoard.getRegId());
            findBoard.setNickname(findBoard.getNickname());
        } else {
            findBoard.setRegId(loggedInUser.getUserId());
            findBoard.setNickname(loggedInUser.getNickname());
        }

        boardService.saveBoard(findBoard);

        return ResponseEntity.ok(findBoard);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        Board board = boardService.findBoardById(id);
        User loggedInUser = userService.loggedInUserInfo();

        if (board == null) {
            return ResponseEntity.notFound().build();
        }

        if (loggedInUser == null || !(loggedInUser.getRole().equals("ADMIN") || loggedInUser.getUserId().equals(board.getRegId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
