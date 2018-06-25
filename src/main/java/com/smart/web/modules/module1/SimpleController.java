package com.smart.web.modules.module1;

import com.smart.web.base.Loader;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by licheng on 2017/10/30.
 */
@SpringBootApplication
@RequestMapping("/simple")
public class SimpleController {
    @RequestMapping("/test")
    @ResponseBody
    public String test(){

        return "hello world!!!";
    }
}
