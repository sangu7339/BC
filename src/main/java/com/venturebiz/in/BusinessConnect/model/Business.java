package com.venturebiz.in.BusinessConnect.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String businnesgiver;
    private String businnesreciver;
    private String businessDecription;

    @Enumerated(EnumType.STRING)
    private Status status;

    private long amount;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createDate;

    // RELATIONSHIP WITH ASK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ask_id")
    private AskAndGive ask;

    public enum Status {
        PENDING,
        ACCEPT,
        REJECT
    }

    @PrePersist
    public void initDefaults() {
        this.createDate = LocalDateTime.now();
        this.status = Status.PENDING;

        this.receiverRequestStatus = CloseRequest.NONE;
        this.giverApprovalStatus = CloseApproval.NONE;
        this.finalStatus = FinalStatus.OPEN;
    }

    @Enumerated(EnumType.STRING)
    private CloseRequest receiverRequestStatus;

    @Enumerated(EnumType.STRING)
    private CloseApproval giverApprovalStatus;

    @Enumerated(EnumType.STRING)
    private FinalStatus finalStatus;

    public enum CloseRequest { NONE, REQUESTED }
    public enum CloseApproval { NONE, ACCEPT, REJECT }
    public enum FinalStatus { OPEN, CLOSED, REJECTED }

}
