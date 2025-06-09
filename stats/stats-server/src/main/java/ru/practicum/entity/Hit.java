package ru.practicum.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "hit")
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "app", nullable = false)
    private String app;

    @Size(max = 255)
    @NotNull
    @Column(name = "uri", nullable = false)
    private String uri;

    @Size(max = 255)
    @NotNull
    @Column(name = "ip", nullable = false)
    private String ip;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

}