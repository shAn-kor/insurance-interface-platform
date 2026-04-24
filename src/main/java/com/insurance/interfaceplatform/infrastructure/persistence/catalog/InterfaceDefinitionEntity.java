package com.insurance.interfaceplatform.infrastructure.persistence.catalog;

import com.insurance.interfaceplatform.domain.catalog.InterfaceAuthConfig;
import com.insurance.interfaceplatform.domain.catalog.InterfaceDefinition;
import com.insurance.interfaceplatform.domain.catalog.InterfaceEndpoint;
import com.insurance.interfaceplatform.domain.catalog.ResiliencePolicy;
import com.insurance.interfaceplatform.domain.common.BackoffType;
import com.insurance.interfaceplatform.domain.common.InterfaceStatus;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "interface_definition",
        uniqueConstraints = @UniqueConstraint(name = "uk_interface_definition_code", columnNames = "interface_code")
)
public class InterfaceDefinitionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "interface_code", nullable = false, length = 80)
    private String interfaceCode;

    @Column(name = "interface_name", nullable = false, length = 200)
    private String interfaceName;

    @Column(name = "business_domain", nullable = false, length = 80)
    private String businessDomain;

    @Column(name = "external_organization", nullable = false, length = 120)
    private String externalOrganization;

    @Enumerated(EnumType.STRING)
    @Column(name = "protocol_type", nullable = false, length = 30)
    private ProtocolType protocolType;

    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private InterfaceStatus status;

    @Column(name = "endpoint_url", length = 1000)
    private String endpointUrl;

    @Column(name = "http_method", length = 20)
    private String httpMethod;

    @Column(name = "topic_name", length = 200)
    private String topicName;

    @Column(name = "queue_name", length = 200)
    private String queueName;

    @Column(name = "batch_job_name", length = 200)
    private String batchJobName;

    @Column(name = "file_path", length = 1000)
    private String filePath;

    @Column(name = "endpoint_timeout_millis")
    private Integer endpointTimeoutMillis;

    @Column(name = "auth_type", length = 50)
    private String authType;

    @Column(name = "api_key_alias", length = 200)
    private String apiKeyAlias;

    @Column(name = "token_alias", length = 200)
    private String tokenAlias;

    @Column(name = "username_alias", length = 200)
    private String usernameAlias;

    @Column(name = "password_alias", length = 200)
    private String passwordAlias;

    @Column(name = "cert_alias", length = 200)
    private String certAlias;

    @Column(name = "resilience_timeout_millis")
    private Integer resilienceTimeoutMillis;

    @Column(name = "max_retry_count")
    private Integer maxRetryCount;

    @Column(name = "retry_interval_millis")
    private Integer retryIntervalMillis;

    @Enumerated(EnumType.STRING)
    @Column(name = "backoff_type", length = 30)
    private BackoffType backoffType;

    @Column(name = "circuit_breaker_enabled")
    private Boolean circuitBreakerEnabled;

    @Column(name = "failure_rate_threshold")
    private Integer failureRateThreshold;

    @Column(name = "slow_call_threshold_millis")
    private Integer slowCallThresholdMillis;

    @Column(name = "minimum_call_count")
    private Integer minimumCallCount;

    @Column(name = "open_state_wait_millis")
    private Integer openStateWaitMillis;

    @Column(name = "bulkhead_enabled")
    private Boolean bulkheadEnabled;

    @Column(name = "max_concurrent_calls")
    private Integer maxConcurrentCalls;

    @Column(name = "rate_limiter_enabled")
    private Boolean rateLimiterEnabled;

    @Column(name = "limit_for_period")
    private Integer limitForPeriod;

    protected InterfaceDefinitionEntity() {
    }

    public InterfaceDefinitionEntity(final InterfaceDefinition interfaceDefinition) {
        this.id = interfaceDefinition.id();
        this.interfaceCode = interfaceDefinition.interfaceCode();
        this.interfaceName = interfaceDefinition.interfaceName();
        this.businessDomain = interfaceDefinition.businessDomain();
        this.externalOrganization = interfaceDefinition.externalOrganization();
        this.protocolType = interfaceDefinition.protocolType();
        this.description = interfaceDefinition.description();
        this.status = interfaceDefinition.status();
        this.endpointUrl = interfaceDefinition.endpoint().endpointUrl();
        this.httpMethod = interfaceDefinition.endpoint().httpMethod();
        this.topicName = interfaceDefinition.endpoint().topicName();
        this.queueName = interfaceDefinition.endpoint().queueName();
        this.batchJobName = interfaceDefinition.endpoint().batchJobName();
        this.filePath = interfaceDefinition.endpoint().filePath();
        this.endpointTimeoutMillis = interfaceDefinition.endpoint().timeoutMillis();
        this.authType = interfaceDefinition.authConfig().authType();
        this.apiKeyAlias = interfaceDefinition.authConfig().apiKeyAlias();
        this.tokenAlias = interfaceDefinition.authConfig().tokenAlias();
        this.usernameAlias = interfaceDefinition.authConfig().usernameAlias();
        this.passwordAlias = interfaceDefinition.authConfig().passwordAlias();
        this.certAlias = interfaceDefinition.authConfig().certAlias();
        this.resilienceTimeoutMillis = interfaceDefinition.resiliencePolicy().timeoutMillis();
        this.maxRetryCount = interfaceDefinition.resiliencePolicy().maxRetryCount();
        this.retryIntervalMillis = interfaceDefinition.resiliencePolicy().retryIntervalMillis();
        this.backoffType = interfaceDefinition.resiliencePolicy().backoffType();
        this.circuitBreakerEnabled = interfaceDefinition.resiliencePolicy().circuitBreakerEnabled();
        this.failureRateThreshold = interfaceDefinition.resiliencePolicy().failureRateThreshold();
        this.slowCallThresholdMillis = interfaceDefinition.resiliencePolicy().slowCallThresholdMillis();
        this.minimumCallCount = interfaceDefinition.resiliencePolicy().minimumCallCount();
        this.openStateWaitMillis = interfaceDefinition.resiliencePolicy().openStateWaitMillis();
        this.bulkheadEnabled = interfaceDefinition.resiliencePolicy().bulkheadEnabled();
        this.maxConcurrentCalls = interfaceDefinition.resiliencePolicy().maxConcurrentCalls();
        this.rateLimiterEnabled = interfaceDefinition.resiliencePolicy().rateLimiterEnabled();
        this.limitForPeriod = interfaceDefinition.resiliencePolicy().limitForPeriod();
    }

    public InterfaceDefinition toDomain() {
        return new InterfaceDefinition(
                id,
                interfaceCode,
                interfaceName,
                businessDomain,
                externalOrganization,
                protocolType,
                description,
                status,
                new InterfaceEndpoint(endpointUrl, httpMethod, topicName, queueName, batchJobName, filePath, endpointTimeoutMillis),
                new InterfaceAuthConfig(authType, apiKeyAlias, tokenAlias, usernameAlias, passwordAlias, certAlias),
                new ResiliencePolicy(resilienceTimeoutMillis, maxRetryCount, retryIntervalMillis, backoffType,
                        circuitBreakerEnabled, failureRateThreshold, slowCallThresholdMillis, minimumCallCount,
                        openStateWaitMillis, bulkheadEnabled, maxConcurrentCalls, rateLimiterEnabled, limitForPeriod)
        );
    }
}
