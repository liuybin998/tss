package com.baizhi.dao;

import com.baizhi.entity.Poet;
import com.baizhi.entity.Poetry;

import java.util.List;

public interface PoetryDao {

    //查询素有唐诗一级作者信息
    List<Poetry> showAll();
}
