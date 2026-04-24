package com.insurance.interfaceplatform.application.execution;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ExecutionIdGenerator {

    public String generate() {
        final String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        final String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "EXE-" + date + "-" + suffix;
    }
}
