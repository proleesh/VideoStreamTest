package org.proleesh.services.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.proleesh.entity.Video;
import org.proleesh.repository.VideoRepository;
import org.proleesh.services.VideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    @Value("${files.video}")
    String DIR;

    private final VideoRepository videoRepository;;

    @PostConstruct
    public void init(){
        File file = new File(DIR);

        if(!file.exists()){
            boolean mkdir = file.mkdir();
            System.out.println("풀더 생성: " + mkdir);
        }else{
            System.out.println("풀더 이미 생성");
        }
    }

    @Override
    public Video save(Video video, MultipartFile file) {

        try {
            // original file name
            String filename = file.getOriginalFilename();
            String contentType = file.getContentType();
            InputStream inputStream = file.getInputStream();

            // folder path : create
            String cleanFileName = StringUtils.cleanPath(filename);
            String cleanFolder = StringUtils.cleanPath(DIR);


            // folder path with filename
            Path path = Paths.get(cleanFolder, cleanFileName);
            System.out.println(contentType);
            System.out.println(path);

            // copy file to the folder
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);


            // video meta data
            video.setContentType(contentType);
            video.setFilePath(path.toString());

            return videoRepository.save(video);

            // metadata save
        }catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Video get(String videoId) {
        return null;
    }

    @Override
    public Video getByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAllVideos() {
        return List.of();
    }
}
