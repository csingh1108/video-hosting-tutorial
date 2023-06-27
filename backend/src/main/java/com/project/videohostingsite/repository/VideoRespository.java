package com.project.videohostingsite.repository;

import com.project.videohostingsite.model.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRespository extends MongoRepository<Video, String> {
}
