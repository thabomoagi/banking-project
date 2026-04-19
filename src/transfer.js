const pool = require('./config/db');

async function transferMoney(fromAccountId, toAccountId, amount) {
  const client = await pool.connect();

  try {
    await client.query('BEGIN');

    const lockQuery = `
      SELECT id, balance 
      FROM accounts 
      WHERE id IN ($1, $2) 
      FOR UPDATE
    `;
    const lockResult = await client.query(lockQuery, [fromAccountId, toAccountId]);

    if (lockResult.rows.length !== 2) {
      throw new Error('One or both accounts not found');
    }

    const fromAccount = lockResult.rows.find(r => r.id === fromAccountId);
    const toAccount = lockResult.rows.find(r => r.id === toAccountId);

    if (fromAccount.balance < amount) {
      throw new Error('Insufficient funds');
    }

    await client.query(
      'UPDATE accounts SET balance = balance - $1 WHERE id = $2',
      [amount, fromAccountId]
    );

    await client.query(
      'UPDATE accounts SET balance = balance + $1 WHERE id = $2',
      [amount, toAccountId]
    );

    await client.query(
      `INSERT INTO transactions (from_account_id, to_account_id, amount, status, completed_at)
       VALUES ($1, $2, $3, 'completed', CURRENT_TIMESTAMP)`,
      [fromAccountId, toAccountId, amount]
    );

    await client.query('COMMIT');

    console.log(`Transfer complete: ${amount} from account ${fromAccountId} to ${toAccountId}`);
    return { success: true };
  } catch (error) {
    await client.query('ROLLBACK');
    console.error('Transfer failed:', error.message);
    return { success: false, error: error.message };
  } finally {
    client.release();
  }
}

transferMoney(1, 2, 250.00);