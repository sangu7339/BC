package com.venturebiz.in.BusinessConnect.controller;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.venturebiz.in.BusinessConnect.dto.BusinessRequest;
import com.venturebiz.in.BusinessConnect.model.*;
import com.venturebiz.in.BusinessConnect.repository.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/user/business")
public class BussinessController {

    private final UserRepository userRepo;
    private final AskAndGiveRepository askRepo;
    private final BusinessRespo businessRepo;
    private final UnitMemberrepository unitMemberRepo;

    private Map<String, Object> response(String msg, Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", msg);
        if (data != null) map.put("data", data);
        return map;
    }

    // 1️⃣ CREATE BUSINESS (Only responder & only once per Ask)
    @PreAuthorize("hasRole('USER')")
    @PostMapping("/create/{askId}")
    public ResponseEntity<?> createBusiness(
            @PathVariable Long askId,
            @RequestBody BusinessRequest req) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        AskAndGive ask = askRepo.findById(askId)
                .orElseThrow(() -> new RuntimeException("Ask not found"));

        // RULE 1: Business allowed only after ACCEPT
        if (ask.getStatus() != AskAndGive.Status.ACCEPT)
            return ResponseEntity.badRequest().body(response(
                    "Business can only be created after ASK is ACCEPTED", null));

        // RULE 2: Only responder can create business
        if (!ask.getRespondedBy().getId().equals(currentUser.getId()))
            return ResponseEntity.badRequest().body(response(
                    "Only the responder can create business for this ask", null));

        // RULE 3: Prevent multiple business creation for same ask
        if (businessRepo.existsByAskId(ask.getId()))
            return ResponseEntity.badRequest().body(response(
                    "Business already created for this ask", null));

        Business business = Business.builder()
                .businnesgiver(ask.getCreatedBy().getFullName())
                .businnesreciver(currentUser.getFullName())
                .businessDecription(req.getBusinessDecription())
                .amount(req.getAmount())
                .startDate(req.getStartDate())
                .endDate(req.getEndDate())
                .ask(ask)
                .build();

        businessRepo.save(business);

        return ResponseEntity.ok(response("Business created successfully", business));
    }

    // 2️⃣ UPDATE BUSINESS (Only receiver allowed)
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBusiness(@PathVariable Long id,
                                            @RequestBody BusinessRequest req) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Business b = businessRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        // Only receiver can update business
        if (!b.getBusinnesreciver().equals(currentUser.getFullName()))
            return ResponseEntity.badRequest().body(response(
                    "Only the receiver can update this business", null));

        b.setBusinessDecription(req.getBusinessDecription());
        b.setAmount(req.getAmount());
        b.setStartDate(req.getStartDate());
        b.setEndDate(req.getEndDate());

        businessRepo.save(b);

        return ResponseEntity.ok(response("Business updated successfully", b));
    }

    // 3️⃣ LIST BUSINESS I RECEIVED
    @GetMapping("/my-received")
    public ResponseEntity<?> myReceivedBusiness() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Business> list = businessRepo.findByBusinnesreciver(me.getFullName());

        return ResponseEntity.ok(response("My received business", list));
    }

    // 4️⃣ LIST BUSINESS I GAVE
    @GetMapping("/my-given")
    public ResponseEntity<?> myGivenBusiness() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Business> list = businessRepo.findByBusinnesgiver(me.getFullName());

        return ResponseEntity.ok(response("My given business", list));
    }

    // 5️⃣ TYFCB
    @PostMapping("/tyfcb/{businessId}")
    public ResponseEntity<?> thankForBusiness(@PathVariable Long businessId) {

        Business business = businessRepo.findById(businessId)
                .orElseThrow(() -> new RuntimeException("Business not found"));

        business.setStatus(Business.Status.ACCEPT);
        businessRepo.save(business);

        return ResponseEntity.ok(response("TYFCB updated successfully!", business));
    }

    // 6️⃣ FULL BUSINESS HISTORY
    @GetMapping("/history")
    public ResponseEntity<?> fullBusinessHistory() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User me = userRepo.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Business> history = businessRepo.findByUserHistory(me.getFullName());

        return ResponseEntity.ok(response("Business history fetched", history));
    }
}
