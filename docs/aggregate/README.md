# Aggregate 문서 인덱스

이 디렉토리에는 **현재 구현된** Aggregate 의 설계 명세를 모아둔다.
새 Aggregate 를 추가하거나 기존 명세가 바뀌면 반드시 이 파일도 갱신한다.

| Aggregate | Bounded Context | 상태 | 문서 |
|---|---|---|---|
| Product | Catalog | ✅ 구현 (P0/P1) | [catalog/product.md](catalog/product.md) |
| Category | Catalog | ✅ 구현 (관리 UseCase 미구현) | [catalog/category.md](catalog/category.md) |
| ProductAttributeSchema | Catalog | ✅ 구현 (관리 UseCase 미구현) | [catalog/product-attribute-schema.md](catalog/product-attribute-schema.md) |
| Member | Member | ✅ 구현 (일부 UseCase 미구현) | [member/member.md](member/member.md) |
| Order | Order | ⬜ 미구현 | — |
| Payment | Payment | ⬜ 미구현 | — |
| Inventory | Inventory | ⬜ 미구현 | — |
| Shipment | Shipping | ⬜ 미구현 | — |
| Review | Review | ⬜ 미구현 | — |
| Coupon / Promotion | Promotion | ⬜ 미구현 | — |

## 흐름 문서

| 흐름 | 문서 |
|---|---|
| 상품 등록 요청 흐름 | [product/register-flow.md](product/register-flow.md) |

## 관련 문서

- [Bounded Context 맵](../bounded-contexts.md)
- [소유권 / 권한 매트릭스](../ownership-matrix.md)
- [도메인 요구사항 카탈로그](../domain-model.md)
