# Category Aggregate

## 개요

- **Bounded Context**: Catalog
- **Aggregate Root**: `Category` (`domain/.../product/category/Category.java`)
- **소유권**: Operator (CRUD) / Seller (R) / Customer (R)
  - 상세 규칙은 [소유권 매트릭스](../../ownership-matrix.md) 참조

---

## Aggregate 구성

```
Category  (Aggregate Root, 단일 엔티티)
└── parent: Category (자기 참조, 트리 구조)
```

Category 는 멤버 엔티티를 포함하지 않는다.
`ProductAttributeSchema` 는 `categoryId` 를 참조하지만 **별도 Aggregate** — Category 에 종속되지 않는다.

---

## 필드

| 필드 | 타입 | 설명 |
|---|---|---|
| `categoryId` | `Long` | PK, auto increment |
| `name` | `String` | 카테고리명 (필수) |
| `parent` | `Category` (LAZY) | 상위 카테고리 (대분류이면 null) |

---

## 불변식

| 불변식 | 강제 위치 |
|---|---|
| `name` 필수 | `Category.create()` 내 `requireNonNull` |
| 트리 사이클 금지 | DB FK(자기 참조) + 미래 UseCase 에서 검증 필요 |

---

## 핵심 메서드

| 메서드 | 설명 |
|---|---|
| `create(name, parentCategory)` | 정적 팩토리. `parentCategory` 가 null 이면 대분류 |

---

## 라이프사이클

```
(없음) → create() → 활성
                   (수정 UseCase 미구현)
                   (삭제 UseCase 미구현 — 하위 카테고리 있을 때 삭제 정책 필요)
```

---

## 현재 구현 상태

| 항목 | 상태 |
|---|---|
| 도메인 레이어 (`Category.create`) | ✅ 구현 |
| Out 포트 (`CategoryRepository.findByCategoryId`) | ✅ 구현 |
| Category 관리 UseCase (CRUD) | ⬜ 미구현 — 현재는 시드 데이터(`schema.sql`)로만 존재 |
| REST Adapter | ⬜ 미구현 |

---

## 시드 데이터 (현재 구조)

`schema.sql` 에 4단계 계층 시드 포함:
- 대분류(parent=null): 전자기기, 패션, 식품, 가전
- 중분류: 노트북, 스마트폰, 태블릿 / 남성의류, 여성의류 / 신선식품, 가공식품 / 대형가전
- 소분류: 티셔츠, 바지 / 냉장고, 세탁기

---

## 영속화 전략

- `@Entity`, `@ManyToOne(fetch=LAZY)` 자기 참조.
- Out 포트: `CategoryRepository extends Repository<Category, Long>` (Spring Data 직접 상속).
