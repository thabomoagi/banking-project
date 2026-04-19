-- Accounts table
DROP TABLE IF EXISTS accounts CASCADE;
CREATE TABLE accounts (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    account_number VARCHAR(20) UNIQUE NOT NULL,
    account_holder VARCHAR(100) NOT NULL,
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00 CHECK (balance >= 0),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- Transactions table
DROP TABLE IF EXISTS transactions;
CREATE TABLE transactions (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    from_account_id INT NOT NULL,
    to_account_id INT NOT NULL,
    amount DECIMAL(15, 2) NOT NULL CHECK (amount > 0),
    status VARCHAR(20) NOT NULL DEFAULT 'pending',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_from_account FOREIGN KEY (from_account_id) REFERENCES accounts(id) ON DELETE RESTRICT,
    CONSTRAINT fk_to_account FOREIGN KEY (to_account_id) REFERENCES accounts(id) ON DELETE RESTRICT
);
-- Audit log table
DROP TABLE IF EXISTS audit_log;
CREATE TABLE audit_log (
    id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    table_name VARCHAR(50) NOT NULL,
    record_id INT NOT NULL,
    action VARCHAR(10) NOT NULL,
    old_values JSONB,
    new_values JSONB,
    changed_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
-- Audit trigger function
CREATE OR REPLACE FUNCTION log_account_changes() RETURNS TRIGGER AS $$ BEGIN IF (TG_OP = 'UPDATE') THEN
INSERT INTO audit_log (
        table_name,
        record_id,
        action,
        old_values,
        new_values
    )
VALUES (
        'accounts',
        OLD.id,
        'UPDATE',
        row_to_json(OLD),
        row_to_json(NEW)
    );
ELSIF (TG_OP = 'DELETE') THEN
INSERT INTO audit_log (
        table_name,
        record_id,
        action,
        old_values,
        new_values
    )
VALUES (
        'accounts',
        OLD.id,
        'DELETE',
        row_to_json(OLD),
        NULL
    );
ELSIF (TG_OP = 'INSERT') THEN
INSERT INTO audit_log (
        table_name,
        record_id,
        action,
        old_values,
        new_values
    )
VALUES (
        'accounts',
        NEW.id,
        'INSERT',
        NULL,
        row_to_json(NEW)
    );
END IF;
RETURN NULL;
END;
$$ LANGUAGE plpgsql;
-- Attach audit trigger
DROP TRIGGER IF EXISTS accounts_audit ON accounts;
CREATE TRIGGER accounts_audit
AFTER
INSERT
    OR
UPDATE
    OR DELETE ON accounts FOR EACH ROW EXECUTE FUNCTION log_account_changes();
-- Updated_at trigger function
CREATE OR REPLACE FUNCTION update_modified_column() RETURNS TRIGGER AS $$ BEGIN NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- Attach updated_at trigger
DROP TRIGGER IF EXISTS update_accounts_modtime ON accounts;
CREATE TRIGGER update_accounts_modtime BEFORE
UPDATE ON accounts FOR EACH ROW EXECUTE FUNCTION update_modified_column();