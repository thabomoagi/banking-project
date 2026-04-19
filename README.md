# Banking Transaction Engine

A PostgreSQL-backed transaction system demonstrating database integrity, concurrency control, and audit compliance. Built to understand what actually happens when money moves between accounts.

## The Problem This Solves

In a banking system, "simple" code is dangerous. If two requests hit at the same time, or if the server crashes halfway through a transfer, money can vanish or be double-spent. This project implements defensive patterns to prevent those failures.

## Technical Choices

### Concurrency: SELECT FOR UPDATE

When a transfer starts, the database locks the rows for both accounts. Any other request touching those accounts waits. This prevents the double-spend problem where a user could send the same money twice if requests arrive simultaneously.

### Atomicity: BEGIN and COMMIT

Everything runs inside a transaction. If any step fails (invalid account, insufficient funds), the entire operation rolls back. The sender is never debited unless the receiver is successfully credited.

### Precision: DECIMAL Not FLOAT

Currency uses `DECIMAL(15,2)`. Floating-point math in binary introduces rounding errors. In banking, losing a fraction of a cent is a compliance failure. DECIMAL ensures exact arithmetic.

### Audit Trail: Database Triggers

A PostgreSQL trigger logs every insert, update, and delete on the accounts table. Even if someone manually changes a balance in the database, the old and new values are captured in `audit_log`. This satisfies regulatory traceability requirements.

### Referential Integrity: ON DELETE RESTRICT

Foreign keys prevent deleting an account that has transaction history. In banking, records must outlive the accounts involved. You cannot erase evidence that money moved.

## Local Setup

### 1. Database

Create a database named `banking_workshop` in PostgreSQL, then run:

```bash
psql -U postgres -d banking_workshop -f db/schema.sql
psql -U postgres -d banking_workshop -f db/seed.sql
```

What I Learned
Database constraints are the last line of defense, not application code

Triggers guarantee audit compliance regardless of how data is modified

Row-level locking prevents race conditions that application-level checks miss

ACID transactions are non-negotiable for financial systems
