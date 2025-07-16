package com.example.demo.mapper;

import com.example.demo.entity.AccessDevice;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

/**
 * 门禁设备的MyBatis映射接口
 * 提供对access_device表的增删改查操作
 */
@Mapper
public interface AccessDeviceMapper {

    /**
     * 新增门禁设备
     * @param device 设备实体
     * @return 影响行数
     */
    @Insert("INSERT INTO access_device(device_id, device_name, device_type, location, status, remark) " +
            "VALUES(#{deviceId}, #{deviceName}, #{deviceType}, #{location}, #{status}, #{remark})")
    int insert(AccessDevice device);

    /**
     * 更新设备信息
     * @param device 设备实体
     * @return 影响行数
     */
    @Update("UPDATE access_device SET " +
            "device_name = #{deviceName}, " +
            "device_type = #{deviceType}, " +
            "location = #{location}, " +
            "status = #{status}, " +
            "remark = #{remark} " +
            "WHERE device_id = #{deviceId}")
    int update(AccessDevice device);

    /**
     * 根据ID删除设备
     * @param deviceId 设备ID
     * @return 影响行数
     */
    @Delete("DELETE FROM access_device WHERE device_id = #{deviceId}")
    int delete(@Param("deviceId") String deviceId);

    /**
     * 根据ID查询设备
     * @param deviceId 设备ID
     * @return 设备实体
     */
    @Select("SELECT * FROM access_device WHERE device_id = #{deviceId}")
    AccessDevice selectById(@Param("deviceId") String deviceId);

    /**
     * 查询所有设备
     * @return 设备列表
     */
    @Select("SELECT * FROM access_device ORDER BY create_time DESC")
    List<AccessDevice> selectAll();

    /**
     * 根据状态查询设备
     * @param status 设备状态
     * @return 设备列表
     */
    @Select("SELECT * FROM access_device WHERE status = #{status} ORDER BY device_name")
    List<AccessDevice> selectByStatus(@Param("status") Integer status);

    /**
     * 条件查询设备
     * @param params 查询参数
     * @return 设备列表
     */
    List<AccessDevice> selectByCondition(@Param("params") Map<String, Object> params);

    /**
     * 统计各状态设备数量
     * @return 统计结果
     */
    @Select("SELECT status, COUNT(*) as count FROM access_device GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 批量更新设备状态
     * @param deviceIds 设备ID列表
     * @param status 目标状态
     * @return 影响行数
     */
    int batchUpdateStatus(@Param("deviceIds") List<String> deviceIds,
                          @Param("status") Integer status);
}