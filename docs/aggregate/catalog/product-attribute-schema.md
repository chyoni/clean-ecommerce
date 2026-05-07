# ProductAttributeSchema Aggregate

## 개요

- **Bounded Context**: Catalog
- **Aggregate Root**: `ProductAttributeSchema` (`domain/.../product/schema/ProductAttributeSchema.java`)
- **소유권**: Operator (CRUD) / Seller (R) / Customer (—)
  - 상세 규칙은 [소유권 매트릭스](../../ownership-matrix.md) 참조

---

## Aggregate 구성

```
ProductAttributeSchema  (Aggregate Root)
└── AttributeDefinition  1:N  (cascade=ALL, orphanRemoval=true)
```

**외부 참조**: `categoryId` (Long) — Category Aggregate 와 1:1 대응.
Category 를 직접 참조하지 않고 ID 만 보관한다 (다른 Aggregate 간 ID 참조 원칙).

---

## 왜 Category 와 분리된 Aggregate 인가

- **라이프사이클 차이**: 카테고리는 분류 체계 변경 시 갱신. 속성 스키마는 운영 정책(필수 항목, 허용값) 개정 시 별도로 갱신.
- **책임 분리**: Category 는 계층 트리 탐색이 주 역할. Schema 는 상품 입력 검증이 주 역할.
- **Aggregate 크기 최소화**: 두 라이프사이클을 하나의 Aggregate 에 넣으면 Category 조회마다 Schema+Definition 이 로딩되어 불필요한 부하 발생.

---

## 필드

### ProductAttributeSchema (Root)

| 필드 | 타입 | 설명 |
|---|---|---|
| `schemaId` | `Long` | PK |
| `categoryId` | `Long` | 대응 Category ID (DB unique) |
| `definitions` | `List<AttributeDefinition>` | 속성 정의 목록 |

### AttributeDefinition (멤버)

| 필드 | 타입 | 설명 |
|---|---|---|
| `definitionId` | `Long` | PK |
| `schema` | `ProductAttributeSchema` (LAZY) | 소속 Schema |
| `attributeKey` | `String` | 속성 키 (예: `screen_size`) |
| `attributeType` | `AttributeType` | `STRING` / `NUMBER` / `DATE` / `BOOLEAN` / `ENUM` |
| `required` | `boolean` | 필수 여부 |
| `allowedValues` | `List<String>` | ENUM 타입일 때 허용 값 목록 (JSON 컬럼) |

---

## 불변식

| 불변식 | 강제 위치 |
|---|---|
| Schema 1개 = Category 1개 | DB `UNIQUE(category_id)` |
| `(schema_id, attribute_key)` 중복 금지 | DB `UNIQUE(schema_id, attribute_key)` |
| `attributeKey` / `attributeType` 필수 | `AttributeDefinition.create()` 내 `requireNonNull` |
| 멤버 생성은 Root 를 통해서만 | `AttributeDefinition.create()` 가 package-private |

---

## 핵심 메서드

### ProductAttributeSchema

| 메서드 | 설명 |
|---|---|
| `create(categoryId)` | 정적 팩토리 |
| `addDefinition(key, type, required, allowedValues)` | 속성 정의 추가. `AttributeDefinition.create()` 위임 |
| `removeDefinition(attributeKey)` | 키 기준 속성 정의 제거 |
| `validate(Map<String,Object> attributes)` | 상품 등록 시 attributes 값 검증 — 필수 누락, 타입 불일치, ENUM 위반, 미정의 키 사용 시 `IllegalArgumentException` |

### AttributeDefinition

| 메서드 | 설명 |
|---|---|
| `validateValue(Object value)` | 단일 속성 값 검증 (type / required / allowedValues) |

---

## validate() 동작 규칙

| 검증 | 동작 |
|---|---|
| 필수 속성 (`required=true`) 값 없음 | `IllegalArgumentException("필수 속성 '...' 이 누락되었습니다")` |
| 타입 불일치 (예: NUMBER 에 String 전달) | `IllegalArgumentException` |
| ENUM 타입에 `allowedValues` 에 없는 값 전달 | `IllegalArgumentException` |
| 정의되지 않은 속성 키 전달 | `IllegalArgumentException("정의되지 않은 속성 키입니다: ...")` |
| 스키마가 없는 카테고리 | 검증 skip — 어떤 attributes 도 허용 |

---

## 라이프사이클

```
(없음) → create(categoryId) → 활성
         addDefinition()           (정의 추가)
         removeDefinition(key)     (정의 제거)
         (Schema 삭제 UseCase 미구현)
```

---

## 현재 구현 상태

| 항목 | 상태 |
|---|---|
| 도메인 레이어 (Schema + Definition 생성·검증) | ✅ 구현 |
| Out 포트 (`findByCategoryId`) | ✅ 구현 |
| Schema/Definition 관리 UseCase | ⬜ 미구현 — 현재는 시드 데이터로만 존재 |
| REST Adapter | ⬜ 미구현 |

---

## 영속화 전략

- `@Entity`. Definition 에 `cascade=ALL`, `orphanRemoval=true`.
- `AttributeDefinition.allowedValues` → `JsonStringListConverter` (Jackson) 로 MySQL `json` 컬럼에 직렬화.
- Out 포트: `ProductAttributeSchemaRepository extends Repository<ProductAttributeSchema, Long>`.
