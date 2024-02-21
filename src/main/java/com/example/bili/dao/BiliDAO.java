package com.example.bili.dao;


import com.example.bili.entity.Bili;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiliDAO {
    Bili selectToday(String date);

    int addLine(String date, int count);

    int updateCount(String date,int count);

    List<Bili> getBilis();
}
