CREATE TABLE user_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT ,
    username VARCHAR(100) NOT NULL ,
    email VARCHAR(100) NOT NULL ,
    encoded_password VARCHAR(100) NOT NULL ,
    bio VARCHAR(200),
    image VARCHAR(200),
    following_ids CLOB,
    favorite_articles_ids CLOB,
    PRIMARY KEY (id)
);
