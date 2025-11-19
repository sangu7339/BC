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

    /**
     * Add User to Unit
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addUserToUnit(@RequestBody Addmember addmember) {

        Map<String, Object> response = new HashMap<>();

        try {
            int unitId = addmember.getUnitId();

            User user = userRepository.findById(addmember.getUserId())
                    .orElseThrow(() -> new RuntimeException("User Not Found"));

            Units unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new RuntimeException("Unit Not Found"));

            // User must not exist in any unit
            boolean alreadyMemberOfAnyUnit = unitMemberrepository.existsByUserId(addmember.getUserId());
            if (alreadyMemberOfAnyUnit) {
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

    /**
     * Remove User From Unit (DELETE)
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeUserFromUnit(
            @RequestParam Long userId,
            @RequestParam int unitId) {

        Map<String, Object> response = new HashMap<>();

        try {
            UnitMember member = unitMemberrepository.findByUserIdAndUnitId(
                    userId,
                    unitId
            ).orElseThrow(() -> new RuntimeException("User is not a member of this unit"));

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

    /**
     * Get All Members of a Unit
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getUnitMembers(@RequestParam int unitId) {

        Map<String, Object> response = new HashMap<>();

        try {
            Units unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new RuntimeException("Unit Not Found"));

            List<UnitMember> members = unitMemberrepository.findByUnitId(unitId);

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
