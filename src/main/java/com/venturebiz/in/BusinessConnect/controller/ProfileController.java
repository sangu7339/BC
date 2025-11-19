package com.venturebiz.in.BusinessConnect.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.venturebiz.in.BusinessConnect.dto.ProfileUpdate;
import com.venturebiz.in.BusinessConnect.model.User;
import com.venturebiz.in.BusinessConnect.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {

        try {
            String email = authentication.getName();

            User user = userRepository.findByEmailAddress(email)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));

            return ResponseEntity.ok(user);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/profile/update")
    public ResponseEntity<?> profileUpdate(
            @RequestBody ProfileUpdate updateRequest,
            Authentication authentication) {

        try {
            String email = authentication.getName();

            User user = userRepository.findByEmailAddress(email)
                    .orElseThrow(() -> new RuntimeException("User Not Found"));

            // Update only if values are NOT null
            if (updateRequest.getFullName() != null)
                user.setFullName(updateRequest.getFullName());

            if (updateRequest.getEmailAddress() != null)
                user.setEmailAddress(updateRequest.getEmailAddress());

            if (updateRequest.getPhoneNumber() != null)
                user.setPhoneNumber(updateRequest.getPhoneNumber());

            if (updateRequest.getCompanyName() != null)
                user.setCompanyName(updateRequest.getCompanyName());

            if (updateRequest.getPositionTitle() != null)
                user.setPositionTitle(updateRequest.getPositionTitle());

            if (updateRequest.getBusinessAddress() != null)
                user.setBusinessAddress(updateRequest.getBusinessAddress());

            if (updateRequest.getBusinessInYear() != null)
                user.setBusinessInYear(updateRequest.getBusinessInYear());

            if (updateRequest.getCountry() != null)
                user.setCountry(updateRequest.getCountry());

            if (updateRequest.getState() != null)
                user.setState(updateRequest.getState());

            if (updateRequest.getBusinessDescription() != null)
                user.setBusinessDescription(updateRequest.getBusinessDescription());

            if (updateRequest.getGstNumber() != null)
                user.setGstNumber(updateRequest.getGstNumber());

            if (updateRequest.getLinkedin() != null)
                user.setLinkedin(updateRequest.getLinkedin());

            if (updateRequest.getWebsiteUrl() != null)
                user.setWebsiteUrl(updateRequest.getWebsiteUrl());

            // Save only modified data
            userRepository.save(user);

            return ResponseEntity.ok("Profile updated successfully!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
