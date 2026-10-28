-- Lab 2, Exercise 2.1
-- Run this in the "igirepay" database to create all tables.

CREATE TABLE customers (
                           id            VARCHAR(20)  PRIMARY KEY,
                           full_name     VARCHAR(100) NOT NULL,
                           email         VARCHAR(100) NOT NULL UNIQUE,
                           phone_number  VARCHAR(20)  NOT NULL UNIQUE
);

CREATE TABLE accounts (
                          id                VARCHAR(20)   PRIMARY KEY,
                          customer_id       VARCHAR(20)   NOT NULL REFERENCES customers(id),
                          account_type      VARCHAR(10)   NOT NULL CHECK (account_type IN ('WALLET', 'SAVINGS')),
                          balance           NUMERIC(15,2) NOT NULL DEFAULT 0 CHECK (balance >= 0),
                          transfer_limit    NUMERIC(15,2),
                          withdrawal_limit  NUMERIC(15,2),
                          withdrawal_fee    NUMERIC(15,2),
                          created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE transactions (
                              id                VARCHAR(50)   PRIMARY KEY,
                              account_id        VARCHAR(20)   NOT NULL REFERENCES accounts(id),
                              reference_id      VARCHAR(50)   NOT NULL,
                              transaction_type  VARCHAR(20)   NOT NULL CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'TRANSFER')),
                              amount            NUMERIC(15,2) NOT NULL CHECK (amount > 0),
                              created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE processed_requests (
                                    id            SERIAL      PRIMARY KEY,
                                    reference_id  VARCHAR(50) NOT NULL UNIQUE,
                                    processed_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
);