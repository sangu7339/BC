package com.venturebiz.in.BusinessConnect.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AskAndGive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ask;

    private String give;

    @Enumerated(EnumType.STRING)
    private Status status;

    private LocalDateTime time;

    // USER WHO CREATED THE ASK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    // USER WHO RESPONDED TO ASK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responded_by")
    private User respondedBy;

    public enum Status {
        PENDING,
        ACCEPT,
        IGNORE
    }

    @PrePersist
    public void setDefault() {
        this.time = LocalDateTime.now();
        this.status = Status.PENDING;
    }
}
