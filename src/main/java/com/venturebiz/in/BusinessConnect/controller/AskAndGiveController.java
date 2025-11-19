package com.venturebiz.in.BusinessConnect.controller;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.venturebiz.in.BusinessConnect.model.*;
import com.venturebiz.in.BusinessConnect.model.AskAndGive.Status;
import com.venturebiz.in.BusinessConnect.repository.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/ask")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AskAndGiveController {

    private final AskAndGiveRepository askRepo;
    private final UserRepository userRepo;
    private final UnitMemberrepository unitMemberRepo;

    private Map<String, Object> res(String message, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        if (data != null) map.put("data", data);
        return map;
    }

    // 1️⃣ CREATE ASK
    @PostMapping("/create")
    public ResponseEntity<?> createAsk(@RequestBody Map<String, String> req) {

        User creator = userRepo.findByEmailAddress(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        unitMemberRepo.findByUserId(creator.getId())
                .orElseThrow(() -> new RuntimeException("You are not in any unit"));

        AskAndGive ask = AskAndGive.builder()
                .ask(req.get("ask"))
                .give(req.get("give"))
                .createdBy(creator)
                .status(Status.PENDING)
                .build();

        askRepo.save(ask);

        return ResponseEntity.ok(res("Ask created successfully", ask));
    }

    // 2️⃣ RESPOND TO ASK
    @PostMapping("/respond/{askId}")
    public ResponseEntity<?> respond(@PathVariable Long askId,
                                     @RequestParam Status status) {

        User responder = userRepo.findByEmailAddress(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AskAndGive ask = askRepo.findById(askId)
                .orElseThrow(() -> new RuntimeException("Ask not found"));
        

        UnitMember creatorUnit = unitMemberRepo.findByUserId(ask.getCreatedBy().getId())
                .orElseThrow(() -> new RuntimeException("Creator not in unit"));

        UnitMember responderUnit = unitMemberRepo.findByUserId(responder.getId())
                .orElseThrow(() -> new RuntimeException("Responder not in unit"));

        if (!creatorUnit.getUnit().getId().equals(responderUnit.getUnit().getId()))
            return ResponseEntity.badRequest()
                    .body(res("You cannot respond to another unit’s ask", null));
        
        
        if (ask.getStatus() != Status.ACCEPT)
            return ResponseEntity.badRequest().body(
                    res("once you response you cannot change once again", null));

        
        ask.setStatus(status);
        ask.setRespondedBy(responder);
        askRepo.save(ask);

        return ResponseEntity.ok(res("Response updated", ask));
    }

    // 3️⃣ DELETE ASK (only pending)
    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/delete/{askId}")
    public ResponseEntity<?> deleteAsk(@PathVariable Long askId) {

        User user = userRepo.findByEmailAddress(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        AskAndGive ask = askRepo.findById(askId)
                .orElseThrow(() -> new RuntimeException("Ask not found"));

        if (!ask.getCreatedBy().getId().equals(user.getId()))
            return ResponseEntity.badRequest().body(res("You cannot delete someone else’s ask", null));

        if (ask.getStatus() != Status.PENDING)
            return ResponseEntity.badRequest().body(
                    res("Cannot delete: someone already responded", null));

        askRepo.delete(ask);

        return ResponseEntity.ok(res("Ask deleted successfully", null));
    }

    // 4️⃣ GET ALL ASKS IN USER'S UNIT
    @GetMapping("/my-unit")
    public ResponseEntity<?> getMyUnitAsks() {

        User user = userRepo.findByEmailAddress(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long unitId = unitMemberRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("You are not in any unit"))
                .getUnit().getId();

        List<UnitMember> members = unitMemberRepo.findByUnitId(unitId);

        List<AskAndGive> asks = members.stream()
                .flatMap(m -> askRepo.findByCreatedById(m.getUser().getId()).stream())
                .toList();

        return ResponseEntity.ok(res("Unit asks fetched", asks));
    }

    // 5️⃣ GET STATUS OF A SPECIFIC ASK
    @GetMapping("/status/{askId}")
    public ResponseEntity<?> getAskStatus(@PathVariable Long askId) {

        AskAndGive ask = askRepo.findById(askId)
                .orElseThrow(() -> new RuntimeException("Ask not found"));

        Map<String, Object> map = new HashMap<>();
        map.put("id", ask.getId());
        map.put("ask", ask.getAsk());
        map.put("give", ask.getGive());
        map.put("createdBy", ask.getCreatedBy().getFullName());
        map.put("respondedBy",
                ask.getRespondedBy() != null ? ask.getRespondedBy().getFullName() : null);
        map.put("status", ask.getStatus());
        map.put("time", ask.getTime());

        return ResponseEntity.ok(res("Ask status retrieved", map));
    }

    // 6️⃣ GET USER'S OWN UNIT DETAILS
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/unit/my")
    public ResponseEntity<?> getMyUnit() {

        User user = userRepo.findByEmailAddress(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UnitMember member = unitMemberRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("You are not assigned to any unit"));

        Units unit = member.getUnit();

        Map<String, Object> data = new HashMap<>();
        data.put("unitId", unit.getId());
        data.put("unitName", unit.getUnitName());
        data.put("communityId", unit.getCommunity().getId());
        data.put("communityName", unit.getCommunity().getCommunityName());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "User's unit fetched",
                "data", data
        ));
    }

    // 7️⃣ GET MEMBERS OF USER'S UNIT
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/unit/my/members")
    public ResponseEntity<?> getMembersOfMyUnit() {

        User user = userRepo.findByEmailAddress(
                SecurityContextHolder.getContext().getAuthentication().getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long unitId = unitMemberRepo.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("You are not assigned to any unit"))
                .getUnit().getId();

        List<UnitMember> members = unitMemberRepo.findByUnitId(unitId);

        List<Map<String, Object>> memberList = members.stream().map(m -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", m.getUser().getId());
            userData.put("fullName", m.getUser().getFullName());
            userData.put("email", m.getUser().getEmailAddress());
            userData.put("phone", m.getUser().getPhoneNumber());
            return userData;
        }).toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Unit members fetched",
                "data", memberList
        ));
    }
}
