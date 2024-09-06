package org.proleesh.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

/**
 * @author sung-hyuk lee
 * date: 2024.09.06
 * description: About video entity
 */
@Entity
@Table(name = "videos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {

    @Id
    private String videoId;
    private String title;
    private String description;
    private String contentType;
    private String filePath;

    @ManyToOne
    private Course course;

}
