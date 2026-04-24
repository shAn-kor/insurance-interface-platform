package com.insurance.interfaceplatform.domain.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExecutionStatusTransitionPolicyTest {

    private final ExecutionStatusTransitionPolicy policy = new ExecutionStatusTransitionPolicy();

    @Test
    @DisplayName("canTransit_요청에서처리중_허용한다")
    void canTransit_requestedToProcessing_returnsTrue() {
        assertThat(policy.canTransit(ExecutionStatus.REQUESTED, ExecutionStatus.PROCESSING)).isTrue();
    }

    @Test
    @DisplayName("canTransit_성공에서처리중_거부한다")
    void canTransit_successToProcessing_returnsFalse() {
        assertThat(policy.canTransit(ExecutionStatus.SUCCESS, ExecutionStatus.PROCESSING)).isFalse();
    }
}
