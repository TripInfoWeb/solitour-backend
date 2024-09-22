DROP TABLE IF EXISTS `great_information`;
DROP TABLE IF EXISTS `great_gathering`;
DROP TABLE IF EXISTS `book_mark_information`;
DROP TABLE IF EXISTS `book_mark_gathering`;
DROP TABLE IF EXISTS `info_tag`;
DROP TABLE IF EXISTS `gathering_tag`;
DROP TABLE IF EXISTS `comment`;
DROP TABLE IF EXISTS `image`;
DROP TABLE IF EXISTS `information`;
DROP TABLE IF EXISTS `gathering_applicants`;
DROP TABLE IF EXISTS `gathering`;
DROP TABLE IF EXISTS `tag`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `gathering_category`;
DROP TABLE IF EXISTS `zone_category`;
DROP TABLE IF EXISTS `place`;
DROP TABLE IF EXISTS `token`;
DROP TABLE IF EXISTS `banner`;
DROP TABLE IF EXISTS `notice`;
DROP TABLE IF EXISTS `qna_message`;
DROP TABLE IF EXISTS `qna`;
DROP TABLE IF EXISTS `user`;
DROP TABLE IF EXISTS `user_image`;
DROP TABLE IF EXISTS `diary_day_content`;
DROP TABLE IF EXISTS `diary`;

CREATE TABLE `user_image`
(
    `user_image_id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `user_image_address`      VARCHAR(200) NOT NULL,
    `user_image_created_date` DATE         NOT NULL,
    CONSTRAINT PK_user_image PRIMARY KEY (`user_image_id`)
);

CREATE TABLE `user`
(
    `user_id`              BIGINT      NOT NULL AUTO_INCREMENT,
    `user_image_id`        BIGINT      NOT NULL,
    `user_status_id`       VARCHAR(20) NOT NULL,
    `user_oauth_id`        VARCHAR(100) NULL UNIQUE,
    `provider`             VARCHAR(10) NULL,
    `user_nickname`        VARCHAR(30) NULL,
    `user_name`            VARCHAR(20) NULL,
    `user_age`             INT NULL,
    `user_sex`             VARCHAR(10) NULL,
    `user_email`           VARCHAR(30) NULL,
    `user_phone_number`    VARCHAR(13) NULL,
    `is_admin`             BOOLEAN     NOT NULL,
    `user_latest_login_at` DATETIME NULL,
    `user_created_at`      DATETIME    NOT NULL,
    `user_deleted_at`      DATETIME NULL,
    CONSTRAINT PK_USER PRIMARY KEY (`user_id`),
    CONSTRAINT FK_USER_IMAGE_TO_USER FOREIGN KEY (`user_image_id`) REFERENCES `user_image` (`user_image_id`)
);

CREATE TABLE `token`
(
    `token_id`      BIGINT       NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT       NOT NULL,
    `refresh_token` VARCHAR(250) NULL,
    `oauth_token`   VARCHAR(250) NULL,
    CONSTRAINT PK_TOKEN PRIMARY KEY (`token_id`),
    CONSTRAINT FK_USER_TO_TOKEN FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `place`
(
    `place_id`        BIGINT         NOT NULL AUTO_INCREMENT,
    `place_search_id` VARCHAR(30) NULL,
    `place_name`      VARCHAR(30)    NOT NULL,
    `place_x_axis`    DECIMAL(10, 7) NOT NULL,
    `place_y_axis`    DECIMAL(10, 7) NOT NULL,
    `place_address`   VARCHAR(50)    NOT NULL,
    CONSTRAINT PK_PLACE PRIMARY KEY (`place_id`)
);

CREATE TABLE `zone_category`
(
    `zone_category_id`        BIGINT      NOT NULL AUTO_INCREMENT,
    `parent_zone_category_id` BIGINT NULL,
    `zone_category_name`      VARCHAR(20) NOT NULL,
    CONSTRAINT PK_ZONE_CATEGORY PRIMARY KEY (`zone_category_id`),
    CONSTRAINT FK_zone_category_TO_zone_category FOREIGN KEY (`parent_zone_category_id`) REFERENCES `zone_category` (`zone_category_id`)
);

CREATE TABLE `category`
(
    `category_id`        BIGINT      NOT NULL AUTO_INCREMENT,
    `parent_category_id` BIGINT NULL,
    `category_name`      VARCHAR(20) NOT NULL,
    CONSTRAINT PK_CATEGORY PRIMARY KEY (`category_id`),
    CONSTRAINT FK_category_TO_category FOREIGN KEY (`parent_category_id`) REFERENCES `category` (`category_id`)
);

CREATE TABLE `gathering_category`
(
    `gathering_category_id`   BIGINT      NOT NULL AUTO_INCREMENT,
    `gathering_category_name` VARCHAR(20) NOT NULL,
    CONSTRAINT PK_GATHERING_CATEGORY PRIMARY KEY (`gathering_category_id`)
);

CREATE TABLE `information`
(
    `information_id`           BIGINT      NOT NULL AUTO_INCREMENT,
    `category_id`              BIGINT      NOT NULL,
    `zone_category_id`         BIGINT      NOT NULL,
    `user_id`                  BIGINT      NOT NULL,
    `place_id`                 BIGINT      NOT NULL,
    `information_title`        VARCHAR(50) NOT NULL,
    `information_address`      VARCHAR(50) NOT NULL,
    `information_created_date` DATETIME    NOT NULL,
    `information_view_count`   INT         NOT NULL DEFAULT 0,
    `information_content`      TEXT NULL,
    `information_tip`          TEXT NULL,
    CONSTRAINT PK_information PRIMARY KEY (`information_id`),
    CONSTRAINT FK_category_TO_information FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`),
    CONSTRAINT FK_zone_category_TO_information FOREIGN KEY (`zone_category_id`) REFERENCES `zone_category` (`zone_category_id`),
    CONSTRAINT FK_user_TO_information FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT FK_place_TO_information FOREIGN KEY (`place_id`) REFERENCES `place` (`place_id`)
);

CREATE TABLE `gathering`
(
    `gathering_id`                  BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`                       BIGINT      NOT NULL,
    `zone_category_id`              BIGINT      NOT NULL,
    `gathering_category_id`         BIGINT      NOT NULL,
    `place_id`                      BIGINT      NOT NULL,
    `gathering_title`               VARCHAR(50) NULL,
    `gathering_content`             TEXT NULL,
    `gathering_person_count`        INT NULL,
    `gathering_view_count`          INT NULL,
    `gathering_created_at`          DATETIME NULL,
    `gathering_edited_at`           DATETIME NULL,
    `gathering_schedule_start_date` DATETIME NULL,
    `gathering_schedule_end_date`   DATETIME NULL,
    `gathering_is_finish`           BOOLEAN NULL,
    `gathering_deadline`            DATETIME NULL,
    `gathering_allowed_sex`         VARCHAR(30) NOT NULL,
    `gathering_start_age`           INT         NOT NULL,
    `gathering_end_age`             INT         NOT NULL,
    `gathering_is_deleted`          BOOLEAN     NOT NULL DEFAULT FALSE,
    `gathering_open_chatting_url`   VARCHAR(255) NULL,

    CONSTRAINT PK_gathering PRIMARY KEY (`gathering_id`),
    CONSTRAINT FK_user_TO_gathering FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT FK_gathering_category_TO_information FOREIGN KEY (`gathering_category_id`) REFERENCES `gathering_category` (`gathering_category_id`),
    CONSTRAINT FK_zone_category_TO_gathering FOREIGN KEY (`zone_category_id`) REFERENCES `zone_category` (`zone_category_id`),
    CONSTRAINT FK_place_TO_gathering FOREIGN KEY (`place_id`) REFERENCES `place` (`place_id`)
);

CREATE TABLE `gathering_applicants`
(
    `gathering_applicants_id`    BIGINT      NOT NULL AUTO_INCREMENT,
    `gathering_id`               BIGINT      NOT NULL,
    `user_id`                    BIGINT      NOT NULL,
    `gathering_applicants_state` VARCHAR(20) NOT NULL,
    CONSTRAINT PK_GATHERING_APPLICANTS PRIMARY KEY (`gathering_applicants_id`),
    CONSTRAINT FK_GATHERING_TO_GATHERING_APPLICANTS FOREIGN KEY (`gathering_id`) REFERENCES `gathering` (`gathering_id`),
    CONSTRAINT FK_user_TO_GATHERING_APPLICANTS FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `image`
(
    `image_id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `image_status_id`    VARCHAR(20)  NOT NULL,
    `information_id`     BIGINT       NOT NULL,
    `image_address`      VARCHAR(200) NOT NULL,
    `image_created_date` DATE         NOT NULL,
    CONSTRAINT PK_image PRIMARY KEY (`image_id`),
    CONSTRAINT FK_information_id_TO_image FOREIGN KEY (`information_id`) REFERENCES `information` (`information_id`)
);

CREATE TABLE `great_information`
(
    `great_information_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`              BIGINT NOT NULL,
    `information_id`       BIGINT NOT NULL,
    CONSTRAINT PK_great_information PRIMARY KEY (`great_information_id`),
    CONSTRAINT FK_great_information_TO_user FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT FK_great_information_TO_information FOREIGN KEY (`information_id`) REFERENCES `information` (`information_id`),
    CONSTRAINT UK_great_information UNIQUE (`user_id`, `information_id`)
);

CREATE TABLE `great_gathering`
(
    `great_gathering_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`            BIGINT NOT NULL,
    `gathering_id`       BIGINT NOT NULL,
    CONSTRAINT PK_great_gathering PRIMARY KEY (`great_gathering_id`),
    CONSTRAINT FK_great_gathering_TO_user FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT FK_great_gathering_TO_gathering FOREIGN KEY (`gathering_id`) REFERENCES `gathering` (`gathering_id`),
    CONSTRAINT UK_great_gathering UNIQUE (`user_id`, `gathering_id`)
);

CREATE TABLE `book_mark_information`
(
    `book_mark_information_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`                  BIGINT NOT NULL,
    `information_id`           BIGINT NOT NULL,
    CONSTRAINT PK_book_mark_information PRIMARY KEY (`book_mark_information_id`),
    CONSTRAINT FK_book_mark_information_TO_user FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT FK_book_mark_information_TO_information FOREIGN KEY (`information_id`) REFERENCES `information` (`information_id`),
    CONSTRAINT UK_book_mark_information UNIQUE (`user_id`, `information_id`)
);

CREATE TABLE `book_mark_gathering`
(
    `book_mark_gathering_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id`                BIGINT NOT NULL,
    `gathering_id`           BIGINT NOT NULL,
    CONSTRAINT PK_book_mark_gathering PRIMARY KEY (`book_mark_gathering_id`),
    CONSTRAINT FK_book_mark_gathering_TO_user FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`),
    CONSTRAINT FK_book_mark_gathering_TO_gathering FOREIGN KEY (`gathering_id`) REFERENCES `gathering` (`gathering_id`),
    CONSTRAINT UK_book_mark_gathering UNIQUE (`user_id`, `gathering_id`)
);

CREATE TABLE `tag`
(
    `tag_id`   BIGINT      NOT NULL AUTO_INCREMENT,
    `tag_name` VARCHAR(16) NOT NULL,
    CONSTRAINT PK_tag PRIMARY KEY (`tag_id`)
);

CREATE TABLE `info_tag`
(
    `info_tag_id`    BIGINT NOT NULL AUTO_INCREMENT,
    `tag_id`         BIGINT NOT NULL,
    `information_id` BIGINT NOT NULL,
    CONSTRAINT PK_info_tag PRIMARY KEY (`info_tag_id`),
    CONSTRAINT FK_info_tag_TO_tag FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`),
    CONSTRAINT FK_info_tag_TO_information FOREIGN KEY (`information_id`) REFERENCES `information` (`information_id`),
    CONSTRAINT UK_info_tag UNIQUE (`tag_id`, `information_id`)
);

CREATE TABLE `gathering_tag`
(
    `gathering_tag_id` BIGINT NOT NULL AUTO_INCREMENT,
    `tag_id`           BIGINT NOT NULL,
    `gathering_id`     BIGINT NOT NULL,
    CONSTRAINT PK_gathering_tag PRIMARY KEY (`gathering_tag_id`),
    CONSTRAINT FK_gathering_tag_TO_tag FOREIGN KEY (`tag_id`) REFERENCES `tag` (`tag_id`),
    CONSTRAINT FK_gathering_tag_TO_gathering FOREIGN KEY (`gathering_id`) REFERENCES `gathering` (`gathering_id`),
    CONSTRAINT UK_gathering_tag UNIQUE (`tag_id`, `gathering_id`)
);

CREATE TABLE `banner`
(
    `id`   BIGINT       NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(255) NOT NULL,
    `url`  VARCHAR(255) NOT NULL,
    PRIMARY KEY (`id`)
);

CREATE TABLE `qna`
(
    `qna_id`            BIGINT NOT NULL AUTO_INCREMENT,
    `qna_category_name` VARCHAR(255) DEFAULT NULL,
    `qna_created_at`    DATETIME     DEFAULT NULL,
    `qna_status`        VARCHAR(255) DEFAULT NULL,
    `qna_title`         VARCHAR(255) DEFAULT NULL,
    `qna_updated_at`    DATETIME     DEFAULT NULL,
    `user_id`           BIGINT       DEFAULT NULL,
    PRIMARY KEY (`qna_id`),
    CONSTRAINT FK_qna_user FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `qna_message`
(
    `id`                     BIGINT NOT NULL AUTO_INCREMENT,
    `qna_message_content`    TEXT     DEFAULT NULL,
    `qna_message_created_at` DATETIME DEFAULT NULL,
    `qna_message_user_id`    BIGINT   DEFAULT NULL,
    `qna_id`                 BIGINT   DEFAULT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT FK_qna_message_qna FOREIGN KEY (`qna_id`) REFERENCES `qna` (`qna_id`),
    CONSTRAINT FK_qna_message_user FOREIGN KEY (`qna_message_user_id`) REFERENCES `user` (`user_id`)
);

CREATE TABLE `notice`
(
    `notice_id`            BIGINT NOT NULL AUTO_INCREMENT,
    `notice_category_name` VARCHAR(255) DEFAULT NULL,
    `notice_content`       TEXT         DEFAULT NULL,
    `notice_created_at`    DATETIME     DEFAULT NULL,
    `notice_is_deleted`    BOOLEAN      DEFAULT FALSE,
    `notice_title`         VARCHAR(255) DEFAULT NULL,
    PRIMARY KEY (`notice_id`)
);

CREATE TABLE `diary`
(
    `diary_id`           BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`            BIGINT      NOT NULL,
    `diary_title`        VARCHAR(50) NOT NULL,
    `diary_title_image`  VARCHAR(200) DEFAULT NULL,
    `diary_start_date`   DATETIME    NOT NULL,
    `diary_end_date`     DATETIME    NOT NULL,
    `diary_created_date` DATETIME    NOT NULL,
    `diary_edited_date`  DATETIME     DEFAULT NULL,
    PRIMARY KEY (`diary_id`)
);

CREATE TABLE `diary_day_content`
(
    `diary_day_content_id`             BIGINT      NOT NULL AUTO_INCREMENT,
    `diary_id`                         BIGINT      NOT NULL,
    `diary_day_content_place`          VARCHAR(50) NOT NULL,
    `diary_day_content_content`        TEXT        NOT NULL,
    `diary_day_content_feeling_status` VARCHAR(20) NOT NULL,
    `diary_day_content_image`          TEXT DEFAULT NULL,
    PRIMARY KEY (`diary_day_content_id`),
    CONSTRAINT `FK_diary_day_content_TO_diary` FOREIGN KEY (`diary_id`) REFERENCES `diary` (`diary_id`)
);