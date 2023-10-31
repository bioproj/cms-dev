package com.wangyang.web.controller.api;


import com.wangyang.pojo.entity.AuthRedirect;
import com.wangyang.service.IAuthRedirectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequestMapping("/api/authRedirect")
//@CrossOrigin
@Slf4j
public class AuthRedirectController {
    @Autowired
    IAuthRedirectService authRedirectService;
    @GetMapping
    public Page<AuthRedirect> page(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){
        Page<AuthRedirect> authRedirects = authRedirectService.pageBy(pageable);
        return authRedirects;
    }

    @PostMapping
    public AuthRedirect add(@RequestBody AuthRedirect authRedirect){
        return authRedirectService.add(authRedirect);
    }
    @PostMapping("/update/{id}")
    public AuthRedirect update(@PathVariable("id") Integer id, @RequestBody AuthRedirect authRedirect){
        return authRedirectService.update(id,authRedirect);
    }
    @GetMapping("/delById/{id}")
    public AuthRedirect delById(@PathVariable("id") Integer id){
        AuthRedirect authRedirect = authRedirectService.delBy(id);
        return authRedirect;
    }

    @GetMapping("/findById/{id}")
    public AuthRedirect findById(@PathVariable("id") Integer id){
        AuthRedirect authRedirect = authRedirectService.findById(id);
        return authRedirect;
    }



}
