package com.venturebiz.in.BusinessConnect.repository;

import com.venturebiz.in.BusinessConnect.model.Community;
import com.venturebiz.in.BusinessConnect.model.Units;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UnitRepository extends JpaRepository<Units, Long> {

    // Fetch all units inside a community
    List<Units> findByCommunity(Community community);

    // Check duplicate unit name inside a community
    boolean existsByUnitNameIgnoreCaseAndCommunity(String unitName, Community community);
}
