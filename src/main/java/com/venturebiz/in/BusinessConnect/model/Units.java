package com.venturebiz.in.BusinessConnect.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor      // REQUIRED for Hibernate
@AllArgsConstructor     // Needed for Builder
@Builder                // Builder works only when both constructors exist
public class Units {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    private String unitName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;
    @OneToMany(mappedBy = "unit", cascade = CascadeType.ALL)
    private List<UnitMember> members;

}
