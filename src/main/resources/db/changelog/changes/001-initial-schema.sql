CREATE TABLE IF NOT EXISTS accounts
(
    account_id UUID PRIMARY KEY ,
    account_number VARCHAR(19) NOT NULL UNIQUE ,
    balance NUMERIC(19, 2) ,
    created_at TIMESTAMP ,
    modified_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS transactions
(
    transaction_id UUID PRIMARY KEY ,
    transfer_amount NUMERIC(19, 2) ,
    sender_id UUID REFERENCES accounts(account_id) ,
    receiver_id UUID REFERENCES accounts(account_id) NOT NULL,
    created_at TIMESTAMP ,
    modified_at TIMESTAMP
);
