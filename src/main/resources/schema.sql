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
