package com.wxs.controller;

import com.wxs.service.luaService;
import com.wxs.service.redisService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

/**
 * @ClassName: redisController
 * @Author: WuXiangShuai
 * @Time: 13:31 2019/8/26.
 * @Description:
 */
@Controller
public class redisController {

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    @RequestMapping("doseckill")
    public void doseckill(@RequestParam Map<String, Object> param, HttpServletResponse response) throws IOException {
        String prodid = (String) param.get("prodid");
        String userid = new Random().nextInt(50000) + "";
//        boolean if_success = redisService.doSecKill(userid, prodid);
        boolean if_success = luaService.doSecKill(userid, prodid);
        response.getWriter().print(if_success);
    }

}
