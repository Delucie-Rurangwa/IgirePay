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

## Lab 3: Console-Based Payment Management Application

Run: `lab3.lab3Main`

Requires the Lab 2 database setup. The schema in `src/lab2/schema.sql` includes the
`pin_hash` column used for PINs.

### Exercise 3.1: Menu-driven application
| Menu | Features |
|---|---|
| Customer Management | Register customer, update customer information, view customer accounts |
| Account Management | Create wallet, create savings, view balance, delete inactive account, create PIN, change PIN |
| Transaction Management | Deposit, withdraw, transfer, view transaction history |
| Reports | Export history to CSV, daily summary, customer statement |

Every transaction requires a reference ID. Reusing a reference ID is rejected as a
duplicate, so a retried request is never processed twice.

### Exercise 3.2: Exception handling
Custom exceptions extend a common `PaymentException`:
- `InvalidAmountException`: amount is zero or negative
- `InvalidAccountException`: account ID does not exist
- `DuplicateTransactionException`: reference ID already processed
- `InsufficientBalanceException`: balance too low (including the savings fee)
- `TransactionFailedException`: rejected by an account rule (limits, savings transfers)

Database errors are handled separately: connection failures (SQL state `08xxx`) show
a "cannot connect" message; other SQL errors show the database message. Invalid
numbers, invalid dates and file errors are also caught, so the app never crashes.

### Exercise 3.3: Reports
- **CSV export:** writes an account's history to `transactions_<accountId>.csv`
- **Daily summary:** count and total of deposits, withdrawals and transfers for a date
- **Customer statement:** customer details, all accounts, balances and full history

### Exercise 3.4: PIN authentication
- PINs are exactly 4 digits.
- Withdrawals and transfers require the source account's PIN. Deposits do not.
- PINs are never stored as plain text: only a SHA-256 hash of `accountId:PIN` is saved.
- Changing a PIN requires the current PIN.

**Limitation:** the PIN is visible while typing, because hidden input
(`System.console().readPassword()`) does not work in IntelliJ's run window.