// MessageRepository.java
package com.internshipfinder.zirah.repository;

import com.internshipfinder.zirah.model.Message;
import com.internshipfinder.zirah.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Simple conversation query
    @Query("SELECT m FROM Message m WHERE " +
           "((m.senderId = :user1Id AND m.senderType = :user1Type AND m.receiverId = :user2Id AND m.receiverType = :user2Type) OR " +
           "(m.senderId = :user2Id AND m.senderType = :user2Type AND m.receiverId = :user1Id AND m.receiverType = :user1Type)) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversation(@Param("user1Id") Long user1Id, @Param("user1Type") UserType user1Type,
                                  @Param("user2Id") Long user2Id, @Param("user2Type") UserType user2Type);
    
    // Get recent messages for thread previews
    @Query("SELECT m FROM Message m WHERE " +
           "m.id IN (SELECT MAX(m2.id) FROM Message m2 WHERE " +
           "(m2.senderId = :userId AND m2.senderType = :userType) OR " +
           "(m2.receiverId = :userId AND m2.receiverType = :userType) " +
           "GROUP BY " +
           "CASE WHEN m2.senderId = :userId THEN m2.receiverId ELSE m2.senderId END, " +
           "CASE WHEN m2.senderId = :userId THEN m2.receiverType ELSE m2.senderType END) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findRecentConversations(@Param("userId") Long userId, @Param("userType") UserType userType);
    
    // Count unread messages between specific users
    @Query("SELECT COUNT(m) FROM Message m WHERE " +
           "m.receiverId = :userId AND m.receiverType = :userType AND " +
           "m.senderId = :otherUserId AND m.senderType = :otherUserType AND " +
           "m.read = false")
    int countUnreadMessagesBetweenUsers(@Param("userId") Long userId, @Param("userType") UserType userType,
                                       @Param("otherUserId") Long otherUserId, @Param("otherUserType") UserType otherUserType);
    
    // Find unread messages for a user
    List<Message> findByReceiverIdAndReceiverTypeAndReadFalse(Long receiverId, UserType receiverType);
    
    // Count total unread messages for a user
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND m.receiverType = :userType AND m.read = false")
    int countUnreadMessages(@Param("userId") Long userId, @Param("userType") UserType userType);
}