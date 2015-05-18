CREATE TABLE "note"(
  "uuid" UUID NOT NULL,
  "user_uuid" UUID NOT NULL REFERENCES "user"(UUID),
  "subject" TEXT NOT NULL,
  "content" TEXT NOT NULL
);