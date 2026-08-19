package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.User;
import com.wmm.eldercare.core.service.UserAdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端用户管理 Controller
 */
@RestController
@RequestMapping("/api/admin/users")
@Slf4j
@RequiredArgsConstructor
public class UserAdminController {
    private final UserAdminService userAdminService;

    /**
     * 分页查询用户列表（可按手机号/姓名搜索）
     * GET /api/admin/users?keyword=x&pageNum=1&pageSize=10
     */
    @GetMapping
    public Result<PageResult<User>> listUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端查询用户列表: adminId={}, keyword={}, pageNum={}, pageSize={}", adminId, keyword, pageNum, pageSize);
        return Result.success(userAdminService.listUsers(keyword, pageNum, pageSize));
    }

    /**
     * 查询用户详情
     * GET /api/admin/users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端查询用户详情: adminId={}, userId={}", adminId, id);
        return Result.success(userAdminService.getUser(id));
    }

    /**
     * 新增用户
     * POST /api/admin/users
     */
    @PostMapping
    public Result<Void> addUser(@RequestBody User user, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端新增用户: adminId={}, phone={}", adminId, user.getPhone());
        userAdminService.addUser(user);
        return Result.success();
    }

    /**
     * 修改用户信息
     * PUT /api/admin/users/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端修改用户: adminId={}, userId={}", adminId, id);
        userAdminService.updateUser(id, user);
        return Result.success();
    }

    /**
     * 启用/禁用用户
     * PUT /api/admin/users/{id}/status?status=DISABLED
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @RequestParam String status,
                                     HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端切换用户状态: adminId={}, userId={}, status={}", adminId, id, status);
        userAdminService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 删除用户
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端删除用户: adminId={}, userId={}", adminId, id);
        userAdminService.deleteUser(id);
        return Result.success();
    }

    /**
     * 批量删除用户
     * DELETE /api/admin/users/batch  body: [1,2,3]
     */
    @DeleteMapping("/batch")
    public Result<Void> batchDeleteUsers(@RequestBody List<Long> ids, HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端批量删除用户: adminId={}, ids={}", adminId, ids);
        userAdminService.batchDeleteUsers(ids);
        return Result.success();
    }
}