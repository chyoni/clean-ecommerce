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