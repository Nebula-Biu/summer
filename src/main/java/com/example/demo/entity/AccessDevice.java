package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 门禁设备实体类
 * 对应access_device表，记录设备信息
 */
@Data
@Entity
@Table(name = "access_device")
@Schema(description = "门禁设备信息")
public class AccessDevice {

    @Id
    @Column(length = 36)
    @Schema(description = "设备唯一ID", example = "d3b8a1f2-5c4d-4e7f-9g0h-1i2j3k4l5m6n")
    private String deviceId;

    @Column(nullable = false, length = 100)
    @Schema(description = "设备名称", example = "东门入口闸机", requiredMode = Schema.RequiredMode.REQUIRED)
    private String deviceName;

    @Column(nullable = false)
    @Schema(description = "设备类型:1-入口,2-出口,3-双向", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer deviceType;

    @Column(nullable = false, length = 200)
    @Schema(description = "安装位置", example = "大楼东门", requiredMode = Schema.RequiredMode.REQUIRED)
    private String location;

    @Column(nullable = false, columnDefinition = "TINYINT DEFAULT 1")
    @Schema(description = "状态:0-离线,1-在线,2-故障", example = "1")
    private Integer status;

    @Column(updatable = false, nullable = false)
    @Schema(description = "创建时间", example = "2023-01-01T00:00:00")
    private LocalDateTime createTime;

    @Column(nullable = false)
    @Schema(description = "更新时间", example = "2023-01-01T00:00:00")
    private LocalDateTime updateTime;

    @Column(length = 200)
    @Schema(description = "备注信息", example = "2023年新安装的设备")
    private String remark;

    @OneToMany(mappedBy = "device",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @Schema(description = "通过数据")
    private List<AccessLog> accessLogs;

}