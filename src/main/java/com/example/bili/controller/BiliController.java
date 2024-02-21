package com.example.bili.controller;

import com.example.bili.entity.Bili;
import com.example.bili.service.BiliService;
import com.example.bili.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin
public class BiliController {

    @Autowired
    private BiliService biliService;

    /**
     * 根据bv获取详细信息，其中avid和cid很重要
     * @param bv
     * @return
     */
    @GetMapping("/av/{bv}")
    public String getAv(@PathVariable("bv") String bv) {
        return HttpUtils.sendGet("https://api.bilibili.com/x/web-interface/view?bvid=" + bv);
    }

    /**
     * 根据avid和cid请求下载地址接口，获取视频地址
     * @param avid
     * @param cid
     * @return
     */
    @GetMapping("/download/{avid}/{cid}")
    public String getDownloadUrl(@PathVariable("avid") String avid, @PathVariable("cid") String cid) {
        // 统计请求次数
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        Bili bili = biliService.selectToady(date);
        if (bili != null) {
            // 查询当前的count
            Bili bili1 = biliService.selectToady(date);
            Integer count = bili1.getCount();
            // 访问量加1
            count += 1;
            // 修改今天访问量
            biliService.updateCount(date, count);
        } else {
            // 新增记录
            int i = biliService.addLine(date, 0);
            if (i != 0) {
                // 查询当前的count
                Bili bili1 = biliService.selectToady(date);
                Integer count = bili1.getCount();
                // 访问量加1
                count += 1;
                // 修改今天访问量
                biliService.updateCount(date, count);
            }
        }
        return HttpUtils.sendGet("https://api.bilibili.com/x/player/playurl?avid=" + avid + "&cid=" + cid + "&qn=80&type=mp4&platform=html5&high_quality=1");
    }

    /**
     * 对移动端链接的处理接口
     * @param url
     * @return
     */
    @GetMapping("/move")
    public String getMobileContent(@RequestParam("url") String url) {
        return HttpUtils.sendGet(url);
    }

    /**
     * 统计工具每天请求次数，返回所有天数
     * @return
     */
    @GetMapping("/counts")
    public List<Bili> getBilis() {
        return biliService.getBilis();
    }

    /**
     * 根据日期返回下载接口请求次数  例如： /count/2022-11-25
     * @param date
     * @return
     */
    @GetMapping("/count/{date}")
    public Bili getBili(@PathVariable("date") String date){
        return biliService.selectToady(date);
    }
}
