package org.proleesh.services;

import org.proleesh.entity.Video;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;

public interface VideoService {
    // save video
    Video save(Video vide, MultipartFile file);

    // get video by id
    Video get(String videoId);


    // get video by title
    Video getByTitle(String title);

    List<Video> getAllVideos();

    // video processing
    String processVideo(String videoId);
}
