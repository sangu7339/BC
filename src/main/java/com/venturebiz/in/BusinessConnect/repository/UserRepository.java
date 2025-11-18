package com.venturebiz.in.BusinessConnect.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.venturebiz.in.BusinessConnect.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);

    Optional<User> findByReferralCode(String referralCode);

    boolean existsByReferralCode(String referralCode);
}
