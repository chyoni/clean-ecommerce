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

create table if not exists product
(
    product_id       bigint       not null primary key auto_increment comment '상품 ID',
    seller_id        bigint       not null comment '판매자 ID',
    category         varchar(100) null comment '카테고리',
    product_name     varchar(100) not null comment '상품명',
    product_status   varchar(50)  not null default 'AVAILABLE' comment '상품 상태',
    brand            varchar(200) not null comment '브랜드',
    manufacturer     varchar(100) not null comment '제조사',
    price            int          not null comment '가격',
    stock_quantity   int          not null comment '재고 수량',

    sales_start_date datetime     not null default current_timestamp comment '판매 시작일',
    sales_end_date   datetime     null comment '판매 종료일',
    created_at       datetime     not null default current_timestamp comment '등록일 (시스템 기준)',
    updated_at       datetime     not null default current_timestamp on update current_timestamp comment '수정일',

    CONSTRAINT fk_product_seller_id FOREIGN KEY (seller_id) REFERENCES member (member_id)
);