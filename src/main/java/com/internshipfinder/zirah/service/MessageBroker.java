
package com.internshipfinder.zirah.service;

import com.internshipfinder.zirah.model.Message;
import com.internshipfinder.zirah.model.UserType;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class MessageBroker {
    
    private final Map<String, List<DeferredResult<Message>>> subscribers = new ConcurrentHashMap<>();
    
    private final Map<String, List<Message>> offlineMessages = new ConcurrentHashMap<>();
    

    public void subscribe(Long userId, UserType userType, DeferredResult<Message> deferredResult) {
        String userKey = getUserKey(userId, userType);
        
        subscribers.computeIfAbsent(userKey, k -> new CopyOnWriteArrayList<>())
                  .add(deferredResult);
        
        // Send any pending offline messages
        deliverOfflineMessages(userId, userType);
 
        deferredResult.onTimeout(() -> {
            unsubscribe(userId, userType, deferredResult);
            deferredResult.setErrorResult(new RuntimeException("Timeout"));
        });

        deferredResult.onCompletion(() -> unsubscribe(userId, userType, deferredResult));
    }
   
    public void unsubscribe(Long userId, UserType userType, DeferredResult<Message> deferredResult) {
        String userKey = getUserKey(userId, userType);
        List<DeferredResult<Message>> userSubscribers = subscribers.get(userKey);
        if (userSubscribers != null) {
            userSubscribers.remove(deferredResult);
            if (userSubscribers.isEmpty()) {
                subscribers.remove(userKey);
            }
        }
    }
  
    public void publish(Message message) {
        String recipientKey = getUserKey(message.getReceiverId(), message.getReceiverType());

        List<DeferredResult<Message>> recipientSubscribers = subscribers.get(recipientKey);
        boolean delivered = false;
        
        if (recipientSubscribers != null && !recipientSubscribers.isEmpty()) {
            Iterator<DeferredResult<Message>> iterator = recipientSubscribers.iterator();
            while (iterator.hasNext()) {
                DeferredResult<Message> subscriber = iterator.next();
                if (subscriber.isSetOrExpired()) {
                    iterator.remove();
                } else {
                    try {
                        subscriber.setResult(message);
                        delivered = true;
                        break;
                    } catch (Exception e) {
                        iterator.remove();
                    }
                }
            }
        }

        if (!delivered) {
            storeOfflineMessage(message);
        }
    }
    
    private void storeOfflineMessage(Message message) {
        String recipientKey = getUserKey(message.getReceiverId(), message.getReceiverType());
        offlineMessages.computeIfAbsent(recipientKey, k -> new CopyOnWriteArrayList<>())
                      .add(message);
    }
 
    private void deliverOfflineMessages(Long userId, UserType userType) {
        String userKey = getUserKey(userId, userType);
        List<Message> pendingMessages = offlineMessages.get(userKey);
        
        if (pendingMessages != null && !pendingMessages.isEmpty()) {
            List<DeferredResult<Message>> userSubscribers = subscribers.get(userKey);
            if (userSubscribers != null && !userSubscribers.isEmpty()) {

                for (Message message : pendingMessages) {
                    for (DeferredResult<Message> subscriber : userSubscribers) {
                        if (!subscriber.isSetOrExpired()) {
                            try {
                                subscriber.setResult(message);
                                break;
                            } catch (Exception e) {
                                // Ignore and try next subscriber
                            }
                        }
                    }
                }
                offlineMessages.remove(userKey);
            }
        }
    }
    
    /**
     * Get unread message count for a user
     */
    public int getUnreadMessageCount(Long userId, UserType userType) {
        String userKey = getUserKey(userId, userType);
        List<Message> pendingMessages = offlineMessages.get(userKey);
        return pendingMessages != null ? pendingMessages.size() : 0;
    }
    
    public boolean isUserOnline(Long userId, UserType userType) {
        String userKey = getUserKey(userId, userType);
        return subscribers.containsKey(userKey) && 
               !subscribers.get(userKey).isEmpty();
    }

    public List<String> getOnlineUsers() {
        return new ArrayList<>(subscribers.keySet());
    }
    
    private String getUserKey(Long userId, UserType userType) {
        return userId + "_" + userType.name();
    }
}