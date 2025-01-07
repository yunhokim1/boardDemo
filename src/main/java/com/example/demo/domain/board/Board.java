package com.example.demo.domain.board;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String content;

    @Column(nullable = false, length = 10)
    private String regId;

    @Column(nullable = false, length = 10)
    private String nickname;

    @Column(columnDefinition = "integer default 0")
    private int viewCount;

    @Column(columnDefinition = "integer default 0")
    private int boardLike;

    @Column(columnDefinition = "integer default 0")
    private int comment;

    @Column
    private String regDate;

    @PrePersist
    public void prePersist() {
        if (this.regDate == null) {
            // 현재 날짜와 시간을 "yyyy-MM-dd HH:mm:ss" 형식으로 변환
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            this.regDate = LocalDateTime.now().format(formatter);
        }
    }
}
