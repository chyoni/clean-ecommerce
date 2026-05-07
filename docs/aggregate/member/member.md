# Member Aggregate

## 개요

- **Bounded Context**: Member
- **Aggregate Root**: `Member` (`domain/.../member/Member.java`)
- **소유권**: 본인 (RU) / Operator (CRUD) — 역할 변경은 Operator 만
  - 상세 규칙은 [소유권 매트릭스](../../ownership-matrix.md) 참조

---

## Aggregate 구성

```
Member  (Aggregate Root, 단일 엔티티)
├── email: Email  (Value Object, @Embedded)
└── phoneNumber: PhoneNumber  (Value Object, @Embedded)
```

멤버 엔티티 없음 — 단일 엔티티로 구성.

---

## 필드

| 필드 | 타입 | 설명 |
|---|---|---|
| `memberId` | `Long` | PK, auto increment |
| `name` | `String` | 회원 이름 (필수) |
| `encodedPassword` | `String` | 해시된 비밀번호 (`PasswordEncoder` 위임) |
| `email` | `Email` (VO) | 이메일 (필수, 형식 검증) |
| `phoneNumber` | `PhoneNumber` (VO) | 전화번호 (필수, 형식 검증) |
| `role` | `MemberRole` | 역할 (기본 `NORMAL`) |

---

## MemberRole

| 값 | 한글 | 설명 |
|---|---|---|
| `NORMAL` | 일반 유저 | 구매자. 상품 조회/주문 가능 |
| `SELLER` | 판매자 | 상품 등록·관리 가능. `isSeller()` 로 확인 |
| `ADMIN` | 관리자 | 플랫폼 운영자. 카테고리/스키마 관리, 상품 검수 |

---

## 불변식

| 불변식 | 강제 위치 |
|---|---|
| `name`, `email`, `phoneNumber`, `rawPassword` 필수 | `Member.register()` 내 `requireNonNull` |
| `ADMIN` 역할은 기존 `ADMIN` 만 부여 가능 | `Member.validateAdminRole()` |

---

## 핵심 메서드

| 메서드 | 설명 |
|---|---|
| `register(payload, passwordEncoder)` | 정적 팩토리. 비밀번호는 `PasswordEncoder.encode()` 위임 (도메인이 구현체를 모름) |
| `changeEmail(email)` | 이메일 변경. `Email` VO 재생성 (형식 검증 포함) |
| `changePhoneNumber(phoneNumber)` | 전화번호 변경. `PhoneNumber` VO 재생성 |
| `changeMemberRole(role)` | 역할 변경. `ADMIN` 자가 승격 방지 불변식 적용 |
| `isSeller()` | `role == SELLER` 여부 반환 — Product 등록 시 자격 검증에 사용 |

---

## 라이프사이클

```
(없음) → register() → 활성
         changeEmail()
         changePhoneNumber()
         changeMemberRole()
         휴면 전환 (미구현)
         탈퇴 (미구현 — 개인정보 보존 vs 즉시 삭제 정책 미결정)
```

---

## 현재 구현 상태

| 항목 | 상태 |
|---|---|
| 도메인 레이어 (`Member`, `MemberRole`, `Email`, `PhoneNumber`, `PasswordEncoder`) | ✅ 구현 |
| Out 포트 (`MemberRepository.findByMemberId`) | ✅ 구현 (Product 등록 시 seller 조회용) |
| 회원 등록 UseCase | ⬜ 미구현 |
| 로그인 / 인증 UseCase | ⬜ 미구현 |
| 휴면·탈퇴 UseCase | ⬜ 미구현 |
| REST Adapter | ⬜ 미구현 |

---

## 영속화 전략

- `@Entity`. `Email`, `PhoneNumber` 는 `@Embedded` Value Object.
- Out 포트: `MemberRepository extends Repository<Member, Long>` (Spring Data 직접 상속).
- `PasswordEncoder` 는 도메인 인터페이스 — 구현체(BCrypt 등)는 adapter 레이어에 위치.
