package com.wangyang.web.controller.user;

import com.wangyang.common.exception.ObjectException;
import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.pojo.authorize.*;
import com.wangyang.pojo.dto.UserDto;
import com.wangyang.pojo.support.Token;
import com.wangyang.service.ILiteratureService;
import com.wangyang.service.authorize.IRoleService;
import com.wangyang.service.authorize.IUserRoleService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.util.TokenProvider;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * @author wangyang
 * @date 2021/5/5
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    IUserService userService;

    @Autowired
    TokenProvider tokenProvider;

    @Autowired
    IRoleService roleService;

    @Autowired
    IUserRoleService userRoleService;

    @Value("${cms.role}")
    String role;



    @GetMapping
    public Page<User> page(@PageableDefault(sort = {"id"},direction = DESC) Pageable pageable){
        return userService.pageUser(pageable);
    }




    @GetMapping("/listAll")
    public List<UserDto> listAll(){
        return userService.listAllUserDto();
    }

    @PostMapping("/login")
    @Anonymous
    public LoginUser login(@RequestBody UserLoginParam inputUser){
        UserDetailDTO user = userService.login(inputUser.getUsername(), inputUser.getPassword());
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(user,loginUser);
        Token token = tokenProvider.generateToken(user);
        loginUser.setToken(token.getToken());
        return loginUser;
    }




    @PostMapping("/registry")
    @Anonymous
    public LoginUser registry( @Valid @RequestBody UserParam userParam){
        User username = userService.findUserByUsername(userParam.getUsername());
        if(username!=null){
            throw new ObjectException("用户名"+username.getUsername()+"已经存在！！");
        }
        User email = userService.findUserByEmail(userParam.getEmail());
        if(email!=null){
            throw new ObjectException("用户名"+email.getEmail()+"已经存在！！");
        }
        User user = new User();
        BeanUtils.copyProperties(userParam,user);

//        Role commentRole = roleService.findByEnName("COMMENT");
        Role commentRole = roleService.findByEnName(role);
        User saveUser = userService.save(user);
        UserRole userRole = new UserRole(saveUser.getId(),commentRole.getId());
        userRoleService.save(userRole);

        UserLoginParam loginParam = new UserLoginParam(saveUser.getUsername(),saveUser.getPassword());
        return this.login(loginParam);
    }

    @GetMapping("/getCurrent")
    public User getCurrentUser(HttpServletRequest request){
        Object obj = request.getAttribute("user");
        if(obj!=null){
            User currUser = (User) obj;
            int userId = currUser.getId();
            User user = findById(userId);
            return user;
        }
        return null;
    }
    @PostMapping
    public User addUser(@RequestBody @Validated UserParam inputUser){
        return userService.addUser(inputUser);
    }
//    @PostMapping
//    public User addUser(@Validated UserParam inputUser,
//                        @RequestPart(value = "file",required = false) MultipartFile file){
//        return userService.addUser(inputUser,file);
//    }

//    @PostMapping(value = "/update/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public User updateUser(@PathVariable("id") Integer id, UserParam user,@RequestPart(value = "file",required = false) MultipartFile file){
//        return userDetailService.updateUser(id,user,file);
//    };
    @PostMapping("/update/{id}")
    public User updateUser(@PathVariable("id") Integer id,@RequestBody @Validated UserParam inputUser){
        return userService.updateUser(id,inputUser);
    }

    @GetMapping("/del/{id}")
    public User delUser(@PathVariable("id") Integer id){
        return userService.delUser(id);
    }
    @GetMapping("/findById/{id}")
    public User findById(@PathVariable("id") Integer id){
        User user = userService.findById(id);
        user.setPassword(null);
        return user;
    }
}
