package com.tictactoe.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password; // Хранится в хешированном виде
    private String token;
    private boolean online;

    // Конструкторы
    public Player() {}

    public Player(String username, String password) {
        this.username = username;
        this.password = password; // Фронтенд НЕ должен хешировать
    }

    // Геттеры/сеттеры
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getToken() { return token; }
    public boolean isOnline() { return online; }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }
}
