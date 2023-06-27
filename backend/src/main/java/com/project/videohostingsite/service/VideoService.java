package com.project.videohostingsite.service;

import com.project.videohostingsite.dto.CommentDto;
import com.project.videohostingsite.dto.UploadVideoResponse;
import com.project.videohostingsite.dto.VideoDto;
import com.project.videohostingsite.model.Comment;
import com.project.videohostingsite.model.Video;
import com.project.videohostingsite.repository.VideoRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;


@Service
@RequiredArgsConstructor
public class VideoService {

    //Initialize S3 service
    private final S3Service s3Service;

    //Initalize User service
    private final UserService userService;

    //Initialize Mongo Repo
    private final VideoRespository videoRespository;

    public UploadVideoResponse uploadVideo(MultipartFile multipartFile){
        //Upload video, retrieve url and set url to Video object url
        String videoUrl= s3Service.uploadFile(multipartFile);
        var video = new Video();
        video.setVideoUrl(videoUrl);

        //save video info to DB
        var savedVideo= videoRespository.save(video);
        return new UploadVideoResponse(savedVideo.getId(), savedVideo.getVideoUrl());
    }

    // Maps data from video dto obj to video obj
    // Then saves video obj to DB
    public VideoDto editVideo(VideoDto videoDto) {
        var savedVideo= getVideoById(videoDto.getId());
        //Map video dto fields to video object
        savedVideo.setTitle(videoDto.getTitle());
        savedVideo.setDescription(videoDto.getDescription());
        savedVideo.setTags(videoDto.getTags());
        savedVideo.setThumbnailUrl(videoDto.getThumbnailUrl());
        savedVideo.setVideoStatus(videoDto.getVideoStatus());
        //Save video to DB
        videoRespository.save(savedVideo);
        return videoDto;
    }

    // Finds video by ID, uploads thumbnail url and saves thumbnail details to video in DB
    public String uploadThumbnail(MultipartFile file, String videoId) {
        var savedVideo = getVideoById(videoId);
        String thumbnailUrl = s3Service.uploadFile(file);

        savedVideo.setThumbnailUrl(thumbnailUrl);
        videoRespository.save(savedVideo);
        return thumbnailUrl;
    }

    Video getVideoById(String videoID){
        //Find video by ID
        return videoRespository.findById(videoID)
                .orElseThrow(() -> new IllegalArgumentException("Cannot find video by ID - "+videoID));
    }

    // Fetches video details by ID and saves them to DTO object
    public VideoDto getVideoDetails(String videoId){
        Video savedVideo = getVideoById(videoId);

        increaseVideoCount(savedVideo);
        userService.addVideoToHistory(videoId);

        return mapToVideoDto(savedVideo);
    }

    private VideoDto mapToVideoDto(Video savedVideo) {
        VideoDto videoDto = new VideoDto();
        videoDto.setVideoUrl(savedVideo.getVideoUrl());
        videoDto.setThumbnailUrl(savedVideo.getThumbnailUrl());
        videoDto.setId(savedVideo.getId());
        videoDto.setTitle(savedVideo.getTitle());
        videoDto.setDescription(savedVideo.getDescription());
        videoDto.setTags(savedVideo.getTags());
        videoDto.setVideoStatus(savedVideo.getVideoStatus());
        videoDto.setLikeCount(savedVideo.getLikes().get());
        videoDto.setDislikeCount((savedVideo.getDislikes().get()));
        videoDto.setViewCount(savedVideo.getViewCount().get());

        return videoDto;
    }

    private void increaseVideoCount(Video savedVideo) {
        savedVideo.incrementViewCount();
        videoRespository.save(savedVideo);
    }

    public VideoDto likeVideo(String videoId) {
        //Get video by ID
        Video videoById = getVideoById(videoId);


        //If already liked decrement
        if(userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            //If already disliked, increment like, decrement dislike
        }else if(userService.ifDisLikedVideo(videoId)){
            videoById.decrementdisLikes();
            userService.removeFromDisLikedVideos(videoId);
            //Increment Like and add to liked videos list
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }else{
            //Increment Like and add to liked videos list
            videoById.incrementLikes();
            userService.addToLikedVideos(videoId);
        }

        videoRespository.save(videoById);

        return mapToVideoDto(videoById);
    }

    public VideoDto disLikeVideo(String videoId) {
        //Get video by ID
        Video videoById = getVideoById(videoId);


        //If already disliked decrement
        if(userService.ifDisLikedVideo(videoId)){
            videoById.decrementdisLikes();
            userService.removeFromDisLikedVideos(videoId);
            //If already liked, increment dislike, decrement like
        }else if(userService.ifLikedVideo(videoId)){
            videoById.decrementLikes();
            userService.removeFromLikedVideos(videoId);
            //Increment dislike and add to disliked videos list
            videoById.incrementdisLikes();
            userService.addToDisLikedVideos(videoId);
        }else{
            //Increment dislike and add to disliked videos list
            videoById.incrementdisLikes();
            userService.addToDisLikedVideos(videoId);
        }

        videoRespository.save(videoById);

        return mapToVideoDto(videoById);
    }

    public void addComment(String videoId, CommentDto commentDto) {
        Video video = getVideoById(videoId);
        Comment comment = new Comment();
        comment.setText(commentDto.getCommentText());
        comment.setAuthorId(commentDto.getAuthorId());
        video.addComment(comment);

        videoRespository.save(video);
    }

    public List<CommentDto> getAllComments(String videoId) {
        Video video = getVideoById(videoId);
        List<Comment> commentList = video.getCommentList();

        Collections.reverse(commentList);

        return commentList.stream().map(this::mapToCommentDto).toList();
    }

    private CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentText(comment.getText());
        commentDto.setAuthorId(comment.getAuthorId());
        return commentDto;
    }

    public List<VideoDto> getAllVideos() {
        return videoRespository.findAll().stream().map(this::mapToVideoDto).toList();
    }
}
