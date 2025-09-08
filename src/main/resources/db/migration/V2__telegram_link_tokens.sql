CREATE TABLE telegram_link_tokens (
  id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
  user_id uuid REFERENCES users(id) ON DELETE CASCADE,
  token varchar(128) UNIQUE NOT NULL,
  created_at timestamp DEFAULT now()
);
