# Bounded Context 맵

이 프로젝트는 오픈마켓 서비스를 여러 Bounded Context 로 분리한다.
각 Context 는 자신의 Aggregate 와 UseCase 를 소유하며 다른 Context 의 내부를 직접 참조하지 않는다.

## Context 목록

### ✅ Catalog (구현 완료: P0/P1)

**책임**: 상품 카탈로그 관리 — 카테고리 분류 체계, 카테고리별 속성 스키마 정의, 상품(Product) 등록·수정·상태 관리

**포함 Aggregate**:
- [Product](aggregate/catalog/product.md) — 상품(Root) + SKU(N) + Image(N)
- [Category](aggregate/catalog/category.md) — 카테고리 트리
- [ProductAttributeSchema](aggregate/catalog/product-attribute-schema.md) — 카테고리별 속성 규칙 + AttributeDefinition

**현재 구현 상태**:
- Product 등록 UseCase 완료 (`ProductRegisterUseCase`, `ProductRegisterService`)
- Category / ProductAttributeSchema 관리 UseCase 미구현 — 현재는 시드 데이터로만 존재
- REST Adapter 미구현

---

### ✅ Member (구현: 도메인 레이어, UseCase 일부 미구현)

**책임**: 회원 등록·인증·역할 관리 (NORMAL / SELLER / ADMIN)

**포함 Aggregate**:
- [Member](aggregate/member/member.md) — 회원(단일 엔티티, Value Object: Email, PhoneNumber)

**현재 구현 상태**:
- 도메인 레이어(`Member`, `MemberRole`, `Email`, `PhoneNumber`) 구현 완료
- 회원 등록 / 로그인 / 인증·인가 UseCase 미구현
- REST Adapter 미구현

---

### ⬜ Inventory (미구현)

**책임**: SKU 단위 재고 관리 — 입고/출고/예약 차감/복원

> 참고: 현재 `ProductSku.stockQuantity` 는 Catalog 내에 있지만 동시성 이슈를 고려하면 별도 Context 로 분리하는 것이 적합하다.

---

### ⬜ Order (미구현)

**책임**: 주문 생성·분할·상태 머신 (결제대기 → 결제완료 → … → 구매확정 / 취소 / 반품)

---

### ⬜ Payment (미구현)

**책임**: PG 연동, 결제 수단, 멱등성, 부분 환불

---

### ⬜ Shipping (미구현)

**책임**: 배송지 관리, 송장번호, 배송 상태 추적

---

### ⬜ Review (미구현)

**책임**: 구매 인증 리뷰, 별점, 사진/동영상, 신고, 셀러 답글

---

### ⬜ Promotion (미구현)

**책임**: 쿠폰, 즉시할인, 적립금, 기획전, 타임세일

---

## Context 간 관계

```
Member ◄──── Catalog (Product.sellerId → Member.memberId)
             Catalog (Product.categoryId → Category.categoryId)
             Catalog (Schema.categoryId → Category.categoryId)

Order ───────► Catalog  (주문 시 Product/SKU 정보 조회)
Order ───────► Inventory (재고 예약 차감)
Order ───────► Payment  (결제 요청)
Order ───────► Shipping (배송 시작)

Review ──────► Order    (구매 인증 — 해당 SKU 를 구매한 회원만 작성)
Promotion ───► Catalog  (특정 카테고리/상품에 쿠폰 적용)
```

**현재 구현된 Context 간 참조 방식**: ID 값만 전달 (예: `ProductRegisterPayload.sellerId`, `categoryId`).
도메인 Aggregate 간 직접 참조는 같은 트랜잭션 안의 로딩에만 허용하고 있다.

> 미구현 Context 가 추가될 때는 Anti-Corruption Layer(ACL) 또는 이벤트 기반 통합(Kafka) 방식을 검토한다.
