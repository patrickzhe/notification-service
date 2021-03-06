package com.chenzhe.userservice.controller;

import com.chenzhe.userservice.exp.UserNotExistException;
import com.chenzhe.userservice.service.UserCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.chenzhe.userservice.entity.User;
import com.chenzhe.userservice.pojo.QueryUser;
import com.chenzhe.userservice.pojo.Response;
import com.chenzhe.userservice.pojo.UserBean;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.chenzhe.userservice.pojo.Response.*;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserCommonService userCommonService;

    @GetMapping(value = "/id/{userId}")
    @ResponseBody
    public UserBean getUser(@PathVariable("userId") Long id) {
        User user = userCommonService.findById(id);
        if (user != null) {
            UserBean response = new UserBean(user.getId(), user.getSureName(), user.getFirstName(), user.getGender(), user.getEmail());
            return response;
        }
        throw new UserNotExistException(id);
    }

    @RequestMapping(value = "/all", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public List<UserBean> findUsers(@RequestBody QueryUser queryUser) {
        List<User> users = userCommonService.findAllUsers(queryUser);
        if (users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(u -> new UserBean(u.getId(), u.getSureName(), u.getFirstName(), u.getGender(), u.getEmail())).collect(Collectors.toList());
    }

    @PostMapping(value = "/add")
    @ResponseBody
    public Response createUser(@RequestBody UserBean newUser) {
        // email format
        User nUser = buildUser(newUser);
        nUser.setCreateTime(System.currentTimeMillis());
        userCommonService.save(nUser);
        return SUCCESS;
    }

    @PutMapping(value = "/update")
    @ResponseBody
    public Response updateUser(@RequestBody UserBean newUser) {
        if (newUser.getId() == null) {
            return PARAM_INCORRECT;
        }
        User user = userCommonService.findById(newUser.getId());
        if (user == null) {
            return FAIL;
        }
        User nUser = buildUser(newUser);
        nUser.setCreateTime(user.getCreateTime());
        nUser.setUpdateTime(System.currentTimeMillis());
        userCommonService.updateUser(user, nUser);
        return SUCCESS;
    }

    @DeleteMapping(value = "/del/{userId}")
    @ResponseBody
    public Response deleteUser(@PathVariable("userId") Long id) {
        userCommonService.deleteUser(id);
        return SUCCESS;
    }

    private User buildUser(UserBean newUser) {
        User nUser = new User();
        nUser.setId(newUser.getId());
        nUser.setEmail(newUser.getEmail());
        nUser.setGender(newUser.getGender());
        nUser.setSureName(newUser.getSureName());
        nUser.setFirstName(newUser.getFirstName());
        return nUser;
    }
}
