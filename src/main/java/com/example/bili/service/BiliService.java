package com.example.bili.service;


import com.example.bili.entity.Bili;

import java.util.List;

public interface BiliService {
    /**
     * 查询是不是有今天的记录
     */
    Bili selectToady(String date);

    /**
     * 添加今日的记录
     */
    int addLine(String date,int count);

    /**
     * 更新今日的下载数据
     */
    int updateCount(String date,int count);

    /**
     * 获取所有天的记录
     */
    List<Bili> getBilis();
}
