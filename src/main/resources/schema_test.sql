-- 기존 테이블 삭제
DROP TABLE IF EXISTS "user_image";
DROP TABLE IF EXISTS "user";
DROP TABLE IF EXISTS "token";
DROP TABLE IF EXISTS "place";
DROP TABLE IF EXISTS "zone_category";
DROP TABLE IF EXISTS "category";
DROP TABLE IF EXISTS "gathering_category";
DROP TABLE IF EXISTS "information";
DROP TABLE IF EXISTS "gathering";
DROP TABLE IF EXISTS "gathering_applicants";
DROP TABLE IF EXISTS "image";
DROP TABLE IF EXISTS "great_information";
DROP TABLE IF EXISTS "great_gathering";
DROP TABLE IF EXISTS "book_mark_information";
DROP TABLE IF EXISTS "book_mark_gathering";
DROP TABLE IF EXISTS "tag";
DROP TABLE IF EXISTS "info_tag";
DROP TABLE IF EXISTS "gathering_tag";
DROP TABLE IF EXISTS "banner";
DROP TABLE IF EXISTS "qna";
DROP TABLE IF EXISTS "qna_message";
DROP TABLE IF EXISTS "notice";
DROP TABLE IF EXISTS "diary";
DROP TABLE IF EXISTS "diary_day_content";
DROP TABLE IF EXISTS `term`;

-- 테이블 생성
CREATE TABLE "user_image"
(
    "user_image_id"           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_image_address"      VARCHAR(200) NOT NULL,
    "user_image_created_date" DATE         NOT NULL
);

CREATE TABLE "user"
(
    "user_id"              BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_image_id"        BIGINT      NOT NULL,
    "user_status_id"       VARCHAR(20) NOT NULL,
    "user_oauth_id"        VARCHAR(100),
    "provider"             VARCHAR(10),
    "user_nickname"        VARCHAR(30),
    "user_name"            VARCHAR(20),
    "user_age"             INT,
    "user_sex"             VARCHAR(10),
    "user_email"           VARCHAR(30),
    "user_phone_number"    VARCHAR(13),
    "is_admin"             BOOLEAN     NOT NULL,
    "user_latest_login_at" DATETIME,
    "user_created_at"      DATETIME    NOT NULL,
    "user_deleted_at"      DATETIME,
    FOREIGN KEY ("user_image_id") REFERENCES "user_image" ("user_image_id")
);

CREATE TABLE "token"
(
    "token_id"      BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id"       BIGINT       NOT NULL,
    "refresh_token" VARCHAR(250) NOT NULL,
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id")
);

CREATE TABLE "place"
(
    "place_id"        BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "place_search_id" VARCHAR(30),
    "place_name"      VARCHAR(30)    NOT NULL,
    "place_x_axis"    DECIMAL(10, 7) NOT NULL,
    "place_y_axis"    DECIMAL(10, 7) NOT NULL,
    "place_address"   VARCHAR(50)    NOT NULL
);

CREATE TABLE "zone_category"
(
    "zone_category_id"        BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "parent_zone_category_id" BIGINT,
    "zone_category_name"      VARCHAR(20) NOT NULL,
    FOREIGN KEY ("parent_zone_category_id") REFERENCES "zone_category" ("zone_category_id")
);

CREATE TABLE "category"
(
    "category_id"        BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "parent_category_id" BIGINT,
    "category_name"      VARCHAR(20) NOT NULL,
    FOREIGN KEY ("parent_category_id") REFERENCES "category" ("category_id")
);

CREATE TABLE "gathering_category"
(
    "gathering_category_id"   BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "gathering_category_name" VARCHAR(20) NOT NULL
);

CREATE TABLE "information"
(
    "information_id"           BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "category_id"              BIGINT      NOT NULL,
    "zone_category_id"         BIGINT      NOT NULL,
    "user_id"                  BIGINT      NOT NULL,
    "place_id"                 BIGINT      NOT NULL,
    "information_title"        VARCHAR(50) NOT NULL,
    "information_address"      VARCHAR(50) NOT NULL,
    "information_created_date" DATETIME    NOT NULL,
    "information_view_count"   INT         NOT NULL DEFAULT 0,
    "information_content"      TEXT,
    "information_tip"          TEXT,
    FOREIGN KEY ("category_id") REFERENCES "category" ("category_id"),
    FOREIGN KEY ("zone_category_id") REFERENCES "zone_category" ("zone_category_id"),
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id"),
    FOREIGN KEY ("place_id") REFERENCES "place" ("place_id")
);

CREATE TABLE "gathering"
(
    "gathering_id"                  BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id"                       BIGINT      NOT NULL,
    "zone_category_id"              BIGINT      NOT NULL,
    "gathering_category_id"         BIGINT      NOT NULL,
    "place_id"                      BIGINT      NOT NULL,
    "gathering_title"               VARCHAR(50),
    "gathering_content"             TEXT,
    "gathering_person_count"        INT,
    "gathering_view_count"          INT,
    "gathering_created_at"          DATETIME,
    "gathering_edited_at"           DATETIME,
    "gathering_schedule_start_date" DATETIME,
    "gathering_schedule_end_date"   DATETIME,
    "gathering_is_finish"           BOOLEAN,
    "gathering_deadline"            DATETIME,
    "gathering_allowed_sex"         VARCHAR(30) NOT NULL,
    "gathering_start_age"           INT         NOT NULL,
    "gathering_end_age"             INT         NOT NULL,
    "gathering_is_deleted"          BOOLEAN     NOT NULL DEFAULT FALSE,
    "gathering_open_chatting_url"   VARCHAR(255),
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id"),
    FOREIGN KEY ("gathering_category_id") REFERENCES "gathering_category" ("gathering_category_id"),
    FOREIGN KEY ("zone_category_id") REFERENCES "zone_category" ("zone_category_id"),
    FOREIGN KEY ("place_id") REFERENCES "place" ("place_id")
);

CREATE TABLE "gathering_applicants"
(
    "gathering_applicants_id"    BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "gathering_id"               BIGINT      NOT NULL,
    "user_id"                    BIGINT      NOT NULL,
    "gathering_applicants_state" VARCHAR(20) NOT NULL,
    FOREIGN KEY ("gathering_id") REFERENCES "gathering" ("gathering_id"),
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id")
);

CREATE TABLE "image"
(
    "image_id"           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "image_status_id"    VARCHAR(20)  NOT NULL,
    "information_id"     BIGINT       NOT NULL,
    "image_address"      VARCHAR(200) NOT NULL,
    "image_created_date" DATE         NOT NULL,
    FOREIGN KEY ("information_id") REFERENCES "information" ("information_id")
);

CREATE TABLE "great_information"
(
    "great_information_id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id"              BIGINT NOT NULL,
    "information_id"       BIGINT NOT NULL,
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id"),
    FOREIGN KEY ("information_id") REFERENCES "information" ("information_id"),
    UNIQUE ("user_id", "information_id")
);

CREATE TABLE "great_gathering"
(
    "great_gathering_id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id"            BIGINT NOT NULL,
    "gathering_id"       BIGINT NOT NULL,
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id"),
    FOREIGN KEY ("gathering_id") REFERENCES "gathering" ("gathering_id"),
    UNIQUE ("user_id", "gathering_id")
);

CREATE TABLE "book_mark_information"
(
    "book_mark_information_id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id"                  BIGINT NOT NULL,
    "information_id"           BIGINT NOT NULL,
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id"),
    FOREIGN KEY ("information_id") REFERENCES "information" ("information_id"),
    UNIQUE ("user_id", "information_id")
);

CREATE TABLE "book_mark_gathering"
(
    "book_mark_gathering_id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id"                BIGINT NOT NULL,
    "gathering_id"           BIGINT NOT NULL,
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id"),
    FOREIGN KEY ("gathering_id") REFERENCES "gathering" ("gathering_id"),
    UNIQUE ("user_id", "gathering_id")
);

CREATE TABLE "tag"
(
    "tag_id"   BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "tag_name" VARCHAR(16) NOT NULL
);

CREATE TABLE "info_tag"
(
    "info_tag_id"    BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "tag_id"         BIGINT NOT NULL,
    "information_id" BIGINT NOT NULL,
    FOREIGN KEY ("tag_id") REFERENCES "tag" ("tag_id"),
    FOREIGN KEY ("information_id") REFERENCES "information" ("information_id"),
    UNIQUE ("tag_id", "information_id")
);

CREATE TABLE "gathering_tag"
(
    "gathering_tag_id" BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "tag_id"           BIGINT NOT NULL,
    "gathering_id"     BIGINT NOT NULL,
    FOREIGN KEY ("tag_id") REFERENCES "tag" ("tag_id"),
    FOREIGN KEY ("gathering_id") REFERENCES "gathering" ("gathering_id"),
    UNIQUE ("tag_id", "gathering_id")
);

CREATE TABLE "banner"
(
    "id"   BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "url"  VARCHAR(255) NOT NULL
);

CREATE TABLE "qna"
(
    "qna_id"            BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "qna_category_name" VARCHAR(255),
    "qna_created_at"    DATETIME,
    "qna_status"        VARCHAR(255),
    "qna_title"         VARCHAR(255),
    "qna_updated_at"    DATETIME,
    "user_id"           BIGINT,
    FOREIGN KEY ("user_id") REFERENCES "user" ("user_id")
);

CREATE TABLE "qna_message"
(
    "id"                     BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "qna_message_content"    TEXT,
    "qna_message_created_at" DATETIME,
    "qna_message_user_id"    BIGINT,
    "qna_id"                 BIGINT,
    FOREIGN KEY ("qna_id") REFERENCES "qna" ("qna_id"),
    FOREIGN KEY ("qna_message_user_id") REFERENCES "user" ("user_id")
);

CREATE TABLE "notice"
(
    "notice_id"            BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "notice_category_name" VARCHAR(255),
    "notice_content"       TEXT,
    "notice_created_at"    DATETIME,
    "notice_is_deleted"    BOOLEAN DEFAULT FALSE,
    "notice_title"         VARCHAR(255)
);

CREATE TABLE "diary"
(
    "diary_id"           BIGINT      NOT NULL AUTO_INCREMENT PRIMARY KEY,
    "user_id"            BIGINT      NOT NULL,
    "diary_title"        VARCHAR(50) NOT NULL,
    "diary_title_image"  VARCHAR(200) DEFAULT NULL,
    "diary_start_date"   DATETIME    NOT NULL,
    "diary_end_date"     DATETIME    NOT NULL,
    "diary_created_date" DATETIME    NOT NULL,
    "diary_edited_date"  DATETIME     DEFAULT NULL
);

CREATE TABLE `term`
(
    `term_id`                  BIGINT   NOT NULL AUTO_INCREMENT,
    `user_id`                  BIGINT   NOT NULL,
    `term_condition_agreement` BOOLEAN  NOT NULL,
    `term_privacy_agreement`   BOOLEAN  NOT NULL,
    `term_created_at`          DATETIME NOT NULL,
    PRIMARY KEY (`term_id`),
    CONSTRAINT `FK_term_TO_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
);
