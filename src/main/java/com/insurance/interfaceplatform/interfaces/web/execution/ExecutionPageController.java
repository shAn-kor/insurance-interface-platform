package com.insurance.interfaceplatform.interfaces.web.execution;

import com.insurance.interfaceplatform.application.execution.ExecutionApplicationService;
import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.common.ExecutionType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import com.insurance.interfaceplatform.domain.execution.CreateExecutionCommand;
import com.insurance.interfaceplatform.domain.execution.ExecutionRun;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/executions")
public class ExecutionPageController {

    private final ExecutionApplicationService executionApplicationService;

    public ExecutionPageController(final ExecutionApplicationService executionApplicationService) {
        this.executionApplicationService = executionApplicationService;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            final Model model
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<ExecutionRun> executions = executionApplicationService.list(pageable);
        model.addAttribute("executions", executions);
        model.addAttribute("executionTypes", ExecutionType.values());
        model.addAttribute("protocolTypes", ProtocolType.values());
        model.addAttribute("executionStatuses", ExecutionStatus.values());
        model.addAttribute("createRequest", ExecutionForm.defaultRequest());
        return "execution/list";
    }

    @PostMapping
    public String create(
            @ModelAttribute final ExecutionForm form,
            final RedirectAttributes redirectAttributes
    ) {
        executionApplicationService.create(form.toCommand());
        redirectAttributes.addFlashAttribute("message", "실행 이력이 생성되었습니다.");
        return "redirect:/executions";
    }

    @PostMapping("/status")
    public String changeStatus(
            @RequestParam final String executionId,
            @RequestParam final ExecutionStatus status,
            @RequestParam(defaultValue = "화면에서 상태 변경") final String reason,
            final RedirectAttributes redirectAttributes
    ) {
        executionApplicationService.changeStatus(executionId, status, reason);
        redirectAttributes.addFlashAttribute("message", "실행 상태가 변경되었습니다.");
        return "redirect:/executions";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(
            final Exception exception,
            final RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/executions";
    }

    public record ExecutionForm(
            Long interfaceId,
            ExecutionType executionType,
            ProtocolType protocolType,
            String idempotencyKey,
            String payloadSummary
    ) {

        public static ExecutionForm defaultRequest() {
            return new ExecutionForm(1L, ExecutionType.MANUAL, ProtocolType.REST, "CONTRACT-20260424-FSS-001",
                    "{\"baseDate\":\"2026-04-24\",\"contractCount\":15820}");
        }

        public CreateExecutionCommand toCommand() {
            return new CreateExecutionCommand(interfaceId, executionType, protocolType, idempotencyKey,
                    hash(payloadSummary), payloadSummary);
        }

        public String hash(final String value) {
            try {
                final MessageDigest digest = MessageDigest.getInstance("SHA-256");
                final byte[] hashed = digest.digest((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
                return HexFormat.of().formatHex(hashed);
            } catch (NoSuchAlgorithmException exception) {
                throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", exception);
            }
        }
    }
}
