# 상품 등록 요청 흐름

```bash
⏺ Client                                                                                                                                                                                                                                                                                                           
    │  POST /products                                                                                                                                                                                                                                                                                              
    │  {                                                                                                                                                                                                                                                                                                           
    │    productName, brand, manufacturer, ...,   ← 카탈로그
    │    skus: [                                                                                                                                                                                                                                                                                                   
    │      { skuCode: "IPHONE-128", options: {"color":"BLACK"}, price: 1500000, stockQuantity: 100 },
    │      { skuCode: "IPHONE-256", options: {"color":"BLACK"}, price: 1700000, stockQuantity: 50 }                                                                                                                                                                                                                
    │    ],       
    │    images: [                                                                                                                                                                                                                                                                                                 
    │      { type: "THUMBNAIL", path: "img/thumb.jpg", order: 0 },
    │      { type: "DETAIL",    path: "img/detail.jpg", order: 1 }                                                                                                                                                                                                                                                 
    │    ]                                                                                                                                                                                                                                                                                                         
    │  }
    ▼                                                                                                                                                                                                                                                                                                              
  [Adapter — Controller]
    │  요청 DTO → UseCase 커맨드로 변환                                                                                                                                                                                                                                                                            
    ▼
  [Application — ProductRegisterService]                                                                                                                                                                                                                                                                           
    │  1. Product.register(payload)                         ← 카탈로그 애그리거트 루트 생성
    │  2. for each sku  → product.registerSku(...)          ← 루트가 SKU 생성 및 편입                                                                                                                                                                                                                              
    │  3. for each image → product.addImage(...)            ← 루트가 이미지 생성 및 편입                                                                                                                                                                                                                           
    │  4. productRepository.save(product)                   ← 애그리거트 통째로 저장                                                                                                                                                                                                                               
    ▼                                                                                                                                                                                                                                                                                                              
  [Adapter — ProductRepository (JPA)]                                                                                                                                                                                                                                                                              
    │  cascade = ALL 이므로 단일 트랜잭션 안에서                                                                                                                                                                                                                                                                   
    │  INSERT product                                                                                                                                                                                                                                                                                              
    │  INSERT product_sku × 2                                                                                                                                                                                                                                                                                      
    │  INSERT product_image × 2                                                                                                                                                                                                                                                                                    
    ▼             
  DB    
```

## Schema, AttributeDefinition 포함

```bash
⏺ 흐름을 단계별로 정리하겠습니다.                                                                                                                                                                                                                                                                                  
                                                                                                                                                                                                                                                                                                                 
  ---                                                                                                                                                                                                                                                                                                              
  전체 구조 한 눈에
                                                                                                                                                                                                                                                                                                                   
  [운영자] ────→ ProductAttributeSchema + AttributeDefinition (사전 정의)
                          ↓ categoryId 참조                                                                                                                                                                                                                                                                        
  [Category] ─────────────── (단순 트리, 변경 없음)                                                                                                                                                                                                                                                                
                          ↑ category_id FK                                                                                                                                                                                                                                                                         
  [셀러] ──────→ ProductRegisterPayload (attributes: Map 포함) ──→ Product                                                                                                                                                                                                                                         
                                                                                                                                                                                                                                                                                                                   
  ---                                                    
  엔티티별 데이터                                                                                                                                                                                                                                                                                                  
                                                         
  Category — 분류만
  category_id = 3                                                                                                                                                                                                                                                                                                  
  name        = "노트북"
  parent_id   = 1  (→ "전자기기")                                                                                                                                                                                                                                                                                  
                                                         
  ProductAttributeSchema — "노트북 카테고리 상품은 어떤 속성을 가져야 하는가" 정의                                                                                                                                                                                                                                 
  schema_id   = 10                                                                                                                                                                                                                                                                                                 
  category_id = 3   (↑ 노트북 카테고리와 1:1)                                                                                                                                                                                                                                                                      
                                                                                                                                                                                                                                                                                                                   
  AttributeDefinition — 속성 하나하나의 규칙                                                                                                                                                                                                                                                                       
  definition_id  = 1                                                                                                                                                                                                                                                                                               
  schema_id      = 10                                                                                                                                                                                                                                                                                              
  attribute_key  = "screen_size"                                                                                                                                                                                                                                                                                   
  attribute_type = NUMBER       
  required       = true                                                                                                                                                                                                                                                                                            
  allowed_values = null                                  
                       
  definition_id  = 2
  schema_id      = 10
  attribute_key  = "storage"
  attribute_type = ENUM     
  required       = true
  allowed_values = ["256", "512", "1024"]   ← 이 값들만 허용

  Product — 실제 상품. attributes는 위 정의를 따라야 함                                                                                                                                                                                                                                                            
  product_id   = 100
  category_id  = 3   (→ 노트북)                                                                                                                                                                                                                                                                                    
  product_name = "맥북 프로 16인치"                                                                                                                                                                                                                                                                                
  attributes   = {"screen_size": 16.2, "storage": "512"}   ← Map<String,Object>
                                                                                                                                                                                                                                                                                                                   
  ---                                                                                                                                                                                                                                                                                                              
  상품 등록 흐름 (ProductRegisterService)                                                                                                                                                                                                                                                                          
                                                                                                                                                                                                                                                                                                                   
  셀러가 보내는 데이터:                                  
    {                                                                                                                                                                                                                                                                                                              
      category_id: 3,                                    
      product_name: "맥북 프로",
      attributes: {"screen_size": 16.2, "storage": "512"}                                                                                                                                                                                                                                                          
    }
                                                                                                                                                                                                                                                                                                                   
  Service 처리 순서:                                                                                                                                                                                                                                                                                               
    1. categoryRepository.findById(3)
       └ 없으면 → IllegalArgumentException ("존재하지 않는 카테고리")                                                                                                                                                                                                                                              
                                                                                                                                                                                                                                                                                                                   
  category_id  = 3   (→ 노트북)
  product_name = "맥북 프로 16인치"
  attributes   = {"screen_size": 16.2, "storage": "512"}   ← Map<String,Object>

  ---
  상품 등록 흐름 (ProductRegisterService)

  셀러가 보내는 데이터:
    {
      category_id: 3,
      product_name: "맥북 프로",
      attributes: {"screen_size": 16.2, "storage": "512"}
    }

  Service 처리 순서:
    1. categoryRepository.findById(3)
       └ 없으면 → IllegalArgumentException ("존재하지 않는 카테고리")

    2. productAttributeSchemaRepository.findByCategoryId(3)
       ├ 없으면 → 검증 skip (스키마 미정의 카테고리는 자유 attributes)
       └ 있으면 → schema.validate({"screen_size": 16.2, "storage": "512"})
                    ├ "screen_size" 필수인데 없음? → 예외
                    ├ "screen_size" 값이 String? → 예외 (NUMBER여야 함)
                    ├ "storage" 값이 "999"? → 예외 (allowed_values에 없음)
                    └ 정의 안 된 키 "color" 있음? → 예외

    3. 검증 통과 → Product.register(payload) → save

  ---
  Schema / Definition은 누가 만드는가

  **운영자(Operator)**가 만들어야 합니다. 예를 들어:

  ▎ "노트북 카테고리로 등록하는 셀러는 반드시 화면 크기(NUMBER)와 저장 용량(ENUM: 256/512/1024)을 입력해야 한다"

  셀러가 보내는 데이터:
    {
      category_id: 3,
      product_name: "맥북 프로",
      attributes: {"screen_size": 16.2, "storage": "512"}
    }

  Service 처리 순서:
    1. categoryRepository.findById(3)
       └ 없으면 → IllegalArgumentException ("존재하지 않는 카테고리")

    2. productAttributeSchemaRepository.findByCategoryId(3)
       ├ 없으면 → 검증 skip (스키마 미정의 카테고리는 자유 attributes)
       └ 있으면 → schema.validate({"screen_size": 16.2, "storage": "512"})
                    ├ "screen_size" 필수인데 없음? → 예외
                    ├ "screen_size" 값이 String? → 예외 (NUMBER여야 함)
                    ├ "storage" 값이 "999"? → 예외 (allowed_values에 없음)
                    └ 정의 안 된 키 "color" 있음? → 예외

    3. 검증 통과 → Product.register(payload) → save

  ---
  Schema / Definition은 누가 만드는가

  **운영자(Operator)**가 만들어야 합니다. 예를 들어:

  ▎ "노트북 카테고리로 등록하는 셀러는 반드시 화면 크기(NUMBER)와 저장 용량(ENUM: 256/512/1024)을 입력해야 한다"

  이걸 운영자 어드민에서 정의합니다.

  현재 상태: ProductAttributeSchemaRepository 포트(인터페이스)만 있고, 운영자가 Schema를 생성·수정하는 UseCase가 없습니다. 테스트에서 직접 객체를 만드는 수준. P2에서 운영자 어드민 UseCase(ProductAttributeSchemaManageUseCase 같은)와 adapter 구현이 필요합니다.

  없는 카테고리에 스키마 없는 경우 (지금 현실): Schema가 없으면 아무 attributes나 넣어도 통과합니다. 스키마가 정의된 카테고리만 검증됩니다.

  ---
  요약하면: Category = 분류, Schema/Definition = 그 분류에 속한 상품이 지켜야 할 규칙, Product.attributes = 규칙을 따른 실제 값.

```