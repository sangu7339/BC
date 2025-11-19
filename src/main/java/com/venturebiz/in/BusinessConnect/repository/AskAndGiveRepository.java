package com.venturebiz.in.BusinessConnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.venturebiz.in.BusinessConnect.model.AskAndGive;

public interface AskAndGiveRepository extends JpaRepository<AskAndGive, Long> {
    List<AskAndGive> findByCreatedById(Long userId);
    List<AskAndGive> findByStatus(AskAndGive.Status status);
}
