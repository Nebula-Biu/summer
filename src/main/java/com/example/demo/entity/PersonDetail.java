package com.example.demo.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 人员详细信息实体类
 * 对应person_detail表，包含姓名、性别、身份证号、联系方式等
 */
@Data
@Entity
@Table(name = "person_detail")
@Schema(description = "人员详细信息")
public class PersonDetail {
    @MapsId
    @OneToOne
    @JoinColumn(name = "person_id")
    @Schema(description = "关联的人员对象")
    private Person person;

    @Id
    @Schema(description = "关联人员ID", example = "1234567890123456789")
    private Long personId;

    @Column(nullable = false, length = 50)
    @Schema(description = "姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "性别:0-女,1-男,2-其他", example = "1")
    private Integer gender;

    @Column(unique = true, length = 18)
    @Schema(description = "身份证号", example = "110101199003072345")
    private String idCard;

    @Column(length = 20)
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Column(length = 50)
    @Schema(description = "身份/职位", example = "员工")
    private String position;

    @Column(updatable = false)
    @Schema(description = "注册时间", example = "2023-01-01T00:00:00")
    private LocalDateTime registerTime;

    @Schema(description = "更新时间", example = "2023-01-01T00:00:00")
    private LocalDateTime updateTime;

    @Column(columnDefinition = "TINYINT DEFAULT 1")
    @Schema(description = "状态:0-禁止,1-可通行", example = "1")
    private Integer status;
}