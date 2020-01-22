package com.mobei.member.mapper;

import com.mobei.member.mapper.entity.UserTokenDo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserTokenMapper {

	/**
	 * 根据userid+logintype+is_availability=0进行查询
	 * @param userId
	 * @param loginType
	 * @return
	 */
    @Select("SELECT id as id ,token as token ,login_type as LoginType, device_infor as deviceInfor ,is_availability as isAvailability,user_id as userId"
            + "" + ""
            + " , create_time as createTime,update_time as updateTime FROM meite_user_token WHERE user_id=#{userId} AND login_type=#{loginType} and is_availability ='0'; ")
	UserTokenDo  selectByUserIdAndLoginType(@Param("userId") Long userId, @Param("loginType") String loginType);

	/**
	 * 根据userid+logintype+token的状态修改为不可用
	 * @param userId
	 * @param loginType
	 * @return
	 */
//    @Update("    update meite_user_token set is_availability ='1',update_time=now()   where user_id=#{userId} and login_type =#{loginType} ")
//    int updateTokenAvailability(@Param("userId") Long userId, @Param("loginType") String loginType);

	@Update("update meite_user_token set is_availability ='1',update_time=now() where token =#{token} ")
	int updateTokenAvailability(@Param("token") String token);


	// INSERT INTO `meite_user_token` VALUES ('2', '1', 'PC', '苹果7p', '1', '1');

	/**
	 * token表中添加一条记录
	 * @param userTokenDo
	 * @return
	 */
    @Insert("    INSERT INTO `meite_user_token` VALUES (null, #{token},#{loginType}, #{deviceInfor}, 0, #{userId} ,now(),null ); ")
    int insertUserToken(UserTokenDo userTokenDo);
}
