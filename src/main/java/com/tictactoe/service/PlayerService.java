package com.tictactoe.service;

import com.tictactoe.model.LoginCountByDateDto;
import com.tictactoe.model.LoginRecord;
import com.tictactoe.model.Player;
import com.tictactoe.repository.LoginRecordRepo;
import com.tictactoe.repository.PlayerRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepo repository;
    private final LoginRecordRepo loginRecordRepo;

    public PlayerService(PlayerRepo repository, LoginRecordRepo loginRecordRepo) {
        this.repository = repository;
        this.loginRecordRepo = loginRecordRepo;
    }

    public List<LoginCountByDateDto> getLoginCountsByDate() {
        List<LoginRecord> records = loginRecordRepo.findAll();

        Map<LocalDate, Long> counts = records.stream()
                .collect(Collectors.groupingBy(
                        lr -> lr.getLoginTime().toLocalDate(),
                        Collectors.mapping(lr -> lr.getPlayer().getId(), Collectors.toSet())
                ))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> (long) e.getValue().size()
                ));

        return counts.entrySet().stream()
                .map(e -> new LoginCountByDateDto(e.getKey(), e.getValue().intValue()))
                .toList();
    }



    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public Optional<Player> getPlayerById(Long id) {
        return repository.findById(id);
    }

    public boolean deletePlayerById(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public Boolean registerPlayer(Player player) {
        try {
            repository.save(player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean login(Player player) {
        Optional<Player> userOptional = repository.findByUsername(player.getUsername());
        if (userOptional.isEmpty()) {
            return false; // User not found
        }

        Player dbPlayer = userOptional.get();
        // Add this logging for debugging
        System.out.println("Input password: " + player.getPassword());
        System.out.println("DB password: " + dbPlayer.getPassword());

        if (dbPlayer.getPassword().equals(player.getPassword())) {
            // Сохраняем запись входа
            LoginRecord loginRecord = new LoginRecord(dbPlayer, LocalDateTime.now());
            loginRecordRepo.save(loginRecord);

            return true;
        }
        return false;
    }

}
