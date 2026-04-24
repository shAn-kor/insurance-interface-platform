# 보험사 금융 IT 인터페이스 통합관리시스템

보험사 내부 핵심 시스템과 외부 기관 간 인터페이스를 중앙화해서 등록, 설정, 실행, 모니터링, 재처리, 로그, 성능 관리하는 백엔드 포트폴리오 프로젝트입니다.

## 프로젝트 목표

REST API, MQ/Kafka, Batch 기반 인터페이스를 공통 실행 모델로 통합 관리하고, 장애 상황에서도 실행 이력과 처리 로그가 유실되지 않도록 Retry, Circuit Breaker, Outbox, Dead Letter, Staging, Cache Fallback을 적용하는 금융 IT 운영 플랫폼을 목표로 합니다.

## 핵심 기능 범위

- 인터페이스 등록/설정/조회/활성화/비활성화
- 실행 요청 및 실행 이력 관리
- 실행 상태 전이와 상태 변경 이력 관리
- REST API, Kafka/MQ, Batch 실행 Adapter 확장 구조
- SOAP, SFTP/FTP 확장 Adapter 구조
- 실패 재처리 및 Dead Letter 전환 구조
- 실행 로그 및 민감정보 마스킹 구조
- Resilience4j 기반 Timeout, Retry, Circuit Breaker 확장
- Kafka Producer Outbox, Consumer Retry Topic, Dead Letter Topic 확장
- Batch chunk/restart/failed item 처리 확장
- SFTP/FTP 스트림 기반 파일 전송, staging, checksum, temp rename, quarantine 구조
- Actuator, Prometheus, Grafana 기반 모니터링 확장

## 현재 구현 상태

현재 1차 기반 구현이 완료된 범위는 다음과 같습니다.

- Spring Boot 프로젝트 기본 구조
- 공통 응답/예외 구조
- 핵심 Enum
- catalog 도메인 및 API
- execution 도메인 및 API
- Adapter 공통 계약
- 스트림 기반 파일 전송 계약과 로컬 구현
- Docker Compose 기반 로컬 인프라
- 단위 테스트

## 기술 스택

- Java 17
- Spring Boot 3.3.5
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Kafka
- Spring Batch
- Spring Data Redis
- Spring Actuator
- Resilience4j
- Micrometer Prometheus Registry
- Springdoc OpenAPI
- PostgreSQL
- Kafka
- Redis
- Prometheus
- Grafana
- Gradle
- Testcontainers

## 아키텍처 규칙

이 프로젝트는 `e-commerce-copy` 프로젝트 규칙을 기준으로 합니다.

- Controller는 요청/응답 변환만 수행합니다.
- Controller에서 여러 Application Service를 직접 조합하지 않습니다.
- 여러 도메인 조합이 필요한 경우에만 Facade를 사용합니다.
- Facade는 Repository를 직접 호출하지 않습니다.
- Application Service는 자기 도메인 Repository와 Domain Service만 접근합니다.
- Application Service 간 직접 호출을 금지합니다.
- Domain Service는 저장, 외부 I/O, 트랜잭션 없이 순수 비즈니스 규칙만 담당합니다.
- `@Transactional`은 Application Service에만 둡니다.
- Application Service와 Domain Service에는 private 메서드를 만들지 않습니다.
- 대량 데이터는 페이지네이션, chunk, streaming 방식으로 처리합니다.
- 통합 테스트는 H2가 아니라 Testcontainers 기반으로 작성합니다.

## 패키지 구조

```text
com.insurance.interfaceplatform
 ├─ support
 │   ├─ error
 │   ├─ response
 │   └─ time
 │
 ├─ interfaces
 │   └─ api
 │       ├─ catalog
 │       └─ execution
 │
 ├─ application
 │   ├─ catalog
 │   └─ execution
 │
 ├─ domain
 │   ├─ adapter
 │   ├─ catalog
 │   ├─ common
 │   └─ execution
 │
 └─ infrastructure
     ├─ adapter
     │   └─ filetransfer
     └─ persistence
         ├─ catalog
         └─ execution
```

## 주요 도메인

### catalog

인터페이스 기준 정보를 관리합니다.

- `InterfaceDefinition`
- `InterfaceEndpoint`
- `InterfaceAuthConfig`
- `ResiliencePolicy`

제공 API:

```text
POST   /api/interfaces
GET    /api/interfaces
GET    /api/interfaces/{interfaceId}
PUT    /api/interfaces/{interfaceId}
PATCH  /api/interfaces/{interfaceId}/enable
PATCH  /api/interfaces/{interfaceId}/disable
```

### execution

모든 프로토콜 실행을 공통 모델로 관리합니다.

- `ExecutionRun`
- `ExecutionStatusHistory`
- `ExecutionStatusTransitionPolicy`

제공 API:

```text
POST   /api/interfaces/{interfaceId}/executions
GET    /api/executions/{executionId}
GET    /api/executions/{executionId}/status
PATCH  /api/executions/{executionId}/status
```

### adapter

실제 프로토콜 실행 방식을 공통 계약으로 추상화합니다.

- `InterfaceAdapter`
- `AdapterExecutionCommand`
- `AdapterExecutionResult`
- `FileTransferAdapter`
- `FileTransferCommand`
- `FileTransferResult`

## 실행 상태 모델

```text
REQUESTED
VALIDATED
DISPATCHED
PROCESSING
SUCCESS
FAILED
RETRY_WAIT
RETRYING
DEAD_LETTER
CANCELLED
```

## 프로토콜 모델

```text
REST
SOAP
MQ
KAFKA
BATCH
SFTP
FTP
```

## 레실리언스 정책

| 영역 | 장애 상황 | 대응 전략 | 폴백 |
|---|---|---|---|
| REST 송신 | Timeout, 5xx, Connection Error | Timeout, Retry, Circuit Breaker | 성공 처리 금지, RETRY_WAIT 또는 DEAD_LETTER |
| REST 조회 | 외부기관 장애, 응답 지연 | Timeout, Retry, Circuit Breaker | 마지막 성공 Snapshot 반환 |
| SOAP | SOAP Fault, Timeout | Retry, Circuit Breaker | 실패 유형 저장 후 재처리 대기 |
| Kafka/MQ Producer | Broker 장애, publish 실패 | Retry, Outbox | DB Outbox 저장 후 재발행 |
| Kafka/MQ Consumer | 처리 실패, 역직렬화 실패 | Retry Topic, DLT | 재시도 초과 시 Dead Letter |
| Batch | Step 실패, 일부 데이터 오류 | Retry, Skip, Restart | Failed Item 분리, 재시작 |
| SFTP/FTP | 접속 실패, 파일 업로드 실패 | Retry, Staging | Staging 파일 보존 후 재전송 |
| SFTP/FTP 반복 실패 | 파일 오류, 반복 전송 실패 | Quarantine | 운영자 확인 대상으로 분리 |
| Redis | Lock/Cache 장애 | DB Fallback | DB idempotency로 중복 방지 |
| Audit | 감사 로그 저장 실패 | Fail-closed, Outbox | 설정 변경은 실패, 실행 이벤트는 Audit Outbox |
| Monitoring | Prometheus/Grafana 장애 | DB 집계 | execution_run 기반 지표 제공 |

## 파일 전송 원칙

SFTP/FTP와 Batch 파일 처리는 스트림 기반으로 설계합니다.

- 파일 전체를 `byte[]`, `String`, `List<String>`으로 메모리에 올리지 않습니다.
- 업로드는 `InputStream` 기반 streaming upload로 처리합니다.
- 다운로드는 원격 `InputStream`을 로컬 staging `OutputStream`으로 저장합니다.
- Batch 파일 생성은 chunk 기반으로 처리합니다.
- Batch 파일 읽기는 streaming reader 방식으로 처리합니다.
- 파일 전송 중 checksum을 계산합니다.
- 업로드는 임시 파일명으로 전송 후 최종 파일명으로 rename합니다.
- 전송 실패 시 staging 파일을 보존합니다.
- 반복 실패 파일은 quarantine 처리합니다.

## Docker 인프라

Docker Compose로 로컬 인프라를 실행할 수 있습니다.

| 서비스 | 포트 | 용도 |
|---|---:|---|
| PostgreSQL | 5432 | 메인 DB |
| Kafka | 29092 | 비동기 메시징 |
| Kafka UI | 8090 | Kafka 관리 UI |
| Redis | 6379 | 락/캐시/중복 방지 확장 |
| Prometheus | 9090 | Actuator metrics 수집 |
| Grafana | 3000 | 모니터링 대시보드 |

인프라 실행:

```bash
docker compose up -d
```

인프라 종료:

```bash
docker compose down
```

볼륨까지 삭제:

```bash
docker compose down -v
```

## 로컬 실행

1. 인프라 실행

```bash
docker compose up -d
```

2. 애플리케이션 실행

```bash
./gradlew bootRun
```

3. Swagger 접속

```text
http://localhost:8080/swagger-ui.html
```

## 주요 접속 URL

- Swagger: `http://localhost:8080/swagger-ui.html`
- Actuator Health: `http://localhost:8080/actuator/health`
- Prometheus Metrics: `http://localhost:8080/actuator/prometheus`
- Kafka UI: `http://localhost:8090`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`

Grafana 초기 계정:

```text
admin / admin
```

## 환경 변수

`application.yml`은 다음 환경 변수를 지원합니다.

| 변수 | 기본값 |
|---|---|
| POSTGRES_HOST | localhost |
| POSTGRES_PORT | 5432 |
| POSTGRES_DB | insurance_interface |
| POSTGRES_USER | insurance |
| POSTGRES_PASSWORD | insurance |
| KAFKA_BOOTSTRAP_SERVERS | localhost:29092 |
| REDIS_HOST | localhost |
| REDIS_PORT | 6379 |

## 테스트

전체 테스트 실행:

```bash
./gradlew test
```

현재 단위 테스트는 다음 영역을 검증합니다.

- catalog Application Service
- 인증 설정 alias 저장 정책
- execution Application Service
- 실행 상태 전이 정책
- 스트림 기반 파일 전송 Adapter

## 검증된 명령

Docker Compose 설정 검증:

```bash
docker compose config
```

테스트 검증:

```bash
./gradlew test
```

## 다음 구현 예정

- REST Adapter 실제 구현
- Resilience4j 기반 Timeout, Retry, Circuit Breaker 연결
- execution log 도메인 구현
- 민감정보 마스킹 정책 구현
- retry/deadletter 도메인 구현
- Kafka Outbox 구현
- Batch chunk/restart/failed item 구현
- Monitoring/Audit 구현
