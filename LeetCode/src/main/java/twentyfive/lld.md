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
