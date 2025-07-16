package com.example.demo.mapper;

import com.example.demo.entity.AccessLog;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 通行记录的MyBatis映射接口
 * 提供对access_log表的增删改查操作
 */
@Mapper
public interface AccessLogMapper {

    /**
     * 插入通行记录
     * @param accessLog 通行记录实体
     * @return 影响行数
     */
    @Insert("INSERT INTO access_log(person_id, access_time, access_type, result, " +
            "temperature, confidence, capture_image, device_id) " +
            "VALUES(#{personId}, #{accessTime}, #{accessType}, #{result}, " +
            "#{temperature}, #{confidence}, #{captureImage}, #{deviceId})")
    int insert(AccessLog accessLog);

    /**
     * 批量插入通行记录
     * @param logs 通行记录列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<AccessLog> logs);

    /**
     * 根据ID查询通行记录
     * @param id 记录ID
     * @return 通行记录
     */
    @Select("SELECT * FROM access_log WHERE id = #{id}")
    AccessLog selectById(Long id);

    /**
     * 根据人员ID查询通行记录
     * @param personId 人员ID
     * @return 通行记录列表
     */
    @Select("SELECT * FROM access_log WHERE person_id = #{personId} ORDER BY access_time DESC LIMIT 100")
    List<AccessLog> selectByPersonId(Long personId);

    /**
     * 根据设备ID查询通行记录
     * @param deviceId 设备ID
     * @param limit 查询条数
     * @return 通行记录列表
     */
    List<AccessLog> selectByDeviceId(@Param("deviceId") String deviceId,
                                     @Param("limit") int limit);

    /**
     * 按时间范围查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 通行记录列表
     */
    List<AccessLog> selectByTimeRange(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各设备通行次数
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    @MapKey("deviceId")
    List<Map<String, Integer>> countByDevice(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 查询异常通行记录(识别置信度低于阈值)
     * @param threshold 置信度阈值
     * @return 异常记录列表
     */
    @Select("SELECT * FROM access_log WHERE confidence < #{threshold} ORDER BY access_time DESC")
    List<AccessLog> selectAbnormalRecords(float threshold);
}