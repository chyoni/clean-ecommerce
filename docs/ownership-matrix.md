# 소유권 / 권한 매트릭스

각 엔티티에 대해 어떤 주체가 어떤 작업을 할 수 있는지 정의한다.

- **C** = Create, **R** = Read, **U** = Update, **D** = Delete
- **—** = 접근 권한 없음
- **본인만** = 자신이 소유한 데이터에 한해

## 매트릭스

| 엔티티 | Operator (ADMIN) | Seller | Customer (NORMAL) | 비고 |
|---|---|---|---|---|
| **Category** | CRUD | R | R | 카테고리 트리는 Operator 만 관리. 셀러/구매자는 조회만 |
| **ProductAttributeSchema** | CRUD | R | — | 카테고리별 속성 규칙 정의. 구매자에겐 노출 불필요 |
| **AttributeDefinition** | CRUD | R | — | Schema 의 멤버 엔티티, Schema 와 동일 정책 |
| **Product** (Root) | R + 상태전이(검수) | CRUD (본인만) | R | 셀러는 자신이 등록한 상품만 수정 가능. Operator 는 검수 후 PENDING_REVIEW→AVAILABLE or DRAFT 로 전이 |
| **ProductSku** | R | CRUD (본인 상품만) | R | Product Aggregate 내 멤버. Product 접근 권한과 동일 |
| **ProductImage** | R | CRUD (본인 상품만) | R | Product Aggregate 내 멤버. Product 접근 권한과 동일 |
| **Member** (본인) | CRUD | RU (본인) | RU (본인) | 본인 프로필(이메일·전화번호) 변경 가능 |
| **MemberRole** | U | — | — | 역할 변경은 Operator 만. `validateAdminRole` 로 ADMIN 자가 승격 방지 |

## 왜 이렇게 나눴는가

**Category / ProductAttributeSchema 가 Operator 전용인 이유**
- 카테고리 분류 체계가 셀러에 의해 임의 생성되면 검색·필터·정산 카테고리 매핑이 깨진다.
- 속성 스키마(Schema/Definition)가 셀러 자유 입력이면 동일 카테고리 안에서 `{"size":"M"}` / `{"SIZE":"medium"}` 처럼 키가 파편화되어 비교·검색이 불가능해진다.

**Product 가 Seller CRUD + Operator 상태전이인 이유**
- 셀러가 직접 등록·수정하는 자산이지만 플랫폼이 심사 프로세스를 통해 실제 판매 노출을 통제한다.
- `PENDING_REVIEW → AVAILABLE` 전이는 Operator 가 심사 승인해야만 가능하다 (미래 어드민 UseCase 에서 강제).

## 현재 코드 적용 상태

| 항목 | 적용 여부 |
|---|---|
| 도메인 레이어 불변식(`validateSeller`, `validateAdminRole`) | ✅ 적용됨 |
| Spring Security 인가(`@PreAuthorize`, `SecurityFilterChain`) | ⬜ 미도입 |
| UseCase 레이어 권한 체크 | ⬜ 미구현 (향후 `sellerId == 요청자 ID` 비교 등) |
| REST Adapter 레이어 권한 분리 | ⬜ 미구현 |

> Spring Security 도입 시 Operator/Seller/Customer 엔드포인트는 별도 경로 또는 `@PreAuthorize("hasRole('ADMIN')")` 로 분리한다.
