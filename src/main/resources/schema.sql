CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL
);

--SET FOREIGN_KEY_CHECKS = 0;
--DROP TABLE IF EXISTS chatbot_messages;
--DROP TABLE IF EXISTS next_message_mapping;
--DROP TABLE IF EXISTS chatbot_triggers;
--SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS contacts (
    user_id BIGINT NOT NULL,
    phone_number VARCHAR(20) NOT NULL,
    name VARCHAR(255),
    PRIMARY KEY (user_id, phone_number),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS whatsapp_contacts (
    phone_number_id VARCHAR(50) NOT NULL,
    wa_id VARCHAR(50) NOT NULL,
    display_name VARCHAR(100),
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (phone_number_id, wa_id)
);

CREATE TABLE IF NOT EXISTS chat_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone_number_id VARCHAR(50) NOT NULL,
    wa_id VARCHAR(50) NOT NULL,
    message_id VARCHAR(100) UNIQUE NOT NULL,
    timestamp BIGINT NOT NULL,
    message_type VARCHAR(50) NOT NULL, -- Supports dynamic message types
    message_body TEXT, -- For plain text messages
    metadata JSON, -- For structured data like buttons, lists, etc.
    direction ENUM('INCOMING', 'OUTGOING') NOT NULL,
    status VARCHAR(20), -- SENT, DELIVERED, READ
    status_timestamp BIGINT, -- Timestamp for status update
    FOREIGN KEY (phone_number_id, wa_id) REFERENCES whatsapp_contacts(phone_number_id, wa_id) ON DELETE CASCADE
);
--
--DROP INDEX IF EXISTS idx_chat_wa_id ON chat_messages;
--CREATE INDEX idx_chat_wa_id ON chat_messages(wa_id);
--DROP INDEX IF EXISTS idx_chat_wa_id;
--CREATE INDEX idx_chat_wa_id ON chat_messages(wa_id);

-- Create chatbot_triggers without foreign key initially
CREATE TABLE IF NOT EXISTS chatbot_triggers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trigger_text VARCHAR(255) NOT NULL UNIQUE,
    chatbot_message_id BIGINT
);

-- Create chatbot_messages
CREATE TABLE IF NOT EXISTS chatbot_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    request JSON NOT NULL,
    trigger_id BIGINT,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_trigger FOREIGN KEY (trigger_id) REFERENCES chatbot_triggers(id) ON DELETE CASCADE
);

-- Create next_message_mapping
CREATE TABLE IF NOT EXISTS next_message_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_message_id BIGINT NOT NULL,
    action_trigger VARCHAR(50) NOT NULL,
    next_message_id BIGINT,
    CONSTRAINT fk_parent_message FOREIGN KEY (parent_message_id)
        REFERENCES chatbot_messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_next_message FOREIGN KEY (next_message_id)
        REFERENCES chatbot_messages(id) ON DELETE SET NULL
);

-- ✅ Create chat_message_mapping
CREATE TABLE IF NOT EXISTS chat_message_mapping (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    chat_message_id VARCHAR(100) NOT NULL, -- Change type to match message_id type
    chatbot_message_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chat_message FOREIGN KEY (chat_message_id)
        REFERENCES chat_messages(message_id) ON DELETE CASCADE,
    CONSTRAINT fk_chatbot_message_in_mapping FOREIGN KEY (chatbot_message_id)
        REFERENCES chatbot_messages(id) ON DELETE SET NULL
);

-- ✅ Add the foreign key to chatbot_triggers after chatbot_messages is created
SET @constraint_exists = (
    SELECT COUNT(*)
    FROM information_schema.table_constraints
    WHERE constraint_name = 'fk_chatbot_message'
      AND table_name = 'chatbot_triggers'
);

SET @query = IF(
    @constraint_exists = 0,
    'ALTER TABLE chatbot_triggers ADD CONSTRAINT fk_chatbot_message FOREIGN KEY (chatbot_message_id) REFERENCES chatbot_messages(id) ON DELETE CASCADE;',
    'SELECT "Constraint already exists"'
);

PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
