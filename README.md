# CS180 Movie Theater Reservation System - Team 1

## Contributors
- Tanish Misra
- Ved Joshi
- Sravya Malladi
- Logan Dalton
- Sebastian Ting

## Compiling/Running

**Primary Method (Client-Server Architecture):**
1. Start the server: Run Server.java (listens on port 4242)
2. Start the client: Run ClientDriver.java (connects to localhost:4444)

**Note:** There is a port mismatch in the current implementation. Server.java uses port 4242 while ClientDriver.java attempts to connect to port 4444. For testing:
- Option A: Modify ClientService.java line to connect to port 4242 instead of 4444
- Option B: Modify Server.java to use port 4444
- The MovieTheaterServer.java implementation uses port 4242 and can be used as an alternative server

**Alternative Method (Standalone Terminal):**
- Run TerminalDriver.java for direct database access without networking

Note: The client-server architecture is the current implementation. TerminalDriver is a legacy standalone version.

## Submissions

Ved Joshi - Submitted Phase 1 Vocareum Workspace.

## Testing IO Functionality

### Primary Testing Method: Client-Server Architecture

**All testing should primarily be done using the client-server system (Server.java + ClientDriver.java) as this is the current implementation.**

### Network IO Testing (Primary)

#### Client-Server Communication

**Testing Socket-Based IO:**

1. **Server Startup:**
   ```
   Terminal 1: Run Server.java
   Expected Output: "Waiting for client to connect..."
   Verify: Server listening on port 4242
   Note: Server automatically loads/creates db.dat and populates defaults
   ```

2. **Client Connection:**
   ```
   Terminal 2: Run ClientDriver.java
   Expected: "Welcome to the ticketing client!"
   Expected: Login/Register menu appears
   Verify: Server shows "Client connected!" message
   ```

3. **Login/Registration Flow:**
   ```
   Test Registration:
   - Select option 2 (Register)
   - Enter username: "testuser"
   - Enter password: "pass123"
   - Server receives REGISTER request with RegistrationPayload
   - Server creates BasicUser and adds to database
   - Server responds with ServerPayload(success=true)
   - Expected: "Registration successful! Please log in."
   - Verify: Can immediately login with credentials
   
   Test Login:
   - Select option 1 (Login)
   - Enter username: "testuser"
   - Enter password: "pass123"
   - Server validates password hash
   - Server responds with ServerPayload containing admin status
   - Expected: "Login successful!"
   - Verify: User flow menu appears (or admin menu if admin)
   ```

4. **Movie Browsing and Reservation Flow:**
   ```
   Test Movie List Request:
   - After login, ClientDriver automatically sends REQUEST_MOVIES
   - Server responds with MovieListPayload containing movie names
   - Expected: "Available showings:" with list of movies
   - Verify: Default movies from populateDefaults() appear
   
   Test Auditorium Request:
   - Select a movie from the list
   - ClientDriver sends REQUEST_AUDITORIUM with movie name
   - Server responds with AuditoriumPayload containing seat layout
   - Expected: Seating chart displayed (_=empty, X=taken)
   - Verify: Seats shown correctly
   
   Test Seat Reservation:
   - Enter row and column numbers
   - ClientDriver sends RESERVE_SEAT with ReserveSeatPayload
   - Server validates seat availability via ReservationDatabase.reserve()
   - Server updates auditorium and user reservations
   - Server responds with ServerPayload(success status)
   - Expected: "Reservation successful!" or error message
   - Verify: Next client shows seat as taken (X)
   ```

5. **Admin Operations:**
   ```
   Test Admin Access:
   - Login as admin user (use MakeAdmin.java to grant privileges)
   - Expected: Admin menu appears with options 1-3
   - Verify: Regular users don't see admin menu
   
   Test Reserve Seat for User (Admin):
   - Select option 1
   - Enter movie name, username, row, col
   - Server processes RESERVE_SEAT request
   - Expected: Confirmation message
   - Verify: That user's account has the reservation
   
   Test Edit Showing:
   - Select option 2, then choose name/time/cancel
   - For name change: sends EDIT_SHOWING_NAME with EditShowingNamePayload
   - For time change: sends EDIT_SHOWING_TIME with EditShowingTimePayload
   - For cancel: sends CANCEL_SHOWING with CancelShowingPayload
   - Expected: Server confirms operation
   - Verify: Changes reflected in next movie list request
   
   Test Create Venue:
   - Select option 3
   - Enter venue name, dimensions, showing name, time, price
   - ClientDriver sends CREATE_VENUE with CreateVenuePayload
   - Server creates new Auditorium via database.createAuditorium()
   - Server saves to db.dat
   - Expected: "Venue created successfully"
   - Verify: New showing appears in availability list
   ```

6. **Database Persistence Through Network:**
   ```
   Step 1: Start Server (creates/loads db.dat)
   Step 2: Connect ClientDriver, register user "alice"
   Step 3: Login as alice, make a reservation
   Step 4: Exit ClientDriver
   Step 5: Stop Server (saves database)
   Step 6: Restart Server (loads db.dat)
   Step 7: Connect new ClientDriver instance
   Step 8: Login as alice
   Expected: Previous session data persists
   Verify: alice's reservation is still active
   ```

7. **Concurrent Connection Testing:**
   ```
   Terminal 1: Run Server.java
   Terminal 2: Run ClientDriver.java (User A - login as "userA")
   Terminal 3: Run ClientDriver.java (User B - login as "userB")
   
   Test Scenario:
   - User A reserves seat (Inception, row 5, seat 3)
   - User B browses same movie
   - User B tries to reserve row 5, seat 3
   - Expected: User B sees "Seat taken."
   - Verify: synchronized database prevents double-booking
   
   Test Simultaneous Reservations:
   - Both users select different seats in same movie
   - Expected: Both succeed independently
   - Verify: Thread-safe operations in ReservationDatabase
   ```

8. **Network Error Handling:**
   ```
   Test Disconnection:
   - Start Server and connect ClientDriver
   - Forcibly close Server
   - Expected: ClientDriver's ClientListener.onError() triggered
   - Verify: "Listening failed" error, client disconnects gracefully
   
   Test Invalid Request:
   - Modify ClientDriver to send malformed request
   - Expected: Server's safeRead() returns null
   - Verify: Server continues running, handles next request
   
   Test Server Not Running:
   - Run ClientDriver without Server
   - Expected: "Connection failed" error
   - Verify: Client doesn't hang, exits cleanly
   ```

#### Serialization Testing Over Network

**Testing Payload Serialization:**

```
All network communication uses Java serialization via ObjectOutputStream/ObjectInputStream

Critical serialized objects transmitted:
1. ClientRequest → Server
   - Contains type string and payload object
   - UUID automatically generated
   - Test: Send various request types, verify UUID uniqueness

2. ServerResponse → Client
   - Contains response type and payload
   - Test: Verify client receives correct payload type

3. Complex Payloads:
   - LoginPayload (username, password strings)
   - RegistrationPayload (username, password, isAdmin)
   - ReserveSeatPayload (movieName, row, col, username)
   - AuditoriumPayload (String[][] seats array)
   - MovieListPayload (List<String> movie names)
   - AvailabilityPayload (List<Auditorium> objects)
   - CreateVenuePayload (all venue parameters)

4. Nested Serialization:
   - Auditorium objects contain LocalDateTime fields
   - Lists of complex objects (List<Auditorium>)
   - Test: Create venue, verify all fields transmitted correctly

Verification:
- Run ViewDatabase.java after operations to see server-side state
- Compare with client-side displayed information
- Ensure no data loss during transmission
```

### File-Based IO Testing (Server-Side)

### File-Based IO Testing (Server-Side)

**Note: Database operations occur on the server. Use ViewDatabase.java to inspect server-side state.**

#### Database Persistence (db.dat)

**Testing ReservationDatabase Serialization:**

1. **Initial Database Creation:**
   ```
   Run: Server.java
   Expected: Creates db.dat if not exists, calls populateDefaults()
   Verify: db.dat file appears in project directory
   Check: Run ViewDatabase.java to see default movies loaded
   ```

2. **Data Persistence Test:**
   ```
   Step 1: Start Server.java
   Step 2: Connect ClientDriver.java
   Step 3: Register a new user (e.g., "testuser", "pass123")
   Step 4: Exit ClientDriver (type 'exit' or close)
   Step 5: Stop and restart Server
   Step 6: Connect new ClientDriver instance
   Step 7: Login with same credentials
   Expected: Login succeeds, user data persists across server restarts
   Verify: Run ViewDatabase.java to confirm user in database
   ```

3. **Reservation Persistence Test:**
   ```
   Step 1: Start Server, connect ClientDriver
   Step 2: Login as a user
   Step 3: Make a reservation for a movie
   Step 4: Exit client, stop server
   Step 5: Restart server (loads db.dat)
   Step 6: Connect new client, login
   Step 7: View seat map for same movie
   Expected: Seat shows as taken (X)
   Verify: Run ViewDatabase.java to see reservation in user's list
   ```

4. **Auditorium Persistence Test:**
   ```
   Step 1: Login as admin via ClientDriver
   Step 2: Create a new venue/showing
   Step 3: Exit client, stop and restart server
   Step 4: Connect new client, browse movies
   Expected: New showing appears in the list
   Verify: ViewDatabase.java shows auditorium with correct dimensions/pricing
   ```

5. **Database Corruption Handling:**
   ```
   Test: Delete or corrupt db.dat
   Run: Server.java
   Expected: Server creates fresh database with populateDefaults()
   Verify: ClientDriver shows default movies (Dune 2, Interstellar, etc.)
   Check: ViewDatabase.java confirms default auditoriums loaded
   ```

**Server Database Operations:**

```
The Server class manages database lifecycle:
- Loads on startup: database = ReservationDatabase.loadDatabase()
- Populates defaults: database.populateDefaults()
- Auto-saves after operations (via database.saveData() calls)
- Stores: users, auditoriums, reservations all in db.dat

Test complete lifecycle:
1. Fresh server start → db.dat created
2. Client operations → database modified in memory
3. Server maintains state → multiple clients see same data
4. Server shutdown → state persisted to db.dat
5. Server restart → state restored from db.dat
```

**Using Utility Classes for Server Testing:**

```
ViewDatabase.java - Inspect database contents
- Run to see all users with reservations and transaction history
- View all auditoriums with showing times and dimensions
- Verify data integrity after client operations
- Compare with what clients see to ensure consistency

LoadMovies.java - Batch load movie showings
- Create data/movies.txt with format: movieName,yyyy-MM-dd HH:mm,rows,cols,price
- Example: "Inception,2025-12-25 19:00,10,15,12.50"
- Stop server, run LoadMovies.java, restart server
- Connect client and verify movies appear in listings

MakeAdmin.java - Grant admin privileges
- Edit username in main method (e.g., "tanishmisra")
- Stop server, run MakeAdmin.java, restart server
- Login via ClientDriver with that username
- Verify admin menu (options 1-3) appears
```

#### User Account File IO (UserAccountManager)

**Testing User Data Files:**

1. **Boot from File:**
   ```
   Create users.txt with format: username,hashedPassword,isAdmin,typeCode
   Example: "alice,5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8,false,0"
   Call: UserAccountManager.boot("users.txt")
   Expected: Users loaded into system
   Verify: Can login with loaded credentials
   ```

2. **Save to File:**
   ```
   Create users via registration
   Call: UserAccountManager.saveToFile("users_backup.txt")
   Expected: File created with all user data
   Verify: File readable and properly formatted
   ```

### Network IO Testing

#### Client-Server Communication

**Testing Socket-Based IO:**

1. **Server Startup:**
   ```
   Terminal 1: Run Server.java or MovieTheaterServer.java
   Expected Output: "Waiting for client to connect..."
   Verify: Server listening on port 4242
   ```

2. **Client Connection:**
   ```
   Terminal 2: Run ClientDriver.java or ClientTest.java
   Expected: Client connects to localhost:4444 (ClientDriver) or 4242 (Server)
   Verify: Server shows "Client connected!" message
   ```

3. **Login/Registration Flow:**
   ```
   Test Registration:
   - Client sends REGISTER request with RegistrationPayload
   - Server creates BasicUser and adds to database
   - Server responds with ServerPayload(success=true)
   - Verify: User can immediately login after registration
   
   Test Login:
   - Client sends LOGIN request with LoginPayload
   - Server validates password hash
   - Server responds with ServerPayload containing admin status
   - Verify: Client receives success/failure response
   ```

4. **Reservation Flow:**
   ```
   Test Movie List Request:
   - Client sends REQUEST_MOVIES with null payload
   - Server responds with MovieListPayload containing movie names
   - Server also sends AvailabilityPayload with full auditorium data
   - Verify: Client displays available showings
   
   Test Auditorium Request:
   - Client sends REQUEST_AUDITORIUM with movie name
   - Server responds with AuditoriumPayload containing seat layout
   - Verify: Client displays seating chart with empty/taken seats
   
   Test Seat Reservation:
   - Client sends RESERVE_SEAT with ReserveSeatPayload
   - Server validates seat availability
   - Server updates auditorium and user reservations
   - Server responds with ServerPayload(success status)
   - Verify: Seat marked as taken, user charged, transaction recorded
   ```

5. **Admin Operations:**
   ```
   Test Create Venue:
   - Client sends CREATE_VENUE with CreateVenuePayload
   - Server creates new Auditorium with specifications
   - Server adds to database and saves
   - Verify: New showing appears in availability
   
   Test Edit Showing:
   - Client sends EDIT_SHOWING_TIME with old/new times
   - Server finds auditorium and updates showing time
   - Server maintains sorted order by time
   - Verify: Showing time updated in database
   
   Test Cancel Showing:
   - Client sends CANCEL_SHOWING with showing info
   - Server removes auditorium from database
   - Verify: Showing no longer appears in listings
   ```

6. **Concurrent Connection Testing:**
   ```
   Terminal 1: Run Server
   Terminal 2: Run ClientDriver (User A)
   Terminal 3: Run ClientDriver (User B)
   
   Test Scenario:
   - User A reserves seat (row 5, seat 3)
   - User B tries to reserve same seat
   - Expected: User B receives failure response
   - Verify: Synchronized database prevents double-booking
   ```

7. **Network Error Handling:**
   ```
   Test Disconnection:
   - Start server and connect client
   - Forcibly close server
   - Expected: Client onError() callback triggered
   - Verify: Client handles disconnection gracefully
   
   Test Invalid Payload:
   - Send malformed request
   - Expected: Server's safeRead() returns null
   - Verify: Server continues running, doesn't crash
   ```

#### Serialization Testing

**Testing Payload Serialization:**

```
All payload classes implement Serializable
Test by verifying:
1. Objects can be written to ObjectOutputStream
2. Objects can be read from ObjectInputStream
3. Object state preserved across serialization
4. Collections (List<Auditorium>, etc.) serialize properly

Critical serialized objects:
- ClientRequest (with embedded payload)
- ServerResponse (with embedded payload)
- BasicUser (with reservations and transaction history)
- Auditorium (with seat arrays and prices)
- BasicReservation (with LocalDateTime fields)
- CreditCard (marked transient in BasicUser)
```

### Terminal IO Testing (Legacy - TerminalDriver)

**Note: This section applies to the standalone TerminalDriver.java. For current implementation, see Network IO Testing above.**

#### User Input Validation (TerminalDriver)

**Testing Scanner-Based Input:**

1. **Registration Input:**
   ```
   Run: TerminalDriver.java
   Test empty username:
   Input: "" (empty string)
   Expected: "Username cannot be empty."
   
   Test duplicate username:
   Input: existing username
   Expected: "Username already exists. Please choose another."
   
   Test password mismatch:
   Input: "pass123" and "pass456" for confirmation
   Expected: "Passwords do not match."
   ```

2. **Date/Time Parsing:**
   ```
   When creating showings (admin function):
   Test valid format:
   Input: "2025-12-25 19:00"
   Expected: Parses to LocalDateTime successfully
   
   Test invalid format:
   Input: "25-12-2025 7:00pm"
   Expected: "Invalid date format. Please use yyyy-MM-dd HH:mm"
   
   Test DateTimeParseException handling:
   Verify: Program doesn't crash, prompts user again
   ```

3. **Numeric Input:**
   ```
   Test valid numbers:
   Input: "5" for row selection
   Expected: Parsed successfully
   
   Test invalid numbers:
   Input: "abc" for row selection
   Expected: "Invalid row number." caught by NumberFormatException
   
   Test out-of-bounds:
   Input: "999" for 10x10 auditorium
   Expected: "Out of bounds." validation
   ```

4. **Menu Navigation:**
   ```
   Test valid option:
   Input: "1" for main menu
   Expected: Executes corresponding action
   
   Test invalid option:
   Input: "99" or "xyz"
   Expected: "Invalid option. Please try again."
   
   Test exit commands:
   Input: "0" for logout
   Expected: Returns to guest menu or exits program
   ```

### Client Input Testing (ClientDriver)

**Testing Scanner Input in Client:**

1. **Login/Register Menu:**
   ```
   Test valid choice:
   Input: "1" (Login) or "2" (Register)
   Expected: Prompts for credentials
   
   Test invalid choice:
   Input: "99" or "abc"
   Expected: "Invalid input." continues loop
   ```

2. **Movie Selection:**
   ```
   Test valid movie:
   Input: "Interstellar" (from displayed list)
   Expected: Shows seating chart
   
   Test invalid movie:
   Input: "NonexistentMovie"
   Expected: "Invalid movie!" prompts again
   Note: Input is case-insensitive (equalsIgnoreCase)
   ```

3. **Seat Selection:**
   ```
   Test valid seat:
   Input: row "5", col "3" for available seat
   Expected: Reservation attempted via server
   
   Test invalid numbers:
   Input: "abc" for row
   Expected: "Invalid numbers." caught by NumberFormatException
   
   Test out of bounds:
   Input: row "99", col "99"
   Expected: "Out of bounds." loops back to seat selection
   
   Test taken seat:
   Input: coordinates of taken seat
   Expected: "Seat taken." prompts for different seat
   
   Test exit:
   Input: "exit" when prompted for row
   Expected: Returns to main menu/exits user flow
   ```

4. **Admin Menu Input:**
   ```
   Test create venue:
   - Enter venue name, rows, cols, showing name
   - Time format: "2025-12-25T19:00" (ISO format with T)
   - Price as decimal: "12.50"
   Expected: All parsed correctly, venue created
   
   Test edit showing:
   - Select 1 (name), 2 (time), or 3 (cancel)
   - For time: enter year, month, day, hour, minute separately
   Expected: Server processes edit request
   ```

### Edge Cases and Error Scenarios

#### File IO Edge Cases

```
1. Insufficient Permissions:
   - Make db.dat read-only
   - Attempt to save
   - Expected: IOException caught, error message displayed

2. Disk Full:
   - Simulate full disk during save
   - Expected: IOException handled gracefully

3. Concurrent File Access:
   - Multiple processes accessing db.dat
   - Expected: Serialization handles locks properly

4. Large Database:
   - Database with 1000+ users and auditoriums
   - Test save/load performance
   - Verify: All data loads correctly
```

#### Network IO Edge Cases

```
1. Port Already in Use:
   - Start server twice on same port
   - Expected: BindException, error message

2. Network Timeout:
   - Slow network simulation
   - Expected: Socket timeout handling

3. Large Payload:
   - Send reservation for 100+ seats
   - Expected: Serialization handles large objects

4. Rapid Requests:
   - Client sends multiple requests quickly
   - Expected: BlockingQueue handles properly, responses in order
```

### Automated Testing

**Running JUnit Tests:**

```
AllUserClassesTest.java:
- Tests BasicUser password hashing IO
- Tests CreditCard serialization
- Tests ReservationDatabase add/retrieve operations
- Run: Execute as JUnit test in IDE or via command line

ReservationDatabaseTest.java:
- Tests database save/load cycle
- Tests reserve() method with various scenarios
- Tests getReservations() for users
- Verifies data integrity after serialization

AuditoriumLocalTest.java:
- Tests Auditorium state persistence
- Tests seat reservation updates
- Tests price management operations
- Verifies getter/setter IO operations

ReservationTest.java:
- Tests Reservation equals() for database lookups
- Tests constructor validation
- Tests serialization of reservation objects
```

### Manual Testing Checklist

**Complete IO Flow Test (Client-Server - Primary Method):**

```
Server Setup:
□ Delete db.dat to start fresh
□ Start Server.java
□ Verify "Waiting for client to connect..." message
□ Check db.dat created with populateDefaults()

Client Connection:
□ Start ClientDriver.java in separate terminal
□ Verify "Welcome to the ticketing client!" appears
□ Verify server shows "Client connected!"

Registration Flow:
□ Select option 2 (Register)
□ Enter username: "testuser"
□ Enter password: "testpass"
□ Verify "Registration successful! Please log in."
□ Check ViewDatabase.java shows new user

Login Flow:
□ Select option 1 (Login)
□ Enter username: "testuser"
□ Enter password: "testpass"
□ Verify "Login successful!"
□ Verify user menu appears with movie listings

Movie Browsing:
□ Verify list of available movies displayed
□ Movies should include defaults (Dune 2, Interstellar, etc.)
□ Check all movies have proper date/time formatting

Reservation Flow:
□ Select a movie from the list (e.g., "Interstellar")
□ Verify seating chart displays (_ = empty, X = taken)
□ Enter valid row and column for empty seat
□ Verify reservation confirmation message
□ Check server processes RESERVE_SEAT request
□ Verify seat now shows as X in next view

Database Persistence:
□ Exit ClientDriver
□ Stop Server (Ctrl+C)
□ Restart Server
□ Connect new ClientDriver
□ Login as "testuser"
□ Verify previous reservation still exists
□ Run ViewDatabase.java to confirm data persisted

Admin Operations:
□ Stop server, run MakeAdmin.java with "testuser"
□ Restart server, connect client, login as "testuser"
□ Verify admin menu appears (options 1-3)
□ Test Reserve seat for user:
  - Select option 1
  - Enter movie, username, row, col
  - Verify success message
□ Test Create venue:
  - Select option 3
  - Enter venue details (name, rows, cols, showing, time, price)
  - Time format: "2025-12-25T19:00"
  - Verify venue created
□ Test Edit showing:
  - Select option 2
  - Test name change (option 1)
  - Test time change (option 2)
  - Test cancel showing (option 3)
□ Verify changes reflected in movie listings
□ Run ViewDatabase.java to confirm changes

Concurrent Access Testing:
□ Start Server
□ Start ClientDriver #1, login as "user1"
□ Start ClientDriver #2, login as "user2"
□ User1: Reserve seat (Interstellar, row 5, seat 3)
□ User2: Browse same movie, verify seat shows as X
□ User2: Try to reserve same seat
□ Verify "Seat taken." message
□ User2: Reserve different seat successfully
□ Stop both clients, restart server, reconnect
□ Verify both reservations persisted

Error Handling:
□ Try connecting client before starting server
  - Verify "Connection failed" error
□ Start server, connect client, then stop server
  - Verify client handles disconnection gracefully
□ Enter invalid movie name
  - Verify "Invalid movie!" prompt
□ Enter invalid seat coordinates
  - Verify "Out of bounds." or "Invalid numbers."
□ Try to reserve taken seat
  - Verify "Seat taken." message

Final Verification:
□ Run ViewDatabase.java
□ Verify all users present with correct reservations
□ Verify all auditoriums have correct seat states
□ Verify transaction history matches operations performed
□ Check db.dat file size is reasonable
□ Confirm no exceptions in server terminal output
```

**Alternative Testing (Standalone TerminalDriver - Legacy):**

```
□ Delete db.dat to start fresh
□ Run TerminalDriver.java
□ Register new user with credit card
□ Browse movies (verify defaults loaded)
□ Make reservation
□ View "My Reservations" (verify displayed)
□ View transaction history (verify charges)
□ Exit and restart TerminalDriver
□ Login again, verify all data persists
□ Test admin functions if admin user
□ Run ViewDatabase.java for final verification
```

## Class Descriptions

### Driver Classes

#### TerminalDriver.java
Terminal-based driver for the Movie Theater Reservation System. Provides a complete menu-driven interface for user registration, login, browsing movies, making reservations, and admin functions. Uses ReservationDatabase for data persistence.

#### ClientDriver.java
Client-side driver for the network-based system. Handles user login/registration and provides interfaces for both regular users (seat selection, reservations) and administrators (venue creation, showing management).

#### ClientTest.java
Simple test class for verifying client connectivity and basic operations.

### Core User Classes

#### Interface User
Defines core user account operations including username/password management, admin privileges, and price multipliers based on user tier.

#### Enum UserType
Contains REGULAR, PREMIUM, VIP user types corresponding to different discount levels (1.0, 0.9, 0.75 multipliers respectively).

#### Class BasicUser
Implements the User interface with password hashing (SHA-256), credit card management, reservation tracking, transaction history, and thread-safe operations using locks. Supports account upgrades and cancellations.

#### Class UserAccountManager
Manages user accounts with file persistence. Handles user creation, deletion, login validation, password changes, and account upgrades. Loads and saves user data to external files.

#### Class CreditCard
Represents user credit card information with cardholder name, card number, expiry date, and CVV. Provides masked number display for security.

#### Interface CreditCardInterface
Defines operations for credit card information including getting cardholder name, expiry date, and masked card number.

### Reservation Classes

#### Interface Reservation
Defines core reservation operations including user identification, showtime, movie, date, seating information, and cancellation status.

#### Class BasicReservation
Implements Reservation interface with fields for user, movie, date/time, row, seat, and active status. Overrides equals() and hashCode() to check reservations purely on time, date, movie, and seat to prevent double booking.

#### Class ReservationDatabase
Central database for managing users, reservations, and auditoriums. Handles serialization to db.dat file, reservation validation, auditorium management, and provides methods for editing showings, creating venues, and populating default data.

#### Interface ReservationDatabaseInterface
Defines operations for the reservation database system including reserve, getReservations, addAuditorium, addUser, saveData, editShowingTime, deleteAuditorium, editMovieName, and createAuditorium.

### Auditorium Classes

#### Class Auditorium
Represents a theater auditorium with a jagged array of seats (either "empty" or username) and corresponding seat prices. Manages seat reservations, price modifications (by seat, row, column, or all), showing times, and provides various constructors for different seating configurations.

#### Interface AuditoriumInterface
Defines public contract for auditorium operations including seat management, price management, showing time updates, and reservation management with validation.

### Network/Client-Server Classes

#### Interface Client
Defines client-side network operations including connect, disconnect, isConnected, sendRequest, and listener management.

#### Class BasicClient
Implements Client interface using sockets for network communication. Manages ObjectInputStream/ObjectOutputStream, handles connection lifecycle, and uses CopyOnWriteArrayList for thread-safe listener management.

#### Class ClientService
Service layer for client operations. Provides methods for login, registration, requesting movies, requesting auditorium seating, reserving seats, and admin functions (editing showings, creating venues). Uses BlockingQueue for response handling.

#### Interface ClientListener
Defines callback methods for client events: onConnected, onDisconnected, onResponseReceived, and onError.

#### Class ClientRequest
Represents client requests with unique ID (UUID), request type, and payload object. Serializable for network transmission.

### Server Classes

#### Interface ServerInterface
Defines server operations including run() method and static safeRead() helper for reading ClientRequest objects.

#### Class Server
Server implementation handling client connections on port 4242. Manages login/registration, reservations, availability queries, admin operations (editing showings, canceling showings, creating venues), and uses ReservationDatabase for persistence.

#### Class MovieTheaterServer
Alternative server implementation on port 4242 with basic structure for handling client connections. Uses ReservationDatabase for data management.

#### Class ServerResponse
Server response wrapper containing response type and payload object. Serializable for network transmission.

#### Class ServerPayload
Generic server response payload with success boolean and message string.

### Payload Classes

#### AdminPayloads.java
Contains payload classes for admin operations including AuditoriumPayload (seat layout), ReserveSeatPayload, EditShowingNamePayload, EditShowingTimePayload, CancelShowingPayload, RequestMoviesPayload, RequestAuditoriumPayload, and CreateVenuePayload.

#### LoginPayload.java
Payload for login requests containing username and password.

#### RegistrationPayload.java
Payload for user registration containing username, password, and optional isAdmin flag.

#### CancelPayload.java
Payload for cancellation requests including movie, showtime, number of people, and reservation fee.

#### ReservationPayload.java
Payload for reservation requests including movie, showtime, seat information, number of people, reservation fee, and reservation date.

#### MovieListPayload.java
Payload containing list of movie names for availability display.

#### AvailabilityPayload.java
Payload containing list of Auditorium objects for showing availability information.

### Utility Classes

#### LoadMovies.java
Utility to load movie showings from movies.txt file into the database. Parses format: movieName,yyyy-MM-dd HH:mm,rows,cols,price.

#### MakeAdmin.java
Utility to make a specific user an admin by loading the database, finding the user, setting admin status, and saving.

#### ViewDatabase.java
Utility to view database contents including all users (with type, admin status, credit cards, reservations, transactions) and all auditoriums (with movie, showing time, dimensions).

### Test Classes

#### AllUserClassesTest.java
JUnit tests for BasicUser and CreditCard classes. Tests password hashing, price multipliers, reservations, credit card operations, upgrades, and ReservationDatabase operations.

#### ReservationDatabaseTest.java
JUnit tests for ReservationDatabase class including successful reservations, invalid seats, double booking, wrong movie/time, multiple auditoriums, user management, and save/load functionality.

#### AuditoriumLocalTest.java
Comprehensive JUnit tests for Auditorium class covering all constructors, getters/setters, time updates, price management (individual, row, column, all), and reservation operations with both rectangular and jagged auditoriums.

#### ReservationTest.java
JUnit tests for Reservation functionality including constructor validation with illegal arguments and equals() method testing for same and different users.