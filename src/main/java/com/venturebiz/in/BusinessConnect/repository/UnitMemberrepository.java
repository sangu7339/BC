package com.venturebiz.in.BusinessConnect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venturebiz.in.BusinessConnect.model.UnitMember;

public interface UnitMemberrepository extends JpaRepository<UnitMember, Long> {

    boolean existsByUserIdAndUnitId(Long userId, int unitId);

    Optional<UnitMember> findByUserIdAndUnitId(Long userId, int unitId);

    List<UnitMember> findByUnitId(int unitId);
    
    boolean existsByUserId(Long userId);
}

