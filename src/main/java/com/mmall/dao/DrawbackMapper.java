package com.mmall.dao;

import com.mmall.pojo.Drawback;
import org.apache.ibatis.annotations.Param;
import sun.util.resources.ga.LocaleNames_ga;

public interface DrawbackMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Drawback record);

    int insertSelective(Drawback record);

    Drawback selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Drawback record);

    int updateByPrimaryKey(Drawback record);

    Drawback selectByUserIdAndOrderNo(@Param("userId") Integer userId,@Param("orderNo") Long orderNo);
}