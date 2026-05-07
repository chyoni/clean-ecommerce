# 상품 등록 요청 흐름

> Aggregate 명세는 [../catalog/product.md](../catalog/product.md) 참조

```bash
  예시 입력 JSON

  {
    "sellerId": 3,
    "categoryId": 12,
    "productName": "갤럭시 S25",
    "descriptionHtml": "<p>최신 플래그십</p>",
    "brand": "Samsung",
    "manufacturer": "Samsung Electronics",
    "salesStartDate": "2026-05-10T00:00:00",
    "attributes": { "screen_size": 6.2, "storage": 256, "color": "BLACK" },
    "skus": [
      { "skuCode": "GS25-BLK-256", "options": {"color":"BLACK","storage":256}, "price": 1500000, "stockQuantity": 100 },
      { "skuCode": "GS25-WHT-256", "options": {"color":"WHITE","storage":256}, "price": 1500000, "stockQuantity": 80 }
    ],
    "images": [
      { "imageType": "THUMBNAIL", "imagePath": "images/gs25/thumb.jpg", "displayOrder": 0 },
      { "imageType": "MAIN",      "imagePath": "images/gs25/main.jpg",  "displayOrder": 1 }
    ]
  }

  ---
  호출 흐름

  [Adapter: 미구현]   ← 아직 Controller 없음
         │
         ▼
  ProductRegisterUseCase  (application/port/in)
         │ register(payload)
         ▼
  ProductRegisterService  (application/product)
         │
         │ ① memberRepository.findByMemberId(sellerId)
         │      └ 없으면 IllegalArgumentException("존재하지 않는 판매자입니다")
         │
         │ ② categoryRepository.findByCategoryId(categoryId)
         │      └ 없으면 IllegalArgumentException("존재하지 않는 카테고리입니다")
         │
         │ ③ productAttributeSchemaRepository.findByCategoryId(categoryId)
         │      └ 있으면 schema.validate(attributes)
         │            • 필수 키 누락 검증
         │            • AttributeType 일치 검증 (STRING/NUMBER/DATE/BOOLEAN/ENUM)
         │            • ENUM allowedValues 위반 검증
         │            • 정의되지 않은 키 사용 시 거부
         │
         │ ④ Product.register(payload, seller, category)  ← 도메인 팩토리
         │      • validateSku        : skus 비어있으면 IllegalStateException
         │      • validateSeller     : seller.isSeller() 아니면 IllegalStateException
         │      • validateSalesDate  : 종료일 < 시작일이면 IllegalStateException
         │      • payload.skus 순회 → product.registerSku(skuCode, options, price, stockQuantity)
         │      • payload.images 순회 → product.addImage(imageType, imagePath, displayOrder)
         │      • 상태 기본값: DRAFT
         │      • 판매 시작일 기본값: now()
         │
         │ ⑤ productRepository.save(product)
         │      • @OneToMany cascade=ALL 로 SKU·Image 일괄 저장 (트랜잭션 1개)
         │
         ▼
     Product 반환
```
