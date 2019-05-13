package com.mmall.dao;

import com.mmall.pojo.DzfpItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DzfpItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DzfpItem record);

    int insertSelective(DzfpItem record);

    DzfpItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DzfpItem record);

    int updateByPrimaryKey(DzfpItem record);

    List<DzfpItem> selectAllDzfpItem();

    List<DzfpItem> selectDzfpItemByLsh(String lsh);

    void batchInsert(@Param("dzfpItemList") List<DzfpItem> dzfpItemList);
}