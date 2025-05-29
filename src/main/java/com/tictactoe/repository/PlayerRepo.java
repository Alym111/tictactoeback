package com.tictactoe.repository;

import com.tictactoe.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepo extends JpaRepository<Player, Long> {
    Optional<Player> findByUsername(String username);
}
