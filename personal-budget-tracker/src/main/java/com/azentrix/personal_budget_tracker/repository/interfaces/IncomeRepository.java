package com.azentrix.personal_budget_tracker.repository.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.azentrix.personal_budget_tracker.entity.Income;

@Repository
public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT i FROM Income i WHERE i.date BETWEEN :start AND :end ORDER BY i.date")
    List<Income> findByDateBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT i FROM Income i WHERE i.user.userId = :userId ORDER BY i.date DESC")
    List<Income> findAllByUserId(@Param("userId") Long userId);

    @Query("SELECT i FROM Income i WHERE i.user.userId = :userId AND i.date BETWEEN :start AND :end ORDER BY i.date")
    List<Income> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT i FROM Income i WHERE i.id = :id AND i.user.userId = :userId")
    Optional<Income> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
