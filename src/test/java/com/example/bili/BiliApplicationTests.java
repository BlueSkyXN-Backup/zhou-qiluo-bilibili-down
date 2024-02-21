package com.example.bili;

import com.example.bili.utils.HttpUtils;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@SpringBootTest
class BiliApplicationTests {

    @SneakyThrows
    @Test
    void contextLoads() {
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        System.out.println(format);
    }

    @Test
    void baidu() {
        String s = HttpUtils.sendGet("https://rh147n.jomodns.com/b/nv0.baidupcs.com/video/netdisk-videotran-baoding/cc5b603a180f7626e229403e87043667_1140_3_ts/eff08a0717ecd92e87f520c6656663b5?ts_size=9464108&app_id=250528&clienttype=0&csl=0&dp-logid=492746978404927921&fn=%E8%AF%BE%E6%97%B697+%E5%B8%B8%E6%95%B0%E9%A1%B9%E7%BA%A7%E6%95%B001.mp4&from_type=3&fsid=27628419843771&iot_vip=0&isplayer=1&iv=2&logid=492746978404927921&mtime=1664116687&ouk=870885073&r=217884832&size=423317364&sta_cs=8391&sta_dt=video&sta_dx=403&time=1668600629&to=hsn00&tot=bwDWS&uo=cmnet&uva=3527247757&vuk=870885073&dtime=10&etag=eff08a0717ecd92e87f520c6656663b5&fid=66d56b22b4baef1cf098f1935814b8e0-870885073&len=398560&range=5638684-6037243&region=baoding&resv4=&sign=BOUTHNF-F3530edecde9cd71b79378b290804a96-tg3EBqfZFYS%252BcPHEX7gXlunnK%252FA%253D&xcode=fe28a57129a7678aa87fc68fcb266e16aff6eb6e7c635ab84e34d6810d556f4f068391ae721f9c6016fc357fc1507fdc0b2977702d3e6764&xv=6&need_suf=&pmk=1400eff08a0717ecd92e87f520c6656663b5dc0fb70100000090692c&by=my-streaming");
        System.out.println(s);
    }
}
