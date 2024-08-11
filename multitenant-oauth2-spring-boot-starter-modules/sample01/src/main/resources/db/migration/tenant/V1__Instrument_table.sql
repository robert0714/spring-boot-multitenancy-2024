CREATE TABLE instrument(
   id      UUID PRIMARY KEY,
   name    VARCHAR(255) NOT NULL,
   type    VARCHAR(255),
   created_at timestamptz  NOT NULL,
   updated_at timestamptz  NOT NULL,
   created_by varchar(255)  NOT NULL,
   updated_by varchar(255)  NOT NULL
);
