package com.example.demo.controller.board;

import com.example.demo.domain.board.Board;
import com.example.demo.service.board.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    //모든 게시글 조회
    /*@GetMapping
    public List<Board> getAllBoards() {
        return boardService.findAllBoards();
    }*/

    //게시글 ID로 조회
    @GetMapping("/{id}")
    public ResponseEntity<Board> getBoardById(@PathVariable int id) {
        Board board = boardService.findBoardById(id);

        if (board == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(board);
    }

    //새 게시글 추가
    @PostMapping
    public Board createBoard(@RequestBody Board board) {
        return boardService.saveBoard(board);
    }

    //게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable int id, @RequestBody Board updateBoard) {
        Board board = boardService.findBoardById(id);
        if (board == null) {
            return ResponseEntity.notFound().build();
        }

        board.setTitle(updateBoard.getTitle());
        board.setContent(updateBoard.getContent());
        board.setWriter(updateBoard.getWriter());
        boardService.saveBoard(board);
        return ResponseEntity.ok(board);
    }

    //게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable int id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
