package com.umograd.content.infrastructure.persistence.jpa;

import com.umograd.content.domain.task.Difficulty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer minAge;
    private Integer maxAge;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;
}
