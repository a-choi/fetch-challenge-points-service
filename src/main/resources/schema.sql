DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS balances;
DROP TABLE IF EXISTS payers;
DROP TABLE IF EXISTS users;
DROP INDEX IF EXISTS payer_name_idx;

CREATE TABLE users
(
    user_id   INT PRIMARY KEY AUTO_INCREMENT,
    user_name VARCHAR(255)
);

CREATE TABLE payers
(
    payer_id   INT AUTO_INCREMENT PRIMARY KEY,
    payer_name VARCHAR(255) UNIQUE NOT NULL
);
CREATE UNIQUE INDEX payer_name_idx on payers (payer_name);

CREATE TABLE balances
(
    point_balance INT,
    user_id       INT,
    CONSTRAINT fk_user_id_balances FOREIGN KEY (user_id) REFERENCES users,
    payer_id      INT,
    CONSTRAINT fk_payer_id_balances FOREIGN KEY (payer_id) REFERENCES payers,
    CONSTRAINT pk_balance_id PRIMARY KEY (user_id, payer_id)
);

CREATE TABLE transactions
(
    transaction_id     INT AUTO_INCREMENT PRIMARY KEY,
    transaction_points INT,
    timestamp          TIMESTAMP,
    user_id            INT,
    CONSTRAINT fk_user_id_transactions FOREIGN KEY (user_id) REFERENCES users,
    payer_id           INT,
    CONSTRAINT fk_payer_id_transactions FOREIGN KEY (payer_id) REFERENCES payers,
    CONSTRAINT fk_user_payer_id_transactions FOREIGN KEY (user_id, payer_id) REFERENCES balances
);
