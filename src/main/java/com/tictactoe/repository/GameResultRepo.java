package com.tictactoe.repository;

import com.tictactoe.model.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameResultRepo extends JpaRepository<GameResult, Long> {
    @Query("SELECT gr FROM GameResult gr WHERE gr.player1 = :username OR gr.player2 = :username")
    List<GameResult> findAllByPlayer(@Param("username") String username);
    @Query("SELECT DISTINCT gr.player1 FROM GameResult gr UNION SELECT DISTINCT gr.player2 FROM GameResult gr")
    List<String> findAllDistinctPlayers();

}