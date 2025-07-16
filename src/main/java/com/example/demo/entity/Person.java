package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 人员基础信息实体类
 * 对应person表，包含人员ID、姓名等
 */
@Data
@Entity
@Table(name = "person")
@Schema(description = "人员基础信息")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "雪花算法生成的ID", example = "1234567890123456789")
    private Long id;

    @Column(nullable = false, length = 50)
    @Schema(description = "姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    // 一对一关联详细信息
    @OneToOne(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "人员详细信息")
    private PersonDetail detail;

    // 一对多关联人脸数据
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Schema(description = "人脸特征数据列表")
    private List<FaceData> faceDataList;
}