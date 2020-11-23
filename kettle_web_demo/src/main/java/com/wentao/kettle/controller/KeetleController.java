package com.wentao.kettle.controller;


import com.wentao.kettle.job.JobSubmitter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * date: 2020/11/2 0002 15:49
 * description:
 */
@Controller
@RequestMapping("/kettle")
public class KeetleController {
    @RequestMapping("/exe")
    public static void test() {
        JobSubmitter jobSubmitter = new JobSubmitter();
        Map<String, String> hashMapParam = new HashMap<>();
        String[] strings = new String[2];
        jobSubmitter.standAlonerunTransfer(hashMapParam, strings, "D:\\test\\test.ktr");

    }



    @RequestMapping("/hello")
    @ResponseBody
    public static String hello() {
        return
        "hellow";
    }


}
