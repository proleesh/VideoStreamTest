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

    @Value("${file.video.hls}")
    String HLS_DIR;

    private final VideoRepository videoRepository;
    ;

    @PostConstruct
    public void init() {
        File file = new File(DIR);


        try {
            Files.createDirectories(Paths.get(HLS_DIR));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!file.exists()) {
            boolean mkdir = file.mkdir();
            System.out.println("풀더 생성: " + mkdir);
        } else {
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


            // metadata save
            Video savedVideo = videoRepository.save(video);

            // processing video
            processVideo(savedVideo.getVideoId());

            // delete actual video file and database entry if exception



            return videoRepository.save(video);

            // metadata save
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Video get(String videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(() -> new RuntimeException("Video not found"));
        return video;
    }

    @Override
    public Video getByTitle(String title) {
        return null;
    }

    @Override
    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    @Override
    public String processVideo(String videoId) {

        Video video = this.get(videoId);
        String filepath = video.getFilePath();

        Path videoPath = Paths.get(filepath);

        String output360p = HLS_DIR + videoId + "/360p/";
        String output720p = HLS_DIR + videoId + "/720p/";
        String output1080p = HLS_DIR + videoId + "/1080p/";



        try {
            Files.createDirectories(Paths.get(output360p));
            Files.createDirectories(Paths.get(output720p));
            Files.createDirectories(Paths.get(output1080p));

            Path outputPath = Paths.get(HLS_DIR, videoId);
            Files.createDirectories(outputPath);

            String ffmpegCmd = String.format(
                    "ffmpeg -i \"%s\" -c:v libx264 -c:a aac -strict -2 -f hls -hls_time 10 -hls_list_size 0 -hls_segment_filename \"%s/segment_%%3d.ts\"  \"%s/master.m3u8\" ",
                    videoPath, HLS_DIR, HLS_DIR
            );

            //ffmpeg command
//            StringBuilder ffmpegCmd = new StringBuilder();
//            ffmpegCmd.append("ffmpeg -i")
//                    .append(videoPath.toString())
//                    .append(" -c:v libx264 -c:a aac").append(" ")
//                    .append("-map 0:v -map 0:a -s:v:0 640x360 -b:v:0 800k")
//                    .append("-map 0:v -map 0:a -s:v:1 1280x720 -b:v:1 2800k")
//                    .append("-map 0:v -map 0:a -s:v:2 1920x1080 -b:v:2 5000k")
//                    .append("-var_stream_map \"v:0,a:0 v:1,a:0 v:2,a:0\" ")
//                    .append("-master_pl_name ").append(HLS_DIR).append(videoId).append("/master.m3u8 ")
//                    .append("-f hls -hls_time 10 -hls_list_size 0 ")
//                    .append("-hls_segment_filename \"").append(HLS_DIR).append(videoId).append("/v%v/fileSequence%d.ts\" ")
//                    .append("\"").append(HLS_DIR).append(videoId).append("/v%v/prog_index.m3u8\"");
            System.out.println(ffmpegCmd);
            //file this command
            ProcessBuilder processBuilder = new ProcessBuilder("/bin/bash", "-c", ffmpegCmd);
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exit = process.waitFor();
            if (exit != 0) {
                throw new RuntimeException("video processing failed!!");
            }

            return videoId;
        } catch (IOException e) {
            throw new RuntimeException("Video processing fail: " + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
