package com.whut.blog;

import com.whut.blog.utils.MD5Utils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BlogV1ApplicationTests {

    @Test
    void contextLoads() {
        String zyq123 = MD5Utils.md5("admin");
        System.out.println(zyq123);
    }

}
