package com.tictactoe.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class LoginRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime loginTime;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    public LoginRecord() {}

    public LoginRecord(Player player, LocalDateTime loginTime) {
        this.player = player;
        this.loginTime = loginTime;
    }

    // геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getLoginTime() { return loginTime; }
    public void setLoginTime(LocalDateTime loginTime) { this.loginTime = loginTime; }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
}
