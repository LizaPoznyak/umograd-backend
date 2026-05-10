package com.umograd.content.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "task_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskQuestionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contentType;
    private String question;

    @Lob
    private String options;

    private String answer;

    private String hint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskJpaEntity task;
}