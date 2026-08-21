package com.wmm.eldercare.admin.controller;

import com.wmm.eldercare.core.common.BusinessException;
import com.wmm.eldercare.core.common.Result;
import com.wmm.eldercare.core.mapper.AppointmentMapper;
import com.wmm.eldercare.core.pojo.Appointment;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 体检报告上传 / 下载
 * <p>对应详细设计 5.5「体检报告流程」：
 * 管理员上传 PDF 存本地 data/upload/report/yyyyMM/，会员/管理员可下载，仅本人或管理员可访问。</p>
 */
@RestController
@RequestMapping("/api/report")
@Slf4j
@RequiredArgsConstructor
public class ReportController {

    private final AppointmentMapper appointmentMapper;

    /** 报告存储根目录 */
    @Value("${report.upload-dir:data/upload/report}")
    private String reportDir;

    /**
     * 管理端上传体检报告（仅 PDF）
     * POST /api/report/upload?appointmentId=6   multipart file
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("appointmentId") Long appointmentId,
                                 @RequestParam("file") MultipartFile file,
                                 HttpServletRequest request) {
        Long adminId = (Long) request.getAttribute("userId");
        // 校验预约
        Appointment appt = appointmentMapper.findById(appointmentId);
        if (appt == null) {
            throw new BusinessException(404, "预约不存在");
        }
        // 校验文件类型（仅 PDF）
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String lower = original.toLowerCase();
        if (!lower.endsWith(".pdf")) {
            throw new BusinessException(400, "仅支持 PDF 格式的报告");
        }
        if (file.isEmpty()) {
            throw new BusinessException(400, "文件不能为空");
        }
        // 存储路径：reportDir/yyyyMM/uuid.pdf
        try {
            String yyyyMM = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            Path dir = Paths.get(reportDir, yyyyMM).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String fileName = UUID.randomUUID() + ".pdf";
            Path target = dir.resolve(fileName);
            file.transferTo(target.toFile());
            // 相对路径存入 report_url（相对应用工作目录）
            String relUrl = reportDir + "/" + yyyyMM + "/" + fileName;
            appointmentMapper.updateReport(appointmentId, relUrl, original, adminId);
            return Result.success(relUrl);
        } catch (Exception e) {
            log.error("体检报告上传失败", e);
            throw new BusinessException(500, "报告上传失败：" + e.getMessage());
        }
    }

    /**
     * 下载体检报告（仅本人或管理员可访问）
     * GET /api/report/download?appointmentId=6
     */
    @GetMapping("/download")
    public ResponseEntity<Resource> download(@RequestParam("appointmentId") Long appointmentId,
                                             HttpServletRequest request) throws IOException {
        Long userId = (Long) request.getAttribute("userId");
        Appointment appt = appointmentMapper.findById(appointmentId);
        if (appt == null) {
            throw new BusinessException(404, "预约不存在");
        }
        // 鉴权：本人或管理员
        boolean isAdmin = "ADMIN".equals(request.getAttribute("role"));
        if (!isAdmin && !userId.equals(appt.getUserId())) {
            throw new BusinessException(403, "无权访问该报告");
        }
        if (appt.getReportUrl() == null || appt.getReportUrl().isBlank()) {
            throw new BusinessException(404, "该预约暂无体检报告");
        }
        // 读取文件
        Path path = Paths.get(appt.getReportUrl()).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new BusinessException(404, "报告文件不存在");
        }
        Resource resource = new UrlResource(path.toUri());
        // 下载文件名：预约ID_报告
        String filename = "report_" + appt.getId() + ".pdf";
        String encoded = URLEncoder.encode(filename, "UTF-8").replace("+", "%20");
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encoded + "\"")
                .body(resource);
    }
}
