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