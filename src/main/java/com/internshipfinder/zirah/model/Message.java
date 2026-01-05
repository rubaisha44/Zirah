
package com.internshipfinder.zirah.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long senderId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType senderType;
    
    @Column(nullable = false)
    private Long receiverId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserType receiverType;
    
    @Column(nullable = false, length = 1000)
    private String content;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false, name = "is_read") // Changed from 'read' to 'is_read'
    private boolean read = false;
    
    // Constructors, getters, and setters remain the same
    public Message() {}
    
    public Message(Long senderId, UserType senderType, Long receiverId, UserType receiverType, String content) {
        this.senderId = senderId;
        this.senderType = senderType;
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public UserType getSenderType() { return senderType; }
    public void setSenderType(UserType senderType) { this.senderType = senderType; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public UserType getReceiverType() { return receiverType; }
    public void setReceiverType(UserType receiverType) { this.receiverType = receiverType; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
}