// MessageService.java - FIXED VERSION
package com.internshipfinder.zirah.service;

import com.internshipfinder.zirah.model.*;
import com.internshipfinder.zirah.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MessageService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private StudentService studentService;
    
    @Autowired
    private RecruiterService recruiterService;
    
    @Autowired
    private MessageBroker messageBroker;
    
    /**
     * Send a message using broker pattern - FIXED
     */
    public Message sendMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        // Set creation timestamp
        message.setCreatedAt(LocalDateTime.now());
        message.setRead(false);
        
        // Save to database
        Message savedMessage = messageRepository.save(message);
        
        // Publish to broker for real-time delivery
        messageBroker.publish(savedMessage);
        
        return savedMessage;
    }
    
    /**
     * Get conversation between two users - FIXED
     */
    public List<Message> getConversation(Long user1Id, UserType user1Type, 
                                        Long user2Id, UserType user2Type) {
        if (user1Id == null || user2Id == null || user1Type == null || user2Type == null) {
            return new ArrayList<>();
        }
        
        try {
            List<Message> conversation = messageRepository.findConversation(
                user1Id, user1Type, user2Id, user2Type);
            
            // Mark messages as read when retrieving conversation
            if (conversation != null && !conversation.isEmpty()) {
                markMessagesAsRead(conversation, user1Id, user1Type);
            }
            
            return conversation != null ? conversation : new ArrayList<>();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Get user's message threads with preview - FIXED
     */
    public List<MessageThreadDTO> getUserThreadsWithPreview(Long userId, UserType userType) {
        if (userId == null || userType == null) {
            return new ArrayList<>();
        }
        
        try {
            List<Message> recentMessages = messageRepository.findRecentConversations(userId, userType);
            Map<String, MessageThreadDTO> threadMap = new HashMap<>();
            
            for (Message message : recentMessages) {
                boolean isSender = message.getSenderId().equals(userId) && 
                                  message.getSenderType() == userType;
                
                Long otherUserId = isSender ? message.getReceiverId() : message.getSenderId();
                UserType otherUserType = isSender ? message.getReceiverType() : message.getSenderType();
                String threadKey = otherUserId + "_" + otherUserType;
                
                if (!threadMap.containsKey(threadKey)) {
                    String otherUserName = getUserName(otherUserId, otherUserType);
                    int unreadCount = getUnreadCountWithUser(userId, userType, otherUserId, otherUserType);
                    
                    MessageThreadDTO thread = new MessageThreadDTO();
                    thread.setOtherUserId(otherUserId);
                    thread.setOtherUserType(otherUserType);
                    thread.setOtherUserName(otherUserName);
                    thread.setUnreadCount(unreadCount);
                    thread.setLastMessage(message.getContent());
                    thread.setLastMessageTime(message.getCreatedAt());
                    thread.setOnline(messageBroker.isUserOnline(otherUserId, otherUserType));
                    
                    threadMap.put(threadKey, thread);
                }
            }
            
            return new ArrayList<>(threadMap.values());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Mark messages as read - FIXED
     */
    public void markMessagesAsRead(List<Message> messages, Long userId, UserType userType) {
        if (messages == null || userId == null || userType == null) return;
        
        try {
            List<Message> unreadMessages = messages.stream()
                .filter(message -> !message.getSenderId().equals(userId) || 
                                  message.getSenderType() != userType)
                .filter(message -> !message.isRead())
                .collect(Collectors.toList());
            
            for (Message message : unreadMessages) {
                message.setRead(true);
                messageRepository.save(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get unread message count with specific user - FIXED
     */
    public int getUnreadCountWithUser(Long userId, UserType userType, 
                                     Long otherUserId, UserType otherUserType) {
        try {
            if (userId == null || otherUserId == null) return 0;
            return messageRepository.countUnreadMessagesBetweenUsers(
                userId, userType, otherUserId, otherUserType);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get total unread message count for user - FIXED
     */
    public int getTotalUnreadCount(Long userId, UserType userType) {
        try {
            if (userId == null) return 0;
            return messageRepository.countUnreadMessages(userId, userType);
        } catch (Exception e) {
            return 0;
        }
    }
    
    /**
     * Get user name by ID and type - FIXED
     */
    public String getUserName(Long userId, UserType userType) {
        try {
            if (userId == null || userType == null) return "User";
            
            if (userType == UserType.STUDENT) {
                Student student = studentService.findById(userId);
                return student != null ? student.getName() : "Student";
            } else {
                Recruiter recruiter = recruiterService.findById(userId);
                return recruiter != null ? recruiter.getName() : "Recruiter";
            }
        } catch (Exception e) {
            return "User";
        }
    }
    
    // DTO Classes remain the same
    public static class MessageThreadDTO {
        private Long otherUserId;
        private UserType otherUserType;
        private String otherUserName;
        private String lastMessage;
        private LocalDateTime lastMessageTime;
        private int unreadCount;
        private boolean online;
        
        // Getters and setters
        public Long getOtherUserId() { return otherUserId; }
        public void setOtherUserId(Long otherUserId) { this.otherUserId = otherUserId; }
        
        public UserType getOtherUserType() { return otherUserType; }
        public void setOtherUserType(UserType otherUserType) { this.otherUserType = otherUserType; }
        
        public String getOtherUserName() { return otherUserName; }
        public void setOtherUserName(String otherUserName) { this.otherUserName = otherUserName; }
        
        public String getLastMessage() { return lastMessage; }
        public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
        
        public LocalDateTime getLastMessageTime() { return lastMessageTime; }
        public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }
        
        public int getUnreadCount() { return unreadCount; }
        public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
        
        public boolean isOnline() { return online; }
        public void setOnline(boolean online) { this.online = online; }
    }
}