package com.venturebiz.in.BusinessConnect.dto;

import java.time.LocalDate;

import com.venturebiz.in.BusinessConnect.model.Business.Status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BusinessRequest {

	private String businessDecription;
    private long amount;
    private LocalDate startDate;
    private LocalDate endDate;
}
