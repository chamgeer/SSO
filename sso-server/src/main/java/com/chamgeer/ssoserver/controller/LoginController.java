package com.chamgeer.ssoserver.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @author chamgeer
 * @Date 2021/7/15
 */
@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

    @RequestMapping("sso-server")
    public String loginRequest(@RequestParam("redirect-url")String url, Model model,
                               @CookieValue(name = "sso-cookie",required = false)String cookie){
        if(StringUtils.isNoneBlank(cookie)){
            return "redirect:"+url+"?token="+cookie;
        }
        model.addAttribute("redirectUrl",url);
        return "login";
    }

    @RequestMapping("/doLogin")
    public String login(String username, String password, String url , HttpServletResponse response){
        if(StringUtils.isNotEmpty(username)&&StringUtils.isNotEmpty(password)&&StringUtils.isNotEmpty(url)){
            String uuid=UUID.randomUUID().toString().replace("-","");
            redisTemplate.opsForValue().set(uuid,username);
            Cookie cookie=new Cookie("sso-cookie",uuid);
            response.addCookie(cookie);
            return "redirect:"+url+"?token="+uuid;
        }else{
            return "forward:sso-server";
        }
    }

    @RequestMapping("/getLoginUser")
    @ResponseBody
    public String getLoginUser(@RequestParam("token")String token){
        if(StringUtils.isNoneBlank(token)){
            String username = redisTemplate.opsForValue().get(token);
            if(StringUtils.isNotEmpty(username))
            return username;
        }
        return "";
    }

}
