package com.venturebiz.in.BusinessConnect.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venturebiz.in.BusinessConnect.model.UnitMember;

public interface UnitMemberrepository extends JpaRepository<UnitMember, Long> {

    boolean existsByUserIdAndUnitId(Long userId, Long unitId);

    Optional<UnitMember> findByUserIdAndUnitId(Long userId, Long unitId);

    List<UnitMember> findByUnitId(Long unitId);

    boolean existsByUserId(Long userId);

    Optional<UnitMember> findByUserId(Long userId);
}
