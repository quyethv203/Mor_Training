CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id          INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT          NOT NULL,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expiry_date DATETIME    NOT NULL,
    revoked     BOOLEAN      NOT NULL DEFAULT false,
    created_at  DATETIME             DEFAULT CURRENT_TIMESTAMP,
    modified_at  DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id)
);
