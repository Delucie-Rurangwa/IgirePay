## Lab 1: Object-Oriented Design in Action

Run: `lab1.lab1Main`

### Exercise 1.1: Class design (inheritance and encapsulation)
- `Account`: account ID, customer ID, balance.
- `WalletAccount` (adds a transfer limit) and `SavingsAccount` (adds a withdrawal
  limit and fee) inherit from `Account`.
- `Customer`: customer ID, full name, email, phone number.
- `Transaction`: transaction ID, reference ID, amount, transaction type
  (DEPOSIT, WITHDRAWAL or TRANSFER), timestamp.
- All fields are private, with getters, setters and `toString()`.

### Exercise 1.2: Polymorphism
- `Account` provides `deposit()`, `withdraw()` and `processTransaction()`.
- `WalletAccount` overrides `processTransaction()` to allow instant transfers.
- `SavingsAccount` overrides `withdraw()` to apply a withdrawal limit and a fee,
  and inherits the rule that transfers are not allowed.

### Exercise 1.3: Collections and duplicate detection
- `Customer` keeps its accounts in a `List`.
- `Account` keeps its transaction history in a `List`.
- `PaymentProcessor` manages the whole system:
    - `Map` of customers and `Map` of accounts (lookup by ID)
    - `Set` of processed reference IDs (duplicate detection)
    - `List` of failed transaction logs

**Idempotency:** every request carries a reference ID. If a client retries a
request after a network timeout, the reference ID is already in the Set, so the
request is rejected and logged instead of charging the customer twice.

## Lab 2: Database Integration with JDBC

Run: `lab2.lab2Main`

### Setup
1. Create a PostgreSQL database named `igirepay`.
2. Run `src/lab2/schema.sql` in it to create the tables.
3. Update the password in `src/lab2/DatabaseConnection.java`.
4. The PostgreSQL JDBC driver is in the `lib` folder.

### Exercise 2.1: Schema
Four tables: `customers`, `accounts`, `transactions`, `processed_requests`.
- Foreign keys link accounts to customers and transactions to accounts.
- `CHECK` constraints block negative balances, non-positive amounts and invalid types.
- `processed_requests.reference_id` is `UNIQUE`, so the database itself refuses
  to process the same request twice.
- `accounts` also stores `transfer_limit`, `withdrawal_limit` and `withdrawal_fee`,
  so wallet and savings accounts can be rebuilt with their own rules.

### Exercise 2.2 and 2.4: DAOs and CRUD
| DAO | Methods |
|---|---|
| `CustomerDAO` | create, findById, findAll, update, delete, findByPhone (custom) |
| `AccountDAO` | create, findById, findByCustomerId (custom), updateBalance, deleteInactive |
| `TransactionDAO` | create, findByAccountId, findByReferenceId (custom), update, delete |
| `ProcessedRequestDAO` | create, exists, findAll, delete |

`PaymentService` combines the DAOs with the Lab 1 classes to perform deposits,
withdrawals and transfers.

**Design decisions:**
- An **inactive account** has a zero balance and no transactions. Accounts with
  money or history cannot be deleted.
- `TransactionDAO.update` and `delete` exist for full CRUD and test cleanup only.
  Real payments never change or delete transactions, because they are financial records.
- `ProcessedRequestDAO` has no update method: processed reference IDs are permanent.

### Exercise 2.3: PreparedStatements
Every query uses `PreparedStatement` with `?` placeholders, which prevents SQL
injection in customer, account and reference ID queries.

### Exercise 2.5: Idempotency
Every payment carries a reference ID. Before processing, `PaymentService` claims it with
`INSERT ... ON CONFLICT DO NOTHING`. If the ID already exists, the request is rejected
as a duplicate. If the payment is rejected by a business rule, the ID is released so the
customer can retry.

**Known limitation:** each DAO call uses its own connection, so a transfer's changes are
not saved as a single unit. Adding JDBC transactions (the rollback bonus challenge)
would fix this.