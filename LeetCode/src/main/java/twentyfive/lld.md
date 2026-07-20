# Low Level Design (LLD) - Parking Lot Design Interview Guide

# Problem Statement

Design an object-oriented Parking Lot system.

The parking lot should support:

* Multiple floors
* Multiple parking spots on each floor
* Different vehicle types
* Different parking spot types
* Parking tickets
* Parking and unparking vehicles

Supported Operations:

```java
parkVehicle(vehicle)

unparkVehicle(ticketId)

displayAvailableSpots()

displayOccupiedSpots()
```

---

# Step 1 - Clarify Requirements

Before writing any code, clarify assumptions.

Example questions:

* Can a parking lot have multiple floors?
* Can each floor have multiple parking spots?
* Can different spot types exist?
* Are parking fees required?
* Should we support handicapped parking?
* Are reservations supported?
* Is thread safety required?

Interviewers appreciate candidates who ask clarifying questions before coding.

---

# Step 2 - Identify Classes

Initially I identified:

```text
ParkingLot

Vehicle

Parking

ParkingTicket
```

This is a good starting point.

However, after discussion, the responsibilities can be improved.

---

# Final Class Diagram

```text
ParkingLot
      |
      |
      +----------------+
                       |
                ParkingFloor
                       |
                       |
             +---------+---------+
             |                   |
             |                   |
       ParkingSpot        ParkingTicket
             |
             |
          Vehicle
```

Notice that:

* ParkingLot contains multiple ParkingFloors.
* Each ParkingFloor contains multiple ParkingSpots.
* A ParkingTicket references both Vehicle and ParkingSpot.

This models the real world much better.

---

# ParkingLot

Instead of storing:

```java
floorId

spotId
```

ParkingLot should own:

```java
class ParkingLot{

    int parkingLotId;

    List<ParkingFloor> floors;
}
```

Reason:

One parking lot contains many floors.

---

# ParkingFloor

```java
class ParkingFloor{

    int floorId;

    List<ParkingSpot> spots;
}
```

Responsibilities:

* Maintain parking spots
* Find available spot

Possible API:

```java
ParkingSpot findAvailableSpot(Vehicle vehicle)
```

---

# ParkingSpot

```java
class ParkingSpot{

    int spotId;

    SpotType type;

    boolean occupied;

    boolean handicapped;
}
```

Responsibilities:

* Represents one parking space
* Knows whether occupied
* Knows spot type

---

# Vehicle

Initially:

```java
Vehicle

vehicleId

vehicleType
```

A better design:

```java
abstract class Vehicle{

    int vehicleId;

    VehicleType type;
}
```

Subclasses:

```text
Vehicle
   |
   +---- Bike
   |
   +---- Car
   |
   +---- Truck
```

This follows good OOP principles.

Adding new vehicle types becomes easier.

---

# ParkingTicket

Instead of storing IDs only:

```java
ticketId

spotId

vehicleId
```

prefer storing object references:

```java
class ParkingTicket{

    int ticketId;

    Vehicle vehicle;

    ParkingSpot parkingSpot;

    LocalDateTime entryTime;

    LocalDateTime exitTime;

    TicketStatus status;
}
```

Object references produce cleaner OO design.

---

# Where Should park() and unpark() Go?

Initially:

```java
Parking{

    park()

    unpark()
}
```

Problem:

Parking is acting as both:

* Data
* Service

This violates Single Responsibility Principle.

A better design:

```java
ParkingLotService
```

Responsibilities:

```java
parkVehicle()

unparkVehicle()

displayAvailableSpots()

displayOccupiedSpots()
```

The service coordinates all entities.

---

# Finding Available Parking Spots

Question:

Suppose parking lot has:

```text
1000 spots
```

Should we scan every spot?

```java
for(all spots)
```

Time Complexity:

```text
O(n)
```

Not ideal.

---

# Better Design

Maintain:

```java
Map<SpotType, Queue<ParkingSpot>>
```

Example:

```text
CAR

↓

Queue of available car spots
```

Now:

```java
findAvailableSpot()
```

becomes:

```text
O(1)
```

Much better.

This is a common senior-level optimization.

---

# Follow-Up Requirement

Suppose interviewer says:

"We are introducing Electric Vehicle charging."

How would your design change?

---

# First Thought

Add:

```java
VehicleType

CAR

TRUCK

BIKE

ELECTRIC
```

This seems reasonable initially.

However, this mixes two concepts.

---

# Why ELECTRIC Is Not A Great VehicleType

Question:

Is Tesla a different vehicle type?

No.

Tesla is still:

```text
Car
```

Electric motorcycle?

Still:

```text
Bike
```

Electric describes:

```text
Power Source
```

not

```text
Vehicle Category
```

---

# Better Design

Separate:

```java
VehicleType
```

from

```java
FuelType
```

Example:

```java
Vehicle{

    VehicleType

    FuelType
}
```

Enums:

```java
VehicleType{

    BIKE

    CAR

    TRUCK
}
```

```java
FuelType{

    GASOLINE

    DIESEL

    ELECTRIC

    HYBRID
}
```

Now Tesla becomes:

```text
VehicleType = CAR

FuelType = ELECTRIC
```

Much cleaner.

---

# How Should ParkingSpot Change?

Instead of:

```java
SpotType.ELECTRIC
```

I would add:

```java
boolean hasElectricCharger;
```

Example:

```java
class ParkingSpot{

    SpotType type;

    boolean occupied;

    boolean handicapped;

    boolean hasElectricCharger;
}
```

Reason:

An electric charger is a capability of the parking spot.

It is not necessarily a different spot type.

---

# Parking Logic

When parking:

```java
if(vehicle.getFuelType()==ELECTRIC){

    preferChargingSpot();

}
else{

    allocateNormalSpot();
}
```

This keeps responsibilities clean.

---

# Why This Design Is Better

VehicleType answers:

```text
What kind of vehicle is this?
```

FuelType answers:

```text
How is it powered?
```

ParkingSpot answers:

```text
Does this parking spot provide charging?
```

Each class has one responsibility.

---

# Interview Follow-Up

Suppose interviewer asks:

"What if normal cars are allowed to park in charging spots when no EVs are waiting?"

Our design still works.

Because:

```java
hasElectricCharger
```

is simply a capability.

We are not forcing charging spots to be a separate spot category.

---

# SOLID Principles Applied

## Single Responsibility Principle

ParkingSpot

↓

Represents one parking space.

ParkingLotService

↓

Coordinates parking operations.

Vehicle

↓

Represents vehicle information.

ParkingTicket

↓

Represents parking session.

---

## Open Closed Principle

Adding:

* Electric Vehicles
* VIP Parking
* Reserved Parking

requires extending classes rather than modifying existing logic.

---

## Liskov Substitution

Bike

Car

Truck

can all be treated as:

```java
Vehicle
```

---

## Interface Segregation

Different interfaces can exist for:

* Payment
* Parking
* Display

instead of one large interface.

---

## Dependency Inversion

ParkingLotService should depend upon abstractions rather than concrete implementations.

---

# Final Interview Class Diagram

```text
                   ParkingLot
                        |
                        |
          +-------------+-------------+
          |                           |
          |                           |
     ParkingFloor               ParkingLotService
          |
          |
     ParkingSpot
          |
          |
       Vehicle
          |
     ParkingTicket
```

---

# Senior Interview Thought Process

When solving any LLD problem:

1. Clarify requirements.
2. Identify entities.
3. Identify relationships.
4. Assign responsibilities.
5. Avoid mixing data and business logic.
6. Look for opportunities to improve complexity.
7. Think about extensibility.
8. Think about SOLID principles.
9. Discuss trade-offs before coding.

---

# Interview Takeaways

* Model the real world first before thinking about code.
* Services should coordinate behaviour; entities should represent domain objects.
* Use inheritance only when there is a true "is-a" relationship.
* Use composition for capabilities (for example, a parking spot **has** an electric charger).
* Optimize common operations (like finding an available spot) using appropriate data structures instead of repeatedly scanning collections.
* Explain trade-offs. Interviewers often care more about your reasoning than your final class diagram.

# My Low-Level Design (LLD) Interview Framework

This framework is optimized for Senior Backend interviews (Amazon, Audible, Microsoft, Uber, etc.) and expands upon the classic "Cracking the Coding Interview" approach.

---

# Step 1 - Clarify Requirements (Handle Ambiguity)

Before writing classes, spend 2-5 minutes understanding the problem.

Interviewers intentionally leave requirements vague.

Good engineers ask questions before designing.

Think about the **6 Ws**:

* Who uses the system?
* What does it do?
* Why does it exist?
* Where is it used?
* When is it used?
* How is it expected to behave?

Example questions:

* Is this single machine or distributed?
* Is persistence required?
* Is this thread-safe?
* Is this in-memory only?
* Is this a library or an application?
* What scale are we targeting?
* Are there any future extensibility requirements?

Never assume requirements.

---

# Step 2 - Identify Core Objects (Entities)

Ask:

"What are the nouns in the problem statement?"

Example:

Parking Lot

↓

ParkingLot

ParkingFloor

ParkingSpot

Vehicle

ParkingTicket

Restaurant

↓

Restaurant

Table

Guest

Order

Menu

Library

↓

Library

Book

Member

Librarian

BookCopy

Try to identify the real-world entities first.

---

# Step 3 - Define Relationships

Now determine how the objects relate.

Ask:

* Is it inheritance?
* Is it composition?
* Is it aggregation?
* One-to-one?
* One-to-many?
* Many-to-many?

Example:

ParkingLot

↓

contains

↓

ParkingFloor

↓

contains

↓

ParkingSpot

Vehicle

↓

creates

↓

ParkingTicket

Use inheritance only when there is a true **"is-a"** relationship.

Prefer composition when possible.

---

# Step 4 - Assign Responsibilities

Probably the most important step.

Ask:

"Who owns what?"

Every class should have one primary responsibility.

Example:

ParkingLot

* Owns floors

ParkingFloor

* Owns parking spots
* Finds available spots

ParkingSpot

* Represents one parking space

ParkingTicket

* Represents one parking session

ParkingLotService

* Coordinates parking and unparking

Avoid mixing:

* Data
* Business Logic

inside the same class.

This naturally follows the Single Responsibility Principle (SRP).

---

# Step 5 - Design Public APIs

Before implementation, define the public methods.

Example:

```java
ParkingTicket parkVehicle(Vehicle vehicle);

void unparkVehicle(String ticketId);

List<ParkingSpot> getAvailableSpots();

List<ParkingSpot> getOccupiedSpots();
```

APIs become the contract between objects.

---

# Step 6 - Choose Data Structures

This is where many candidates lose points.

Ask:

How will I efficiently support the required operations?

Example:

Need fast lookup?

↓

HashMap

Need ordering?

↓

TreeMap

Need FIFO?

↓

Queue

Need priority?

↓

PriorityQueue

Need stack behaviour?

↓

Stack / Deque

Need O(1) insertion/removal?

↓

LinkedHashMap

Need concurrent access?

↓

ConcurrentHashMap

Example:

Instead of scanning all parking spots every time:

```java
for(all spots)
```

Maintain:

```java
Map<SpotType, Queue<ParkingSpot>>
```

Now finding an available spot becomes O(1).

Always discuss complexity.

---

# Step 7 - Walk Through Major Actions

This is equivalent to "Investigate Actions" from Cracking the Coding Interview.

Ask:

How does the system actually work?

Example:

User arrives.

↓

ParkingLotService

↓

ParkingFloor

↓

Find Spot

↓

Allocate Spot

↓

Generate Ticket

Walking through the flow often reveals:

* Missing classes
* Missing APIs
* Missing relationships

---

# Step 8 - Consider Edge Cases

Think about:

* Invalid inputs
* Duplicate requests
* Empty collections
* Resource exhaustion
* Concurrency
* Race conditions

Example:

Two users attempt to park simultaneously.

How is this handled?

---

# Step 9 - Think About Extensibility

Interviewers almost always ask:

"Tomorrow we want to add..."

Examples:

* Electric vehicles
* VIP parking
* Reserved parking
* New payment method
* Membership discounts

Ask yourself:

Can I support this without rewriting existing classes?

This demonstrates the Open/Closed Principle.

---

# Step 10 - Discuss Design Patterns (Only If They Naturally Fit)

Do not force patterns into the solution.

Mention them only when they solve a problem.

Examples:

Factory Pattern

Creating different Vehicle types.

Strategy Pattern

Different parking allocation strategies.

Observer Pattern

Parking availability notifications.

Singleton

ParkingLot instance.

Decorator

Premium parking features.

State Pattern

ParkingTicket status.

Design patterns are a bonus—not the goal.

---

# Step 11 - Analyze Complexity

Always conclude with complexity.

Examples:

Finding available spot:

O(1)

Parking vehicle:

O(1)

Unparking:

O(1)

Display occupied spots:

O(n)

Interviewers appreciate candidates who discuss trade-offs.

---

# Step 12 - Discuss Thread Safety (Senior Interviews)

Finally, think about concurrency.

Questions to ask:

* Can two threads modify the same object?
* Do I need synchronization?
* Should I use ConcurrentHashMap?
* Do I need locking?
* Is optimistic locking sufficient?

Example:

Two users trying to park simultaneously.

How is the race condition prevented?

Senior interviews almost always include some discussion of concurrency.

---

# SOLID Checklist

Before finishing, mentally verify:

✔ Single Responsibility Principle

✔ Open/Closed Principle

✔ Liskov Substitution Principle

✔ Interface Segregation Principle

✔ Dependency Inversion Principle

---

# My Mental Checklist During Every LLD Interview

1. Clarify Requirements
2. Identify Core Objects
3. Define Relationships
4. Assign Responsibilities
5. Design Public APIs
6. Choose Data Structures
7. Walk Through Main Workflow
8. Handle Edge Cases
9. Think About Extensibility
10. Mention Appropriate Design Patterns
11. Analyze Time Complexity
12. Discuss Thread Safety & Concurrency

---

# One-Line Summary

A strong LLD interview is not about writing Java classes first.

It is about demonstrating a structured design process:

Requirements → Objects → Relationships → Responsibilities → APIs → Data Structures → Workflow → Extensibility → Complexity → Concurrency.

# SOLID Principles - Complete Interview Guide

# Introduction

The **SOLID Principles** are five object-oriented design principles that help create software that is:

* Easy to maintain
* Easy to extend
* Easy to test
* Loosely coupled
* Highly cohesive

In Low-Level Design (LLD) interviews, interviewers often ask:

> "How does your design follow SOLID principles?"

Understanding these principles helps you justify your design decisions rather than simply drawing class diagrams.

---

# Overview

| Principle | Full Form                       | Interview Question It Answers                              |
| --------- | ------------------------------- | ---------------------------------------------------------- |
| **S**     | Single Responsibility Principle | Who owns this responsibility?                              |
| **O**     | Open/Closed Principle           | Can I add new features without modifying existing code?    |
| **L**     | Liskov Substitution Principle   | Can child classes safely replace the parent?               |
| **I**     | Interface Segregation Principle | Are my interfaces too large?                               |
| **D**     | Dependency Inversion Principle  | Am I depending on abstractions instead of implementations? |

---

# S - Single Responsibility Principle (SRP)

## Definition

A class should have **only one reason to change**.

Notice that it does **not** say:

> One method.

It says:

> One responsibility.

Each class should focus on one job.

---

# Bad Example

```java
class Parking {

    parkVehicle();

    unparkVehicle();

    calculateFee();

    printReceipt();

    sendEmail();

}
```

Responsibilities:

* Parking
* Billing
* Receipt generation
* Notifications

This class has multiple reasons to change.

It violates SRP.

---

# Better Design

Split responsibilities.

```text
ParkingService

↓

Parking Operations
```

```text
PaymentService

↓

Payment Calculation
```

```text
NotificationService

↓

Email / SMS
```

```text
ReceiptService

↓

Receipt Generation
```

Each class now has one responsibility.

---

# Parking Lot Example

Instead of:

```java
ParkingTicket{

calculateFee()

sendReceipt()

sendEmail()
}
```

Use:

```text
ParkingTicket

↓

Represents parking session
```

```text
PaymentService

↓

Calculates parking fee
```

```text
NotificationService

↓

Sends receipt
```

---

# Interview Tip

Whenever the interviewer asks:

> "Where should this logic go?"

They are often testing the Single Responsibility Principle.

---

# O - Open/Closed Principle (OCP)

## Definition

Software should be:

```text
Open for Extension

Closed for Modification
```

Meaning:

We should be able to introduce new functionality **without modifying existing classes**.

---

# Bad Example

Suppose:

```java
enum VehicleType{

CAR

BIKE

TRUCK
}
```

Parking logic:

```java
if(vehicle==CAR)

...

else if(vehicle==TRUCK)

...

else if(vehicle==BIKE)

...
```

Tomorrow:

Electric Vehicle

Now modify:

```java
parkVehicle()
```

Again.

Next:

Hybrid.

Modify again.

Soon the method becomes very large.

---

# Better Design

Use inheritance.

```text
Vehicle

↓

Car

Truck

Bike

ElectricCar
```

Now:

```java
parkVehicle(Vehicle vehicle)
```

never changes.

We simply introduce a new subclass.

---

# Another Example

Instead of:

```java
if(paymentType==PAYPAL)

...

else if(paymentType==CARD)

...

else if(paymentType==APPLEPAY)
```

Create:

```text
PaymentStrategy

↓

PaypalPayment

CardPayment

ApplePayPayment
```

Classic Strategy Pattern.

New payment methods require adding a new class instead of modifying existing code.

---

# Parking Lot Example

Adding:

* VIP Parking
* Reserved Parking
* Electric Parking

should ideally require extending existing classes instead of modifying multiple methods.

---

# L - Liskov Substitution Principle (LSP)

## Definition

A child class should be usable anywhere the parent class is expected.

If:

```java
parkVehicle(Vehicle vehicle)
```

accepts:

```java
Vehicle
```

then it should work correctly for:

* Car
* Truck
* Bike

without changing any code.

---

# Good Example

```text
Vehicle

↓

Car

Bike

Truck
```

Every subclass behaves like a Vehicle.

---

# Bad Example

Suppose:

```java
class Bird{

fly();
}
```

Then:

```java
class Penguin extends Bird
```

Problem:

Penguins cannot fly.

Calling:

```java
penguin.fly();
```

throws an exception.

Now the subclass cannot replace the parent.

LSP is violated.

---

# Parking Lot Example

Suppose:

```java
parkVehicle(Vehicle vehicle)
```

This method should work regardless of whether:

* Car
* Bike
* Truck

is passed.

No special handling should be required simply because a subclass is used.

---

# I - Interface Segregation Principle (ISP)

## Definition

Clients should not be forced to depend on methods they do not use.

Prefer several small interfaces instead of one large interface.

---

# Bad Example

```java
interface ParkingOperations{

park();

unpark();

pay();

sendEmail();

generateInvoice();

reserveSpot();

cancelReservation();

}
```

A parking attendant only needs:

```text
park()

unpark()
```

Yet they are forced to implement payment and invoice methods.

---

# Better Design

Split the interfaces.

```java
ParkingOperations
```

```java
PaymentOperations
```

```java
NotificationOperations
```

```java
ReservationOperations
```

Each client depends only on the methods it actually uses.

---

# Parking Lot Example

Separate responsibilities into focused interfaces.

Instead of one huge Parking interface, create:

* Parking Interface
* Payment Interface
* Notification Interface

---

# D - Dependency Inversion Principle (DIP)

## Definition

Depend upon abstractions rather than concrete implementations.

High-level modules should not depend directly on low-level classes.

---

# Bad Example

```java
class ParkingService{

PaypalPayment payment = new PaypalPayment();

}
```

ParkingService is tightly coupled to Paypal.

Tomorrow:

Stripe.

Need to modify ParkingService.

---

# Better Design

Create an abstraction.

```java
interface Payment{

pay();

}
```

Implementations:

```text
PaypalPayment

CreditCardPayment

ApplePayPayment

StripePayment
```

Now:

```java
class ParkingService{

Payment payment;

}
```

ParkingService no longer cares which payment implementation is provided.

Someone else injects the implementation.

This greatly improves flexibility and testability.

---

# Parking Lot Example

ParkingService should depend upon:

```java
Payment
```

rather than:

```java
PaypalPayment
```

Tomorrow we can switch to:

* Stripe
* Apple Pay
* Credit Card

without changing ParkingService.

---

# SOLID Applied To Parking Lot

## Single Responsibility

ParkingSpot

↓

Represents one parking space.

ParkingTicket

↓

Represents one parking session.

ParkingLotService

↓

Coordinates parking operations.

PaymentService

↓

Calculates fees.

NotificationService

↓

Sends notifications.

---

## Open Closed

Need to add:

* Electric Vehicle
* VIP Parking
* Reserved Parking

Create new classes rather than modifying existing business logic.

---

## Liskov Substitution

Car

Bike

Truck

should all work wherever:

```java
Vehicle
```

is expected.

---

## Interface Segregation

Separate interfaces for:

* Parking
* Payment
* Notifications
* Reservations

instead of one large interface.

---

## Dependency Inversion

ParkingLotService should depend upon:

```java
Payment
```

rather than:

```java
PaypalPayment
```

---

# Memory Tricks

## S

```text
One Class

↓

One Job
```

---

## O

```text
Extend

Don't Modify
```

---

## L

```text
Child behaves exactly like Parent
```

---

## I

```text
Small Focused Interfaces
```

---

## D

```text
Depend on Interfaces

Not Concrete Classes
```

---

# Common Interview Questions

## Question

Why did you create ParkingLotService?

Answer:

To follow the Single Responsibility Principle. ParkingLot represents the domain entity, while ParkingLotService coordinates business logic.

---

## Question

How would you add Electric Vehicles?

Answer:

I would extend the existing design rather than modifying existing parking logic, following the Open/Closed Principle.

---

## Question

Why use a Payment interface?

Answer:

ParkingLotService should depend on an abstraction rather than a specific implementation. This follows the Dependency Inversion Principle and allows payment providers to be swapped without changing business logic.

---

## Question

Why split interfaces?

Answer:

Different clients require different functionality. Small focused interfaces prevent unnecessary dependencies and follow the Interface Segregation Principle.

---

# SOLID Interview Cheat Sheet

| Principle | Simple Meaning                 | Parking Lot Example                                                      |
| --------- | ------------------------------ | ------------------------------------------------------------------------ |
| **S**     | One class → One responsibility | Separate ParkingService, PaymentService and NotificationService          |
| **O**     | Extend without modifying       | Add new Vehicle or Payment classes instead of changing existing methods  |
| **L**     | Child should replace Parent    | Car, Bike and Truck all work wherever Vehicle is expected                |
| **I**     | Small focused interfaces       | Separate ParkingOperations, PaymentOperations and NotificationOperations |
| **D**     | Depend on abstractions         | ParkingLotService depends on Payment interface, not PaypalPayment        |

---

# Final Interview Summary

Whenever solving an LLD interview, mentally verify:

✔ Does every class have one responsibility?

✔ Can I extend functionality without modifying existing code?

✔ Can subclasses safely replace their parent?

✔ Are my interfaces focused and small?

✔ Am I depending on abstractions instead of concrete implementations?

Following these five principles results in code that is easier to maintain, easier to extend, easier to test, and more scalable as the system evolves.


# Online Book Reader LLD - Identifying Core Objects

# Problem Statement

Design an **Online Book Reader System** similar to Kindle, Audible eBooks or O'Reilly.

For the initial design, assume:

* In-memory application
* Ignore databases
* Ignore distributed systems
* Ignore payments
* Users purchase books permanently
* Users cannot download the raw book
* Books are rendered through our application
* System remembers where the user stopped reading

---

# LLD Design Process

Before writing any Java code, always follow this sequence:

1. Clarify Requirements
2. Identify Core Objects (Entities)
3. Define Relationships
4. Assign Responsibilities
5. Design APIs
6. Choose Data Structures
7. Walk Through Main Workflow
8. Discuss Extensibility
9. Analyze Complexity
10. Discuss SOLID Principles

This chapter focuses only on **Step 2 - Identifying Core Objects**.

---

# What Are Core Objects?

A common mistake is immediately thinking about classes and methods.

Instead, first ask:

> **"What are the nouns in this system?"**

Almost every domain can first be broken into nouns.

For this problem the nouns are:

* User
* Book
* Author
* Library
* Purchase
* Reading Progress
* Catalog

Notice these are **things**.

Not actions.

---

# Important Rule

Always ask yourself:

> **Is this a Thing or an Action?**

Things become:

```text
Entities
```

Actions become:

```text
Services
```

This simple rule helps avoid many poor designs.

---

# Core Entity 1 - User

Represents the customer using the application.

Possible fields:

```java
class User{

    userId;

    name;

    email;

    address;
}
```

Question:

Should User contain:

```java
currentPage
```

No.

Why?

Because a user may read hundreds of books.

Which page would we store?

Instead, reading progress belongs somewhere else.

---

# Core Entity 2 - Book

Represents one book.

```java
class Book{

    bookId;

    title;

    author;

    genre;

    price;

    numberOfPages;
}
```

Question:

Should Book contain:

```java
currentPage
```

Absolutely not.

Imagine:

Harry Potter

is read by

* John
* Alice
* Bob

Each user is on a different page.

Current page is **not a property of the book.**

---

# Core Entity 3 - Author (Optional)

Initially we can simply store:

```java
String author;
```

However, I would ask the interviewer:

Should Author become its own class?

For example:

```java
class Author{

    authorId;

    name;

    biography;
}
```

If future requirements include:

* List all books by author
* Author profile
* Author biography

then Author deserves to be its own entity.

---

# Core Entity 4 - BookCatalog

Question:

Where do all books live?

Not inside User.

Not inside Book.

Instead create:

```java
class BookCatalog{

    List<Book> books;

}
```

Responsibilities:

* Stores all available books
* Allows searching

Possible APIs:

```java
searchByTitle()

searchByAuthor()

searchByGenre()
```

Notice:

BookCatalog owns books.

It does not own users.

---

# Core Entity 5 - UserLibrary

This is one of the most commonly missed classes.

Suppose John purchases:

100 books.

Where do they live?

Many candidates simply write:

```java
User{

    List<Book>
}
```

This works for very small systems.

However, it becomes difficult to support:

* Favorites
* Purchased Books
* Archived Books
* Downloaded Books
* Collections

Instead:

```java
class UserLibrary{

    List<BookPurchase>
}
```

Now UserLibrary owns everything related to the user's collection.

This is a cleaner separation of responsibilities.

---

# Core Entity 6 - BookPurchase

This was one of the strongest entities identified during the discussion.

Question:

Why not simply store:

```java
User

↓

List<Book>
```

Because a purchase contains information of its own.

Example:

```java
class BookPurchase{

    userId;

    bookId;

    purchaseDate;

    pricePaid;

    couponUsed;

    paymentMethod;
}
```

Notice:

These fields belong neither to:

User

nor

Book

They belong to:

The relationship between User and Book.

This is called an **Association Class** (Relationship Entity).

---

# Core Entity 7 - BookRead

Another excellent relationship entity.

BookRead represents:

> **A particular user reading a particular book.**

Possible fields:

```java
class BookRead{

    userId;

    bookId;

    currentPage;

    lastReadTimestamp;

    readingProgress;

    totalReadingTime;
}
```

Notice something important.

Question:

Where should currentPage be stored?

Option 1

Inside User?

No.

User can read multiple books.

Option 2

Inside Book?

No.

Millions of users can read the same book simultaneously.

Option 3

Inside BookRead?

Yes.

Because current page belongs to the relationship between:

User

and

Book.

---

# Why BookRead Is Needed

Suppose:

John opens Harry Potter.

He reads until page:

```text
100
```

He closes the application.

Tomorrow he opens Harry Potter again.

What changes?

Not:

User

Not:

Book

Only:

BookRead

Before:

```text
BookRead

currentPage = 87
```

After:

```text
BookRead

currentPage = 100

lastReadTimestamp = Now
```

The next day the Reader simply loads BookRead and opens the book at page 100.

---

# Association Classes

Both BookPurchase and BookRead are examples of Association Classes.

An Association Class stores information that belongs to the relationship between two objects.

Instead of:

```text
User -------- Book
```

we now have:

```text
User
    \
     \
   BookPurchase
      /
     /
Book
```

and

```text
User
    \
     \
    BookRead
      /
     /
Book
```

These classes do **not** describe the User.

They do **not** describe the Book.

They describe the interaction between them.

This is a very common modeling technique in LLD interviews.

---

# Things vs Actions

One of the biggest improvements during the discussion was separating:

Things

from

Actions.

Ask yourself:

Is this a Thing?

or

Is this an Action?

Example:

Book

↓

Thing

Entity

---

User

↓

Thing

Entity

---

Purchase

↓

Thing

Entity

---

Reading Progress

↓

Thing

Entity

---

Registration

↓

Action

Service

---

Login

↓

Action

Service

---

Search

↓

Action

Service

---

Open Book

↓

Action

Service

---

# Service Classes

These classes perform work.

They are not part of the domain model.

---

# UserService

Responsibilities:

* Register user
* Login
* Logout
* Manage profile

Possible APIs:

```java
register()

login()

logout()
```

Initially, I had considered creating:

```java
UserRegistration
```

However, registration is not a business object.

It is an action.

Therefore it belongs inside UserService.

---

# ReaderService

Question:

Who performs:

```java
openBook()

nextPage()

previousPage()

closeBook()
```

Not Book.

Not BookRead.

These are actions.

Therefore:

```java
ReaderService
```

Responsibilities:

```java
openBook()

nextPage()

previousPage()

saveProgress()
```

ReaderService updates BookRead whenever the user changes pages.

---

# LibraryService

Responsibilities:

* Purchase books
* View user's library
* Remove books (if supported)
* List purchased books

Possible APIs:

```java
purchaseBook()

viewLibrary()
```

Again,

Purchasing is an action.

Not an entity.

---

# BookCatalog

BookCatalog is a service-like domain object responsible for:

* Maintaining available books
* Searching books

Possible APIs:

```java
searchByTitle()

searchByAuthor()

searchByGenre()
```

---

# Domain Objects vs Services

One useful technique during interviews is separating all classes into two groups.

---

## Domain Objects (Entities)

These represent business concepts.

```text
User

Book

Author

UserLibrary

BookPurchase

BookRead
```

They primarily contain state.

---

## Services

These coordinate behaviour.

```text
UserService

LibraryService

ReaderService

BookCatalog
```

They primarily contain business logic.

---

# Why This Separation Is Important

Suppose someone asks:

Who performs:

```java
purchaseBook()
```

Book?

No.

User?

No.

Purchase?

No.

Instead:

```text
LibraryService
```

coordinates the process.

Similarly,

Who performs:

```java
nextPage()
```

Not Book.

Not BookRead.

Instead:

```text
ReaderService
```

updates BookRead.

Keeping entities focused on representing data and services focused on business operations naturally follows the **Single Responsibility Principle (SRP)** and results in a cleaner, more maintainable design.

---

# Final Object List

## Domain Entities

```text
Book
```

Represents one book.

---

```text
User
```

Represents one customer.

---

```text
Author (Optional)
```

Represents an author if additional author information is required.

---

```text
UserLibrary
```

Represents a user's collection of purchased books.

---

```text
BookPurchase
```

Represents ownership of a particular book by a user.

Stores relationship-specific information such as purchase date and price.

---

```text
BookRead
```

Represents a user's reading state for a particular book.

Stores relationship-specific information such as current page and reading progress.

---

## Service Classes

```text
UserService
```

Handles registration and authentication.

---

```text
LibraryService
```

Handles book purchases and library management.

---

```text
ReaderService
```

Handles opening books, turning pages and saving reading progress.

---

```text
BookCatalog
```

Maintains the collection of books and provides search functionality.

---

# Key Interview Takeaways

* Start by identifying **nouns**, not methods.
* Separate **Things (Entities)** from **Actions (Services)**.
* Information that belongs to the relationship between two entities should be modeled as an **Association Class** (for example, `BookPurchase` and `BookRead`).
* Do not put behaviour everywhere. Let services coordinate workflows while entities represent the business domain.
* Before moving to relationships or APIs, make sure every class has a clear purpose and a single responsibility.

A well-structured set of entities makes the rest of the LLD—relationships, APIs, workflows, and SOLID principles—much easier to reason about and explain during an interview.

# Online Book Reader LLD - Step 3: Defining Relationships

# Introduction

Once the core entities have been identified, the next step in any Low-Level Design interview is to define the relationships between them.

This is often where interviewers determine whether you truly understand object-oriented modeling.

At this stage we are **NOT** writing methods or Java code.

We are simply answering:

> **How do these objects relate to each other?**

---

# Relationship Types

Every relationship is one of the following:

```text
One-to-One

One-to-Many

Many-to-One

Many-to-Many
```

A useful question to ask yourself is:

> **"How many?"**

For every pair of objects.

---

# Relationship 1 - User ↔ UserLibrary

Question:

Can one user have multiple libraries?

No.

Can one library belong to multiple users?

No.

Relationship:

```text
User 1 -------- 1 UserLibrary
```

Meaning:

Every user owns exactly one library.

The library represents the user's personal collection of books.

---

# Relationship 2 - UserLibrary ↔ BookPurchase

Suppose John owns:

* Harry Potter
* Lord of the Rings
* Hobbit

Question:

How many purchases does John's library contain?

Three.

Relationship:

```text
UserLibrary 1 -------- * BookPurchase
```

One library contains many purchases.

---

# Relationship 3 - BookPurchase ↔ Book

Question:

Does one purchase represent multiple books?

No.

Each purchase represents one book.

However,

Can one book be purchased by many users?

Yes.

Example:

```text
Harry Potter

↓

Purchased by

John

Alice

Bob

David
```

Relationship:

```text
Book 1 -------- * BookPurchase
```

One book can have many purchase records.

---

# Relationship 4 - User ↔ BookRead

Initially it appears that:

```text
User

↓

reads

↓

Book
```

is a Many-to-Many relationship.

Question:

Can John read multiple books?

Yes.

Can Harry Potter be read by multiple users?

Yes.

Instead of connecting User directly to Book, we introduce:

```text
BookRead
```

Now the relationship becomes:

```text
User 1 -------- * BookRead
```

and

```text
Book 1 -------- * BookRead
```

BookRead breaks the many-to-many relationship into two one-to-many relationships.

---

# Relationship 5 - BookCatalog ↔ Book

Simple relationship.

One catalog stores many books.

```text
BookCatalog

↓

List<Book>
```

Relationship:

```text
BookCatalog 1 -------- * Book
```

---

# Relationship 6 - UserService ↔ User

Important distinction.

UserService does NOT own users.

It manages them.

Relationship:

```text
UserService

------>

User
```

This is a dependency rather than ownership.

Responsibilities include:

* Register
* Login
* Delete

---

# Relationship 7 - ReaderService

ReaderService coordinates:

```text
User

Book

BookRead
```

ReaderService does not own any of these objects.

It simply performs operations on them.

Typical operations:

* Open Book
* Next Page
* Previous Page
* Save Progress

Again, this is a dependency relationship.

---

# Complete Relationship Diagram

```text
                    BookCatalog
                         |
                         |
                     List<Book>
                         |
                         |
     -----------------------------------------
     |                                       |
     |                                       |
   Book                           BookPurchase
     |                                   |
     |                                   |
     |                              UserLibrary
     |                                   |
     |                                   |
  BookRead -------------------------- User
```

Notice something important.

BookPurchase

and

BookRead

sit between

User

and

Book.

Why?

Because they represent information about the relationship between User and Book.

---

# Association Classes (Relationship Entities)

One of the most important concepts in LLD is recognizing when information belongs to the relationship between two entities rather than either entity individually.

Instead of:

```text
User -------- Book
```

we introduce an intermediate class.

Examples:

```text
User
   \
    \
 BookPurchase
    /
   /
Book
```

and

```text
User
   \
    \
   BookRead
    /
   /
Book
```

These are called:

* Association Classes
* Relationship Entities

---

# Why BookPurchase Exists

Question:

Where should:

* Purchase Date
* Price Paid
* Coupon Used
* Payment Method

be stored?

Option 1

Inside User?

No.

A user purchases many books.

Option 2

Inside Book?

No.

A book is purchased by many users.

Correct Answer:

```text
BookPurchase
```

Because purchase information belongs to:

> User purchasing a Book.

Not User.

Not Book.

---

# Why BookRead Exists

Question:

Where should:

* Current Page
* Last Read Timestamp
* Reading Progress

be stored?

Suppose:

Harry Potter

is being read by

* John
* Alice
* Bob

If Book contained:

```java
currentPage
```

Question:

Whose page?

Impossible.

If User contained:

```java
currentPage
```

Question:

Which book?

Also impossible.

Correct Answer:

```text
BookRead
```

Because reading progress belongs to:

> User reading a Book.

---

# Interview Scenario

Suppose:

John opens Harry Potter.

Reads until page:

```text
100
```

Closes application.

Returns tomorrow.

Which object changes?

Not:

User

Not:

Book

Only:

```text
BookRead
```

Before:

```text
BookRead

currentPage = 87
```

After:

```text
BookRead

currentPage = 100

lastReadTimestamp = Now
```

ReaderService simply loads BookRead the next day and resumes from page 100.

---

# Ownership vs Dependency

Another common interview topic.

---

## Ownership (Composition)

Examples:

```text
BookCatalog

↓

Book
```

BookCatalog owns books.

---

```text
UserLibrary

↓

BookPurchase
```

UserLibrary owns purchases.

---

## Dependency

Services usually do not own entities.

Instead they coordinate them.

Examples:

```text
UserService

↓

User
```

---

```text
ReaderService

↓

BookRead
```

---

```text
BookStoreService

↓

BookCatalog
```

Services use entities.

They do not own them.

---

# Interview Follow-Up

Suppose the interviewer now says:

> We want to support Bookmarks.

Question:

Which class should own bookmarks?

Not:

Book

Reason:

Millions of users can read the same book.

Bookmarks cannot belong to the book.

---

Not:

User

Reason:

A user reads multiple books.

Which bookmark belongs to which book?

---

Correct Answer:

```text
BookRead
```

Because bookmarks belong to:

> A specific user reading a specific book.

---

# Extending BookRead

BookRead can naturally evolve into the complete reading state.

Example:

```java
class BookRead{

    User user;

    Book book;

    int currentPage;

    LocalDateTime lastRead;

    List<Bookmark> bookmarks;

    List<Highlight> highlights;

    List<Annotation> notes;

}
```

Notice something elegant.

Every feature related to reading now belongs in one place.

---

# Bookmarks

Bookmark itself deserves its own entity.

Example:

```java
class Bookmark{

    int pageNumber;

    String name;

    LocalDateTime createdAt;
}
```

BookRead then contains:

```java
List<Bookmark>
```

Tomorrow if the interviewer asks:

Support multiple bookmarks.

No redesign required.

---

# Why Not Store Bookmarks Inside User?

Suppose John owns:

* Harry Potter
* Hobbit
* Lord of the Rings

Question:

Which book does each bookmark belong to?

You would end up needing:

```java
Map<Book, Bookmark>
```

This immediately suggests that Bookmark belongs to the User–Book relationship instead.

---

# Why Not Store Bookmarks Inside Book?

Suppose Harry Potter has one million readers.

Whose bookmarks would the Book contain?

Again,

Bookmarks belong to the relationship.

Not the book itself.

---

# General Interview Rule

Whenever a new requirement is introduced, ask yourself:

> **Does this information belong to one entity, or does it belong to the interaction between two entities?**

Examples:

Purchase Date

↓

User purchases Book

↓

BookPurchase

---

Current Page

↓

User reads Book

↓

BookRead

---

Bookmark

↓

User reads Book

↓

BookRead

---

Highlight

↓

User reads Book

↓

BookRead

---

Annotation

↓

User reads Book

↓

BookRead

---

Rating

↓

User reviews Book

↓

BookReview

Notice a pattern.

Many features naturally become relationship entities.

---

# Ownership vs Relationship

A useful mental model:

Book owns:

* Title
* Genre
* Price
* Author

User owns:

* Name
* Email
* Address

BookPurchase owns:

* Purchase Date
* Price Paid
* Coupon

BookRead owns:

* Current Page
* Reading Progress
* Last Read
* Bookmarks
* Highlights
* Notes

Each class owns only the information that logically belongs to it.

---

# Interview Takeaways

* Always identify cardinality (One-to-One, One-to-Many, Many-to-Many).
* Use Association Classes when information belongs to the relationship between two entities.
* Separate ownership from dependency.
* Services coordinate objects; they do not own them.
* Whenever a new feature is introduced, ask whether it belongs to a single entity or to the interaction between two entities.
* Thinking in terms of relationship entities (`BookPurchase`, `BookRead`, `BookReview`) is a hallmark of strong object-oriented design and is frequently tested in senior LLD interviews.

# Online Book Reader LLD - Step 4, Step 5 & Step 6

# Overview

By this point in the interview we have already completed:

* Clarified the requirements
* Identified the core entities
* Defined the relationships between those entities

The remaining design process focuses on:

1. Assigning responsibilities
2. Designing public APIs
3. Choosing appropriate data structures

These three steps naturally build on one another.

A useful way to think about them is:

```text
Relationships tell us WHO owns WHAT.

Responsibilities tell us WHO should DO WHAT.

APIs tell us HOW they expose that functionality.

Data Structures tell us HOW they efficiently implement those APIs.
```

---

# Step 4 - Assign Responsibilities

One of the biggest mistakes candidates make is asking:

> "Where should I put this method?"

Instead, ask:

> **"Which class is responsible for this behavior?"**

This naturally leads to cleaner designs and follows the **Single Responsibility Principle (SRP).**

Every class should ideally have one primary responsibility.

---

# Book

## Responsibility

Represents book metadata.

It owns:

```text
Book Id

Title

Author

Genre

Price

Number Of Pages
```

Question:

Should Book know how to:

```java
purchaseBook()

nextPage()

bookmarkPage()
```

No.

Those are workflows.

Book simply represents information about a book.

---

# User

## Responsibility

Represents a customer.

Owns:

```text
User Id

Name

Email

Address

Payment Information
```

Question:

Should User contain:

```java
purchaseBook()

updateCurrentPage()
```

No.

Purchasing books and reading books involve multiple entities and therefore belong inside services.

---

# UserLibrary

## Responsibility

Represents the user's collection of purchased books.

Owns:

```text
Purchased Books
```

Possible operations:

```java
addPurchase()

removePurchase()

getPurchasedBooks()
```

Notice:

UserLibrary does not perform payment.

It simply maintains the user's owned collection.

---

# BookPurchase

## Responsibility

Represents ownership of a book.

Owns:

```text
Purchase Date

Price Paid

Coupon Used

Payment Method
```

Question:

Should BookPurchase perform:

```java
purchaseBook()
```

No.

BookPurchase represents the result of a purchase.

The purchasing workflow belongs elsewhere.

---

# BookRead

## Responsibility

Represents the user's reading state.

Owns:

```text
Current Page

Reading Progress

Bookmarks

Highlights

Annotations

Last Read Timestamp
```

Question:

Should BookRead perform:

```java
openBook()
```

No.

BookRead stores state.

Opening a book is a workflow handled by a service.

---

# BookCatalog

## Responsibility

Maintains the available books.

Provides searching functionality.

Owns:

```text
Available Books
```

Operations include:

```java
searchByTitle()

searchByAuthor()

searchByGenre()
```

Question:

Should BookCatalog purchase books?

No.

Searching and purchasing are separate responsibilities.

---

# UserService

## Responsibility

Manages users.

Possible responsibilities:

```java
register()

login()

logout()

deleteUser()
```

Notice:

UserService manages users.

It does not manage books.

---

# BookStoreService

## Responsibility

Coordinates bookstore operations.

Typical responsibilities:

```java
purchaseBook()

addBook()

removeBook()
```

Notice something important.

BookStoreService coordinates multiple entities:

```text
BookCatalog

↓

UserLibrary

↓

BookPurchase
```

It does not own these objects.

It coordinates them.

---

# ReaderService

## Responsibility

Coordinates the reading workflow.

Responsibilities include:

```java
openBook()

nextPage()

previousPage()

closeBook()

saveProgress()
```

ReaderService updates BookRead whenever the user changes pages.

Again,

ReaderService coordinates objects.

It does not own them.

---

# Interview Thought Process

Instead of asking:

> "Where should this method go?"

Ask:

> **"Who is responsible for this behavior?"**

Example:

Bookmarks.

Question:

Who stores bookmarks?

Answer:

```text
BookRead
```

Question:

Who creates bookmarks?

Answer:

```text
ReaderService
```

One stores state.

The other performs behaviour.

---

# One-Sentence Responsibility Rule

Every class should be explainable in one sentence.

| Class            | Responsibility                        |
| ---------------- | ------------------------------------- |
| Book             | Represents book metadata.             |
| User             | Represents a customer.                |
| UserLibrary      | Represents a user's owned collection. |
| BookPurchase     | Represents ownership of a book.       |
| BookRead         | Represents a user's reading state.    |
| BookCatalog      | Stores and searches books.            |
| UserService      | Manages users.                        |
| BookStoreService | Coordinates purchasing.               |
| ReaderService    | Coordinates reading.                  |

If a class requires several sentences to explain, it probably has too many responsibilities.

---

# Step 5 - Design Public APIs

Once responsibilities are clear, APIs become almost obvious.

The APIs should naturally reflect each class's responsibility.

---

# Book

Book is primarily a data object.

Typical APIs:

```java
getTitle()

getAuthor()

getGenre()

getPrice()
```

Notice:

Book does not expose:

```java
purchaseBook()
```

or

```java
nextPage()
```

---

# User

Typical APIs:

```java
getName()

updateEmail()

updateAddress()
```

Again,

User does not purchase books.

---

# UserLibrary

Responsibilities:

Maintain purchased books.

Possible APIs:

```java
List<BookPurchase> getPurchasedBooks();

void addPurchase(BookPurchase purchase);

void removePurchase(BookPurchase purchase);
```

---

# BookCatalog

Responsibilities:

Search books.

Possible APIs:

```java
searchByTitle(String title);

searchByAuthor(String author);

searchByGenre(Genre genre);

getBook(BookId id);
```

Searching belongs here.

Purchasing does not.

---

# BookPurchase

Mostly state.

Possible APIs:

```java
getPurchaseDate();

getPricePaid();
```

---

# BookRead

Represents reading progress.

Possible APIs:

```java
getCurrentPage();

updateCurrentPage(int page);

addBookmark(Bookmark bookmark);

getBookmarks();
```

Notice:

BookRead stores reading information.

It does not open books.

---

# UserService

Possible APIs:

```java
registerUser();

login();

logout();

deleteUser();
```

---

# BookStoreService

Coordinates purchasing.

Possible APIs:

```java
purchaseBook(User user, Book book);

addBook(Book book);

removeBook(Book book);
```

Question:

Why isn't purchaseBook() inside User?

Because purchasing involves:

* User
* Book
* UserLibrary
* BookPurchase

It coordinates multiple entities.

Therefore it belongs in a service.

---

# ReaderService

Coordinates reading.

Possible APIs:

```java
openBook(User user, Book book);

nextPage();

previousPage();

closeBook();

saveProgress();
```

Question:

Who updates BookRead?

Answer:

ReaderService.

BookRead stores state.

ReaderService performs the workflow.

---

# Senior Interview Thought Process

Entities represent nouns.

Services expose verbs.

Examples:

```java
purchaseBook()

registerUser()

openBook()

nextPage()

searchByAuthor()
```

Every service method should read almost like English.

---

# Example Workflow

Suppose John purchases Harry Potter.

The API called is:

```java
BookStoreService.purchaseBook(user, book)
```

Internally the service performs:

```text
Validate User

↓

Validate Book Exists

↓

Create BookPurchase

↓

Update UserLibrary

↓

Return Success
```

Notice:

BookStoreService coordinates multiple objects.

It does not perform every operation itself.

---

Suppose John opens Harry Potter.

The API called is:

```java
ReaderService.openBook(user, book)
```

Internally:

```text
Verify User Owns Book

↓

Find Existing BookRead

↓

If none exists, create BookRead

↓

Resume From Last Page

↓

Render Book
```

---

# Step 6 - Choose Data Structures

After APIs have been defined, ask:

> **"How can these APIs be implemented efficiently?"**

Do not begin with data structures.

Begin with operations.

---

# Example

Operation:

```java
getBook(bookId)
```

Desired complexity:

```text
O(1)
```

Question:

Which data structure provides O(1) lookup?

Answer:

```text
HashMap
```

Now the choice becomes obvious.

---

# BookCatalog

Suppose we only stored:

```java
List<Book>
```

Searching by ID requires:

```java
for(Book b : books)
```

Complexity:

```text
O(n)
```

Not ideal.

---

## Better Design

Maintain multiple indexes.

```java
Map<BookId, Book> booksById;
```

Now:

```java
getBook(bookId)
```

becomes:

```text
O(1)
```

---

# Searching By Title

Maintain:

```java
Map<String, List<Book>> booksByTitle;
```

Searching by title becomes:

```text
O(1)
```

instead of scanning the complete catalog.

---

# Searching By Author

Maintain:

```java
Map<String, List<Book>> booksByAuthor;
```

---

# Searching By Genre

Maintain:

```java
Map<Genre, List<Book>> booksByGenre;
```

Notice:

Instead of repeatedly scanning a list,

we maintain indexes.

This is exactly how relational databases work internally.

---

# Interview Tip

A good explanation is:

> Since searching by title, author and genre are common operations, I would maintain separate in-memory indexes using HashMaps. This avoids repeatedly scanning the entire collection and reduces lookup time from O(n) to O(1).

Interviewers love hearing the word:

```text
Index
```

---

# UserLibrary

Current design:

```java
List<BookPurchase>
```

Question:

How do we determine whether a user already owns a particular book?

Using a List:

```java
for(...)
```

Complexity:

```text
O(n)
```

Better:

```java
Map<BookId, BookPurchase>
```

Now:

```java
ownsBook(bookId)
```

becomes:

```text
O(1)
```

---

# BookRead

ReaderService frequently needs to retrieve reading progress.

Suppose it receives:

```text
UserId

BookId
```

Instead of:

```java
List<BookRead>
```

Maintain:

```java
Map<Pair<UserId, BookId>, BookRead>
```

or

```java
Map<UserId,
    Map<BookId, BookRead>>
```

Now retrieving reading progress becomes:

```text
O(1)
```

---

# UserService

Maintain:

```java
Map<UserId, User>
```

Fast lookup.

---

# Services

Notice something.

Services rarely own data.

They coordinate entities.

Therefore:

ReaderService

BookStoreService

UserService

typically do not maintain large collections themselves.

---

# Trade-Offs

HashMaps provide:

Advantages:

```text
O(1) Lookup

O(1) Insert
```

Disadvantages:

```text
Higher Memory Usage

Multiple Indexes Must Be Updated
```

For example,

adding a new book requires updating:

```text
booksById

booksByTitle

booksByAuthor

booksByGenre
```

This is the classic trade-off between read performance and write complexity.

---

# Final Data Structures

| Class            | Data Structure                                       | Why                             |
| ---------------- | ---------------------------------------------------- | ------------------------------- |
| BookCatalog      | Map<BookId, Book>                                    | Fast lookup by ID               |
| BookCatalog      | Map<String, List<Book>>                              | Search by title                 |
| BookCatalog      | Map<String, List<Book>>                              | Search by author                |
| BookCatalog      | Map<Genre, List<Book>>                               | Search by genre                 |
| UserLibrary      | Map<BookId, BookPurchase>                            | Fast ownership lookup           |
| Reading Progress | Map<Pair<UserId, BookId>, BookRead> (or nested maps) | Fast retrieval of reading state |
| UserService      | Map<UserId, User>                                    | Fast user lookup                |

---

# Key Interview Takeaways

* Relationships identify ownership.
* Responsibilities identify behaviour.
* APIs naturally follow responsibilities.
* Data structures are chosen based on the operations they need to support.
* Think about operations first, then optimize them with the appropriate data structure.
* Services coordinate workflows; entities own business state.
* A strong LLD design balances readability, maintainability, extensibility and performance.

# Online Book Reader LLD - Step 4, 5 & 6

## Responsibilities → Public APIs → Data Structures

By this point, we have already:

* Clarified the requirements
* Identified the core entities
* Defined the relationships

Now the remaining design becomes much easier.

Notice the natural flow:

```text
Relationships
        ↓
Responsibilities
        ↓
Public APIs
        ↓
Data Structures
```

Each step naturally follows from the previous one.

---

# Step 4 & 5 - Responsibilities and Public APIs

A mistake many candidates make is asking:

> "Where should I put this method?"

Instead ask:

> **Who is responsible for this behavior?**

Once you answer that question, the APIs become almost obvious.

---

# Book

## Responsibility

Represents book metadata.

It owns:

* Book Id
* Title
* Author
* Genre
* Price
* Number Of Pages

Book is simply a domain object.

It should **not** know how users purchase or read books.

### Should NOT Do

```java
purchaseBook()

nextPage()

bookmark()

openBook()
```

Those are workflows involving multiple objects.

### Typical APIs

```java
getTitle()

getAuthor()

getGenre()

getPrice()

getNumberOfPages()
```

Notice these are simple accessor methods.

---

# User

## Responsibility

Represents a customer.

Owns:

* User Id
* Name
* Email
* Address
* Payment Information

Again,

User should not know how to purchase books.

That is a business workflow.

### Should NOT Do

```java
purchaseBook()

openBook()

nextPage()
```

### Typical APIs

```java
updateEmail()

updateAddress()

getName()
```

---

# UserLibrary

## Responsibility

Represents everything a user owns.

Owns:

```text
Purchased Books
```

Notice that I prefer:

```java
Map<BookId, BookPurchase>
```

instead of

```java
List<Book>
```

because ownership contains more than just the Book.

We also need:

* Purchase Date
* Price Paid
* Coupon Used

### Typical APIs

```java
addPurchase(BookPurchase purchase)

removePurchase(BookPurchase purchase)

getPurchasedBooks()

ownsBook(BookId id)
```

Notice:

UserLibrary maintains the collection.

It does not perform payment.

---

# BookPurchase

## Responsibility

Represents ownership of a particular book.

This is an **Association Class** between:

```text
User

↓

Book
```

Owns:

* Purchase Date
* Price Paid
* Coupon Used
* Payment Method

### Typical APIs

```java
getPurchaseDate()

getPricePaid()
```

Notice:

BookPurchase does **not** perform purchasing.

It represents the result of the purchase.

---

# BookRead

## Responsibility

Represents the reading state of one user for one book.

This is another Association Class.

It owns:

* Current Page
* Reading Progress
* Last Read Timestamp
* Bookmarks
* Highlights
* Notes

### Typical APIs

```java
getCurrentPage()

updateCurrentPage()

addBookmark()

getBookmarks()
```

Question:

Should BookRead expose:

```java
openBook()
```

No.

BookRead stores reading state.

ReaderService performs the workflow.

---

# BookCatalog

## Responsibility

Stores and searches all available books.

Owns:

```text
All Books
```

### Typical APIs

```java
searchByTitle()

searchByAuthor()

searchByGenre()

getBook(BookId)
```

Question:

Should it expose:

```java
purchaseBook()
```

No.

Searching and purchasing are different responsibilities.

---

# UserService

## Responsibility

Manage users.

### Typical APIs

```java
register()

login()

logout()

deleteUser()
```

Notice:

UserService coordinates user-related workflows.

---

# BookStoreService

## Responsibility

Coordinates bookstore operations.

Notice the word:

```text
Coordinates
```

It doesn't own:

* Books
* Purchases
* Libraries

Instead it orchestrates them.

### Typical APIs

```java
purchaseBook(User, Book)

addBook(Book)

removeBook(Book)
```

Internally it coordinates:

```text
BookCatalog

↓

BookPurchase

↓

UserLibrary
```

---

# ReaderService

## Responsibility

Coordinates the reading workflow.

Again,

it coordinates.

It doesn't own BookRead.

### Typical APIs

```java
openBook(User, Book)

nextPage()

previousPage()

closeBook()

saveProgress()
```

Internally it updates:

```text
BookRead
```

---

# Interview Thought Process

Suppose the interviewer asks:

> "Who should own bookmarks?"

Think like this.

Who stores bookmarks?

```text
BookRead
```

Who creates bookmarks?

```text
ReaderService
```

One stores state.

One performs behaviour.

That separation follows the Single Responsibility Principle.

---

# Step 6 - Choosing Data Structures

Now ask a completely different question.

Not:

> Who owns this?

Instead ask:

> **How can I efficiently support my APIs?**

Always begin with the operation.

Then choose the data structure.

---

## Operation

```java
getBook(bookId)
```

Desired complexity:

```text
O(1)
```

Data Structure:

```java
Map<BookId, Book>
```

---

## Operation

```java
searchByTitle()
```

Desired complexity:

```text
Better than O(n)
```

Data Structure:

```java
Map<String, List<Book>>
```

---

## Operation

```java
searchByAuthor()
```

Data Structure:

```java
Map<String, List<Book>>
```

---

## Operation

```java
searchByGenre()
```

Data Structure:

```java
Map<Genre, List<Book>>
```

Notice something interesting.

Instead of repeatedly scanning:

```java
List<Book>
```

we maintain multiple indexes.

This is exactly how databases work internally.

---

## UserLibrary

Operation:

```java
Does this user own this book?
```

Using:

```java
List<BookPurchase>
```

requires

```text
O(n)
```

A better choice:

```java
Map<BookId, BookPurchase>
```

Now:

```java
ownsBook(bookId)
```

becomes

```text
O(1)
```

---

## BookRead

ReaderService frequently needs:

```text
UserId

+

BookId
```

to retrieve reading progress.

Instead of:

```java
List<BookRead>
```

I'd maintain:

```java
Map<Pair<UserId, BookId>, BookRead>
```

or

```java
Map<UserId,
    Map<BookId, BookRead>>
```

Both provide efficient lookup.

---

## UserService

Need:

```java
getUser(userId)
```

Use:

```java
Map<UserId, User>
```

---

# General Rule

Never choose a data structure first.

Always think:

```text
Operation

↓

Desired Complexity

↓

Data Structure
```

---

# Trade-offs

HashMaps are excellent because they provide:

* O(1) lookup
* O(1) insertion

However they also require:

* More memory
* Multiple indexes to remain synchronized

For example, when adding a new book, BookCatalog must update:

```text
booksById

booksByTitle

booksByAuthor

booksByGenre
```

This is the classic trade-off between:

```text
Faster Reads

vs

Higher Memory & More Complex Writes
```

---

# Final Interview Mindset

When designing any LLD problem:

1. Identify the responsibility.
2. The responsibility tells you the APIs.
3. The APIs tell you which operations are important.
4. The important operations determine the data structures.

Thinking in this order produces designs that are easier to explain, easier to extend, and easier to defend during an interview.

# Parking Lot LLD

# Problem Statement

Design a Parking Lot system for a multi-floor shopping mall.

Assumptions:

* 5 parking floors
* Supports:

  * Motorcycle
  * Car
  * Truck
  * Electric Vehicle (EV)
* Each floor contains:

  * Motorcycle spots
  * Compact spots
  * Large spots
  * EV charging spots
* Vehicles should only park in compatible spots.
* Payment happens when the vehicle exits.
* Ignore reservations initially.
* Ignore towing.
* Support Cash and Credit Card payments.
* Focus on object-oriented design rather than database implementation.

---

# LLD Design Framework

For every LLD problem, follow the same approach.

1. Clarify Requirements
2. Identify Core Objects
3. Define Relationships
4. Responsibilities + Public APIs
5. Data Structures
6. Workflow
7. Extensibility
8. SOLID Principles

---

# Step 1 - Clarify Requirements

Some important clarification questions:

* Is this an Airport, Mall or Office parking lot?
* What vehicle types are supported?
* How many parking floors are there?
* Are different parking spot types supported?
* Are there different pricing models?
* Which payment methods are supported?
* What happens if a vehicle overstays?
* Do we support reservations?

---

# Step 2 - Core Objects

## Vehicle

Represents a vehicle entering the parking lot.

```java
abstract class Vehicle{

    int vehicleId;

    String licensePlate;

    VehicleType vehicleType;

}
```

---

## ParkingLot

Represents the entire parking facility.

```java
class ParkingLot{

    int parkingLotId;

    String location;

    List<ParkingFloor> floors;

    int totalCapacity;

}
```

---

## ParkingFloor

Represents one parking floor.

```java
class ParkingFloor{

    int floorNumber;

    List<ParkingSpot> parkingSpots;

}
```

---

## ParkingSpot

Represents one parking space.

```java
class ParkingSpot{

    int spotId;

    SpotType spotType;

    boolean isOccupied;

}
```

---

## ParkingTicket

Represents one parking session.

Notice this is an **Association Class**.

It represents:

```
Vehicle
     ↓
Parking Session
     ↓
Parking Spot
```

```java
class ParkingTicket{

    int ticketId;

    Vehicle vehicle;

    ParkingSpot parkingSpot;

    LocalDateTime entryTime;

    LocalDateTime exitTime;

}
```

---

## ParkingService

Coordinates the complete parking workflow.

```java
class ParkingService{

}
```

---

# Step 3 - Relationships

## ParkingLot → ParkingFloor

One ParkingLot contains many ParkingFloors.

```
ParkingLot 1 -------- * ParkingFloor
```

---

## ParkingFloor → ParkingSpot

One ParkingFloor contains many ParkingSpots.

```
ParkingFloor 1 -------- * ParkingSpot
```

---

## Vehicle → ParkingTicket

A vehicle may enter the parking lot multiple times.

Each visit creates a new ParkingTicket.

```
Vehicle 1 -------- * ParkingTicket
```

---

## ParkingSpot → ParkingTicket

One parking spot is used by many parking sessions over time.

```
ParkingSpot 1 -------- * ParkingTicket
```

---

## Why ParkingTicket?

Instead of directly connecting

```
Vehicle

↓

ParkingSpot
```

we introduce

```
Vehicle

↓

ParkingTicket

↓

ParkingSpot
```

ParkingTicket represents the relationship between the vehicle and the parking spot.

It stores:

* Entry Time
* Exit Time
* Assigned Spot
* Vehicle
* Payment Details

This follows the same pattern as:

```
BookPurchase

BookRead
```

from the Online Book Reader problem.

---

# Step 4 - Responsibilities + Public APIs

## Vehicle

### Responsibility

Represents a vehicle.

Owns:

* Vehicle Id
* License Plate
* Vehicle Type

### Typical APIs

```java
getVehicleId()

getLicensePlate()

getVehicleType()
```

Vehicle should **not** know how to park itself.

---

## ParkingLot

### Responsibility

Represents the entire parking facility.

Owns:

* Parking Floors
* Location
* Capacity

### Typical APIs

```java
getLocation()

getParkingFloors()

getCapacity()
```

ParkingLot should not coordinate parking.

---

## ParkingFloor

### Responsibility

Represents one parking floor.

Maintains available parking spots.

### Typical APIs

```java
getAvailableSpots()

getTotalSpots()
```

---

## ParkingSpot

### Responsibility

Represents one parking spot.

Owns:

* Spot Id
* Spot Type
* Occupancy Status

### Typical APIs

```java
getSpotId()

getSpotType()

isOccupied()
```

ParkingSpot should not assign vehicles.

---

## ParkingTicket

### Responsibility

Represents one parking session.

Owns:

* Vehicle
* Parking Spot
* Entry Time
* Exit Time

### Typical APIs

```java
getVehicle()

getSpot()

getEntryTime()

getExitTime()
```

ParkingTicket does not compute payment.

It simply represents the parking session.

---

## ParkingService

### Responsibility

Coordinates the complete parking workflow.

Notice the keyword:

**Coordinates**

It does not own:

* Vehicle
* ParkingSpot
* ParkingTicket

Instead it coordinates them.

### Typical APIs

```java
parkVehicle()

findAvailableSpot()

unparkVehicle()

computeTicketCost()
```

Typical workflow:

```
parkVehicle()

↓

findAvailableSpot()

↓

Create ParkingTicket

↓

Mark Spot Occupied

↓

Return Ticket
```

---

# Step 5 - Data Structures

At this point we stop asking:

> Who owns this?

Instead ask:

> How do we support these APIs efficiently?

Always think:

```
Operation

↓

Desired Complexity

↓

Data Structure
```

---

## ParkingLot

```
List<ParkingFloor>
```

Reason:

Very small fixed collection.

No optimization required.

---

## ParkingFloor

Suppose a Car arrives.

We need to quickly find:

```
Available Car Spot
```

Using

```java
List<ParkingSpot>
```

requires scanning every spot.

Complexity:

```
O(n)
```

Better:

```java
Map<SpotType, Queue<ParkingSpot>>
```

Example:

```
COMPACT

↓

Spot 1

Spot 5

Spot 10

----------------

EV

↓

Spot 3

Spot 8

----------------

TRUCK

↓

Spot 30

Spot 35
```

Now allocating a parking spot becomes

```
queue.poll()

↓

O(1)
```

---

## ParkingService

Need fast lookup by Ticket.

Maintain:

```java
Map<TicketId, ParkingTicket>
```

Now

```
unparkVehicle(ticketId)
```

becomes

```
O(1)
```

---

Optionally maintain:

```java
Map<VehicleId, ParkingTicket>
```

to quickly locate an active parking session.

---

# Interview Discussion

Suppose interviewer asks:

> Why Queue instead of Stack?

Answer:

Parking spots are allocated in arrival order.

FIFO allocation is more natural.

Queue is therefore a better fit.

---

Suppose interviewer asks:

> Why HashMap?

Answer:

Most operations involve exact lookups.

HashMap provides O(1) lookup.

---

# Extensibility

## New Requirement

Support VIP Parking.

My initial solution would be simple.

Extend ParkingSpot.

```java
boolean isReserved;
```

Extend Vehicle.

```java
boolean isVIP;
```

During

```java
parkVehicle()
```

simply check:

```java
if(vehicle.isVIP())
```

and allocate from reserved spots.

---

## Data Structure

Instead of one map:

```java
Map<VehicleType, Queue<ParkingSpot>>
```

maintain:

```java
Map<VehicleType, Queue<ParkingSpot>> regularSpots;

Map<VehicleType, Queue<ParkingSpot>> reservedSpots;
```

This keeps the implementation simple and satisfies the current requirement.

---

## Why Not a Generic Solution?

A more extensible design could introduce a composite key or a strategy pattern.

However, for a 45-minute interview this is unnecessary complexity.

It is better to solve today's requirement cleanly than to over-engineer for hypothetical future requirements.

If later the interviewer adds multiple customer categories (VIP, Employee, Handicapped, Emergency), we can refactor to a more generic allocation strategy.

---

# Key Interview Takeaways

* Separate **entities** from **services**.
* Use **Association Classes** (`ParkingTicket`) when information belongs to the relationship between two entities.
* Responsibilities naturally lead to APIs.
* Choose data structures based on the operations they need to support.
* Prefer simple designs that satisfy current requirements.
* Refactor only when new requirements justify additional abstraction.
* Always explain **why** a class exists and **why** a method belongs there. This reasoning is often more important than the final class diagram itself.


# Rate Limiter LLD (Without Strategy Pattern)

# Problem Statement

Design a Rate Limiter for an API Gateway.

Assumptions:

* Rate limiting is performed per API Key.
* Every API Key is allowed **100 requests per minute**.
* Use the **Token Bucket** algorithm.
* Return **HTTP 429 (Too Many Requests)** when the limit is exceeded.
* Assume a **single JVM**.
* The rate limit should be configurable.

---

# LLD Design Framework

For every LLD problem, follow the same approach:

1. Clarify Requirements
2. Identify Core Objects
3. Define Relationships
4. Responsibilities + Public APIs
5. Data Structures
6. Workflow
7. Extensibility
8. SOLID Principles

---

# Step 1 - Clarify Requirements

Typical interview questions:

* What are we rate limiting?

  * API Key?
  * User Id?
  * Session?
  * IP Address?
* How many requests are allowed?
* Which algorithm should be used?

  * Fixed Window
  * Sliding Window
  * Token Bucket
  * Leaky Bucket
* What response should be returned when limit is exceeded?
* Single server or distributed?
* Should configuration be dynamic?

---

# Step 2 - Core Objects

## Request

Represents an incoming API request.

```java
class Request{

    String requestId;

    String apiKey;

    long timestamp;

}
```

---

## ApiClient

Represents a registered API consumer.

```java
class ApiClient{

    String apiKey;

    UserType userType;

}
```

Initially all clients may belong to the same plan.

UserType is added for future extensibility.

---

## RateLimitPolicy (TokenConfig)

Represents the configuration for a rate limit.

```java
class RateLimitPolicy{

    int maxTokens;

    int refillRate;

}
```

Notice:

This is configuration.

It should not contain runtime state.

---

## TokenBucket

Represents the runtime state of one client's bucket.

```java
class TokenBucket{

    int currentTokens;

    long lastRefillTimestamp;

    RateLimitPolicy policy;

}
```

Notice the separation.

Configuration:

* maxTokens
* refillRate

Runtime State:

* currentTokens
* lastRefillTimestamp

---

## RateLimiterService

Coordinates the complete rate limiting workflow.

```java
class RateLimiterService{

}
```

---

# Step 3 - Relationships

## ApiClient → Request

One client can send many requests.

```
ApiClient 1 -------- * Request
```

---

## ApiClient → TokenBucket

Each client owns one token bucket.

```
ApiClient 1 -------- 1 TokenBucket
```

---

## RateLimitPolicy → ApiClient

Many clients may share the same policy.

```
RateLimitPolicy 1 -------- * ApiClient
```

Example:

```
Premium Policy

↓

Client A

↓

Client B

↓

Client C
```

---

## RateLimiterService

Internally maintains:

```
Map<ApiKey, ApiClient>

Map<ApiKey, TokenBucket>
```

---

# Step 4 - Responsibilities + Public APIs

## Request

### Responsibility

Represents an incoming API request.

Owns:

* Request Id
* API Key
* Timestamp

### APIs

```java
getApiKey()

getTimestamp()

getRequestId()
```

Request does not know anything about rate limiting.

---

## ApiClient

### Responsibility

Represents a registered API client.

Owns:

* API Key
* User Type

### APIs

```java
getApiKey()

getUserType()
```

ApiClient should not perform rate limiting.

---

## RateLimitPolicy

### Responsibility

Represents the configuration for a subscription plan.

Owns:

* Maximum Tokens
* Refill Rate

### APIs

```java
getMaxTokens()

getRefillRate()
```

Notice:

It only stores configuration.

It should not modify token counts.

---

## TokenBucket

### Responsibility

Represents one client's token bucket.

Owns:

* Current Tokens
* Last Refill Time
* RateLimitPolicy

This class owns its state.

Therefore it should also own the behavior that modifies the state.

### APIs

```java
boolean allowRequest()

private refillTokens()

int getCurrentTokens()
```

Why only expose allowRequest()?

Because the outside world shouldn't know:

* when refill happens
* how refill happens
* when tokens are decremented

Internally:

```
allowRequest()

↓

refillTokens()

↓

tokens > 0 ?

↓

Yes

↓

tokens--

↓

Return true

↓

Else

↓

Return false
```

This is an example of **Encapsulation**.

---

## RateLimiterService

### Responsibility

Coordinates the rate limiting workflow.

It does NOT own:

* Token count
* Refill logic

Instead it coordinates existing objects.

### APIs

```java
boolean processRequest(Request request)
```

Typical workflow:

```
Request arrives

↓

Find ApiClient

↓

Find TokenBucket

↓

bucket.allowRequest()

↓

Allowed?

↓

Yes → Continue Request

↓

No → HTTP 429
```

Notice:

RateLimiterService never performs

```java
tokens--;
```

The bucket owns that responsibility.

---

# Step 5 - Data Structures

At this stage ask:

> Which operations should be efficient?

Always think:

```
Operation

↓

Desired Complexity

↓

Data Structure
```

---

## Find Client

Operation

```
Find ApiClient using API Key
```

Desired Complexity

```
O(1)
```

Data Structure

```java
Map<ApiKey, ApiClient>
```

---

## Find Token Bucket

Operation

```
Find TokenBucket using API Key
```

Desired Complexity

```
O(1)
```

Data Structure

```java
Map<ApiKey, TokenBucket>
```

---

No additional complex data structures are required for the Token Bucket algorithm.

Each bucket maintains only its own runtime state.

---

# Step 6 - Workflow

```
Incoming Request

↓

Extract API Key

↓

Lookup ApiClient

↓

Lookup TokenBucket

↓

TokenBucket.allowRequest()

↓

Refill Tokens if needed

↓

Token Available?

↓

YES

↓

Consume Token

↓

Allow Request

-------------------------

NO

↓

Return HTTP 429
```

---

# Step 7 - Extensibility

## New Requirement

Different subscription plans.

Example:

```
FREE

100 requests/min

----------------

PREMIUM

1000 requests/min

----------------

ENTERPRISE

5000 requests/min
```

Very little changes.

ApiClient

```java
UserType
```

already exists.

RateLimitPolicy

can now store

```
FREE

↓

Policy

----------------

PREMIUM

↓

Policy

----------------

ENTERPRISE

↓

Policy
```

Every TokenBucket simply references its client's policy.

No change to the workflow.

---

# SOLID Principles

## Single Responsibility Principle

Each class has one responsibility.

Request

* Represents a request

ApiClient

* Represents a client

RateLimitPolicy

* Stores configuration

TokenBucket

* Stores runtime state and rate limiting behavior

RateLimiterService

* Coordinates the workflow

---

## Open/Closed Principle

Current implementation supports only Token Bucket.

If requirements change to another algorithm (Sliding Window, Fixed Window, Leaky Bucket), the implementation will need modification.

In the next iteration we will improve this design using the **Strategy Pattern** so that RateLimiterService remains unchanged while different rate limiting algorithms become interchangeable.

---

# Key Interview Takeaways

* Separate **configuration** from **runtime state**.
* The object that owns the state should own the behavior that modifies it.
* `TokenBucket` owns token count, so it should expose `allowRequest()` instead of allowing external classes to manipulate its state.
* `RateLimiterService` should coordinate objects rather than implement the Token Bucket algorithm itself.
* Use `HashMap<ApiKey, TokenBucket>` for O(1) bucket lookup.
* Design for today's requirements first; introduce design patterns only when new requirements justify additional abstraction.

# Rate Limiter LLD - Strategy Pattern Follow-up

> **Interview Follow-up**
>
> We have successfully designed a Token Bucket based Rate Limiter.
>
> The interviewer now introduces a new requirement:
>
> "Tomorrow we may replace Token Bucket with Sliding Window, Fixed Window or Leaky Bucket. Modify your design so that new algorithms can be added without changing the RateLimiterService."

This is a classic **Open/Closed Principle** follow-up and is an excellent opportunity to introduce the **Strategy Pattern**.

---

# Why the Original Design Breaks

Our original design looked like this:

```java
class RateLimiterService{

    Map<ApiKey, TokenBucket> buckets;

    boolean processRequest(Request request){

        TokenBucket bucket = buckets.get(request.getApiKey());

        return bucket.allowRequest();

    }

}
```

The problem:

Tomorrow if the interviewer says:

* Use Sliding Window
* Use Fixed Window
* Use Leaky Bucket

RateLimiterService must change.

For example,

```java
SlidingWindow window = windows.get(apiKey);

return window.allowRequest();
```

Now the service depends directly on one algorithm.

Every new algorithm forces modifications to the service.

This violates the **Open/Closed Principle**.

---

# Goal

We want the service to remain unchanged.

Only the algorithm implementation should change.

---

# Strategy Pattern

Instead of depending on TokenBucket,

RateLimiterService should depend on an abstraction.

```java
interface RateLimitingAlgorithm{

    boolean allowRequest(Request request);

}
```

Now every algorithm implements this interface.

---

# Token Bucket Implementation

```java
class TokenBucketAlgorithm
        implements RateLimitingAlgorithm{

    Map<ApiKey, TokenBucket> buckets;

    @Override
    public boolean allowRequest(Request request){

        TokenBucket bucket =
            buckets.get(request.getApiKey());

        return bucket.allowRequest();

    }

}
```

Notice:

The Token Bucket specific data structure

```java
Map<ApiKey, TokenBucket>
```

belongs inside this implementation.

---

# Sliding Window Implementation

Sliding Window requires completely different state.

Instead of TokenBucket,

it stores timestamps.

```java
class SlidingWindowAlgorithm
        implements RateLimitingAlgorithm{

    Map<ApiKey, Deque<Long>> windows;

    @Override
    public boolean allowRequest(Request request){

        // Sliding Window Logic

    }

}
```

Notice:

Different algorithm

↓

Different state

↓

Different data structure.

RateLimiterService doesn't need to know.

---

# Fixed Window Implementation

```java
class FixedWindowAlgorithm
        implements RateLimitingAlgorithm{

    Map<ApiKey, WindowCounter> counters;

    @Override
    public boolean allowRequest(Request request){

        // Fixed Window Logic

    }

}
```

Again,

the implementation owns its own state.

---

# Leaky Bucket Implementation

```java
class LeakyBucketAlgorithm
        implements RateLimitingAlgorithm{

    Map<ApiKey, LeakyBucket> buckets;

    @Override
    public boolean allowRequest(Request request){

        // Leaky Bucket Logic

    }

}
```

Again,

no changes to RateLimiterService.

---

# Updated RateLimiterService

Now the service depends only on the interface.

```java
class RateLimiterService{

    private RateLimitingAlgorithm algorithm;

    public boolean processRequest(Request request){

        return algorithm.allowRequest(request);

    }

}
```

Notice something important.

RateLimiterService no longer knows:

* Token Bucket
* Sliding Window
* Fixed Window
* Leaky Bucket

It only knows

```java
allowRequest()
```

---

# Why Should the Algorithm Own the Data Structure?

Question:

Where should

```java
Map<ApiKey, TokenBucket>
```

live?

Wrong:

```java
RateLimiterService
```

Correct:

```java
TokenBucketAlgorithm
```

Reason:

That map is part of the Token Bucket implementation.

Tomorrow,

Sliding Window doesn't need

```java
Map<ApiKey, TokenBucket>
```

It needs

```java
Map<ApiKey, Deque<Long>>
```

Similarly,

Fixed Window needs

```java
Map<ApiKey, WindowCounter>
```

Each algorithm owns its own runtime state.

---

# Final Class Diagram

```text
                    +---------------------------+
                    | RateLimiterService        |
                    +---------------------------+
                    | - algorithm              |
                    +---------------------------+
                               |
                               |
                               ▼
                +--------------------------------+
                | RateLimitingAlgorithm          |
                +--------------------------------+
                | +allowRequest(Request):boolean |
                +--------------------------------+
                     ▲         ▲          ▲
                     |         |          |
                     |         |          |
      +------------------+ +------------------+ +------------------+
      | TokenBucketAlgo  | | SlidingWindow    | | FixedWindowAlgo  |
      +------------------+ +------------------+ +------------------+
      | Map<ApiKey,      | | Map<ApiKey,      | | Map<ApiKey,      |
      | TokenBucket>     | | Deque<Long>>     | | WindowCounter>   |
      +------------------+ +------------------+ +------------------+
```

---

# Workflow

```text
Incoming Request

↓

RateLimiterService

↓

algorithm.allowRequest(request)

↓

Token Bucket?
            \
             \
              Sliding Window?
                       \
                        \
                         Fixed Window?

↓

true / false

↓

Allow Request or HTTP 429
```

Notice:

The workflow never changes.

Only the algorithm changes.

---

# Benefits

## Open/Closed Principle

Open for Extension

* Add a new algorithm.

Closed for Modification

* RateLimiterService never changes.

---

## Single Responsibility Principle

RateLimiterService

* Coordinates the workflow.

Each Algorithm

* Implements one rate limiting algorithm.

Each algorithm owns:

* Its own runtime state
* Its own data structures
* Its own implementation

---

## Encapsulation

Each algorithm hides its implementation.

RateLimiterService never knows:

* How tokens are refilled.
* How timestamps are removed.
* How windows are calculated.

It simply calls

```java
allowRequest(request)
```

---

# Interview Discussion

### Interviewer

Tomorrow we want to support Sliding Window.

How much code changes?

Answer:

Only create

```java
SlidingWindowAlgorithm
        implements RateLimitingAlgorithm
```

No changes to

```java
RateLimiterService
```

---

### Interviewer

Tomorrow we invent a brand new algorithm.

Answer:

Create another implementation.

```java
class CustomRateLimiterAlgorithm
        implements RateLimitingAlgorithm
```

Everything else remains unchanged.

---

# Key Takeaways

* Strategy Pattern abstracts **behavior**, not data.
* The algorithm interface exposes one operation:

```java
allowRequest(Request request)
```

* Every algorithm owns its own runtime state and internal data structures.
* RateLimiterService depends on an interface rather than a concrete implementation.
* This design satisfies both the **Open/Closed Principle** and the **Dependency Inversion Principle**.
* This pattern is common in real-world backend systems where different algorithms or policies may be swapped without affecting the calling code.

---

# Where Else Will You Use Strategy Pattern?

After learning this problem, you'll start recognizing the same pattern in many backend systems:

* **Rate Limiter** → Token Bucket, Sliding Window, Fixed Window
* **Payment System** → Credit Card, PayPal, Apple Pay
* **Notification Service** → Email, SMS, Push Notification
* **Parking Allocation** → Normal, VIP, Handicapped
* **Pricing Engine** → Standard Pricing, Holiday Pricing, Surge Pricing
* **Compression** → ZIP, GZIP, Snappy

The common idea is always the same:

> **The workflow remains the same, but one piece of behavior is interchangeable.**

That is exactly what the **Strategy Pattern** is designed to solve.

# E-Commerce Pricing Engine LLD

# Problem Statement

Design a Pricing Engine for an e-commerce application.

The customer adds products to a shopping cart.

At checkout, the system calculates the final payable amount after applying a discount.

Initially support:

* Percentage Discount
* Flat Discount
* Buy One Get One (BOGO)

Assumptions:

* Amazon-like online retail application.
* One customer owns one shopping cart.
* Cart contains multiple products.
* Discounts are applied to the entire cart.
* Only one discount is active at a time.
* No discount is also a valid scenario.
* Ignore taxes and shipping charges.

---

# LLD Design Framework

For every LLD problem, follow the same process:

1. Clarify Requirements
2. Identify Core Objects
3. Define Relationships
4. Responsibilities + Public APIs
5. Data Structures
6. Workflow
7. Extensibility
8. SOLID Principles

---

# Step 1 - Clarify Requirements

Typical clarification questions:

* Is this an online retail application?
* Are discounts applied to individual products or the entire cart?
* Can multiple discounts be combined?
* Is a discount always present?
* Which discount types should be supported?
* Who selects the discount?
* Should we ignore taxes and shipping?

Assumptions for this design:

* Amazon-like e-commerce application.
* Discount applies to the entire cart.
* Only one discount is active.
* Discount selection happens before checkout.
* No discount is a valid scenario.

---

# Step 2 - Core Objects

## Product

Represents an item sold by the platform.

```java
class Product{

    int productId;

    String name;

    double price;

}
```

---

## Customer

Represents a registered customer.

```java
class Customer{

    int customerId;

    String name;

    PaymentMethod paymentMethod;

}
```

---

## Cart

Represents the customer's shopping cart.

Notice that it contains **CartItems**, not Products.

```java
class Cart{

    Customer customer;

    Map<Integer, CartItem> cartItems;

    Discount discount;

}
```

---

## CartItem (Association Class)

Represents one product inside a shopping cart.

Why is this needed?

Suppose a customer purchases

```
Laptop × 3
```

Question:

Who owns the quantity?

Not Product.

Not Cart.

Quantity belongs to the relationship.

Exactly like:

* ParkingTicket
* BookPurchase
* BookRead

```java
class CartItem{

    Product product;

    int quantity;

}
```

---

## Discount

Initially this is only a configuration object.

```java
class Discount{

    DiscountType type;

    double value;

}
```

Examples:

```
PERCENTAGE

↓

10
```

or

```
FLAT

↓

50
```

No behavior yet.

---

## CheckoutService

Coordinates the checkout process.

```java
class CheckoutService{

}
```

Notice:

The service owns no business objects.

It simply coordinates them.

---

# Step 3 - Relationships

## Customer → Cart

```
Customer 1 -------- 1 Cart
```

---

## Cart → CartItem

```
Cart 1 -------- * CartItem
```

---

## CartItem → Product

A product may appear in many carts.

```
Product 1 -------- * CartItem
```

---

## Cart → Discount

Only one discount can be active.

```
Cart 1 -------- 0..1 Discount
```

---

# Class Diagram

```
                  Customer
                      |
                      |
                     1|1
                      |
                     Cart
                    /    \
                   /      \
                  *        0..1
                 /          \
           CartItem       Discount
                |
                |
               *|1
                |
             Product
```

---

# Step 4 - Responsibilities + Public APIs

## Product

### Responsibility

Represents a product.

### APIs

```java
getProductId()

getPrice()
```

---

## Customer

### Responsibility

Represents a customer.

### APIs

```java
getCustomerId()

getPaymentMethod()
```

---

## CartItem

### Responsibility

Represents one line item.

Owns:

* Product
* Quantity

Since it owns both,

it should compute its own total.

### APIs

```java
getItemTotal()
```

Implementation:

```
price × quantity
```

---

## Cart

### Responsibility

Owns all cart items.

Since it owns them,

it should compute the subtotal.

### APIs

```java
addProduct()

removeProduct()

updateQuantity()

getSubtotal()
```

Implementation:

```
subtotal = Σ item.getItemTotal()
```

Notice:

CheckoutService never iterates through products.

Cart owns that behavior.

---

## Discount

### Responsibility

Represents discount configuration.

Owns:

* Discount Type
* Discount Value

### APIs

```java
getType()

getValue()
```

At this stage,

Discount contains only configuration.

---

## CheckoutService

### Responsibility

Coordinates checkout.

Workflow:

1. Ask Cart for subtotal.
2. Read Discount.
3. Apply business rule.
4. Return final price.

### APIs

```java
calculateFinalPrice(Cart cart,
                    Discount discount)
```

Implementation:

```java
double subtotal = cart.getSubtotal();

switch(discount.getType()){

case PERCENTAGE:

    return subtotal
         - subtotal*discount.getValue()/100;

case FLAT:

    return subtotal
         - discount.getValue();

case BOGO:

    ...

}
```

---

# Step 5 - Data Structures

Always ask:

```
Operation

↓

Desired Complexity

↓

Data Structure
```

---

## Cart

Operations:

* Add Product
* Remove Product
* Update Quantity
* Lookup Product

Using

```java
List<CartItem>
```

requires

```
O(n)
```

search.

Better:

```java
Map<ProductId, CartItem>
```

Benefits:

* O(1) lookup
* O(1) remove
* O(1) update quantity

If insertion order is important for UI,

a

```java
LinkedHashMap<ProductId, CartItem>
```

can be used.

---

# Step 6 - Workflow

```
Customer

↓

Cart

↓

Cart.getSubtotal()

↓

CheckoutService

↓

Read Discount

↓

Apply Discount Logic

↓

Return Final Price
```

---

# Step 7 - Extensibility

## New Requirement

Marketing introduces:

* Black Friday
* Student Discount
* Employee Discount
* Coupon Discount
* Loyalty Discount
* Festival Discount

Current implementation requires

```java
switch(discountType)
```

to keep growing.

This violates the

**Open/Closed Principle**.

Instead,

introduce the **Strategy Pattern**.

---

# Strategy Pattern

## Interface

```java
interface DiscountStrategy{

    double applyDiscount(Cart cart);

}
```

Notice:

We pass the **Cart**, not just the subtotal.

Reason:

Some strategies need:

* Products
* Categories
* Quantities

For example,

BOGO cannot be calculated from subtotal alone.

---

## Implementations

```java
PercentageDiscountStrategy
```

```java
FlatDiscountStrategy
```

```java
BuyOneGetOneStrategy
```

```java
StudentDiscountStrategy
```

```java
BlackFridayDiscountStrategy
```

```java
CouponDiscountStrategy
```

---

## Updated Cart

Instead of storing a simple Discount,

the Cart stores the selected strategy.

```java
class Cart{

    Customer customer;

    Map<Integer, CartItem> cartItems;

    DiscountStrategy discountStrategy;

}
```

---

## Updated CheckoutService

Now CheckoutService no longer needs a switch statement.

```java
class CheckoutService{

    double calculateFinalPrice(Cart cart){

        return cart.getDiscountStrategy()
                   .applyDiscount(cart);

    }

}
```

Notice:

CheckoutService has no knowledge of:

* Percentage
* Flat
* BOGO
* Student
* Coupon

It simply delegates the work.

---

# Optional Enhancement - Discount Optimizer

Suppose the customer qualifies for multiple promotions:

* Student
* Black Friday
* Loyalty
* Credit Card Offer

Business requirement:

Automatically choose the best discount.

Introduce:

```java
class DiscountOptimizer{

    DiscountStrategy
        getBestStrategy(Cart cart);

}
```

Workflow:

```
Cart

↓

DiscountOptimizer

↓

Best Discount Strategy

↓

Attach Strategy to Cart

↓

CheckoutService

↓

strategy.applyDiscount()

↓

Final Price
```

This keeps CheckoutService unchanged.

---

# SOLID Principles

## Single Responsibility Principle

Product

* Represents a product.

Customer

* Represents a customer.

CartItem

* Represents one line item.

Cart

* Owns cart items and computes subtotal.

CheckoutService

* Coordinates checkout.

Each DiscountStrategy

* Implements one discount algorithm.

---

## Open/Closed Principle

New discounts require creating new strategy classes.

CheckoutService never changes.

---

## Dependency Inversion Principle

CheckoutService depends on

```java
DiscountStrategy
```

instead of concrete implementations.

---

# Key Interview Takeaways

* Start with a simple design before introducing patterns.
* Introduce **association classes** (`CartItem`) when data belongs to a relationship.
* The object that owns the state should own the behavior.
* `Cart` computes the subtotal because it owns the cart items.
* `CartItem` computes the line-item total because it owns the product and quantity.
* `CheckoutService` coordinates the checkout process instead of performing every calculation.
* Choose data structures based on operations (`Map<ProductId, CartItem>` for O(1) lookup).
* Introduce the **Strategy Pattern** only when business rules become interchangeable and frequently change.
* Design patterns should emerge naturally from changing requirements rather than being introduced prematurely.

# Pricing Engine LLD - Follow-up: Supporting Multiple Promotions

After completing the basic Pricing Engine using the **Strategy Pattern**, an interviewer may introduce a new requirement.

---

# New Requirement

The interviewer says:

> "A customer may qualify for multiple promotions at the same time."

For example:

* Student Discount
* Coupon Discount
* Black Friday Discount
* Credit Card Cashback
* Loyalty Discount

How would you extend your design?

---

# First Step - Clarify the Business Rules

Before changing the design, ask the interviewer:

> **How should multiple promotions behave?**

Possible business rules include:

1. Apply **all** promotions.
2. Apply **only the best** promotion.
3. Apply multiple promotions, but only certain combinations are allowed.

The design depends entirely on the answer.

Never make assumptions.

---

# Case 1 - Apply All Promotions

Example:

```text
Subtotal = $100

↓

10% Student Discount

↓

$90

↓

$20 Coupon

↓

$70

↓

5% Cashback

↓

$66.50
```

Instead of storing one strategy,

```java
DiscountStrategy discountStrategy;
```

the cart now stores multiple strategies.

```java
class Cart{

    Map<Integer, CartItem> cartItems;

    List<DiscountStrategy> discountStrategies;

}
```

---

## Strategy Interface

The interface also changes.

Instead of

```java
double applyDiscount(Cart cart);
```

use

```java
interface DiscountStrategy{

    double applyDiscount(double currentPrice,
                         Cart cart);

}
```

Why?

Each strategy receives the result of the previous strategy.

Example:

```text
Current Price

↓

Student Discount

↓

New Price

↓

Coupon Discount

↓

New Price

↓

Cashback

↓

Final Price
```

---

## CheckoutService

CheckoutService simply executes each strategy sequentially.

```java
double calculateFinalPrice(Cart cart){

    double total = cart.getSubtotal();

    for(DiscountStrategy strategy :
            cart.getDiscountStrategies()){

        total = strategy.applyDiscount(total,
                                       cart);

    }

    return total;

}
```

Notice:

CheckoutService still does not know:

* Percentage Discount
* Flat Discount
* Coupon
* Cashback

It only coordinates the strategies.

---

# Case 2 - Apply Only the Best Promotion

Example:

```text
Student Discount

↓

$20 Savings

----------------

Coupon

↓

$30 Savings

----------------

Black Friday

↓

$40 Savings

↓

Apply Black Friday
```

In this case,

multiple strategies exist,

but only one should be selected.

Introduce a new object.

```java
class DiscountOptimizer{

    DiscountStrategy
    getBestStrategy(Cart cart);

}
```

---

## Workflow

```text
Available Promotions

↓

DiscountOptimizer

↓

Best Discount Strategy

↓

Attach Strategy to Cart

↓

CheckoutService

↓

Apply Discount

↓

Final Price
```

CheckoutService remains unchanged.

---

# Case 3 - Promotion Combination Rules

Real e-commerce systems often have complex rules.

Example:

```text
Student

+

Coupon

Allowed

----------------

Coupon

+

Black Friday

Not Allowed

----------------

Employee Discount

Cannot combine with any other promotion
```

Now introduce another object.

```java
class PromotionEngine{

    List<DiscountStrategy>
        getApplicablePromotions(Cart cart);

}
```

Responsibilities:

* Validate promotion combinations.
* Remove invalid promotions.
* Return only the applicable strategies.

---

## Workflow

```text
Available Promotions

↓

PromotionEngine

↓

Applicable Promotions

↓

CheckoutService

↓

Apply Promotions

↓

Final Price
```

---

# Which Design Pattern Is This?

Originally,

we used the **Strategy Pattern**.

```text
CheckoutService

↓

DiscountStrategy

↓

Percentage

Flat

BOGO
```

Once multiple promotions are applied sequentially,

the design also resembles the

**Chain of Responsibility** (or Processing Pipeline).

Example:

```text
Subtotal

↓

Student Discount

↓

Coupon Discount

↓

Cashback

↓

Loyalty Discount

↓

Final Price
```

Each strategy performs one transformation

and passes the updated price to the next strategy.

---

# Interview Discussion

If the interviewer asks:

> "Can multiple promotions be applied?"

A good answer is:

> "I'd first clarify the business rules because there are multiple possible interpretations."

Then discuss the three approaches:

### Apply All Promotions

* Store a `List<DiscountStrategy>`.
* Execute each strategy sequentially.

### Apply Best Promotion

* Introduce a `DiscountOptimizer`.
* Select the strategy that gives the maximum savings.

### Apply Based on Business Rules

* Introduce a `PromotionEngine`.
* Validate combinations before checkout.

---

# Key Takeaways

* Never assume how promotions should be combined.
* Clarify the business rule first.
* The object model changes based on the requirement.
* `CheckoutService` should remain a coordinator.
* `DiscountStrategy` continues to encapsulate the discount calculation.
* A `DiscountOptimizer` is useful when only the best promotion should be applied.
* A `PromotionEngine` is useful when complex stacking rules exist.
* Applying multiple strategies sequentially naturally forms a processing pipeline similar to the **Chain of Responsibility** pattern.


Notice how I go from broad to detailed:

Is it a single parking lot or multiple?
What vehicle types are supported?
Multiple floors?
How are parking spots assigned (system vs user)?
Do we generate tickets?
What is the pricing model?
Do we need to support concurrent entry/exit?
Is nearest spot allocation required?
Any special spots (VIP, handicapped, EV)?
What happens when the lot is full?



# BookMyShow LLD — Part 1
# Requirements Clarification & Identifying Entities

---

# Step 1 — Clarify the Requirements

Before writing any classes, spend a few minutes understanding the problem.

## Questions to Ask

1. Is the system for a single theater or multiple theaters?
2. Can a theater have multiple screens?
3. Can a screen have multiple shows throughout the day?
4. Is booking cancellation supported?
5. Is pricing fixed or based on seat type/time/day?
6. Should we support concurrent bookings?

---

## Final Assumptions

- Multiple theaters
- Each theater has multiple screens
- Each screen can host multiple shows
- Booking cancellation is supported
- Pricing depends on seat type (can evolve later)
- Multiple users may try to book the same seat simultaneously

> **Interview Tip**
>
> Never assume the requirements. Spend the first 2–3 minutes clarifying them with the interviewer.

---

# Step 2 — Identify the Domain Entities

Now ask yourself:

> **What are the main nouns in the problem statement?**

Initial entities:

- Movie
- Theater
- Screen
- Seat
- Show
- Booking
- User
- Payment

At this stage, don't worry about services or design patterns.

---

# Important Design Discussion

Initially, it looks like a seat should contain:

```java
boolean booked;
```

But let's think about it.

Suppose we have:

```
Seat A1

10:00 AM Show → BOOKED

7:00 PM Show → AVAILABLE
```

The physical seat is the same.

Its availability changes depending on **which show** we're talking about.

So the booking state **does not belong to Seat**.

---

# Introducing ShowSeat

Instead of storing booking information inside `Seat`, introduce another entity.

```
Show
   |
   +------ ShowSeat ------ Seat
```

`ShowSeat` represents:

> **Seat A1 for a particular show**

Now the booking status belongs to `ShowSeat`, not `Seat`.

---

# Final Entity List

- Movie
- Theater
- Screen
- Seat
- Show
- ShowSeat
- Booking
- User
- Payment

At this point, we have completed the domain model.

We intentionally do **not** design services yet.

---

# LLD Approach Used

```
Requirements
      ↓
Entities
      ↓
Relationships
      ↓
Services
      ↓
Workflow
      ↓
Design Patterns (only if required)
      ↓
Concurrency
```

This structured approach keeps the design clean and avoids introducing unnecessary complexity too early.

---

# Key Takeaways

- Always clarify requirements before writing classes.
- Identify entities before thinking about services.
- Ask whether a piece of state belongs to an object or to the relationship between two objects.
- `ShowSeat` exists because seat availability depends on the **show**, not the physical seat itself.
- Build the domain model first. Services and workflows come afterward.

# BookMyShow LLD — Part 2
# Designing the Domain Classes

Now that we've identified the entities, we'll model each class one by one.

For every class, ask yourself:

1. What fields should it have?
2. What is its responsibility?
3. What methods belong here?
4. Am I duplicating relationships?

---

# 1. Movie

## Initial Thoughts

A movie should contain:

- movieId
- title
- language
- duration
- genre

One suggestion was to add:

```java
boolean isRunning;
```

### Improvement

Avoid adding `isRunning`.

Whether a movie is currently running depends on whether it has active shows.

That information already exists elsewhere.

---

## Final Class

```java
public class Movie {

    private String movieId;

    private String title;

    private Duration duration;

    private Language language;

    private Genre genre;
}
```

### Responsibility

Represents movie information only.

---

# 2. Theater

A theater contains multiple screens.

---

## Final Class

```java
public class Theater {

    private String theaterId;

    private String name;

    private String address;

    private List<Screen> screens;

    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    public void removeScreen(Screen screen) {
        screens.remove(screen);
    }
}
```

---

### Discussion

Initially it seemed reasonable to store:

```java
List<Show> shows;
```

inside Theater.

### Improvement

Avoid it.

The relationship already exists.

```
Theater
    ↓
Screen
    ↓
Show
```

Duplicating relationships creates unnecessary maintenance.

---

### Responsibility

Manage screens.

---

# 3. Screen

A screen has:

- Seats
- Shows

---

## Final Class

```java
public class Screen {

    private String screenId;

    private List<Seat> seats;

    private List<Show> shows;

    public void addSeat(Seat seat) {
        seats.add(seat);
    }

    public void removeSeat(Seat seat) {
        seats.remove(seat);
    }

    public void addShow(Show show) {
        shows.add(show);
    }

    public void removeShow(Show show) {
        shows.remove(show);
    }
}
```

---

### Responsibility

Represents one physical screen inside a theater.

---

# 4. Seat

Initially, we considered:

```java
boolean booked;
```

### Improvement

Booking status depends on the show.

So Seat only represents the physical seat.

---

## Final Class

```java
public class Seat {

    private String seatNumber;

    private SeatType seatType;
}
```

---

### Responsibility

Represents a physical seat.

No booking information belongs here.

---

# 5. Show

A show represents one screening of a movie.

---

## Final Class

```java
public class Show {

    private String showId;

    private Movie movie;

    private Screen screen;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private List<ShowSeat> showSeats;

    public List<ShowSeat> getAvailableSeats() {

        return showSeats.stream()
                .filter(seat -> seat.getStatus() == SeatStatus.AVAILABLE)
                .toList();
    }
}
```

---

### Discussion

Initially it looked like Show should contain Theater.

Instead:

```
Show
   ↓
Screen
   ↓
Theater
```

Avoid storing duplicate references.

---

### Responsibility

Represents one movie screening.

---

# 6. ShowSeat

This class solves the booking problem.

Instead of storing booking information inside Seat, we create:

```
ShowSeat
```

which represents:

> Seat for a particular show.

---

## Final Class

```java
public class ShowSeat {

    private Show show;

    private Seat seat;

    private SeatStatus status;

    public void lock() {
        status = SeatStatus.LOCKED;
    }

    public void markBooked() {
        status = SeatStatus.BOOKED;
    }

    public void release() {
        status = SeatStatus.AVAILABLE;
    }
}
```

---

### Seat Lifecycle

```
AVAILABLE
      ↓
LOCKED
      ↓
BOOKED
```

If payment fails:

```
LOCKED
      ↓
AVAILABLE
```

---

### Responsibility

Maintain seat state for a specific show.

---

# 7. Booking

A booking ties everything together.

---

## Final Class

```java
public class Booking {

    private String bookingId;

    private User user;

    private Show show;

    private List<ShowSeat> bookedSeats;

    private BigDecimal totalAmount;

    private BookingStatus status;
}
```

---

### Discussion

Should Booking store the amount?

Yes.

Pricing is calculated by `PricingService`, but once calculated, the final amount should be stored inside Booking.

This preserves historical accuracy if pricing changes later.

---

### Responsibility

Represents one booking made by a user.

---

# 8. User

```java
public class User {

    private String userId;

    private String name;

    private String email;
}
```

---

### Responsibility

Represents the customer using the system.

---

# 9. Payment

```java
public class Payment {

    private String paymentId;

    private BigDecimal amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus status;
}
```

---

### Responsibility

Represents one payment transaction.

---

# Enums

```java
enum SeatStatus {
    AVAILABLE,
    LOCKED,
    BOOKED
}
```

```java
enum BookingStatus {
    CREATED,
    CONFIRMED,
    CANCELLED
}
```

```java
enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}
```

```java
enum SeatType {
    REGULAR,
    PREMIUM,
    RECLINER
}
```

---

# Class Relationships

```
                 Movie
                   ▲
                   │
                 Show
                /    \
               /      \
          Screen    ShowSeat
             ▲          │
             │          ▼
         Theater      Seat

Booking
   │
   ├── User
   ├── Show
   └── List<ShowSeat>

Payment
```

---

# Key Takeaways

- Each class should have a single responsibility.
- Avoid duplicate relationships (e.g., don't store both Theater → Show and Theater → Screen → Show).
- Store booking state in `ShowSeat`, not `Seat`.
- Store the final amount in `Booking`, but let `PricingService` calculate it.
- Model the domain completely before introducing services.

# BookMyShow LLD — Part 3
# Designing the Services

Now that our domain model is complete, we can design the services.

A common mistake is to create a service for every entity:

- MovieService
- TheaterService
- ScreenService
- SeatService

Most of these are just CRUD services and don't represent business operations.

Instead, identify the **business use cases**.

---

# Business Use Cases

The user wants to:

- Search for movies
- View available shows
- Book tickets
- Cancel bookings
- Make payments

From these use cases, we derive the services.

---

# Final Services

- SearchService
- BookingService
- PricingService
- PaymentService

---

# 1. SearchService

### Responsibility

Search for movies and shows.

---

```java
public class SearchService {

    List<Movie> searchMovie(String movieName);

    List<Show> getShows(Movie movie);

    List<Show> getShows(Movie movie, Theater theater);
}
```

---

### Why not Booking logic here?

Searching and booking are completely different responsibilities.

Keep searching read-only.

---

# 2. BookingService

This is the heart of the application.

It coordinates the booking process.

It does **not**

- calculate prices
- process payments
- implement pricing rules

It delegates those responsibilities.

---

## Dependencies

```java
private PricingService pricingService;

private PaymentService paymentService;
```

---

## Final Class

```java
public class BookingService {

    private PricingService pricingService;

    private PaymentService paymentService;

    public Booking createBooking(
            User user,
            Show show,
            List<ShowSeat> seats) {

        // implementation later
    }

    public void cancelBooking(String bookingId) {

        // implementation later
    }
}
```

---

### Discussion

Initially, it looked like this:

```java
bookShow(User user,
         Show show,
         int tickets)
```

### Improvement

The user doesn't book "3 tickets".

The user selects:

```
A1

A2

A3
```

So the service should receive the selected seats.

```java
List<ShowSeat>
```

---

### Responsibility

Coordinate the booking workflow.

---

# 3. PricingService

Initially pricing is simple.

```java
public class PricingService {

    public BigDecimal calculatePrice(Booking booking);

    public BigDecimal calculateRefund(Booking booking);
}
```

Notice that the input is:

```java
Booking
```

instead of

```java
List<ShowSeat>
```

---

### Why?

Pricing may depend on:

- Seat type
- User membership
- Coupons
- Show timing
- Weekend pricing
- Taxes

All of this is reachable through Booking.

---

### Responsibility

Calculate prices and refunds.

It does **not** process payments.

---

# 4. PaymentService

Very small.

```java
public class PaymentService {

    public Payment processPayment(Booking booking);

    public Payment refund(Booking booking);
}
```

---

### Responsibility

Only process payments.

It should not know:

- pricing rules
- booking logic
- seat locking

It simply charges or refunds the final amount.

---

# Service Relationships

```
             BookingService
             /            \
            /              \
           ▼                ▼
PricingService      PaymentService

SearchService
```

Notice:

BookingService coordinates everything.

PricingService and PaymentService don't communicate directly.

---

# Why doesn't PaymentService use PricingService?

A common question is:

> Should PaymentService calculate the price?

No.

The flow should be:

```
BookingService
      |
      ▼
PricingService.calculatePrice()

      |

booking.setTotalAmount()

      |

PaymentService.processPayment()
```

PaymentService simply charges:

```
booking.getTotalAmount()
```

It should never know:

- weekend pricing
- discounts
- surge pricing

Those belong to PricingService.

---

# If Pricing Becomes Complex

Suppose the interviewer adds:

- Weekend pricing
- Holiday pricing
- Dynamic pricing
- Premium member discounts

Instead of writing:

```java
if (...)

else if (...)

else if (...)
```

inside PricingService,

introduce the Strategy Pattern.

---

## PricingStrategy

```java
public interface PricingStrategy {

    BigDecimal calculate(Booking booking);
}
```

Implementations:

```java
WeekendPricingStrategy

FestivalPricingStrategy

DynamicPricingStrategy

DefaultPricingStrategy
```

PricingService becomes:

```java
public class PricingService {

    private PricingStrategy pricingStrategy;

    public BigDecimal calculatePrice(Booking booking) {

        return pricingStrategy.calculate(booking);
    }
}
```

BookingService remains unchanged.

PaymentService remains unchanged.

Only PricingService evolves.

---

# Design Principle

> Services orchestrate.

> Entities store state.

Examples:

- BookingService → coordinates booking.
- PricingService → calculates prices.
- PaymentService → processes payments.
- Booking → stores the final amount.
- ShowSeat → stores seat state.

---

# Key Takeaways

- Create services from business use cases, not entities.
- Keep public APIs small.
- BookingService coordinates the workflow.
- PricingService calculates.
- PaymentService charges.
- Introduce Strategy only when requirements justify it.
- Avoid adding design patterns too early.


# BookMyShow LLD — Part 4
# Booking & Cancellation Workflow

Now that we have our domain model and services, let's connect everything together.

The goal of `BookingService` is **not** to perform every operation itself.

Its responsibility is to **orchestrate** the workflow by coordinating the domain objects and other services.

---

# Booking Workflow

Suppose a user selects:

```
Movie : Avengers

Show : 7 PM

Seats :

A1

A2
```

The high-level flow becomes:

```
Validate Seats

        ↓

Lock Seats

        ↓

Create Booking

        ↓

Calculate Price

        ↓

Process Payment

        ↓

Payment Successful?

     YES        NO
      ↓          ↓
 Book Seats   Release Seats
      ↓          ↓
Confirm      Cancel Booking
Booking
```

---

# BookingService

High-level implementation:

```java
public Booking createBooking(
        User user,
        Show show,
        List<ShowSeat> seats) {

    // 1. Validate seats

    // 2. Lock seats

    // 3. Create Booking

    // 4. Calculate price

    // 5. Store total amount

    // 6. Process payment

    // 7. If success
    //      mark seats booked
    //      confirm booking

    // 8. Else
    //      release seats
    //      cancel booking

    return booking;
}
```

Notice that this is orchestration code.

Most of the work is delegated elsewhere.

---

# Step 1 — Validate Seats

Before doing anything, verify that every selected seat is still available.

```java
for (ShowSeat seat : seats) {

    if (seat.getStatus() != SeatStatus.AVAILABLE) {

        throw new SeatUnavailableException();
    }
}
```

---

# Step 2 — Lock Seats

Once validation succeeds:

```java
for (ShowSeat seat : seats) {

    seat.lock();
}
```

Seat lifecycle:

```
AVAILABLE

      ↓

LOCKED
```

This prevents another user from booking the same seat while payment is in progress.

---

# Step 3 — Create Booking

```java
Booking booking = new Booking(
        user,
        show,
        seats
);
```

Booking status becomes:

```
CREATED
```

---

# Step 4 — Calculate Price

Delegate pricing.

```java
BigDecimal amount =
        pricingService.calculatePrice(booking);
```

Notice:

BookingService does **not** calculate prices.

---

# Step 5 — Store Final Amount

```java
booking.setTotalAmount(amount);
```

Why store it?

Suppose ticket prices increase tomorrow.

Old bookings should still display the amount originally paid.

Booking stores the historical amount.

PricingService computes it.

---

# Step 6 — Process Payment

Delegate payment.

```java
Payment payment =
        paymentService.processPayment(booking);
```

BookingService never knows:

- Card details
- Payment gateway
- UPI
- Credit card APIs

That belongs to PaymentService.

---

# Step 7 — Payment Success

If payment succeeds:

```java
for (ShowSeat seat : seats) {

    seat.markBooked();
}
```

Booking becomes:

```
CONFIRMED
```

Seat lifecycle:

```
AVAILABLE

      ↓

LOCKED

      ↓

BOOKED
```

---

# Step 8 — Payment Failure

If payment fails:

```java
for (ShowSeat seat : seats) {

    seat.release();
}
```

Booking becomes:

```
CANCELLED
```

Seat lifecycle:

```
LOCKED

      ↓

AVAILABLE
```

Now another user can book those seats.

---

# Cancellation Workflow

User decides to cancel a booking.

High-level flow:

```
Load Booking

       ↓

Calculate Refund

       ↓

Refund Payment

       ↓

Release Seats

       ↓

Cancel Booking
```

---

# BookingService

High-level implementation:

```java
public void cancelBooking(String bookingId) {

    // 1. Load booking

    // 2. Calculate refund

    // 3. Process refund

    // 4. Release seats

    // 5. Cancel booking
}
```

---

# Step 1

Retrieve booking.

---

# Step 2

Delegate refund calculation.

```java
BigDecimal refund =
        pricingService.calculateRefund(booking);
```

---

# Step 3

Delegate refund.

```java
paymentService.refund(booking);
```

---

# Step 4

Release every booked seat.

```java
for (ShowSeat seat : booking.getBookedSeats()) {

    seat.release();
}
```

Seat becomes:

```
AVAILABLE
```

---

# Step 5

Booking status becomes:

```
CANCELLED
```

---

# Responsibilities

BookingService coordinates.

PricingService calculates.

PaymentService charges/refunds.

ShowSeat manages seat state.

Booking stores booking information.

Every class has one clear responsibility.

---

# Complete Booking Flow

```
User

   │

   ▼

BookingService

   │

Validate Seats

   │

Lock Seats

   │

Create Booking

   │

PricingService

   │

PaymentService

   │

Payment Success?

   │

YES ─────────────► Mark Seats Booked

NO ──────────────► Release Seats

   │

Return Booking
```

---

# Key Takeaways

- Services should orchestrate, not implement every business rule.
- Delegate pricing to PricingService.
- Delegate payment to PaymentService.
- Keep BookingService thin.
- Lock seats before payment.
- Confirm booking only after successful payment.
- Release seats if payment fails.
- Store the final amount in Booking for historical accuracy.

# BookMyShow LLD — Part 5
# Design Improvements & Evolving the Design

At this point, we have a working design.

Now imagine the interviewer starts changing the requirements.

This is where senior-level design begins.

The important principle is:

> **Don't introduce design patterns until the requirements justify them.**

---

# Improvement 1 — Pricing Strategy

## Initial Requirement

Pricing is simple.

```
Regular Seat = $10

Premium Seat = $20
```

A simple `PricingService` is enough.

```java
public class PricingService {

    public BigDecimal calculatePrice(Booking booking) {

        // calculate price
    }
}
```

No design pattern is required.

---

## New Requirement

Now the interviewer says:

- Weekend pricing
- Holiday pricing
- Dynamic pricing
- Premium member discounts
- Coupons

A large if-else chain starts appearing.

```java
if (weekend) {

}

else if (festival) {

}

else if (member) {

}

else if (coupon) {

}
```

Not a good design.

---

## Introduce Strategy Pattern

```java
public interface PricingStrategy {

    BigDecimal calculate(Booking booking);
}
```

Implementations:

```java
DefaultPricingStrategy

WeekendPricingStrategy

FestivalPricingStrategy

DynamicPricingStrategy
```

PricingService becomes:

```java
public class PricingService {

    private PricingStrategy pricingStrategy;

    public BigDecimal calculatePrice(
            Booking booking) {

        return pricingStrategy.calculate(booking);
    }
}
```

---

### Important Observation

Nothing changes in BookingService.

```java
pricingService.calculatePrice(booking);
```

still works.

Only PricingService evolves.

---

# Improvement 2 — Should PaymentService Know Pricing?

A common interview question:

> Should PaymentService use PricingStrategy?

No.

PaymentService should never know:

- Weekend pricing
- Discounts
- Coupons
- Seat types

Its responsibility is only:

```
Charge

Refund
```

Flow remains:

```
BookingService

        ↓

PricingService

        ↓

booking.setTotalAmount()

        ↓

PaymentService
```

PaymentService simply reads:

```java
booking.getTotalAmount();
```

---

# Improvement 3 — Store Final Amount

Another common discussion.

Should Booking contain:

```java
BigDecimal totalAmount;
```

Yes.

PricingService calculates.

Booking stores.

Reason:

Suppose:

```
Today

Premium Seat = $20
```

Tomorrow

```
Premium Seat = $30
```

Old bookings should still show:

```
$20
```

not

```
$30
```

Booking stores the historical value.

---

# Improvement 4 — Seat Lock Service

Initially we wrote:

```java
seat.lock();
```

inside BookingService.

That's perfectly acceptable.

---

## New Requirement

The interviewer says:

> Locks should expire after 5 minutes.

or

> Multiple servers are running.

Now seat locking becomes its own responsibility.

Introduce:

```java
public class SeatLockService {

    boolean lockSeats(List<ShowSeat> seats);

    void releaseSeats(List<ShowSeat> seats);
}
```

BookingService becomes cleaner.

```
BookingService

      │

SeatLockService

      │

PricingService

      │

PaymentService
```

---

### Why separate it?

Tomorrow SeatLockService can handle:

- Lock timeout
- Lock ownership
- Redis
- Distributed locking

without modifying BookingService.

---

# Improvement 5 — Keep Services Small

Bad design:

```
BookingService

book()

cancel()

refund()

calculatePrice()

searchMovie()

addMovie()

addTheater()
```

One service doing everything.

---

Good design:

```
SearchService

BookingService

PricingService

PaymentService
```

Each service has one responsibility.

---

# Improvement 6 — Avoid Duplicate Relationships

Bad:

```
Theater

↓

Screen

↓

Show

AND

Theater

↓

Show
```

Good:

```
Theater

↓

Screen

↓

Show
```

Only maintain one relationship.

Avoid duplicate state.

---

# Improvement 7 — ShowSeat

Initially it looks like:

```java
Seat {

    boolean booked;
}
```

But ask:

> Booked for which show?

Instead:

```
Seat

Physical object

↓

ShowSeat

Booking state
```

This is one of the most important modeling decisions in the design.

---

# Design Principles Used

## Single Responsibility Principle

Every class has one clear job.

Examples:

BookingService

↓

Coordinate booking

PricingService

↓

Calculate price

PaymentService

↓

Process payment

ShowSeat

↓

Maintain seat state

---

## Information Expert (GRASP)

Pass the object that already contains the required information.

Instead of:

```java
calculatePrice(
    seats,
    user,
    show,
    coupon
)
```

prefer:

```java
calculatePrice(Booking booking)
```

Booking already knows:

- User
- Show
- Seats

---

## Open Closed Principle

PricingService remains unchanged.

Adding:

```
WeekendPricingStrategy
```

does not require modifying BookingService.

We extend the behavior without changing existing clients.

---

# Final Architecture

```
                 BookingService
                        │
        ┌───────────────┴───────────────┐
        │                               │
        ▼                               ▼
PricingService                 PaymentService
        │
        ▼
PricingStrategy
        │
  ┌─────┼───────────┐
  │     │           │
Default Weekend  Festival
```

---

# Key Takeaways

- Start simple.
- Add design patterns only when new requirements demand them.
- BookingService should not calculate prices.
- PaymentService should not know pricing rules.
- Booking stores the final amount.
- Introduce Strategy only when pricing becomes variable.
- Introduce SeatLockService only when locking becomes complex.
- Keep every class and service focused on a single responsibility.

# BookMyShow LLD — Part 6
# Concurrency & Scaling Discussion

Once the basic LLD is complete, interviewers often ask:

> **"What happens if two users try to book the same seat at exactly the same time?"**

At this point, the discussion shifts from object-oriented design to concurrency and distributed systems.

---

# The Problem

Suppose seat A1 is available.

```
Seat A1

Status = AVAILABLE
```

Now two users try to book it simultaneously.

```
            User A                   User B

               │                        │

       Check AVAILABLE         Check AVAILABLE

               │                        │

          Both see AVAILABLE

               │                        │

           Lock Seat               Lock Seat

               │                        │

          Double Booking ❌
```

Both users successfully book the same seat.

This is called a **Race Condition**.

---

# Why Does This Happen?

Imagine this code:

```java
if(seat.getStatus() == AVAILABLE){

    seat.lock();
}
```

User A executes:

```
AVAILABLE
```

Before User A locks it,

User B also executes:

```
AVAILABLE
```

Both continue.

Checking and locking are **not atomic**.

---

# Solution 1 — Pessimistic Locking

Lock the database row before checking availability.

Example:

```sql
SELECT *
FROM SHOW_SEAT
WHERE ID = ?
FOR UPDATE;
```

Now:

```
User A

gets lock

↓

User B

waits
```

Only one transaction proceeds.

---

## Pros

- Prevents double booking.
- Simple to understand.

---

## Cons

- Database locks reduce scalability.
- Other users must wait.

---

# Solution 2 — Optimistic Locking

Instead of locking first,

allow both users to read.

Maintain a version number.

```
Seat

Version = 5
```

Both users read:

```
Version = 5
```

User A updates.

Database changes:

```
Version = 6
```

User B tries updating Version 5.

Database rejects the update.

Booking fails.

---

## Pros

- High throughput.
- Better scalability.

---

## Cons

- Failed updates need retries.

---

# Solution 3 — Redis Distributed Lock

Suppose there are multiple application servers.

```
App Server 1

App Server 2

App Server 3
```

A Java synchronized block only works inside one JVM.

It cannot coordinate multiple servers.

Instead,

acquire a distributed lock.

```
Redis

↓

Seat:A1

↓

LOCKED
```

Only one application server obtains the lock.

Others fail immediately.

---

## Typical Flow

```
Acquire Redis Lock

        ↓

Lock successful?

YES

↓

Book Seat

↓

Release Lock
```

---

# Seat Lock Timeout

Suppose:

```
User locks seat

↓

Never pays

↓

Leaves website
```

The seat remains locked forever.

Bad user experience.

---

## Solution

Every lock has an expiration.

Example:

```
Seat Locked

↓

5 Minutes

↓

Automatically Released
```

Then another customer can book it.

---

# Updated Workflow

```
Validate Seat

        ↓

Acquire Lock

        ↓

Create Booking

        ↓

Calculate Price

        ↓

Process Payment

        ↓

Payment Success?

YES                     NO

↓                       ↓

Book Seat         Release Lock

↓                       ↓

Confirm Booking   Cancel Booking
```

---

# Why Introduce SeatLockService?

Initially we simply wrote:

```java
seat.lock();
```

inside BookingService.

That is perfectly acceptable.

When lock management becomes complex:

- Timeout
- Redis
- Distributed locks
- Lock ownership

extract it.

```java
public class SeatLockService {

    boolean lockSeats(
            List<ShowSeat> seats);

    void releaseSeats(
            List<ShowSeat> seats);
}
```

BookingService becomes:

```
BookingService

↓

SeatLockService

↓

PricingService

↓

PaymentService
```

Cleaner responsibilities.

---

# Failure Scenarios

## Payment Fails

```
Seats Locked

↓

Payment Failed

↓

Release Seats

↓

Booking Cancelled
```

---

## Server Crashes After Locking

Suppose:

```
Lock Seats

↓

Server Crash
```

Without expiration,

the seat remains locked forever.

Redis TTL (Time To Live) automatically releases it.

---

## Payment Succeeds But Booking Update Fails

Example:

```
Payment Success

↓

Database Crash

↓

Booking Not Confirmed
```

Possible solutions:

- Retry
- Transaction
- Event-driven recovery
- Compensation logic

Interviewers usually appreciate hearing that you've considered failure scenarios, even if you don't implement them.

---

# Scaling Considerations

Suppose BookMyShow serves millions of users.

Possible improvements:

- Cache movie and show searches using Redis.
- Cache theater information.
- Database indexing on `showId`, `movieId`, and `seatStatus`.
- Partition bookings by `showId`.
- Read replicas for search APIs.
- Separate read and write workloads.

---

# Summary of Concurrency Options

| Solution | Best For | Pros | Cons |
|-----------|----------|------|------|
| Pessimistic Locking | High contention | Prevents conflicts | Lower scalability |
| Optimistic Locking | Low contention | High throughput | Retry on conflicts |
| Redis Distributed Lock | Multiple application servers | Distributed coordination | Extra infrastructure |

---

# Interview Tips

### Version 1

Keep it simple.

```
BookingService

↓

seat.lock()
```

No need to over-engineer.

---

### Version 2

When interviewer asks:

> "Thousands of users are booking simultaneously."

Introduce:

```
SeatLockService
```

Discuss:

- Optimistic locking
- Pessimistic locking
- Redis distributed locks
- Lock expiration

---

### Most Important Lesson

A good LLD interview is **iterative**.

Start with a clean, simple design.

As new requirements arrive:

- Introduce new services.
- Introduce design patterns.
- Discuss concurrency.
- Discuss scaling.

Avoid designing the most complex system from the beginning.

That's exactly what interviewers want to see.

---

# Final Learning

The progression we followed was:

```
Requirements
        ↓
Entities
        ↓
Relationships
        ↓
Services
        ↓
Workflow
        ↓
Design Improvements
        ↓
Concurrency
        ↓
Scaling
```

This same approach can be reused for almost every LLD interview problem (Parking Lot, Library Management, Amazon Locker, Splitwise, Vending Machine, etc.).


Amazon Locker LLD – Final Design Notes
Requirements
Multiple locker locations
Small, Medium, Large lockers
One package per locker
One package per locker
Auto locker assignment
OTP-based pickup
Package expiration
Exception if no suitable locker is available
---
Entities
LockerLocation
```java
class LockerLocation {

    String lockerLocationId;
    Address address;
    List<Locker> lockers;

    Locker findAvailableLocker(LockerSize size);

    void addLocker(Locker locker);
    void removeLocker(Locker locker);
}
```
Locker
```java
class Locker {

    String lockerId;
    LockerSize size;
    LockerStatus status;
    Package currentPackage;

    boolean canFit(Package pkg);
    void assignPackage(Package pkg);
    void removePackage();
}
```
Package
```java
class Package {

    String packageId;
    User user;
    String otp;
    LocalDateTime pickupStartTime;
    LocalDateTime pickupExpireTime;
    PackageStatus status;
}
```
```java
enum PackageStatus {
    READY_FOR_PICKUP,
    PICKED_UP,
    EXPIRED
}
```
Relationship
Preferred:
```
Locker
   |
   v
Package
```
Reason:
Avoid duplicate state
Simpler object model
PickupService can fetch Locker using the repository
---
Services
LockerService
Responsibilities
Assign locker
Generate OTP
Set pickup window
Release locker
```java
assignLocker(Package pkg);

releaseLocker(Locker locker);
```
PickupService
Responsibilities
Validate OTP
Validate package status
Validate expiration
Find locker
Release locker
Mark package picked up
```java
pickupPackage(String otp);
```
---
Package Assignment Workflow
```
Driver arrives

↓

LockerService.assignLocker(package)

↓

LockerLocation.findAvailableLocker(size)

↓

No Locker?

↓

Throw LockerNotAvailableException

↓

locker.assignPackage(package)

↓

Generate OTP

↓

package.setOtp()

↓

package.setPickupStartTime()

↓

package.setPickupExpireTime()

↓

package.setStatus(READY_FOR_PICKUP)

↓

Notify User
```
---
Package Pickup Workflow
```
Customer enters OTP

↓

PickupService.pickupPackage(otp)

↓

Find Package by OTP

↓

Validate OTP

↓

Validate READY_FOR_PICKUP

↓

Validate not expired

↓

Find Locker (Repository)

↓

LockerService.releaseLocker(locker)

↓

locker.removePackage()

↓

locker.status = AVAILABLE

↓

package.status = PICKED_UP
```
---
Concurrency
Single JVM
synchronized
ReentrantLock
Distributed
Redis Distributed Lock
OR Database Transaction + SELECT FOR UPDATE
---
Design Decisions
Keep the model unidirectional.
Services orchestrate workflows.
Entities manage their own state.
Use repository lookup instead of maintaining duplicate references.
Introduce bidirectional relationships only if future requirements justify them.

# Amazon Locker LLD - Entities

## Entity Relationship

```
LockerLocation
      |
      | 1
      |
      | *
   Locker
      |
      | 1
      |
      | 0..1
   Package

User
 |
 | 1
 |
 | *
Package
```

---

# LockerLocation

Represents a physical Amazon Locker location.

## Responsibilities

- Maintains all lockers at this location.
- Finds an available locker based on package size.
- Adds and removes lockers.

```java
class LockerLocation {

    String lockerLocationId;

    Address address;

    List<Locker> lockers;

    Locker findAvailableLocker(LockerSize size);

    void addLocker(Locker locker);

    void removeLocker(Locker locker);
}
```

### Why does LockerLocation choose the locker?

This follows the **Information Expert** principle.

Since LockerLocation already owns all lockers, it is the right place to determine which locker is available.

---

# Locker

Represents an individual locker.

## Responsibilities

- Stores exactly one package.
- Knows its size.
- Knows whether it is available.
- Assigns and removes packages.

```java
class Locker {

    String lockerId;

    LockerSize size;

    LockerStatus status;

    Package currentPackage;

    boolean canFit(Package pkg);

    void assignPackage(Package pkg);

    void removePackage();
}
```

---

## LockerStatus

```java
enum LockerStatus {

    AVAILABLE,

    OCCUPIED
}
```

---

# Package

Represents a package waiting for pickup.

## Responsibilities

- Stores package information.
- Stores OTP.
- Stores pickup window.
- Tracks package lifecycle.

```java
class Package {

    String packageId;

    User user;

    String otp;

    LocalDateTime pickupStartTime;

    LocalDateTime pickupExpireTime;

    PackageStatus status;
}
```

---

## PackageStatus

```java
enum PackageStatus {

    READY_FOR_PICKUP,

    PICKED_UP,

    EXPIRED
}
```

---

# User

Represents the customer.

```java
class User {

    String userId;

    String name;

    String email;

    String phoneNumber;
}
```

---

# Relationship Choice

Preferred relationship:

```
Locker
   |
   ▼
Package
```

Package does **not** contain:

```java
Locker locker;
```

### Why?

Keeping the relationship unidirectional:

- Avoids duplicate state.
- Makes the object model simpler.
- Reduces the chance of inconsistent references.

During pickup, the locker can be retrieved using the repository.

Example:

```java
Locker locker =
    lockerRepository.findByPackage(package);
```

---

# Why not Bidirectional?

Alternative:

```
Locker  -----------> Package
   ▲                   |
   |                   |
   +-------------------+
```

Advantages

- Easy navigation from Package → Locker.

Disadvantages

- Must update both references.

Example:

```java
locker.setCurrentPackage(package);

package.setLocker(locker);
```

On release:

```java
locker.setCurrentPackage(null);

package.setLocker(null);
```

If one update is forgotten, the object model becomes inconsistent.

For the given requirements, the unidirectional relationship is simpler and preferred.

# Amazon Locker LLD - Services

Services are responsible for orchestrating workflows.

They coordinate multiple entities but do not own business state.

---

# LockerService

## Responsibilities

- Assign a locker to a package.
- Generate OTP.
- Set pickup window.
- Release a locker after pickup.

```java
class LockerService {

    Locker assignLocker(Package pkg);

    void releaseLocker(Locker locker);
}
```

---

## assignLocker()

### Responsibility

Assigns a suitable locker for the package.

### Workflow

```
Receive Package

↓

Find Locker Location

↓

LockerLocation.findAvailableLocker(size)

↓

No Locker?

↓

Throw LockerNotAvailableException

↓

locker.assignPackage(package)

↓

Generate OTP

↓

Set OTP

↓

Set Pickup Start Time

↓

Set Pickup Expiry Time

↓

Set Package Status

↓

Notify User

↓

Return Locker
```

---

### Why does LockerService generate the OTP?

Generating the OTP is part of the locker assignment workflow.

It does **not** belong inside the Package entity because:

- OTP generation is business logic.
- Package simply stores the generated OTP.

This follows the **Single Responsibility Principle (SRP)**.

---

## releaseLocker()

### Responsibility

Makes the locker available again.

```java
releaseLocker(Locker locker)
```

Example implementation:

```java
locker.removePackage();

locker.setStatus(AVAILABLE);
```

Notice that LockerService is responsible only for the locker lifecycle.

Updating the Package status is handled by PickupService.

---

# PickupService

## Responsibilities

- Validate OTP.
- Validate package status.
- Validate package has not expired.
- Retrieve the assigned locker.
- Release the locker.
- Mark package as picked up.

```java
class PickupService {

    void pickupPackage(String otp);
}
```

---

## Why does pickupPackage() accept an OTP?

The customer arrives with an OTP.

They do **not** arrive with a Package object.

Therefore:

Preferred

```java
pickupPackage(String otp)
```

Instead of

```java
pickupPackage(Package package)
```

This models the real-world workflow more accurately.

---

## Pickup Workflow

```
Customer enters OTP

↓

Find Package using OTP

↓

Validate OTP

↓

Validate READY_FOR_PICKUP

↓

Validate package not expired

↓

Find Locker using Repository

↓

LockerService.releaseLocker(locker)

↓

package.setStatus(PICKED_UP)
```

---

# Why not let Locker release itself?

Instead of calling

```java
locker.removePackage();
```

directly from PickupService,

we delegate to

```java
LockerService.releaseLocker(locker);
```

Reason:

Locker lifecycle is owned by LockerService.

PickupService should orchestrate the pickup process, not manage locker operations.

This follows the **Single Responsibility Principle**.

---

# Repository Lookup

Since Package does not store a Locker reference, PickupService retrieves it through the repository.

Example:

```java
Locker locker =
    lockerRepository.findByPackage(package);
```

This avoids introducing a bidirectional relationship solely for easier navigation.

---

# Design Principles Used

## Single Responsibility Principle (SRP)

- LockerService manages locker operations.
- PickupService manages pickup workflow.

---

## Information Expert

LockerLocation knows all lockers.

Therefore,

```java
LockerLocation.findAvailableLocker(size)
```

belongs inside LockerLocation.

---

## Orchestration

Services coordinate multiple entities.

Entities manage their own state.

Examples:

Service:

```java
LockerService.assignLocker(package);
```

Entity:

```java
locker.assignPackage(package);
```

Service:

```java
PickupService.pickupPackage(otp);
```

Entity:

```java
package.setStatus(PICKED_UP);
```

This separation keeps the design clean, maintainable, and easy to extend.

# Amazon Locker LLD - Services

Services are responsible for orchestrating workflows.

They coordinate multiple entities but do not own business state.

---

# LockerService

## Responsibilities

- Assign a locker to a package.
- Generate OTP.
- Set pickup window.
- Release a locker after pickup.

```java
class LockerService {

    Locker assignLocker(Package pkg);

    void releaseLocker(Locker locker);
}
```

---

## assignLocker()

### Responsibility

Assigns a suitable locker for the package.

### Workflow

```
Receive Package

↓

Find Locker Location

↓

LockerLocation.findAvailableLocker(size)

↓

No Locker?

↓

Throw LockerNotAvailableException

↓

locker.assignPackage(package)

↓

Generate OTP

↓

Set OTP

↓

Set Pickup Start Time

↓

Set Pickup Expiry Time

↓

Set Package Status

↓

Notify User

↓

Return Locker
```

---

### Why does LockerService generate the OTP?

Generating the OTP is part of the locker assignment workflow.

It does **not** belong inside the Package entity because:

- OTP generation is business logic.
- Package simply stores the generated OTP.

This follows the **Single Responsibility Principle (SRP)**.

---

## releaseLocker()

### Responsibility

Makes the locker available again.

```java
releaseLocker(Locker locker)
```

Example implementation:

```java
locker.removePackage();

locker.setStatus(AVAILABLE);
```

Notice that LockerService is responsible only for the locker lifecycle.

Updating the Package status is handled by PickupService.

---

# PickupService

## Responsibilities

- Validate OTP.
- Validate package status.
- Validate package has not expired.
- Retrieve the assigned locker.
- Release the locker.
- Mark package as picked up.

```java
class PickupService {

    void pickupPackage(String otp);
}
```

---

## Why does pickupPackage() accept an OTP?

The customer arrives with an OTP.

They do **not** arrive with a Package object.

Therefore:

Preferred

```java
pickupPackage(String otp)
```

Instead of

```java
pickupPackage(Package package)
```

This models the real-world workflow more accurately.

---

## Pickup Workflow

```
Customer enters OTP

↓

Find Package using OTP

↓

Validate OTP

↓

Validate READY_FOR_PICKUP

↓

Validate package not expired

↓

Find Locker using Repository

↓

LockerService.releaseLocker(locker)

↓

package.setStatus(PICKED_UP)
```

---

# Why not let Locker release itself?

Instead of calling

```java
locker.removePackage();
```

directly from PickupService,

we delegate to

```java
LockerService.releaseLocker(locker);
```

Reason:

Locker lifecycle is owned by LockerService.

PickupService should orchestrate the pickup process, not manage locker operations.

This follows the **Single Responsibility Principle**.

---

# Repository Lookup

Since Package does not store a Locker reference, PickupService retrieves it through the repository.

Example:

```java
Locker locker =
    lockerRepository.findByPackage(package);
```

This avoids introducing a bidirectional relationship solely for easier navigation.

---

# Design Principles Used

## Single Responsibility Principle (SRP)

- LockerService manages locker operations.
- PickupService manages pickup workflow.

---

## Information Expert

LockerLocation knows all lockers.

Therefore,

```java
LockerLocation.findAvailableLocker(size)
```

belongs inside LockerLocation.

---

## Orchestration

Services coordinate multiple entities.

Entities manage their own state.

Examples:

Service:

```java
LockerService.assignLocker(package);
```

Entity:

```java
locker.assignPackage(package);
```

Service:

```java
PickupService.pickupPackage(otp);
```

Entity:

```java
package.setStatus(PICKED_UP);
```

This separation keeps the design clean, maintainable, and easy to extend.

# Online Library LLD - Requirements

## Functional Requirements

- Users can search books.
- Users can borrow books.
- Users can return books.
- Users can view their borrowed books.

---

## Requirement Clarification Questions

### 1. Are there limited e-book copies?

**Yes**

The library owns a fixed number of licenses for every book.

Example:

Harry Potter

- Total Copies = 10
- Available Copies = 3

Only 3 more users can borrow the book.

---

### 2. Should we track reading progress?

**No**

Out of scope.

---

### 3. What is the borrowing period?

**14 days**

Every borrowed book is due after 14 days.

---

### 4. What happens after the due date?

The system automatically returns the book.

Meaning:

- BorrowRecord is updated.
- Available copies are increased.
- The user no longer owns the book.

---

### 5. Is there a borrowing limit?

Yes.

A user can borrow at most **5 books** simultaneously.

---

### 6. Different user types?

No.

Assume all users have the same borrowing privileges.

---

### 7. Subscription / Payment?

Out of scope.

Assume users already have access to the library.

---

## Out of Scope

- Reservations / Waitlist
- Reviews
- Ratings
- Recommendations
- Notifications
- Payments
- Fine calculation
- Reading progress

---

## Assumptions

- One online library.
- Books may have multiple authors.
- Every borrow is independent.
- The system keeps borrowing history.

# Online Library LLD - Requirements

## Functional Requirements

- Users can search books.
- Users can borrow books.
- Users can return books.
- Users can view their borrowed books.

---

## Requirement Clarification Questions

### 1. Are there limited e-book copies?

**Yes**

The library owns a fixed number of licenses for every book.

Example:

Harry Potter

- Total Copies = 10
- Available Copies = 3

Only 3 more users can borrow the book.

---

### 2. Should we track reading progress?

**No**

Out of scope.

---

### 3. What is the borrowing period?

**14 days**

Every borrowed book is due after 14 days.

---

### 4. What happens after the due date?

The system automatically returns the book.

Meaning:

- BorrowRecord is updated.
- Available copies are increased.
- The user no longer owns the book.

---

### 5. Is there a borrowing limit?

Yes.

A user can borrow at most **5 books** simultaneously.

---

### 6. Different user types?

No.

Assume all users have the same borrowing privileges.

---

### 7. Subscription / Payment?

Out of scope.

Assume users already have access to the library.

---

## Out of Scope

- Reservations / Waitlist
- Reviews
- Ratings
- Recommendations
- Notifications
- Payments
- Fine calculation
- Reading progress

---

## Assumptions

- One online library.
- Books may have multiple authors.
- Every borrow is independent.
- The system keeps borrowing history.

# 02_Entities.md

# Entities

We identify the following entities from the requirements:

- Library
- Book
- Author
- User
- BorrowRecord

---

# Relationship Diagram

```text
               Library
              /       \
             /         \
            /           \
       List<Book>    List<User>

Book -----------------> List<Author>

BorrowRecord
     |
     +------ User
     |
     +------ Book
```

---

# Library

The Library acts as the root object of the system.

It owns:

- Books
- Users

```java
class Library {

    Map<String, Book> books;

    Map<String, User> users;

    void addBook(Book book);

    void addUser(User user);
}
```

### Why Map instead of List?

Book IDs and User IDs are unique.

Using a Map provides O(1) lookup by ID.

Using a List is also acceptable in an interview unless performance is discussed.

---

# Book

```java
class Book {

    String bookId;

    String title;

    Genre genre;

    List<Author> authors;

    int totalCopies;

    int availableCopies;
}
```

### Why both totalCopies and availableCopies?

Example:

Harry Potter

Total Copies = 10

Available Copies = 3

This tells us:

- 10 licenses exist
- 7 are currently borrowed

---

# Author

```java
class Author {

    String authorId;

    String name;
}
```

One author can write many books.

One book may have multiple authors.

---

# User

```java
class User {

    String userId;

    String name;

    String email;
}
```

Notice that we intentionally do NOT keep:

```java
List<BorrowRecord> borrowRecords;
```

inside User.

---

# BorrowRecord

BorrowRecord represents one borrowing transaction.

```java
class BorrowRecord {

    String borrowId;

    User user;

    Book book;

    LocalDate borrowDate;

    LocalDate dueDate;

    LocalDate returnDate;

    BorrowStatus status;
}
```

---

# BorrowStatus

```java
enum BorrowStatus {

    ACTIVE,

    RETURNED
}
```

Since books are automatically returned after the due date, we don't need an ACTIVE + OVERDUE + RETURNED lifecycle unless the business explicitly wants to distinguish overdue records.

---

# Why BorrowRecord is a Separate Entity

A common interview question:

> Why not simply keep List<Book> inside User?

Because borrowing is **not just a relationship**.

It has its own business information:

- Borrow Date
- Due Date
- Return Date
- Status

Therefore it becomes a **first-class domain entity**.

It is similar to:

- Booking (BookMyShow)
- Ticket (Parking Lot)
- Order (Amazon)
- Rental (Car Rental)

---

# Why NOT List<BorrowRecord> inside User?

There are two possible designs.

## Option 1

```java
class User {

    List<BorrowRecord> borrowRecords;
}
```

Advantages

- Easy navigation
- Pure object-oriented design

Disadvantages

- Bidirectional relationship
- More synchronization
- Duplicate references

---

## Option 2 (Chosen)

BorrowRecord remains an independent entity.

```text
BorrowRecord

↓

User

↓

Book
```

Borrow records are retrieved through the service/repository when needed.

Advantages

- Single source of truth
- Cleaner domain model
- Scales better as the application grows

---

# Interview Answer

If the interviewer asks:

> Why did you create BorrowRecord?

A good answer is:

> "BorrowRecord is a first-class transaction entity rather than just a relationship because it has its own lifecycle and business attributes such as borrow date, due date, return date, and status."

This is much stronger than saying:

> "That's how repositories work."

# 04_Workflows.md

# Borrow Book Workflow

API

```java
BorrowRecord borrowBook(String userId, String bookId);
```

---

## Workflow

```text
borrowBook(userId, bookId)

↓

Validate User

↓

Validate Book

↓

Check Borrow Limit

↓

Check Book Availability

↓

Create BorrowRecord

↓

Decrease Available Copies

↓

Persist Changes

↓

Return BorrowRecord
```

---

## Step-by-Step Algorithm

### Step 1

Validate the user.

If the user does not exist:

```java
throw new UserNotFoundException();
```

---

### Step 2

Validate the book.

If the book does not exist:

```java
throw new BookNotFoundException();
```

---

### Step 3

Check borrowing limit.

A user can borrow at most **5 books**.

Example:

```java
activeBorrows =
    borrowRecordRepository.findActiveBorrows(userId);

if(activeBorrows.size() >= 5)
    throw new UserBorrowLimitExceededException();
```

---

### Step 4

Check inventory.

```java
if(book.getAvailableCopies()==0)
    throw new BookNotAvailableException();
```

---

### Step 5

Create BorrowRecord.

```java
BorrowRecord record = new BorrowRecord();
```

Populate:

- borrowId
- user
- book
- borrowDate
- dueDate
- status = ACTIVE

---

### Step 6

Decrease inventory.

```java
book.availableCopies--;
```

---

### Step 7

Persist.

Save:

- BorrowRecord
- Book

These updates should happen inside **one database transaction**.

---

### Step 8

Return BorrowRecord.

---

# Exceptions

Possible exceptions:

```java
UserNotFoundException

BookNotFoundException

BookNotAvailableException

UserBorrowLimitExceededException
```

---

# Return Book Workflow

API

```java
void returnBook(String borrowId);
```

---

## Workflow

```text
returnBook(borrowId)

↓

Fetch BorrowRecord

↓

Validate BorrowRecord

↓

Already Returned?

↓

Update Return Date

↓

Update Status

↓

Increase Available Copies

↓

Persist Changes
```

---

## Step-by-Step Algorithm

### Step 1

Fetch BorrowRecord.

If it doesn't exist:

```java
throw new BorrowRecordNotFoundException();
```

---

### Step 2

Check current status.

If already returned:

Either

```java
throw new InvalidOperationException();
```

or

Return success (idempotent API).

Both are acceptable if justified.

---

### Step 3

Update return date.

```java
returnDate = today;
```

---

### Step 4

Update status.

```java
status = RETURNED;
```

---

### Step 5

Increase inventory.

```java
book.availableCopies++;
```

---

### Step 6

Persist.

Update:

- BorrowRecord
- Book

Inside one database transaction.

---

# Why One Transaction?

Suppose:

BorrowRecord updated

↓

Application crashes

↓

Book inventory NOT updated

Now the system becomes inconsistent.

To avoid this:

- update BorrowRecord
- update Book

inside one transaction.

---

# Interview Questions

## Why return using borrowId?

BorrowRecord uniquely identifies one borrowing transaction.

A book can be borrowed many times over its lifetime.

Using borrowId uniquely identifies the exact transaction.

---

## Why not return using bookId?

Because:

One book

↓

Borrowed many times

↓

Many BorrowRecords

borrowId avoids ambiguity.

---

# Interview Tip

Whenever an operation updates multiple entities, mention:

> "I would perform these updates inside a single database transaction to maintain consistency."

Interviewers like hearing this because it shows you understand transactional integrity.	


