package com.venturebiz.in.BusinessConnect.controller;

import com.venturebiz.in.BusinessConnect.dto.UnitCreateRequest;
import com.venturebiz.in.BusinessConnect.model.Community;
import com.venturebiz.in.BusinessConnect.model.Units;
import com.venturebiz.in.BusinessConnect.model.User;
import com.venturebiz.in.BusinessConnect.repository.CommunityRepository;
import com.venturebiz.in.BusinessConnect.repository.UnitRepository;
import com.venturebiz.in.BusinessConnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/unit")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UnitController {

    private final UnitRepository unitRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    private Map<String, Object> response(boolean status, String message, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);
        map.put("message", message);
        if (data != null) map.put("data", data);
        return map;
    }

    // 1️⃣ CREATE UNIT
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createUnit(@RequestBody UnitCreateRequest request) {
        try {
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User admin = userRepository.findByEmailAddress(email)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            Community community = communityRepository.findById(request.getCommunityId())
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            if (!community.getAdmin().getId().equals(admin.getId())) {
                return ResponseEntity.badRequest().body(
                        response(false, "You are not authorized to manage this community", null)
                );
            }

            if (unitRepository.existsByUnitNameIgnoreCaseAndCommunity(request.getUnitName(), community)) {
                return ResponseEntity.badRequest().body(
                        response(false, "Unit already exists in this community", null)
                );
            }

            Units unit = Units.builder()
                    .unitName(request.getUnitName().trim())
                    .community(community)
                    .build();

            unitRepository.save(unit);

            return ResponseEntity.ok(
                    response(true, "Unit created successfully", unit)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response(false, e.getMessage(), null));
        }
    }

    // 2️⃣ GET UNITS BY COMMUNITY
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/by-community/{communityId}")
    public ResponseEntity<?> getUnitsByCommunity(@PathVariable int communityId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        if (!community.getAdmin().getId().equals(admin.getId())) {
            return ResponseEntity.badRequest().body(
                    response(false, "You are not authorized to view units of this community", null)
            );
        }

        return ResponseEntity.ok(
                response(true, "Units fetched successfully", unitRepository.findByCommunity(community))
        );
    }

    // 3️⃣ DELETE UNIT
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUnit(@PathVariable Long id) {
        try {
            Units unit = unitRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Unit not found"));

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User admin = userRepository.findByEmailAddress(email)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

            if (!unit.getCommunity().getAdmin().getId().equals(admin.getId())) {
                return ResponseEntity.badRequest().body(
                        response(false, "You are not authorized to delete this unit", null)
                );
            }

            unitRepository.delete(unit);

            return ResponseEntity.ok(response(true, "Unit deleted successfully", null));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response(false, e.getMessage(), null));
        }
    }

    // 4️⃣ GET ALL COMMUNITIES
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/community-list")
    public ResponseEntity<?> getAllCommunities() {
        try {
            return ResponseEntity.ok(
                    response(true, "All communities fetched successfully", communityRepository.findAll())
            );
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(response(false, e.getMessage(), null));
        }
    }
}
