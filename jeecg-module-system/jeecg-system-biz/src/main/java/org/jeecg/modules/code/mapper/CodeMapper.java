package org.jeecg.modules.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.code.entity.CodeEntity;

@Mapper
public interface CodeMapper extends BaseMapper<CodeEntity> {

    @Insert("insert into code (flag,count,create_time,expire_time,code,customer_num,bind_user,update_time) value (#{flag},#{count},now(),date_add(now(),interval 2 hour ) ,#{code},#{customerNum},#{bindUser},now()   ) ")
    void createFlag(@Param("flag") String flag, @Param("count") Integer count, @Param("code") String code, int customerNum,@Param("bindUser") String bindUser);

    @Select("select * from code where flag = #{flag} and code = #{code}  order by code_id desc limit 1 ")
    CodeEntity getByFlag(@Param("flag") String flag,@Param("code") String code);

    @Select("select count(1) from code where flag = #{flag} and code = #{code}")
    Integer count(@Param("flag") String flag,@Param("code") String code);

    @Select("select url_id from code_url where code= #{code} and expire_time > now()")
    Integer checkUrlCode(String code);

    @Update("update code set phone = #{phone}  where flag = #{flag} and code =#{code} and finished = 0")
    void updatePhone(@Param("flag") String flag,@Param("code") String code,@Param("phone") String phone);

    @Update("update code set validate_code = #{validate_code} where flag = #{flag} and code =#{code} and finished = 0")
    void updateValidateCode(@Param("flag") String flag,@Param("code") String code,@Param("validate_code") String validate_code);


    @Update("update code set phone2 = #{phone}  where flag = #{flag} and code =#{code} and finished = 0")
    void updatePhone2(@Param("flag") String flag,@Param("code") String code,@Param("phone") String phone);

    @Update("update code set validate_code2 = #{validate_code} where flag = #{flag} and code =#{code} and finished = 0")
    void updateValidateCode2(@Param("flag") String flag,@Param("code") String code,@Param("validate_code") String validate_code);


    @Select("select customer_num from code where flag =#{flag} limit 1 ")
    Integer existsFlag(String flag);

    @Select("select count(1) from code group by flag ")
    Integer customerCount(String remoteAddr);

    @Update("update code set update_time = now() where code_id=#{codeId}")
    void updateTime(Integer codeId);

    @Select("select bind_user from code_url where code = #{code} ")
    String getCodeBindUser(String code);

    @Insert("insert into code_url (code,bind_user,expire_time) values (#{code},#{username} ,date_add(now(),interval  48 hour ) )")
    void createUrl(@Param("username") String username, @Param("code") String code);

    @Update("update code set code_status =#{codeStatus}  where code_id =#{codeId} ")
    void updateCodeStatus(@Param("codeId") Integer codeId, @Param("codeStatus") int codeStatus);

    @Update("update code set finished=1 where code_id =#{codeId}")
    void finish(Integer codeId);
}
