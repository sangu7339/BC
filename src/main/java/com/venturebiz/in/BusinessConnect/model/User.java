package com.venturebiz.in.BusinessConnect.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true, nullable = false)
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

    // UNIQUE REFERRAL CODE for this user
    @Column(unique = true)
    private String referralCode;

    // REFERRAL CODE OF ANOTHER USER (who referred this user)
    private String referredBy;

    private String linkedin;
    private String websiteUrl;
    private String subCategory;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Registration timestamp
    private LocalDateTime registeredAt;

    // OTP fields
    private String otp;
    private LocalDateTime otpGeneratedAt;

    @PrePersist
    public void onCreate() {
        this.registeredAt = LocalDateTime.now();
    }
    
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UnitMember> units;

}
