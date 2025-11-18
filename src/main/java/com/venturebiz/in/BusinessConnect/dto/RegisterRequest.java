package com.venturebiz.in.BusinessConnect.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RegisterRequest {

    private String fullName;
    private String emailAddress;
    private Long phoneNumber;

    private String companyName;
    private String businessCategory;
    private String positionTitle;
    private String businessAddress;
    private LocalDate businessInYear;
    private String country;
    private String state;
    private String businessDescription;
    private Long gstNumber;
    private String community;
    private String category;
    private String sponsorName;

    // The referral code of the person who referred this user
    private String referredBy;

    private String linkedin;
    private String websiteUrl;
    private String subCategory;

    // Optional role (default USER if not provided)
    private String role;
}
