# CLEAN ECOMMERCE 

- 이 프로젝트는 초대형 오픈마켓 서비스의 백엔드를 구축하는 프로젝트이다
- 이 프로젝트는 **클린 아키텍처 + 헥사고날 아키텍처** 기반의 프로젝트이다
- 이 프로젝트는 DDD 핵심 철학(애그리거트, 바운디드 컨텍스트 등)을 최대한 수용하는 프로젝트이다
- 이 프로젝트는 Spring Boot 4.0.6 / Java 21 기준으로 작성되어 있다. 따라서 반드시 이 버전에 맞는 제안을 해야 한다

## 모듈 구조

```bash
├── clean-ecommerce
│    ├── adapter      # 어댑터 레이어
│    ├── application  # 애플리케이션 레이어
│    ├── batch        # 배치 작업
│    ├── bootstrap    # 애플리케이션 실행 모듈 
│    └── domain       # 도메인 레이어
├── build.gradle.kts
└── ...
```

## 도메인 및 요구사항 정의

- 필요할 때 다음 파일을 참고한다: [도메인 모델 정의](docs/domain-model.md)

## RDB 스키마 정의

- 필요할 때 다음 파일을 참고한다: [스키마 정의](bootstrap/src/main/resources/schema.sql)
  - 완성된 상태가 아닐 수 있다.

## 절대적으로 지켜야 하는 내용

- **의존성 방향은 바깥에서 안쪽으로 향한다**: Adapter/Infrastructure → Application → Domain
- 사용자에게 질문을 받았을 때 시점의 현재 코드 구조
  - 이 말은, 내가 너에게 질문 또는 해결책을 제시했을 때 현재 구현된 코드와 구조를 따라야 한다는 것이다.
