CREATE TABLE "user"(
  "uuid" UUID NOT NULL UNIQUE,
  "email_address" TEXT UNIQUE NOT NULL,
  "password_hash" TEXT,
  "created" TIMESTAMPTZ NOT NULL,
  "activated" BOOLEAN NOT NULL,
  "deleted" boolean NOT NULL,
  "email_token" TEXT UNIQUE,
  "email_token_valid_to" TIMESTAMPTZ,
  "facebook_id" TEXT
);

CREATE TABLE "auth_token"(
  "value" TEXT NOT NULL PRIMARY KEY,
  "user_uuid" UUID NOT NULL REFERENCES "user"(UUID),
  "valid_to" TIMESTAMPTZ NOT NULL
);
