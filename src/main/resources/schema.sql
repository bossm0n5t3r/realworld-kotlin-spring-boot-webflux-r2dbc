CREATE TABLE user_info
(
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    username         VARCHAR(100) NOT NULL,
    email            VARCHAR(100) NOT NULL,
    encoded_password VARCHAR(100) NOT NULL,
    bio VARCHAR(200),
    image VARCHAR(200),
    PRIMARY KEY (id)
);

INSERT INTO user_info (username, email, encoded_password)
VALUES ('Jacob1', 'jake1@jake.jake', '{bcrypt}$2a$10$2c/2Pks8UX5nASWSLa9pcO5/Zhx5m2DHsUYDHlDxVmKJwOXrBc2cm');
INSERT INTO user_info (username, email, encoded_password)
VALUES ('Jacob2', 'jake2@jake.jake', '{bcrypt}$2a$10$2c/2Pks8UX5nASWSLa9pcO5/Zhx5m2DHsUYDHlDxVmKJwOXrBc2cm');
INSERT INTO user_info (username, email, encoded_password)
VALUES ('Jacob3', 'jake3@jake.jake', '{bcrypt}$2a$10$2c/2Pks8UX5nASWSLa9pcO5/Zhx5m2DHsUYDHlDxVmKJwOXrBc2cm');
INSERT INTO user_info (username, email, encoded_password)
VALUES ('Jacob4', 'jake4@jake.jake', '{bcrypt}$2a$10$2c/2Pks8UX5nASWSLa9pcO5/Zhx5m2DHsUYDHlDxVmKJwOXrBc2cm');
INSERT INTO user_info (username, email, encoded_password)
VALUES ('Jacob5', 'jake5@jake.jake', '{bcrypt}$2a$10$2c/2Pks8UX5nASWSLa9pcO5/Zhx5m2DHsUYDHlDxVmKJwOXrBc2cm');

CREATE TABLE meta_followee_follower
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    followee_user_id BIGINT NOT NULL,
    follower_user_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

-- Jacob1 을 Jacob2 ~ Jacob5 가 follow, 나머지는 X
INSERT INTO meta_followee_follower(followee_user_id, follower_user_id)
VALUES (1, 2);
INSERT INTO meta_followee_follower(followee_user_id, follower_user_id)
VALUES (1, 3);
INSERT INTO meta_followee_follower(followee_user_id, follower_user_id)
VALUES (1, 4);
INSERT INTO meta_followee_follower(followee_user_id, follower_user_id)
VALUES (1, 5);

CREATE TABLE article_info
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    slug        VARCHAR(500) NOT NULL,
    author_id BIGINT NOT NULL,
    title       VARCHAR(500) NOT NULL,
    description VARCHAR(500) NOT NULL,
    body        VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-1', 1, 'How to train your dragon 1', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-2', 1, 'How to train your dragon 2', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-3', 1, 'How to train your dragon 3', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-4', 1, 'How to train your dragon 4', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-5', 1, 'How to train your dragon 5', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-6', 1, 'How to train your dragon 6', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-7', 1, 'How to train your dragon 7', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-8', 1, 'How to train your dragon 8', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-9', 1, 'How to train your dragon 9', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-10', 1, 'How to train your dragon 10', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-11', 1, 'How to train your dragon 11', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-12', 1, 'How to train your dragon 12', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-13', 1, 'How to train your dragon 13', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-14', 1, 'How to train your dragon 14', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-15', 1, 'How to train your dragon 15', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-16', 1, 'How to train your dragon 16', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-17', 1, 'How to train your dragon 17', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-18', 1, 'How to train your dragon 18', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-19', 1, 'How to train your dragon 19', 'Ever wonder how?', 'You have to believe');
INSERT INTO article_info (slug, author_id, title, description, body)
VALUES ('how-to-train-your-dragon-20', 1, 'How to train your dragon 20', 'Ever wonder how?', 'You have to believe');

CREATE TABLE comment_info
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    author_id  BIGINT NOT NULL,
    article_id BIGINT NOT NULL,
    body       VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE tag_info
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    name       VARCHAR(500) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO tag_info (name)
VALUES ('reactjs');
INSERT INTO tag_info (name)
VALUES ('angularjs');
INSERT INTO tag_info (name)
VALUES ('dragons');

CREATE TABLE meta_article_tag
(
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    article_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (1, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (1, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (1, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (2, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (2, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (2, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (3, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (3, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (3, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (4, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (4, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (4, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (5, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (5, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (5, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (6, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (6, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (6, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (7, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (7, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (7, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (8, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (8, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (8, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (9, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (9, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (9, 3);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (10, 1);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (10, 2);
INSERT INTO meta_article_tag (article_id, tag_id)
VALUES (10, 3);

CREATE TABLE meta_user_favorite_article
(
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    user_id             BIGINT NOT NULL,
    favorite_article_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO meta_user_favorite_article (user_id, favorite_article_id)
VALUES (2, 2);
INSERT INTO meta_user_favorite_article (user_id, favorite_article_id)
VALUES (3, 3);
INSERT INTO meta_user_favorite_article (user_id, favorite_article_id)
VALUES (4, 4);
INSERT INTO meta_user_favorite_article (user_id, favorite_article_id)
VALUES (5, 5);
