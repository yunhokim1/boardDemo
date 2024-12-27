package com.example.demo.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Users")
public class User {

    @Id
    @Column(length = 15)
    private String userId;

    @Column(nullable = false)
    private String password;

    @Column(length = 15, nullable = false)
    private String nickname;

    @Column(length = 5, nullable = false)
    private String name;

    @Column(length = 30, nullable = false)
    private String email;

    @Column(length = 13, nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private boolean enabled = true;
}
