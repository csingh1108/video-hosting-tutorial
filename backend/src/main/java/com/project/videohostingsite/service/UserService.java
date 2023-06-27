package com.project.videohostingsite.service;

import com.project.videohostingsite.model.User;
import com.project.videohostingsite.model.Video;
import com.project.videohostingsite.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser(){
        String sub = ((Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getClaim("sub");

        return userRepository.findBySub(sub)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find user with sub - "+sub));

    }

    public void addToLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public boolean ifLikedVideo(String videoId){
        return getCurrentUser().getLikedVideos().stream().anyMatch(likedVideo -> likedVideo.equals(videoId));
    }

    public boolean ifDisLikedVideo(String videoId){
        return getCurrentUser().getDislikedVideos().stream().anyMatch(dislikedVideo -> dislikedVideo.equals(videoId));
    }

    public void removeFromLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void removeFromDisLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.removeFromDisLikedVideos(videoId);
        userRepository.save(currentUser);

    }

    public void addToDisLikedVideos(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addToDisLikedVideos(videoId);
        userRepository.save(currentUser);
    }

    public void addVideoToHistory(String videoId) {
        User currentUser = getCurrentUser();
        currentUser.addtoVideoHistory(videoId);
        userRepository.save(currentUser);
    }

    public void subscribeUser(String userId){
        // Get current user and add user id to subscribed users set.
        User currentUser = getCurrentUser();
        currentUser.suscribeToUser(userId);
        // Get target user and add current user to subscriber set.
        User user = getUserById(userId);
        user.addToSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    public void unSubscribeUser(String userId){
        // Get current user and remove user id to subscribed users set.
        User currentUser = getCurrentUser();
        currentUser.unSuscribeToUser(userId);
        // Get target user and remove current user to subscriber set.
        User user = getUserById(userId);
        user.removeSubscribers(currentUser.getId());

        userRepository.save(currentUser);
        userRepository.save(user);
    }

    //Returns video history from user
    public Set<String> userHistory(String userId) {
        User user = getUserById(userId);
        return user.getVideoHistory();

    }

    //finds user by Id
    private User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("Cannot find user with id - "+ userId));
    }
}
