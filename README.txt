HOTEL RESERVATION MANAGEMENT SYSTEM
===================================

This is a Spring Boot-based web application for managing hotel reservations, users, payments, and staff operations. It includes functionalities for searching rooms, processing payments(simulation), making reservations, managing accounts, and submitting reports.

FEATURES
--------
✔ Room search with filters (type, capacity, check-in and check-out dates)
✔ Payment system using stored cards (with encryption and masking)
✔ Reservation management (edit/cancel 2 days before check-in)
✔ User roles: Manager, Customer, Staff
✔ Staff can submit reports; Manager can view all reservations
✔ Secure login/logout
✔ Role-based dashboards(Manager and Staff)
✔ Review system for customers
✔ Room management (CRUD)
✔ Auto room availability tracking

TECHNOLOGIES USED
-----------------
- Java 17
- Spring Boot (MVC, Data JPA)
- Thymeleaf
- MySQL 
- Bootstrap 5
- JPA & Hibernate

PROJECT MODULES
---------------
- `User`: Login, role-based access (admin, staff, customer)
- `Room`: Add/edit/delete rooms, show available/booked rooms
- `Reservation`: Reservation process after successful payment
- `Payment`: Store masked and encrypted card details
- `Review`: Customer feedback system
- `Report`: Staff-generated reports for managers

RUNNING THE PROJECT
-------------------
1. Clone the repository or download the project folder.
2. Make sure Java and Maven are installed.
3. Update `application.properties` with your database credentials.
4. Run the application using:


5. Visit `http://localhost:1999` in your browser.

DEFAULT ROLES (example)
------------------------
- Manager: Manages all rooms, users, reservations, Can edit any reservation.
- Staff: Can submit reports, Can see reservation
- Customer: Can book rooms, submit reviews, Edit Account and Payment

DATABASE NOTES
--------------
Make sure the database schema is created. Use `schema.sql` or let JPA auto-generate it via `spring.jpa.hibernate.ddl-auto=update`.

SECURITY
--------
- Role-based access is enforced for all operations.
- Card numbers are encrypted using AES before storing.
- Only the last 4 digits of card numbers are shown (masked).


CONTACT
-------
Developer: Aung Thawe Thit Oo
Email: aungthawethit@gmail.com
Phone: 09 668680333

