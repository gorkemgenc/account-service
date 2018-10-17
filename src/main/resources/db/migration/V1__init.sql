/*
 * Server: MySQl
 * Version: V1
 * Description: Fill process_type table with Deposit, Withdraw, Purchase
*/
-- Create User_Account table
CREATE TABLE IF NOT EXISTS user_account
(
	id INT AUTO_INCREMENT,
    balance NUMERIC(15,2) DEFAULT 0 NOT NULL,
    created_time TIMESTAMP DEFAULT NOW(),
    last_updated_time TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY(id)
);

-- Create Product table
CREATE TABLE IF NOT EXISTS product
(
	id INT AUTO_INCREMENT,
    product_name NVARCHAR(100) NOT NULL,
    price NUMERIC(15,2) NOT NULL,
    product_count INT DEFAULT 0,
    created_time TIMESTAMP DEFAULT NOW(),
    updated_time TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY(id),
    UNIQUE(product_name)
);

-- Create Process_type table for transaction type
CREATE TABLE IF NOT EXISTS process_type
(
	id int AUTO_INCREMENT,
    description VARCHAR(20),
    PRIMARY KEY(id)
);

-- Create Account_Transaction table
CREATE TABLE IF NOT EXISTS account_transaction
(
	id BIGINT AUTO_INCREMENT,
    type_id INT NOT NULL REFERENCES process_type(id),
    amount NUMERIC(15,2) NOT NULL,
    account_id INT REFERENCES user_account(id),
    product_id INT DEFAULT NULL REFERENCES product(id),
    created_time TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY(id)
);