package com.venturebiz.in.BusinessConnect.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.venturebiz.in.BusinessConnect.model.Business;

@Repository
public interface BusinessRespo extends JpaRepository<Business, Long> {

    List<Business> findByBusinnesgiver(String giver);
    List<Business> findByBusinnesreciver(String receiver);

    @Query("SELECT b FROM Business b WHERE b.businnesgiver = :name OR b.businnesreciver = :name")
    List<Business> findByUserHistory(@Param("name") String name);
	boolean existsByAskId(Long id);
}

