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

