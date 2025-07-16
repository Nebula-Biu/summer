package com.example.demo.mapper;

import com.example.demo.entity.PersonDetail;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 人员详细信息的MyBatis映射接口
 * 提供对person_detail表的增删改查操作
 */
@Mapper
public interface PersonDetailMapper {

    /**
     * 插入人员详细信息
     * @param detail 人员详细信息
     * @return 影响的行数
     */
    @Insert("INSERT INTO person_detail(person_id, name, gender, id_card, phone, position, status) " +
            "VALUES(#{personId}, #{name}, #{gender}, #{idCard}, #{phone}, #{position}, #{status})")
    @Options(useGeneratedKeys = false) // 不使用自增主键
    int insert(PersonDetail detail);

    /**
     * 更新人员详细信息
     * @param detail 人员详细信息
     * @return 影响的行数
     */
    @Update("UPDATE person_detail SET " +
            "name = #{name}, " +
            "gender = #{gender}, " +
            "id_card = #{idCard}, " +
            "phone = #{phone}, " +
            "position = #{position}, " +
            "status = #{status} " +
            "WHERE person_id = #{personId}")
    int update(PersonDetail detail);

    /**
     * 根据人员ID查询详细信息
     * @param personId 人员ID
     * @return 人员详细信息
     */
    @Select("SELECT * FROM person_detail WHERE person_id = #{personId}")
    PersonDetail selectByPersonId(Long personId);

    /**
     * 根据身份证号查询
     * @param idCard 身份证号
     * @return 人员详细信息
     */
    @Select("SELECT * FROM person_detail WHERE id_card = #{idCard}")
    PersonDetail selectByIdCard(String idCard);

    /**
     * 根据姓名精确查询
     * @param name 姓名
     * @return 人员详细信息
     */
    @Select("SELECT * FROM person_detail WHERE name = #{name}")
    PersonDetail selectByName(String name);

    /**
     * 删除人员详细信息
     * @param personId 人员ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM person_detail WHERE person_id = #{personId}")
    int delete(Long personId);

    /**
     * 批量查询人员详细信息
     * @param personIds 人员ID列表
     * @return 人员详细信息列表
     */
    List<PersonDetail> selectBatch(@Param("personIds") List<Long> personIds);

    /**
     * 分页查询人员详细信息（使用XML resultMap，字段与文档一致）
     * @param offset 偏移量
     * @param size 每页数量
     * @return 人员详细信息列表
     */
    @ResultMap("personDetailResultMap")
    @Select("SELECT * FROM person_detail ORDER BY register_time DESC LIMIT #{size} OFFSET #{offset}")
    List<PersonDetail> selectPaged(@Param("offset") int offset, @Param("size") int size);
}