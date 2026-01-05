package com.internshipfinder.zirah.repository;

import com.internshipfinder.zirah.model.MessageThread;
import com.internshipfinder.zirah.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageThreadRepository extends JpaRepository<MessageThread, Long> {
    
    @Query("SELECT mt FROM MessageThread mt WHERE " +
           "(mt.participant1Id = :userId AND mt.participant1Type = :userType) OR " +
           "(mt.participant2Id = :userId AND mt.participant2Type = :userType) " +
           "ORDER BY mt.lastMessageAt DESC")
    List<MessageThread> findUserThreads(@Param("userId") Long userId, @Param("userType") UserType userType);
    
    Optional<MessageThread> findByParticipant1IdAndParticipant1TypeAndParticipant2IdAndParticipant2Type(
            Long participant1Id, UserType participant1Type, Long participant2Id, UserType participant2Type);
    
    Optional<MessageThread> findByParticipant2IdAndParticipant2TypeAndParticipant1IdAndParticipant1Type(
            Long participant2Id, UserType participant2Type, Long participant1Id, UserType participant1Type);
}