package ru.tusur.ShaurmaWebSiteProject.backend.service;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tusur.ShaurmaWebSiteProject.backend.security.SecurityService;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MyRestController {

    private final SecurityService securityService;

    public MyRestController(SecurityService securityService) {
        this.securityService = securityService;
    }

    @GetMapping("/getAuthStatus")
    public Map<String, String> getAuthStatus() {
        Map<String, String> jsonResponse = new HashMap<>();
        if (securityService.getAuthenticatedUser()==null){
            jsonResponse.put("Message", "user is unanimous");
        }else {
            jsonResponse.put("Message", STR."username is\{securityService.getAuthenticatedUser().getUsername()}");
        }
        return jsonResponse;
    }

}
