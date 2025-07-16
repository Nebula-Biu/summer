package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人脸数据实体类
 * 对应face_data表，存储人脸特征、图片等
 */
@Data
@Entity
@Table(name = "face_data")
@Schema(description = "人脸特征数据")
public class FaceData {

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    @Schema(description = "关联的人员ID", example = "1234567890123456789")
    private Person person;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "人脸数据ID")
    private Long id;

    @Lob
    @Column(nullable = false)
    @Schema(description = "人脸特征数据(二进制)")
    private byte[] featureData;

    @Column(length = 255)
    @Schema(description = "人脸图片存储路径", example = "/images/faces/123.jpg")
    private String imagePath;

    @Column(updatable = false)
    @Schema(description = "注册时间", example = "2023-01-01T00:00:00")
    private LocalDateTime registerTime;

    @Schema(description = "人脸质量评分(0-1)", example = "0.95")
    private Float qualityScore;

    @Column(length = 20)
    @Schema(description = "特征提取算法版本", example = "1.0.0")
    private String version;
}