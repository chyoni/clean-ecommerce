drop table if exists product_image;
drop table if exists product_sku;
drop table if exists product;
drop table if exists product_attribute_definition;
drop table if exists product_attribute_schema;
drop table if exists category;
drop table if exists member;

create table if not exists member
(
    member_id        bigint       not null primary key auto_increment comment '회원 ID',
    name             varchar(100) not null comment '회원 이름',
    encoded_password varchar(150) not null comment '비밀번호 (해시)',
    email_address    varchar(150) not null comment '이메일',
    phone_number     varchar(15)  not null comment '핸드폰번호',
    role             varchar(20)  not null default 'NORMAL' comment '회원 유형',

    created_at       datetime     not null default current_timestamp comment '등록일 (시스템 기준)',
    updated_at       datetime     not null default current_timestamp on update current_timestamp comment '수정일'
);

create table if not exists category
(
    category_id bigint       not null primary key auto_increment comment '카테고리 ID',
    name        varchar(100) not null comment '카테고리명',
    parent_id   bigint       null comment '상위 카테고리 ID',

    created_at  datetime     not null default current_timestamp comment '등록일 (시스템 기준)',
    updated_at  datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_category_parent_id FOREIGN KEY (parent_id) REFERENCES category (category_id),
    UNIQUE KEY uq_category_name (name)
);

create table if not exists product_attribute_schema
(
    schema_id   bigint   not null primary key auto_increment comment '스키마 ID',
    category_id bigint   not null comment '카테고리 ID (1:1)',

    created_at  datetime not null default current_timestamp comment '등록일',
    updated_at  datetime not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_pas_category FOREIGN KEY (category_id) REFERENCES category (category_id),
    UNIQUE KEY uq_pas_category (category_id)
);

create table if not exists product_attribute_definition
(
    definition_id  bigint       not null primary key auto_increment comment '정의 ID',
    schema_id      bigint       not null comment '스키마 ID',
    attribute_key  varchar(100) not null comment '속성 키',
    attribute_type varchar(20)  not null comment '속성 타입',
    required       tinyint(1)   not null default 0 comment '필수 여부',
    allowed_values json         null comment '허용값 (ENUM 타입)',

    created_at     datetime     not null default current_timestamp comment '등록일',
    updated_at     datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_pad_schema FOREIGN KEY (schema_id) REFERENCES product_attribute_schema (schema_id),
    CONSTRAINT uq_pad_key UNIQUE (schema_id, attribute_key)
);

create table if not exists product
(
    product_id       bigint       not null primary key auto_increment comment '상품 ID',
    seller_id        bigint       not null comment '판매자 ID',
    category_id      bigint       not null comment '카테고리 ID',
    product_name     varchar(100) not null comment '상품명',
    product_status   varchar(50)  not null default 'DRAFT' comment '상품 상태',
    description_html text         null comment '상품 상세 HTML',
    brand            varchar(200) not null comment '브랜드',
    manufacturer     varchar(100) not null comment '제조사',
    attributes       json         null comment '속성',

    sales_start_date datetime     not null default current_timestamp comment '판매 시작일',
    sales_end_date   datetime     null comment '판매 종료일',
    created_at       datetime     not null default current_timestamp comment '등록일 (시스템 기준)',
    updated_at       datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_product_seller_id FOREIGN KEY (seller_id) REFERENCES member (member_id),
    CONSTRAINT fk_product_category_id FOREIGN KEY (category_id) REFERENCES category (category_id)
);

create table if not exists product_sku
(
    sku_id         bigint       not null primary key auto_increment comment 'SKU ID',
    product_id     bigint       not null comment '상품 ID',
    sku_code       varchar(100) not null comment 'SKU 코드',
    options        json         null comment '옵션 (예: {"color":"RED","size":"L"})',
    price          int          not null comment '가격',
    stock_quantity int          not null comment '재고 수량',
    active         tinyint(1)   not null default 1 comment '활성 여부',

    created_at     datetime     not null default current_timestamp comment '등록일',
    updated_at     datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_product_sku_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT uq_product_sku_code UNIQUE (product_id, sku_code)
);

create table if not exists product_image
(
    image_id      bigint       not null primary key auto_increment comment '이미지 ID',
    product_id    bigint       not null comment '상품 ID',
    image_type    varchar(20)  not null comment '이미지 유형 (THUMBNAIL/DETAIL/OPTION)',
    image_path    varchar(500) not null comment '외부 노출 URL (CDN 도메인 기준)',
    storage_key   varchar(300) not null comment '스토리지 객체 키 (변경 불가)',
    mime_type     varchar(100) not null comment 'MIME 타입',
    file_size     bigint       not null comment '파일 크기 (바이트)',
    display_order int          not null default 0 comment '표시 순서',

    created_at    datetime     not null default current_timestamp comment '등록일',
    updated_at    datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_product_image_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
    CONSTRAINT uq_product_image_storage_key UNIQUE (storage_key),
    INDEX idx_product_image_lookup (product_id, image_type, display_order)
);

-- ============================================================
-- 로컬 개발용 시드 데이터
-- 운영 환경에서는 분리된 data.sql 또는 마이그레이션 도구로 관리할 것
-- ============================================================

-- 회원
insert into member (member_id, name, encoded_password, email_address, phone_number, role)
values (1, '관리자', '$2a$10$placeholderHashForLocalDevOnly...........................', 'admin@local.dev', '01000000000',
        'ADMIN'),
       (2, '애플코리아', '$2a$10$placeholderHashForLocalDevOnly...........................', 'apple.kr@local.dev',
        '01011111111', 'SELLER'),
       (3, '삼성스토어', '$2a$10$placeholderHashForLocalDevOnly...........................', 'samsung@local.dev',
        '01022222222', 'SELLER'),
       (4, '유니클로', '$2a$10$placeholderHashForLocalDevOnly...........................', 'uniqlo@local.dev',
        '01033333333', 'SELLER'),
       (5, '신선마켓', '$2a$10$placeholderHashForLocalDevOnly...........................', 'fresh@local.dev', '01044444444',
        'SELLER'),
       (6, '구매자A', '$2a$10$placeholderHashForLocalDevOnly...........................', 'buyer@local.dev', '01055555555',
        'NORMAL');

-- 카테고리 (대 → 중 → 소)
insert into category (category_id, name, parent_id)
values
    -- 대분류
    (1, '전자기기', null),
    (2, '패션', null),
    (3, '식품', null),
    (4, '가전', null),
    -- 중분류 (전자기기)
    (10, '노트북', 1),
    (11, '스마트폰', 1),
    (12, '태블릿', 1),
    -- 중분류 (패션)
    (20, '남성의류', 2),
    (21, '여성의류', 2),
    -- 소분류 (남성의류)
    (30, '티셔츠', 20),
    (31, '바지', 20),
    -- 중분류 (식품)
    (40, '신선식품', 3),
    (41, '가공식품', 3),
    -- 중분류 (가전)
    (50, '대형가전', 4),
    -- 소분류 (대형가전)
    (60, '냉장고', 50),
    (61, '세탁기', 50);

-- 상품 속성 스키마 (카테고리별 1:1)
insert into product_attribute_schema (schema_id, category_id)
values (1, 10), -- 노트북
       (2, 11), -- 스마트폰
       (3, 30), -- 티셔츠
       (4, 40), -- 신선식품
       (5, 41), -- 가공식품
       (6, 60);
-- 냉장고

-- 속성 정의
insert into product_attribute_definition
    (schema_id, attribute_key, attribute_type, required, allowed_values)
values
    -- [노트북] schema_id = 1
    (1, 'screen_size_inch', 'NUMBER', 1, null),
    (1, 'storage_gb', 'ENUM', 1, '[
      "256",
      "512",
      "1024",
      "2048"
    ]'),
    (1, 'ram_gb', 'NUMBER', 1, null),
    (1, 'cpu', 'STRING', 0, null),
    (1, 'weight_kg', 'NUMBER', 0, null),

    -- [스마트폰] schema_id = 2
    (2, 'screen_size_inch', 'NUMBER', 1, null),
    (2, 'storage_gb', 'ENUM', 1, '[
      "128",
      "256",
      "512",
      "1024"
    ]'),
    (2, 'color', 'ENUM', 1, '[
      "BLACK",
      "WHITE",
      "BLUE",
      "GOLD",
      "SILVER"
    ]'),
    (2, 'waterproof', 'BOOLEAN', 0, null),

    -- [티셔츠] schema_id = 3
    (3, 'size', 'ENUM', 1, '[
      "XS",
      "S",
      "M",
      "L",
      "XL",
      "XXL"
    ]'),
    (3, 'color', 'STRING', 1, null),
    (3, 'material', 'STRING', 0, null),
    (3, 'season', 'ENUM', 0, '[
      "SPRING",
      "SUMMER",
      "FALL",
      "WINTER"
    ]'),

    -- [신선식품] schema_id = 4
    (4, 'expiry_date', 'DATE', 1, null),
    (4, 'origin', 'STRING', 1, null),
    (4, 'storage_type', 'ENUM', 1, '[
      "FROZEN",
      "REFRIGERATED",
      "ROOM"
    ]'),
    (4, 'weight_g', 'NUMBER', 0, null),

    -- [가공식품] schema_id = 5
    (5, 'expiry_date', 'DATE', 1, null),
    (5, 'weight_g', 'NUMBER', 1, null),
    (5, 'allergens', 'STRING', 0, null),

    -- [냉장고] schema_id = 6
    (6, 'manufacture_date', 'DATE', 1, null),
    (6, 'capacity_l', 'NUMBER', 1, null),
    (6, 'energy_grade', 'ENUM', 1, '[
      "1",
      "2",
      "3",
      "4",
      "5"
    ]'),
    (6, 'door_count', 'NUMBER', 1, null),
    (6, 'color', 'STRING', 0, null);

