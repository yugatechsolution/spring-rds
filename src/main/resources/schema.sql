CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL
);

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
    message_type ENUM('TEXT', 'TEMPLATE', 'FLOW') NOT NULL,
    message_body TEXT,
    metadata JSON,
    direction ENUM('INCOMING', 'OUTGOING') NOT NULL,
    FOREIGN KEY (phone_number_id, wa_id) REFERENCES whatsapp_contacts(phone_number_id, wa_id)
);

--DROP INDEX IF EXISTS idx_chat_wa_id ON chat_messages;
--CREATE INDEX idx_chat_wa_id ON chat_messages(wa_id);
--DROP INDEX IF EXISTS idx_chat_wa_id;
--CREATE INDEX idx_chat_wa_id ON chat_messages(wa_id);

CREATE TABLE IF NOT EXISTS chatbot_messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    type VARCHAR(50) NOT NULL,
    request JSON NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);

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

CREATE TABLE IF NOT EXISTS chatbot_triggers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    trigger_text VARCHAR(255) NOT NULL UNIQUE,
    chatbot_message_id BIGINT NOT NULL,
    CONSTRAINT fk_chatbot_message FOREIGN KEY (chatbot_message_id)
        REFERENCES chatbot_messages(id) ON DELETE CASCADE
);