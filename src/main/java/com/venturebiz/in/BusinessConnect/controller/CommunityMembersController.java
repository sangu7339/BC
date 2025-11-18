package com.venturebiz.in.BusinessConnect.controller;

import com.venturebiz.in.BusinessConnect.model.*;
import com.venturebiz.in.BusinessConnect.repository.*;
import com.venturebiz.in.BusinessConnect.response.ResponseBuilder;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/community-members")
@RequiredArgsConstructor
public class CommunityMembersController {

    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    // ✅ 1. ADD USER TO A COMMUNITY (ADMIN ONLY)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<?> addUserToCommunity(
            @RequestParam int communityId,
            @RequestParam Long userId) {

        try {
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Prevent duplicate
            if (communityMemberRepository.existsByCommunityAndUser(community, user)) {
                return ResponseEntity.badRequest().body(
                        ResponseBuilder.error("User already exists in this community")
                );
            }

            CommunityMember member = CommunityMember.builder()
                    .community(community)
                    .user(user)
                    .build();

            communityMemberRepository.save(member);

            return ResponseEntity.ok(
                    ResponseBuilder.success("User added to community successfully", member)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseBuilder.error("Error: " + e.getMessage())
            );
        }
    }


    // ✅ 2. REMOVE USER FROM A COMMUNITY (ADMIN ONLY)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/remove")
    public ResponseEntity<?> removeUserFromCommunity(
            @RequestParam int communityId,
            @RequestParam Long userId) {

        try {
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CommunityMember> members = communityMemberRepository.findByCommunity(community);

            for (CommunityMember member : members) {
                if (member.getUser().getId().equals(userId)) {
                    communityMemberRepository.delete(member);

                    return ResponseEntity.ok(
                            ResponseBuilder.success("User removed from community successfully")
                    );
                }
            }

            return ResponseEntity.badRequest().body(
                    ResponseBuilder.error("User is NOT in this community")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseBuilder.error("Error: " + e.getMessage())
            );
        }
    }


    // ✅ 3. GET ALL MEMBERS OF A COMMUNITY (ADMIN ONLY)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/members/{communityId}")
    public ResponseEntity<?> getCommunityMembers(@PathVariable int communityId) {

        try {
            Community community = communityRepository.findById(communityId)
                    .orElseThrow(() -> new RuntimeException("Community not found"));

            List<CommunityMember> members = communityMemberRepository.findByCommunity(community);

            return ResponseEntity.ok(
                    ResponseBuilder.success("Community members fetched successfully", members)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseBuilder.error("Error: " + e.getMessage())
            );
        }
    }


    // ✅ 4. GET ALL COMMUNITIES THAT A USER BELONGS TO
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserCommunities(@PathVariable Long userId) {

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<CommunityMember> communities = communityMemberRepository.findByUser(user);

            return ResponseEntity.ok(
                    ResponseBuilder.success("User's communities fetched successfully", communities)
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    ResponseBuilder.error("Error: " + e.getMessage())
            );
        }
    }
}
