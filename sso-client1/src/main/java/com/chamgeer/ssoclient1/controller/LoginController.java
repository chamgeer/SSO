package com.chamgeer.ssoclient1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chamgeer
 * @Date 2021/7/15
 */
@Controller
public class LoginController {

    @Value("${sso.server.url}")
    private String redirect_url;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/hello")
    @ResponseBody
    public String hello(){
        return "hello";
    }

    @RequestMapping("/userList")
    public String login(Model model, HttpSession httpSession, HttpServletRequest request,
                        @RequestParam(name = "token",required = false)String token){
        Object loginUser = httpSession.getAttribute("loginUser");
        String url = request.getRequestURL().toString();
        if(loginUser==null){
            if(token!=null){
                //去sso-server查询用户具体信息
                String getLoginUserrUrl="http://localhost:9090/getLoginUser?token="+token;
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(getLoginUserrUrl, String.class);
                if(responseEntity.getStatusCodeValue()==200){
                    String username = responseEntity.getBody();
                    httpSession.setAttribute("loginUser",username);
                    model.addAttribute("loginUser",username);
                }
                loginInfo(model);
            return "source1";
            }
           return "redirect:"+redirect_url+url;
        }else{
           loginInfo(model);
            return "source1";
        }
    }

    private void loginInfo(Model model){
        List<String> emps=new ArrayList<>();
        emps.add("张三");
        emps.add("李四");
        model.addAttribute("emps",emps);
    }
}
