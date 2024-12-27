package com.example.demo.service.board.impl;

import com.example.demo.dao.board.BoardRepository;
import com.example.demo.domain.board.Board;
import com.example.demo.service.board.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public List<Board> findAllBoards() {
        return boardRepository.findAll();
    }

    public Board findBoardById(int id) {
        Board board = boardRepository.findById(id).orElse(null);
        return board;
    }

    @Override
    public Board findBoardByIdWithViewCountUpdate(int id, HttpServletRequest request) {
        // Redis Key 생성
        String clientIdentifier = getClientIdentifier(request);
        String redisKey = "view:" + id + ":" + clientIdentifier;

        Boolean isViewed = null;

        try {
            // Redis에서 조회 여부 확인
            isViewed = redisTemplate.hasKey(redisKey);
        } catch (Exception e){
            log.info("Redis Key 조회 실패");
            isViewed = null;
        }

        if (isViewed == null || !isViewed) {
            incrementViewCount(id);

            try {
                // Redis에 키 저장 (1시간 TTL)
                redisTemplate.opsForValue().set(redisKey, "true", 1, TimeUnit.HOURS);
            } catch (Exception e) {
                log.info("Redis Key 저장 실패");
            }
        }

        return boardRepository.findById(id).orElse(null);
    }

    public Board saveBoard(Board board) {
        return boardRepository.save(board);
    }

    public void deleteBoard(int id) {
        boardRepository.deleteById(id);
    }

    private void incrementViewCount(int id) {
        Board board = boardRepository.findById(id).orElse(null);

        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
    }

    private String getClientIdentifier(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        return ip + ":" + userAgent;
    }
}
