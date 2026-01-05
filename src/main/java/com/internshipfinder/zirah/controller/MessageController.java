package com.internshipfinder.zirah.controller;

import com.internshipfinder.zirah.model.*;
import com.internshipfinder.zirah.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Main messages page - SIMPLE
    @GetMapping
    public String showMessages(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        UserType userType = getUserType(user);
        Long userId = getUserId(user);

        List<MessageService.MessageThreadDTO> threads = 
            messageService.getUserThreadsWithPreview(userId, userType);
        
        model.addAttribute("threads", threads != null ? threads : List.of());
        model.addAttribute("userType", userType.name());
        return "messages";
    }

    // Compose message page - SIMPLE
    @GetMapping("/compose")
    public String showComposeForm(@RequestParam(required = false) Long toUserId,
                                @RequestParam(required = false) String toUserType,
                                HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        // Set basic attributes
        model.addAttribute("toUserId", toUserId);
        model.addAttribute("toUserType", toUserType);
        
        // Get recipient name if available
        if (toUserId != null && toUserType != null) {
            try {
                UserType userTypeEnum = UserType.valueOf(toUserType.toUpperCase());
                String userName = messageService.getUserName(toUserId, userTypeEnum);
                model.addAttribute("toUserName", userName != null ? userName : "User");
            } catch (Exception e) {
                model.addAttribute("toUserName", "User");
            }
        }

        return "compose-message";
    }

    // Send message - SIMPLE
    @PostMapping("/send")
    public String sendMessage(@RequestParam Long receiverId,
                            @RequestParam String receiverType,
                            @RequestParam String content,
                            HttpSession session) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        // Basic validation
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/messages/compose?error=empty_message&toUserId=" + receiverId + "&toUserType=" + receiverType;
        }

        try {
            UserType senderType = getUserType(user);
            Long senderId = getUserId(user);
            UserType receiverUserType = UserType.valueOf(receiverType.toUpperCase());

            Message message = new Message();
            message.setSenderId(senderId);
            message.setSenderType(senderType);
            message.setReceiverId(receiverId);
            message.setReceiverType(receiverUserType);
            message.setContent(content.trim());

            messageService.sendMessage(message);

            return "redirect:/messages/thread/" + receiverId + "/" + receiverType.toUpperCase() + "?success=true";
        } catch (Exception e) {
            return "redirect:/messages/compose?error=send_failed&toUserId=" + receiverId + "&toUserType=" + receiverType;
        }
    }

    // View conversation - SIMPLE
    @GetMapping("/thread/{otherUserId}/{otherUserType}")
    public String showThread(@PathVariable Long otherUserId, 
                           @PathVariable String otherUserType,
                           HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user == null) return "redirect:/signin";

        try {
            UserType currentUserType = getUserType(user);
            Long currentUserId = getUserId(user);
            UserType otherType = UserType.valueOf(otherUserType.toUpperCase());

            List<Message> conversation = messageService.getConversation(
                currentUserId, currentUserType, otherUserId, otherType);
            
            String otherUserName = messageService.getUserName(otherUserId, otherType);
            
            model.addAttribute("conversation", conversation != null ? conversation : List.of());
            model.addAttribute("otherUserId", otherUserId);
            model.addAttribute("otherUserType", otherUserType);
            model.addAttribute("otherUserName", otherUserName != null ? otherUserName : "User");
            model.addAttribute("currentUserId", currentUserId);
            model.addAttribute("currentUserType", currentUserType.name());

            return "message-thread";
        } catch (Exception e) {
            return "redirect:/messages?error=conversation_error";
        }
    }

    // Helper methods
    private UserType getUserType(Object user) {
        if (user instanceof Student) return UserType.STUDENT;
        if (user instanceof Recruiter) return UserType.RECRUITER;
        throw new IllegalArgumentException("Unknown user type");
    }

    private Long getUserId(Object user) {
        if (user instanceof Student) return ((Student) user).getId();
        if (user instanceof Recruiter) return ((Recruiter) user).getId();
        throw new IllegalArgumentException("Cannot get user ID");
    }
}