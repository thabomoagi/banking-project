-- Clear existing data
TRUNCATE accounts RESTART IDENTITY CASCADE;
-- Insert test accounts
INSERT INTO accounts (account_number, account_holder, balance)
VALUES ('1001', 'Alice Zulu', 5000.00),
    ('1002', 'Bob Smith', 1200.50),
    ('1003', 'Charlie Mokoena', 50.00);