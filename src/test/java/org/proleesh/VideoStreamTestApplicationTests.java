package org.proleesh;

import org.junit.jupiter.api.Test;
import org.proleesh.services.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VideoStreamTestApplicationTests {

    @Autowired
    private VideoService videoService;
    @Test
    void contextLoads() {
        videoService.processVideo("884d600a-2ab7-4740-93a1-2803b544ac40");
    }

}
