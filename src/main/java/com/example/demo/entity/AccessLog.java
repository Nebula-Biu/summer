package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通行记录实体类
 * 对应access_log表，记录门禁通行信息
 */
@Data
@Entity
@Table(name = "access_log")
@Schema(description = "门禁通行记录")
public class AccessLog {

    @ManyToOne
    @JoinColumn(name = "device_id")
    @Schema(description = "通行设备信息")
    private AccessDevice device;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "记录ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "person_id")
    @Schema(description = "识别到的人员信息")
    private Person person;

    @Column(nullable = false)
    @Schema(description = "通行时间", example = "2023-01-01T12:00:00")
    private LocalDateTime accessTime;

    @Schema(description = "识别方式:1-人脸,2-刷卡,3-混合", example = "1")
    private Integer accessType;

    @Schema(description = "结果:0-拒绝,1-通过", example = "1")
    private Integer result;

    @Schema(description = "检测体温(℃)", example = "36.5")
    private Float temperature;

    @Schema(description = "识别置信度(0-1)", example = "0.98")
    private Float confidence;

    @Column(length = 255)
    @Schema(description = "抓拍图片路径", example = "/images/captures/123.jpg")
    private String captureImage;

    public void setDevice(AccessDevice device) {
        this.device = device;
    }
    public String getDeviceId() {
        return device != null ? device.getDeviceId() : null;
    }

    public Long getPersonId() {
        return person != null ? person.getId() : null;
    }
}