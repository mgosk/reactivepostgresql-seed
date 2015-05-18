CREATE TABLE "profile"(
  "user_uuid" UUID NOT NULL REFERENCES "user"(UUID),
  "created" TIMESTAMPTZ NOT NULL,
  "home_latitude" NUMERIC,
  "home_longitude" NUMERIC,
  "nick" TEXT,
  "sex" TEXT,
  "age" NUMERIC,
  "weight" NUMERIC,
  "avatar" TEXT,
  "height" NUMERIC,
  "home_address" TEXT
);