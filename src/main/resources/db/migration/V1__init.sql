CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  university_id varchar(128) UNIQUE NOT NULL,
  full_name text NOT NULL,
  role varchar(20) NOT NULL,
  phone_number varchar(32),
  telegram_chat_id bigint,
  password_hash text NOT NULL,
  created_at timestamp without time zone DEFAULT now()
);

CREATE TABLE elections (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  title text NOT NULL,
  description text,
  start_time timestamp,
  end_time timestamp,
  created_by uuid REFERENCES users(id),
  created_at timestamp DEFAULT now()
);

CREATE TABLE candidates (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  election_id uuid REFERENCES elections(id) ON DELETE CASCADE,
  name text NOT NULL,
  manifesto text
);

CREATE TABLE vote_ledger (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  election_id uuid REFERENCES elections(id) ON DELETE CASCADE,
  user_id uuid REFERENCES users(id) ON DELETE CASCADE,
  otp_verified boolean DEFAULT false,
  cast_at timestamp,
  UNIQUE (election_id, user_id)
);

CREATE TABLE ballot_box (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  election_id uuid REFERENCES elections(id) ON DELETE CASCADE,
  candidate_id uuid REFERENCES candidates(id),
  ballot_uuid uuid DEFAULT uuid_generate_v4()
);

CREATE TABLE audit_logs (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  actor_id uuid REFERENCES users(id),
  action text,
  entity text,
  created_at timestamp DEFAULT now(),
  hash text
);

CREATE TABLE otp_attempts (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id uuid REFERENCES users(id),
  otp_hash text,
  expires_at timestamp,
  attempts int DEFAULT 0,
  ip_address text,
  created_at timestamp DEFAULT now(),
  verified_at timestamp
);

CREATE INDEX idx_users_university_id ON users(university_id);
