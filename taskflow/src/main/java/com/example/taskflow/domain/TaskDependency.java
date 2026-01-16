package com.example.taskflow.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "task_dependencies")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class TaskDependency {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false) // The task that is blocked
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocks_task_id", nullable = false) // The task doing the blocking
    private Task blocksTask;
}