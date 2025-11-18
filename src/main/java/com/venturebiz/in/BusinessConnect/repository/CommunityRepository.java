package com.venturebiz.in.BusinessConnect.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.venturebiz.in.BusinessConnect.model.Community;
import com.venturebiz.in.BusinessConnect.model.User;

public interface CommunityRepository extends JpaRepository<Community, Integer> {

    // For duplicate name check
    boolean existsByCommunityNameIgnoreCase(String communityName);

    // For admin-specific communities
    List<Community> findByAdmin(User admin);
}
