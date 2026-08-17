package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 添加用户
     * @param user
     * @return
     */
    @PostMapping("/users")
    public Result<User> addUser(@RequestBody User user){
        log.info("添加用户: {}", user.getPhone());
        userService.addUser(user);
        return Result.success(user);
    }

    /**
     * 根据用户ID查询用户
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    public Result<User> findUserById(@PathVariable Long id){
        log.info("根据用户ID查询用户: {}", id);
        User user = userService.findUserById(id);
        user.setPassword(null);
        return Result.success(user);
    }

    /**
     * 更新用户
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/users/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user){
        log.info("更新用户ID: {}, {}", id, user.getPhone());
        // 调用服务层更新用户
        userService.updateUser(id, user);
        return Result.success();
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/users/{id}")
    public Result<Void> deleteUser(@PathVariable Long id){
        log.info("删除用户ID: {}", id);
        // 调用服务层删除用户
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 分页查询用户列表
     * @return
     */
    @GetMapping("/users/list")
    public Result<PageResult<User>> listUsers(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ){
        log.info("分页查询用户列表: pageNum={}, pageSize={}", pageNum, pageSize);
        PageResult<User> result = userService.listUsers(pageNum, pageSize);
        return Result.success(result);
    }

    @DeleteMapping("/users/batch")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> ids){
        log.info("批量删除用户ID: {}", ids);
        userService.batchDeleteUsers(ids);
        return Result.success();
    }
}