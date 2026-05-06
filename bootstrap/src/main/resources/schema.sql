drop table if exists product_image;
drop table if exists product_sku;
drop table if exists product;
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

    created_at       datetime     not null default current_timestamp comment '등록일 (시스템 기준)',
    updated_at       datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_category_parent_id FOREIGN KEY (parent_id) REFERENCES category (category_id)
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
    image_path    varchar(500) not null comment '이미지 경로',
    display_order int          not null default 0 comment '표시 순서',

    created_at    datetime     not null default current_timestamp comment '등록일',
    updated_at    datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_product_image_product_id FOREIGN KEY (product_id) REFERENCES product (product_id),
    INDEX idx_product_image_lookup (product_id, image_type, display_order)
);