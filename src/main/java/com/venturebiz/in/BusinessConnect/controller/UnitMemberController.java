package com.venturebiz.in.BusinessConnect.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.venturebiz.in.BusinessConnect.dto.Addmember;
import com.venturebiz.in.BusinessConnect.model.UnitMember;
import com.venturebiz.in.BusinessConnect.model.Units;
import com.venturebiz.in.BusinessConnect.model.User;
import com.venturebiz.in.BusinessConnect.repository.CommunityRepository;
import com.venturebiz.in.BusinessConnect.repository.UnitMemberrepository;
import com.venturebiz.in.BusinessConnect.repository.UnitRepository;
import com.venturebiz.in.BusinessConnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/admin/unitmember")
@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class UnitMemberController {

    private final UnitMemberrepository unitMemberrepository;
    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    // 1️⃣ ADD USER TO UNIT
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addUserToUnit(@RequestBody Addmember addmember) {

        Map<String, Object> response = new HashMap<>();

        try {
            Long unitId = addmember.getUnitId();  // FIXED

            User user = userRepository.findById(addmember.getUserId())
                    .orElseThrow(() -> new RuntimeException("User Not Found"));

            Units unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new RuntimeException("Unit Not Found"));

            boolean alreadyMember = unitMemberrepository.existsByUserId(addmember.getUserId());
            if (alreadyMember) {
                response.put("status", "error");
                response.put("message", "User is already assigned to another unit");
                return ResponseEntity.badRequest().body(response);
            }

            UnitMember member = UnitMember.builder()
                    .user(user)
                    .unit(unit)
                    .build();

            unitMemberrepository.save(member);

            response.put("status", "success");
            response.put("message", "User added to unit successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 2️⃣ REMOVE USER FROM UNIT
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeUserFromUnit(
            @RequestParam Long userId,
            @RequestParam Long unitId) {  // FIXED

        Map<String, Object> response = new HashMap<>();

        try {
            UnitMember member = unitMemberrepository.findByUserIdAndUnitId(userId, unitId)
                    .orElseThrow(() -> new RuntimeException("User is not a member of this unit"));

            unitMemberrepository.delete(member);

            response.put("status", "success");
            response.put("message", "User removed from unit successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 3️⃣ LIST MEMBERS OF A UNIT
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getUnitMembers(@RequestParam Long unitId) { // FIXED

        Map<String, Object> response = new HashMap<>();

        try {
            Units unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new RuntimeException("Unit Not Found"));

            List<UnitMember> members = unitMemberrepository.findByUnitId(unitId);  // FIXED

            response.put("status", "success");
            response.put("members", members);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
