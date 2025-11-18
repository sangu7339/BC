package com.venturebiz.in.BusinessConnect.repository;

import com.venturebiz.in.BusinessConnect.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Integer> {

    boolean existsByCommunityAndUser(Community community, User user);

    List<CommunityMember> findByCommunity(Community community);

    List<CommunityMember> findByUser(User user);
}
