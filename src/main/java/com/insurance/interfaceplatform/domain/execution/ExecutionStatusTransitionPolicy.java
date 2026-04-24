package com.insurance.interfaceplatform.domain.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.support.error.CoreException;
import com.insurance.interfaceplatform.support.error.ErrorType;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ExecutionStatusTransitionPolicy {

    public void validate(final ExecutionStatus currentStatus, final ExecutionStatus nextStatus) {
        if (!canTransit(currentStatus, nextStatus)) {
            throw new CoreException(ErrorType.INVALID_STATUS_TRANSITION,
                    "허용되지 않는 실행 상태 전이입니다. " + currentStatus + " -> " + nextStatus);
        }
    }

    public boolean canTransit(final ExecutionStatus currentStatus, final ExecutionStatus nextStatus) {
        if (currentStatus == null) {
            return nextStatus == ExecutionStatus.REQUESTED;
        }
        return switch (currentStatus) {
            case REQUESTED -> Set.of(ExecutionStatus.VALIDATED, ExecutionStatus.PROCESSING, ExecutionStatus.CANCELLED).contains(nextStatus);
            case VALIDATED -> Set.of(ExecutionStatus.DISPATCHED, ExecutionStatus.PROCESSING, ExecutionStatus.CANCELLED).contains(nextStatus);
            case DISPATCHED -> Set.of(ExecutionStatus.PROCESSING, ExecutionStatus.SUCCESS, ExecutionStatus.FAILED).contains(nextStatus);
            case PROCESSING -> Set.of(ExecutionStatus.SUCCESS, ExecutionStatus.FAILED, ExecutionStatus.RETRY_WAIT).contains(nextStatus);
            case FAILED -> Set.of(ExecutionStatus.RETRY_WAIT, ExecutionStatus.RETRYING, ExecutionStatus.DEAD_LETTER).contains(nextStatus);
            case RETRY_WAIT -> Set.of(ExecutionStatus.RETRYING, ExecutionStatus.DEAD_LETTER, ExecutionStatus.CANCELLED).contains(nextStatus);
            case RETRYING -> Set.of(ExecutionStatus.SUCCESS, ExecutionStatus.FAILED, ExecutionStatus.DEAD_LETTER).contains(nextStatus);
            case SUCCESS, DEAD_LETTER, CANCELLED -> false;
        };
    }
}
