package com.tictactoe.model;

import java.time.LocalDate;

public class LoginCountByDateDto {
    private LocalDate date;
    private int uniqueUserCount;

    public LoginCountByDateDto(LocalDate date, int uniqueUserCount) {
        this.date = date;
        this.uniqueUserCount = uniqueUserCount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getUniqueUserCount() {
        return uniqueUserCount;
    }

    public void setUniqueUserCount(int uniqueUserCount) {
        this.uniqueUserCount = uniqueUserCount;
    }
}