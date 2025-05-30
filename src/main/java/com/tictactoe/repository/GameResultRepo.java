package com.tictactoe.repository;

import com.tictactoe.model.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultRepo extends JpaRepository<GameResult, Long> {}