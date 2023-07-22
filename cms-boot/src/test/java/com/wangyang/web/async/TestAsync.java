package com.wangyang.web.async;

import com.wangyang.service.event.HtmlListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestAsync {

    @Autowired
    HtmlListener htmlListener;

    @Test
    public void  test1(){
        System.out.println("before");
//        htmlListener.asyn1();
        System.out.println("after");
    }
}
