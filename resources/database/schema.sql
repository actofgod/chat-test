
-- Последовательность для установки айди пользователей
CREATE SEQUENCE "users_ids" INCREMENT 1 MAXVALUE 2147483647 CACHE 1;
COMMENT ON SEQUENCE "users_ids" IS 'Последовательность для установки айди пользователей';

-- Таблица пользователей
CREATE TABLE "users" (
-- columns
  "id"            INTEGER                     NOT NULL DEFAULT NEXTVAL('users_ids'::REGCLASS),
  "username"      VARCHAR(32)                 NOT NULL,
  "password_hash" CHAR(64)                    NOT NULL,
  "password_salt" CHAR(32)                    NOT NULL,
  "date_register" TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  "last_activity" TIMESTAMP WITHOUT TIME ZONE          DEFAULT NULL,
-- constraints
  CONSTRAINT "users_pk" PRIMARY KEY ("id")
) WITH (
  OIDS = FALSE
);

CREATE UNIQUE INDEX "users_unq_idx_username" ON "users" (LOWER("username"));

-- Последовательность для установки айди сообщений
CREATE SEQUENCE "chat_messages_ids" INCREMENT 1 MAXVALUE 9223372036854775807 CACHE 1;
COMMENT ON SEQUENCE "chat_messages_ids" IS 'Последовательность для установки айди сообщений пользователей';

-- Таблица приватных сообщений между пользователями
CREATE TABLE "chat_private_messages" (
-- columns
  "id"        BIGINT                      NOT NULL DEFAULT NEXTVAL('chat_messages_ids'::REGCLASS),
  "from_id"   INTEGER                     NOT NULL,
  "to_id"     INTEGER                     NOT NULL,
  "date"      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  "message"   TEXT                        NOT NULL,
  "last_edit" TIMESTAMP WITHOUT TIME ZONE          DEFAULT NULL,
  "date_view" TIMESTAMP WITHOUT TIME ZONE          DEFAULT NULL,
-- constraints
  CONSTRAINT "chat_private_messages_pk" PRIMARY KEY ("id"),
  CONSTRAINT "chat_private_messages_fk_from_id" FOREIGN KEY ("from_id")
    REFERENCES "users" ("id") ON UPDATE RESTRICT ON DELETE RESTRICT,
  CONSTRAINT "chat_private_messages_fk_to_id" FOREIGN KEY ("to_id")
    REFERENCES "users" ("id") ON UPDATE RESTRICT ON DELETE RESTRICT
) WITH (
  OIDS = FALSE
);

CREATE INDEX "chat_private_messages_idx_from_id" ON "chat_private_messages" ("from_id");
CREATE INDEX "chat_private_messages_idx_to_id" ON "chat_private_messages" ("to_id");
CREATE INDEX "chat_private_messages_idx_date" ON "chat_private_messages" ("date" DESC);

-- Таблица сообщений в общем чате
CREATE TABLE "chat_public_messages" (
-- columns
  "id"        BIGINT                      NOT NULL DEFAULT NEXTVAL('chat_messages_ids'::REGCLASS),
  "from_id"   INTEGER                     NOT NULL,
  "date"      TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
  "message"   TEXT                        NOT NULL,
  "last_edit" TIMESTAMP WITHOUT TIME ZONE          DEFAULT NULL,
-- constraints
  CONSTRAINT "chat_public_messages_pk" PRIMARY KEY ("id"),
  CONSTRAINT "chat_public_messages_fk_from" FOREIGN KEY ("from_id")
    REFERENCES "users" ("id") ON UPDATE RESTRICT ON DELETE RESTRICT
) WITH (
  OIDS = FALSE
);

CREATE INDEX "chat_public_messages_idx_from_id" ON "chat_public_messages" ("from_id");
CREATE INDEX "chat_public_messages_idx_date" ON "chat_public_messages" ("date" DESC);
