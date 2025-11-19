package com.venturebiz.in.BusinessConnect.controller;

import com.venturebiz.in.BusinessConnect.dto.CommunityRequest;
import com.venturebiz.in.BusinessConnect.model.Community;
import com.venturebiz.in.BusinessConnect.model.User;
import com.venturebiz.in.BusinessConnect.repository.CommunityRepository;
import com.venturebiz.in.BusinessConnect.repository.UserRepository;
import com.venturebiz.in.BusinessConnect.response.ResponseBuilder;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
//@CrossOrigin(origins = "http://localhost:5174")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

   
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createCommunity(@Valid @RequestBody CommunityRequest request) {
        try {

            if (communityRepository.existsByCommunityNameIgnoreCase(request.getCommunityName())) {
                return ResponseEntity.badRequest().body(
                        ResponseBuilder.error("Community already exists")
                );
            }

          
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User admin = userRepository.findByEmailAddress(email)
                    .orElseThrow(() -> new RuntimeException("Admin not found"));

         
            Community community = Community.builder()
                    .communityName(request.getCommunityName().trim())
                    .admin(admin)
                    .build();

            communityRepository.save(community);

            return ResponseEntity.ok(
                    ResponseBuilder.success("Community created successfully", community)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseBuilder.error(e.getMessage()));
        }
    }

   
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<?> getCommunityList() {
        List<Community> list = communityRepository.findAll();
        return ResponseEntity.ok(
                ResponseBuilder.success("Communities fetched successfully", list)
        );
    }

    
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/my-communities")
    public ResponseEntity<?> getMyCommunities() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByEmailAddress(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        List<Community> communities = communityRepository.findByAdmin(admin);

        return ResponseEntity.ok(
                ResponseBuilder.success("Communities created by admin fetched", communities)
        );
    }

  
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateCommunity(
            @PathVariable int id,
            @Valid @RequestBody CommunityRequest request) {

        try {
            Community community = communityRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            // DUPLICATE CHECK
            if (communityRepository.existsByCommunityNameIgnoreCase(request.getCommunityName())
                    && !community.getCommunityName().equalsIgnoreCase(request.getCommunityName())) {

                return ResponseEntity.badRequest().body(
                        ResponseBuilder.error("Community name already exists")
                );
            }

            community.setCommunityName(request.getCommunityName().trim());
            communityRepository.save(community);

            return ResponseEntity.ok(
                    ResponseBuilder.success("Community updated successfully", community)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseBuilder.error(e.getMessage()));
        }
    }

 
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCommunity(@PathVariable int id) {
        try {
            if (!communityRepository.existsById(id)) {
                return ResponseEntity.badRequest().body(
                        ResponseBuilder.error("Community not found")
                );
            }

            communityRepository.deleteById(id);

            return ResponseEntity.ok(
                    ResponseBuilder.success("Community deleted successfully")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseBuilder.error(e.getMessage()));
        }
    }
}
