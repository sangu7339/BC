package com.venturebiz.in.BusinessConnect.dto;

import java.time.LocalDate;

import lombok.Data;
@Data
public class ProfileUpdate {
	 private String fullName;
	    private String emailAddress;
	    private Long phoneNumber;

	    private String companyName;
	   
	    private String positionTitle;
	    private String businessAddress;
	    private LocalDate businessInYear;
	    private String country;
	    private String state;
	    private String businessDescription;
	    private Long gstNumber;
	    private String linkedin;
	    private String websiteUrl;
	   
}
