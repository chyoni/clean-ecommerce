# 이미지 스토리지 관련 내용

- MinIO를 사용

## MinIO가 뭔지부터

- 파일을 저장하는 서버입니다.
- 우리가 보통 DB에 텍스트 데이터(상품명, 가격 등)를 저장하듯이, 이미지/동영상 같은 파일 데이터는 별도 스토리지 서버에 저장합니다. MinIO가 그 스토리지 서버 역할을 합니다.
                                                         
---                                                                                                                                                                                     

## S3랑 무슨 관계?

AWS S3가 업계 표준 파일 스토리지인데, MinIO는 "S3와 똑같이 동작하는 오픈소스" 입니다.

- AWS S3    ─── 유료, 클라우드                           
- MinIO     ─── 무료, 내 PC/서버에서 직접 실행

둘 다 API 규격이 같아서, 코드는 한 벌이고 설정만 바꾸면 됩니다.

### 로컬 개발
endpoint: http://localhost:9000   ← MinIO

### 나중에 운영으로 배포할 때

```bash
⏺ application.yaml 설정값만 바꾸면 됩니다. 코드 변경 없음.                                                                                                                                
                                                                                                                                                                                        
  ---                                                                                                                                                                                     
  R2 콘솔에서 먼저 해야 할 것
                                                                                                                                                                                          
  Cloudflare 대시보드에서:                               
  1. R2 버킷 생성 (예: product-images)                                                                                                                                                    
  2. API 토큰 발급 → access-key, secret-key 획득                                                                                                                                          
  3. 계정 ID 확인 → endpoint URL 조립           
                                                                                                                                                                                          
  ---                                                                                                                                                                                     
  application.yaml 변경                                                                                                                                                                   
                                                                                                                                                                                          
  # 로컬 (현재)                                          
  app:                                                                                                                                                                                    
    storage:
      image:                                                                                                                                                                              
        endpoint: http://localhost:9000                  
        bucket: product-images                                                                                                                                                            
        access-key: devminio
        secret-key: devminio12345                                                                                                                                                         
        public-url-base: http://localhost:9000                                                                                                                                            
   
  # 운영 (R2로 변경)                                                                                                                                                                      
  app:                                                   
    storage:                                                                                                                                                                              
      image:                                             
        endpoint: https://<ACCOUNT_ID>.r2.cloudflarestorage.com
        region: auto
        bucket: product-images
        access-key: <R2_ACCESS_KEY>         ← 환경변수로 주입                                                                                                                             
        secret-key: <R2_SECRET_KEY>         ← 환경변수로 주입
        public-url-base: https://pub-<ID>.r2.dev  ← R2 public URL 또는 커스텀 도메인                                                                                                      
                                                                                                                                                                                          
  실제로는 yaml에 직접 쓰지 않고 서버 환경변수로 주입합니다.                                                                                                                              
                                                                                                                                                                                          
  STORAGE_ENDPOINT=https://<ACCOUNT_ID>.r2.cloudflarestorage.com                                                                                                                          
  STORAGE_REGION=auto                                    
  STORAGE_BUCKET=product-images                                                                                                                                                           
  STORAGE_PUBLIC_URL_BASE=https://pub-<ID>.r2.dev                                                                                                                                         
   
  ---                                                                                                                                                                                     
  그게 전부입니다                                        
                                                                                                                                                                                          
  S3PresignedImageStorageAdapter 코드는 한 글자도 안 바뀝니다. AWS SDK v2가 endpoint만 보고 거기로 S3 프로토콜로 통신하기 때문에, MinIO든 R2든 S3든 어댑터 입장에서는 구분이 없습니다.
  이게 처음에 포트를 추상화하고 SDK를 AWS SDK v2로 선택한 이유입니다.      
```

> **그런데 S3랑 구격이 같아서 나중에 배포할때 설정만 바꾸면 된다고 했잖아. 근데 왜 S3가 아니라 R2 Cloudflare 라고 했어?**
>
> ⏺ S3는 파일 꺼낼 때(egress) 돈을 냅니다. R2는 꺼낼 때 무료입니다.
> 
> ---
> S3 과금 구조 
> 파일 저장       → GB당 소액 (저렴)
> 
> 파일 업로드     → 무료
> 
> 파일 다운로드   → GB당 $0.09  ← 이게 문제
> 
> 쇼핑몰 이미지는 읽기가 압도적으로 많습니다. 상품 목록 한 페이지만 펼쳐도 썸네일 수십 장이 동시에 다운로드됩니다.
> 트래픽이 커질수록 egress 비용이 폭발적으로 늘어나서, 스타트업이 S3 egress 요금 폭탄 맞는 게 흔한 일입니다.
> ---                                                                                                                                                                                     
> R2 과금 구조
> 파일 저장       → GB당 소액 (S3와 비슷)
> 
> 파일 업로드     → 무료
> 
> 파일 다운로드   → 무료  ← 이게 핵심
> 
> Cloudflare가 "egress 0원"을 내세워서 S3 대항마로 만든 게 R2입니다. 이미지처럼 읽기 트래픽이 많은 서비스에 압도적으로 유리합니다.
>                                                         
> ---                                                                                                                                                                                     
> 그래서 선택 기준
> 
> 개발/학습용    → MinIO (내 PC에서 무료)
> 
> 실제 배포 시   → Cloudflare R2 (egress 0원, S3 API 호환)
> 
> 돈이 넘친다면  → AWS S3 (생태계 최강, CDN/Lambda 연동 등)
> 
> 코드는 셋 다 똑같습니다. application.yaml 의 endpoint, bucket, access-key, secret-key 네 줄만 환경별로 다르게 주입하면 됩니다.

---

## docker-compose.yaml의 두 서비스

- **minio**:          ← 스토리지 서버 자체
- **minio-bootstrap**: ← 버킷(폴더) 자동 생성용 일회성 세팅 스크립트

minio — 실제 파일 저장 서버. 포트 두 개:
- 9000 → 파일 저장/조회 API (우리 앱이 여기와 통신)
- 9001 → 브라우저로 들어가서 파일 확인하는 관리자 UI

minio-bootstrap — MinIO가 뜨자마자 product-images 라는 버킷(=폴더 같은 개념)을 만들고 종료. 없으면 버킷이 없어서 업로드 실패함.

---

## 버킷이 뭐야?

S3/MinIO에서 파일을 저장하는 최상위 컨테이너. DB로 치면 데이터베이스 하나 같은 개념입니다.

```bash
MinIO
└── product-images (버킷)
      └── products/
           └── thumbnail/
                └── 2026/05/
                      └── uuid.jpg  ← 실제 파일
```

---
## 정리하면

- docker compose up -d mysql minio minio-bootstrap

```bash
  ┌─────────────┐    파일 저장/조회    ┌──────────────────────┐
  │   우리 앱    │ ─────────────────▶ │  MinIO (localhost:9000) │
  │  (Spring)   │ ◀───────────────── │  product-images 버킷    │
  └─────────────┘                    └──────────────────────┘

  ┌─────────────┐    텍스트 데이터     ┌──────────────────────┐
  │   우리 앱    │ ─────────────────▶ │  MySQL (localhost:3306) │
  └─────────────┘ ◀───────────────── └──────────────────────┘
```

MySQL이 텍스트 데이터를 담당하듯, MinIO는 파일 데이터를 담당하는 겁니다.

---

# 파일 업로드 흐름

```bash
  ---                                                                                                                                                                                     
  일반적인 파일 업로드 (서버 경유)
                                                                                                                                                                                          
  사용자가 파일 선택                                     
      ↓                                                                                                                                                                                   
  브라우저 → [파일 바이너리] → 서버 → [파일 바이너리] → S3
  서버가 파일을 받아서 다시 S3로 올려주는 중계 역할. 트래픽/메모리 낭비.                                                                                                                  
                                                                                                                                                                                          
  ---                                                                                                                                                                                     
  Presigned URL 방식 (우리 구현)                                                                                                                                                          
                                                         
  사용자가 파일 선택
      ↓                                                                                                                                                                                   
  브라우저 → [파일 이름, 크기, 타입] → 서버   ← 1단계: 메타데이터만 전송
                                         ↓                                                                                                                                                
                                 "이 URL로 직접 올려"    
                                 uploadUrl 발급                                                                                                                                           
      ↓                                                                                                                                                                                   
  브라우저 → [파일 바이너리] → MinIO          ← 2단계: 파일은 MinIO에 직접                                                                                                                
                                                                                                                                                                                          
  클라이언트(브라우저)는 파일을 선택한 순간 파일 데이터는 읽지 않고, 이름/크기/타입만 먼저 서버에 보냅니다. 서버는 "이 storageKey 위치에 이 조건으로 넣을 수 있는 URL"을 발급해주고,      
  클라이언트가 그 URL로 MinIO에 직접 PUT합니다.                                                                                                                                           
                                                                                                                                                                                          
  ---                                                    
  코드로 보면

  // 프론트엔드 코드 (예시)
                                                                                                                                                                                          
  // 파일 선택기에서 파일 선택                                                                                                                                                            
  const file = input.files[0]  // File 객체                                                                                                                                               
                                                                                                                                                                                          
  // 1단계: 서버에 메타데이터만 보내서 업로드 URL 받기                                                                                                                                    
  const { uploadUrl, storageKey, publicUrl, requiredHeaders } = await fetch('/api/images/upload-url', {
      method: 'POST',                                                                                                                                                                     
      body: JSON.stringify({                             
          imageType: 'THUMBNAIL',                                                                                                                                                         
          originalFileName: file.name,   // "photo.jpg"                                                                                                                                   
          contentType: file.type,        // "image/jpeg"
          contentLength: file.size       // 102400 (바이트)                                                                                                                               
      })                                                                                                                                                                                  
  })
                                                                                                                                                                                          
  // 2단계: 파일 바이너리를 MinIO에 직접 PUT                                                                                                                                              
  await fetch(uploadUrl, {
      method: 'PUT',                                                                                                                                                                      
      headers: { ...requiredHeaders },            
      body: file   // 파일 바이너리 여기서 처음 사용
  })                                                                                                                                                                                      
   
  // 3단계: 상품 등록 시 storageKey/publicUrl을 ImagePayload에 담아 전송                                                                                                                  
                                                         
  IssueImageUploadUrlCommand 에 파일 데이터가 없는 이유가 이겁니다. 서버는 파일을 볼 필요가 없고, 어디에 어떤 조건으로 넣을 수 있는지 URL만 서명해주면 됩니다.                            
```