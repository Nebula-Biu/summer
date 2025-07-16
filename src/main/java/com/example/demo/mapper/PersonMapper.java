package com.example.demo.mapper;

import com.example.demo.entity.Person;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 人员基础信息的MyBatis映射接口
 * 提供对person表的增删改查操作
 */
@Mapper
public interface PersonMapper {

    /**
     * 根据ID查询人员信息
     * @param id 人员ID
     * @return 人员实体
     */
    @Select("SELECT * FROM person WHERE id = #{id}")
    Person getPersonById(@Param("id") Long id);

    /**
     * 插入人员信息
     * @param person 人员实体
     * @return 影响的行数
     */
    @Insert("INSERT INTO person(id, name) VALUES(#{id}, #{name})")
    @Options(useGeneratedKeys = false) // 使用雪花ID，不自增
    int insertPerson(Person person);

    /**
     * 更新人员信息
     * @param person 人员实体
     * @return 影响的行数
     */
    @Update("UPDATE person SET name = #{name} WHERE id = #{id}")
    int updatePerson(Person person);

    /**
     * 删除人员信息
     * @param id 人员ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM person WHERE id = #{id}")
    int deletePerson(@Param("id") Long id);

    /**
     * 查询所有人员信息
     * @return 人员列表
     */
    @Select("SELECT * FROM person")
    List<Person> getAllPersons();

    /**
     * 根据姓名模糊查询
     * @param name 姓名关键词
     * @return 人员列表
     */
    List<Person> getPersonsByName(@Param("name") String name);
}