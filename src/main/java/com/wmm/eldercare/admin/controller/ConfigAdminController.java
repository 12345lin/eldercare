package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.PageResult;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.pojo.SysConfig;
import com.wmm.eldercare.core.service.ConfigAdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端系统配置 Controller
 */
@RestController
@RequestMapping("/api/admin/configs")
@Slf4j
@RequiredArgsConstructor
public class ConfigAdminController {
    private final ConfigAdminService configAdminService;

    /**
     * 分页查询配置列表（可按关键字搜索）
     * GET /api/admin/configs?keyword=x&pageNum=1&pageSize=10
     */
    @GetMapping
    public Result<PageResult<SysConfig>> listConfigs(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端查询配置列表: adminId={}, keyword={}, pageNum={}, pageSize={}", adminId, keyword, pageNum, pageSize);
        return Result.success(configAdminService.listConfigs(keyword, pageNum, pageSize));
    }

    /**
     * 新增配置
     * POST /api/admin/configs
     */
    @PostMapping
    public Result<Void> addConfig(@RequestBody SysConfig sysConfig,
                                  HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端新增配置: adminId={}, configKey={}", adminId, sysConfig.getConfigKey());
        configAdminService.addConfig(sysConfig);
        return Result.success();
    }

    /**
     * 修改配置
     * PUT /api/admin/configs/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateConfig(@PathVariable Long id,
                                     @RequestBody SysConfig sysConfig,
                                     HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端修改配置: adminId={}, id={}", adminId, id);
        configAdminService.updateConfig(id, sysConfig);
        return Result.success();
    }

    /**
     * 删除配置
     * DELETE /api/admin/configs/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteConfig(@PathVariable Long id,
                                     HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        log.info("管理端删除配置: adminId={}, id={}", adminId, id);
        configAdminService.deleteConfig(id);
        return Result.success();
    }
}