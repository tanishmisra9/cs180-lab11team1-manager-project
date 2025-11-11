package UserClasses;

Todo: 
password hashing
Account deletion
User manage reservations
user make transactions
    Payment processing
    For applications such as ticket sales - the USER should be able to checkout

reservations made in the past so that users can cancel them
VIP User

Edits made:
- Added a concurrency test in `TestUserConcurrency.java` to simulate two threads creating users concurrently.
- Implemented a `Runnable` task to create 5 users per thread.
- Used `Thread` objects to execute the task concurrently.
- Ensured threads complete execution using `join()`.
- Displayed the total user count and saved the updated user data to the file.

Class Descriptions:

1. **BasicUser**:
   - Represents a user account with basic functionality.
   - Features:
     - Password hashing using SHA-256.
     - Ability to manage reservations and transactions.
     - Admin status management.
     - User type management (e.g., Regular, Premium, VIP).

2. **TestUserConcurrency**:
   - Tests the concurrency of user creation.
   - Features:
     - Simulates two threads creating users concurrently.
     - Ensures thread-safe operations in user creation.
     - Saves updated user data to a file.

3. **User**:
   - Interface defining the structure and operations for user classes.
   - Features:
     - Methods for managing reservations and transactions.
     - Methods for setting and retrieving user details (e.g., username, password, admin status).

4. **UserAccountManager**:
   - Manages user accounts with thread-safe operations.
   - Features:
     - Create, delete, and manage user accounts.
     - Load and save user data from/to a file.
     - Atomic operations for password changes and user deletions.

5. **UserType**:
   - Enum representing different user types.
   - Features:
     - Defines user types: Regular, Premium, and VIP.
