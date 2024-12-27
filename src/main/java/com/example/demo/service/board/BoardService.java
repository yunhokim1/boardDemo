package com.example.demo.service.board;

import com.example.demo.domain.board.Board;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface BoardService {

    public List<Board> findAllBoards();

    public Board findBoardById(int id);

    public Board findBoardByIdWithViewCountUpdate(int id, HttpServletRequest request);

    public Board saveBoard(Board board);

    public void deleteBoard(int id);

}
