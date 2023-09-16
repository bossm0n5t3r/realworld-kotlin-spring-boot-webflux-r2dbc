CREATE TABLE user_info
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at       DATETIME,
    updated_at       DATETIME,
    username         VARCHAR(100) NOT NULL,
    email            VARCHAR(100) NOT NULL,
    encoded_password VARCHAR(100) NOT NULL,
    bio VARCHAR(200),
    image VARCHAR(200),
    PRIMARY KEY (id)
);

CREATE TABLE meta_followee_follower
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME,
    updated_at DATETIME,
    followee_user_id BIGINT NOT NULL,
    follower_user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE article_info
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at  DATETIME,
    updated_at  DATETIME,
    slug        VARCHAR(500) NOT NULL,
    authorId    BIGINT       NOT NULL,
    title       VARCHAR(500) NOT NULL,
    description VARCHAR(500) NOT NULL,
    body        VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE comment_info
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME,
    updated_at DATETIME,
    authorId   BIGINT       NOT NULL,
    articleId  BIGINT       NOT NULL,
    body       VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE tag_info
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME,
    updated_at DATETIME,
    name       VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE meta_article_tag
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME,
    updated_at DATETIME,
    article_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE meta_user_favorite_article
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at          DATETIME,
    updated_at          DATETIME,
    user_id             BIGINT NOT NULL,
    favorite_article_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
