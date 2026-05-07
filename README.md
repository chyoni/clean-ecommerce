# CLEAN ECOMMERCE

이커머스 플랫폼 서비스를 **클린 아키텍처 + 헥사고날 아키텍처** 구조로 설계 및 구현

## 이 프로젝트로 무엇을 공부할 수 있나

- 클린 아키텍처와 헥사고날 아키텍처를 활용한 멀티 모듈 구조
- DDD(도메인 주도 설계) — Aggregate, Bounded Context, Ubiquitous Language
- 버전 카탈로그를 활용한 중앙집중식 의존성 및 버전 관리
- Presigned URL 패턴으로 서버를 거치지 않고 클라이언트가 스토리지에 직접 파일 업로드
- 이커머스 배치 작업
- 모듈로 아키텍처를 분리했을 때 장단점
- 레이어별 테스트 전략 (단위 테스트 / 통합 테스트)

## 모듈 구조

```
├── clean-ecommerce
│    ├── adapter      # 어댑터 레이어 (Web Controller, Storage Adapter)
│    ├── application  # 애플리케이션 레이어 (UseCase 포트 + 서비스)
│    ├── batch        # 배치 작업
│    ├── bootstrap    # 애플리케이션 실행 모듈
│    └── domain       # 도메인 레이어 (Entity, Aggregate, Value Object)
├── build.gradle.kts
└── ...
```

의존성 방향: `bootstrap → adapter → application → domain`
바깥 레이어가 안쪽을 알 수 있지만, 안쪽 레이어는 바깥을 절대 알 수 없다.

## 기술 스택

| 분류             | 기술                                            |
|----------------|-----------------------------------------------|
| Language       | Java 21                                       |
| Framework      | Spring Boot 4.0.6 (Spring Framework 7)        |
| Persistence    | Spring Data JPA, MySQL 8, H2 (테스트)            |
| Validation     | Jakarta Bean Validation (Hibernate Validator) |
| Batch          | Spring Batch (JDBC)                           |
| Object Storage | MinIO (로컬) / AWS S3 호환 — AWS SDK v2 2.29.34   |
| Build          | Gradle (Kotlin DSL), Version Catalog          |
| Test           | JUnit 6, Mockito, Testcontainers (MinIO)      |

## 외부 스토리지 — MinIO (Presigned URL)

상품 이미지 업로드는 서버가 파일을 직접 받지 않는다.
**Presigned URL 패턴**으로 클라이언트가 스토리지에 직접 업로드한다.

```
클라이언트                 서버 (Spring Boot)              MinIO / S3
    │                           │                              │
    │  POST /images/upload-url  │                              │
    │──────────────────────────>│                              │
    │                           │  서명된 PUT URL 생성            │
    │                           │<─────────────────────────────│
    │  { uploadUrl, storageKey }│                              │
    │<──────────────────────────│                              │
    │                           │                              │
    │  PUT {uploadUrl} (파일)    │                              │
    │─────────────────────────────────────────────────────────>│
    │  200 OK                   │                              │
    │<─────────────────────────────────────────────────────────│
```

로컬 개발 환경에서는 MinIO를 Docker로 실행한다. AWS S3, Cloudflare R2 등 S3 호환 스토리지로 그대로 교체 가능하다 (코드 변경 없음).

MinIO 관리자 콘솔: http://localhost:9001 (`devminio` / `devminio12345`)

## 로컬 실행

**인프라 실행 (MySQL + MinIO)**

```bash
docker compose up -d
```

**애플리케이션 실행에 필요한 환경변수 (.env)**

```
SPRING_PROFILES_ACTIVE=local
DB_HOST=localhost
DB_NAME=clean-ecommerce
DB_USERNAME=cwchoiit
DB_PASSWORD=cwchoiit
STORAGE_ENDPOINT=http://localhost:9000
STORAGE_BUCKET=product-images
STORAGE_ACCESS_KEY=devminio
STORAGE_SECRET_KEY=devminio12345
STORAGE_PUBLIC_URL_BASE=http://localhost:9000
```