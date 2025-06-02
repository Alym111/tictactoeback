package com.tictactoe.repository;

import com.tictactoe.model.LoginRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface LoginRecordRepo extends JpaRepository<LoginRecord, Long> {

    @Query(value = "SELECT DATE(login_time) as login_date, COUNT(DISTINCT player_id) FROM login_record GROUP BY DATE(login_time)", nativeQuery = true)
    List<Object[]> countUniqueLoginsByDate();

    List<LoginRecord> findByPlayerId(Long playerId);
}
