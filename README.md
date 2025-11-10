# CS180 Lab 11 Team 1 Movie Theater Project

## Class descriptions

### Interface User
An interface of a User. It is used to create a type of user, in which a username and password can be obtained, can be checked if an administrator, and a password and administration permissions can be set. A price multiplier can also be obtained, depending on the type of users (ie: discounts for certain types of users).

### Enum UserType
An enum that contains REGULAR, PREMIUM, VIP. These correspond to the type of user.

### Class BasicUser 
A class that implements the User interface for a basicUser. They have a price multiplier of 1 (ie no change).

### Class UserAccountManager
A class that manages Users. Retrieves the user database and modifies it to add new users, delete users, login to accounts, change passwords, and view the total amount of users.

### Class TestUserConcurrency
A class that tests the concurrency of users. Users must be able to operate from multiple machines, so multiple threads are used. This class tests if said concurrency is functional without problems.

### Class Auditorium
A class which contains a jagged array of seats, where a seat is either "empty" or the name of the user that fills said seat for the event day. double[][] seatPrices corresponds to each seat's price. It is used for representing the seats and if they are filled. It is also used for setting the prices of seats, and can be used to change prices and reservations.

### Interface Reservation
An interface used for making reservation types. Different reservations have different movies, dates, and seats. A reservation can also be cancelled

### Class Auditorium
A class which contains a jagged array of seats, where a seat is either "empty" or the name of the user that fills said seat for the event day. double[][] seatPrices corresponds to each seat's price. It is used for representing the seats and if they are filled. It is also used for setting the prices of seats, and can be used to change prices and reservations.

### Interface Reservation
An interface used for making reservation types. Different reservations have different movies, dates, and seats. A reservation can also be cancelled

### Class ReservationDatabase
//TODO

### Class BasicReservation
A class which acts as the core record for bookings' relevant information like User, Date & Time of reservation, seat number, etc. Also overrides equals() to check reservations purely on time, date, movie, and seat to prevent double booking.



