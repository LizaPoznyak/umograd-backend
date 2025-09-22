package com.umograd.content.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "task_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResultJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="task_id", nullable=false)
    private Long taskId;

    @Column(name="child_id", nullable=false)
    private Long childId;

    @Column(nullable=false, length=20)
    private String status;

    @Column
    private Integer score;

    @Column(name="finished_at")
    private LocalDateTime finishedAt;

    // getters/setters
    // ...
}
