# 프로젝트 진행 현황 (2026-05-07 기준)

## 완료된 작업

### P0 — Product 도메인 핵심 구조 (2026-05-06)

**설계 결정**
- `Product 1 : ProductSku N` 구조 도입. 모든 상품은 SKU를 최소 1개 보유
- `Product` 가 Aggregate Root. `ProductSku` / `ProductImage` 는 내부 멤버 엔티티
- `Product` 가 멤버 생성 책임: `product.registerSku(...)`, `product.addImage(...)` 를 통해서만 생성
- `ProductSku.create()` / `ProductImage.create()` 는 package-private — 외부 직접 호출 불가
- JPA: `@OneToMany(cascade=ALL, orphanRemoval=true)`, `ProductRepository` 만 외부 노출

**신규/변경 파일**
- `domain/.../product/ProductSku.java`
- `domain/.../product/ProductImage.java`
- `domain/.../product/ProductImageType.java`
- `Product.java`: price/stockQuantity/productImagePath 제거 → descriptionHtml/skus/images 추가
- `ProductRegisterPayload.java`: price/stockQuantity/productImagePath 제거 → descriptionHtml 추가
- `schema.sql`: product 테이블 수정 + product_sku/product_image 테이블 추가
- `ProductFixture.java`: 빌더 동기화
- 테스트: `ProductSkuTest`, `ProductImageTest`

---

### P1 — 속성 스키마 + 상태 머신 (2026-05-06)

**설계 결정**
- `ProductAttributeSchema` 를 **별도 Aggregate Root** 로 분리. Category는 단순 트리 유지
  - 이유: 라이프사이클이 다름(운영자가 별도 정의·개정), Aggregate 크기 최소화
  - `ProductAttributeSchema.validate(Map)` 이 필수 누락·타입 불일치·허용값 위반 검증
- `Product.attributes` : `String` → `Map<String, Object>` + `JsonAttributeConverter` (Jackson)
  - `changeAttributes(Map, ProductAttributeSchema)` 메서드 추가
- **`ProductStatus` 상태 머신**: enum 상수에 `allowedNext()` abstract 메서드 구현
  - DRAFT → PENDING_REVIEW
  - PENDING_REVIEW → AVAILABLE | DRAFT
  - AVAILABLE → OUT_OF_STOCK | DISCONTINUED
  - OUT_OF_STOCK → AVAILABLE | DISCONTINUED
  - DISCONTINUED → (없음)
  - `Product.changeProductStatus` 위반 시 `IllegalStateException`

**신규/변경 파일**
- `AttributeType.java` (enum: STRING/NUMBER/DATE/BOOLEAN/ENUM)
- `AttributeDefinition.java` (@Entity, package-private create, `validateValue`)
- `ProductAttributeSchema.java` (@Entity Aggregate Root, `validate(Map)`)
- `JsonAttributeConverter.java`, `JsonStringListConverter.java` (package-private)
- `application/.../port/out/CategoryRepository.java`
- `application/.../port/out/ProductAttributeSchemaRepository.java`
- `ProductAttributeSchemaFixture.java` (testFixtures)
- 테스트: `ProductStatusTest`, `AttributeDefinitionTest`, `ProductAttributeSchemaTest`

---

### P1 후속1 — Payload ID 기반 리팩터 (2026-05-06)

**설계 결정**
- `ProductRegisterPayload` 에서 `Member seller` / `Category category` 도메인 객체 제거 → `Long sellerId` / `Long categoryId` ID만 전달
- `Product.register()` 시그니처 변경: `register(payload)` → `register(payload, Member seller, Category category)`. 서비스가 엔티티 로딩 후 주입
- `MemberRepository` out 포트 신규 추가 (`findByMemberId`). Spring Data 직접 상속 패턴

**신규/변경 파일**
- `application/.../port/out/MemberRepository.java` (신규)
- `ProductRegisterPayload.java`, `Product.java`, `ProductRegisterService.java`
- `ProductFixture.java`, `ProductTest.java`, `ProductRegisterUseCaseTest.java`

---

### P1 후속2 — SKU/Image 동시 등록 + options JSON 매핑 (2026-05-07)

**설계 결정**
- 상품 등록 시 SKU/Image 동시 처리 — `ProductRegisterPayload` 에 `List<SkuPayload> skus`(최소 1개 필수), `List<ImagePayload> images`(선택) 추가
- `Product.register()` 내부에서 순회하며 `registerSku()` / `addImage()` 호출. 트랜잭션 하나로 일관성 보장
- `ProductSku.options`: `String` → `Map<String, Object>` + `JsonAttributeConverter` 재사용

**신규/변경 파일**
- `domain/.../product/SkuPayload.java`
- `domain/.../product/ImagePayload.java`
- `ProductRegisterPayload.java`, `Product.java`, `ProductSku.java`
- `ProductFixture.java`, `ProductTest.java`, `ProductSkuTest.java`

---

### 이미지 스토리지 인프라 (2026-05-07)

MinIO + AWS SDK v2 기반 이미지 업로드 인프라 구현 완료 (참고: `docs/images-storage.md`).

**구현 범위**
- Out Port: `ImageStoragePort` (`issueUploadUrl`, `resolvePublicUrl`)
- In Port: `IssueImageUploadUrlUseCase` / Command / Result
- 서비스: `IssueImageUploadUrlService`, `StorageKeyGenerator`, `ImageUploadPolicy`
- 어댑터: `S3PresignedImageStorageAdapter`, `S3StorageConfig`, `S3StorageProperties`
- 인프라: `docker-compose.yaml` (minio + minio-bootstrap), `application.yaml` 설정

---

### Web Adapter — ProductController (2026-05-07)

- `POST /api/products/images/upload-url` — `IssueImageUploadUrlUseCase` 호출
- `POST /api/products` — `ProductRegisterUseCase` 호출

---

## 남은 작업

~~### P1 — 즉시 구현 대상~~

~~#### Category CRUD~~
~~`Category` 도메인(entity, create 팩토리)만 존재. UseCase/Service/Controller 없음.~~

| 항목                                                               | 상태 |
|------------------------------------------------------------------|----|
| `CategoryCreateUseCase` + `CategoryCreateService` (parent 지정 가능) | 구현 |
| `CategoryRepository` out port (application/port/out)             | 구현 |
| Controller: `POST /api/v1/categories`                            | 구현 |

~~> 상품 등록 시 categoryId가 필요하므로 카테고리가 먼저 있어야 함.~~

---

### P2 — 그 다음

#### ProductAttributeSchema 관리 (운영자 기능)
`ProductAttributeSchema` / `AttributeDefinition` 도메인은 있음. CRUD UseCase 없음.

| 항목 | 상태 |
|------|------|
| 스키마 생성/조회 UseCase + Service | 미구현 |
| 정의 추가/수정/삭제 UseCase + Service | 미구현 |
| Controller: `POST /api/schemas`, `POST /api/schemas/{id}/definitions` | 미구현 |

#### 이미지 Dangling Key GC (batch 모듈)
Presigned URL 발급 후 미업로드 시 MinIO orphan 객체 누적 문제.

| 항목 | 상태 |
|------|------|
| `image_upload_intent` 테이블 신설 | 미구현 |
| batch 모듈 스케줄 배치 — 만료된 미등록 storageKey 삭제 | 미구현 |

---

### P3 — 이후

| 항목 | 비고 |
|------|------|
| 상품 조회 UseCase + Controller | 현재 등록만 있고 조회 없음 |
| Member 인증/인가 | `Member` 도메인 + JWT 라이브러리 있지만 Spring Security 설정 없음 |
| Order / Payment / Delivery / Review | 다른 바운디드 컨텍스트, 미착수 |

---

## 설계 컨벤션 (반드시 지킬 것)

### Aggregate Root 멤버 생성 책임

애그리거트 루트가 멤버 엔티티 생성까지 책임진다.

```
// 올바른 방법
product.registerSku(skuCode, options, price, stockQuantity);

// 금지
ProductSku.create(product, skuCode, ...); // package-private이므로 컴파일 에러
```

- 팩토리 메서드는 `package-private`으로 두고, Aggregate Root에 위임 메서드를 추가
- Application Service는 루트 메서드만 호출

### Out 포트 구현 패턴 — Spring Data 직접 상속

`application/.../port/out/` 인터페이스는 Spring Data `Repository<T, ID>` 를 직접 상속한다. 별도 JPA Adapter 클래스를 따로 만들지 않는다.

```java
// 올바른 방법
public interface CategoryRepository extends Repository<Category, Long> {
    Optional<Category> findByCategoryId(Long categoryId);
    Category save(Category category);
}

// 금지 — JPA Adapter 클래스를 별도로 만드는 것
public class CategoryJpaAdapter implements CategoryRepository { ... }
```

Spring Data가 런타임에 구현체를 자동 생성한다. 기존 `ProductRepository`, `CategoryRepository`, `ProductAttributeSchemaRepository` 모두 이 패턴을 사용하고 있음.
