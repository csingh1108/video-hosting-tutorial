package com.project.videohostingsite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(value = "User")
public class User {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String emailAddress;
    private String sub;
    private Set<String> subscribedToUsers = ConcurrentHashMap.newKeySet();
    private Set<String> subscribers = ConcurrentHashMap.newKeySet();
    private Set<String> videoHistory = ConcurrentHashMap.newKeySet();
    private Set<String> likedVideos = ConcurrentHashMap.newKeySet();
    private Set<String> dislikedVideos = ConcurrentHashMap.newKeySet();

    public void addToLikedVideos(String videoId) {
        likedVideos.add(videoId);
    }

    public void removeFromLikedVideos(String videoId) {
        likedVideos.remove(videoId);
    }

    public void addToDisLikedVideos(String videoId) {
        dislikedVideos.add(videoId);
    }

    public void removeFromDisLikedVideos(String videoId) {
        dislikedVideos.remove(videoId);
    }

    public void addtoVideoHistory(String videoId) {
        videoHistory.add(videoId);
    }

    public void suscribeToUser(String userId) {
        subscribedToUsers.add(userId);
    }

    public void addToSubscribers(String id) {
        subscribers.add(id);
    }

    public void unSuscribeToUser(String userId) {
        subscribedToUsers.remove(userId);
    }

    public void removeSubscribers(String id) {
        subscribers.remove(id);
    }
}
