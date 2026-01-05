package com.internshipfinder.zirah.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "message_threads")
public class MessageThread {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "participant1_id", nullable = false)
    private Long participant1Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant1_type", nullable = false)
    private UserType participant1Type;

    @Column(name = "participant2_id", nullable = false)
    private Long participant2Id;

    @Enumerated(EnumType.STRING)
    @Column(name = "participant2_type", nullable = false)
    private UserType participant2Type;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt = LocalDateTime.now();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public MessageThread() {}

    public MessageThread(Long participant1Id, UserType participant1Type, 
                        Long participant2Id, UserType participant2Type) {
        this.participant1Id = participant1Id;
        this.participant1Type = participant1Type;
        this.participant2Id = participant2Id;
        this.participant2Type = participant2Type;
    }
}