package com.example.bili.service.impl;

import com.example.bili.dao.BiliDAO;
import com.example.bili.entity.Bili;
import com.example.bili.service.BiliService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BiliServiceImpl implements BiliService {

    @Autowired
    private BiliDAO biliDAO;


    @Override
    public Bili selectToady(String date) {
        return biliDAO.selectToday(date);
    }

    @Override
    public int addLine(String date, int count) {
        return biliDAO.addLine(date,count);
    }

    @Override
    public int updateCount(String date, int count) {
        return biliDAO.updateCount(date,count);
    }

    @Override
    public List<Bili> getBilis() {
        return biliDAO.getBilis();
    }
}
