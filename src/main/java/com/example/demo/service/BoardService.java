package com.example.demo.service;

import com.example.demo.dao.BoardRepository;
import com.example.demo.domain.Board;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BoardService {

    public List<Board> findAllBoards();

    public Board findBoardById(int id);

    public Board findBoardByIdWithViewCountUpdate(int id, HttpServletRequest request);

    public Board saveBoard(Board board);

    public void deleteBoard(int id);

}
