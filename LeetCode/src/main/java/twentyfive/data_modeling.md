# Apple Data Modeling Interview Guide
# Part 1 - Thinking Like a Data Modeler

---

# Introduction

One thing I realized while preparing is that Apple isn't looking for someone who can
memorize table schemas.

They are looking for someone who can answer one simple question repeatedly:

> **Why does this table exist?**

Every entity.
Every column.
Every relationship.

should exist because of a business requirement.

Instead of asking

> "Where should I put this field?"

I should ask

> "What business concept does this represent?"

This document captures that way of thinking.

---

# Problem Statement

Design the data model for Apple Notes.

---

# Step 1 - Clarify Requirements

Before drawing any tables, ask questions.

Example questions:

- Can one user own multiple notes?
- Can notes contain images?
- PDFs?
- Sketches?
- Checklists?
- Are folders supported?
- Can folders be nested?
- Can notes belong to multiple folders?
- Is collaboration supported?
- Is version history required?
- Can deleted notes be restored?
- What are the common access patterns?
- Is search required?

Notice something.

None of these questions are about SQL.

They are all about understanding the business.

---

# Step 2 - Identify Entities

Based on requirements we identified:

User

Folder

Note

Attachment

Tag

These are business entities.

A common mistake is introducing technical concepts as entities.

Example:

Device

This is NOT a business entity.

Whether the note is accessed from an iPhone or MacBook doesn't change the business model.

The backend stores Notes.

Not iPhone Notes or Mac Notes.

---

# Step 3 - Folder Hierarchy

Suppose folders can contain folders.

Example

Work

    Backend

        Apple

Instead of creating another table, use a recursive relationship.

Folder

folderId

userId

name

parentFolderId

where

parentFolderId references Folder.folderId.

Example

| folderId | name | parentFolderId |
|----------|------|----------------|
|1|Work|NULL|
|2|Backend|1|
|3|Apple|2|

This is known as a self-referencing relationship.

---

# Step 4 - One Folder or Multiple Folders?

Initially

Folder

↓

Many Notes

One note belongs to one folder.

Easy.

Later requirement changes.

One note can appear inside multiple folders.

Now relationship changes.

Folder

⇄

Note

Many-to-Many

Therefore create

NoteFolder

noteId

folderId

Notice something.

The new table exists because the relationship changed.

Not because someone memorized NoteFolder.

---

# Step 5 - Tags

Question

Should tags be stored inside Note?

Example

Travel,Interview,Apple

No.

Why?

One note has many tags.

One tag belongs to many notes.

Many-to-Many.

Create

Tag

tagId

tagName

Create

NoteTag

noteId

tagId

Simple.

---

# Step 6 - User Specific Tags

Now requirements change.

Suppose

User A

creates

Work

User B

also creates

Work

Later User A renames

Work

to

Office

Should User B's tag change?

Obviously not.

Now Tag becomes

Tag

tagId

userId

tagName

Notice

The schema changed because the business rule changed.

---

# Step 7 - Ownership vs Collaboration

Initially

One user owns one note.

Simple.

Note

noteId

ownerUserId

title

...

Later

Multiple users can edit.

Question

Should ownerUserId disappear?

Not necessarily.

Think of Google Docs.

You create a document.

You invite Alice.

Alice can edit.

Does Alice become owner?

No.

Ownership

and

Collaboration

are different concepts.

Owner answers

Who created this note?

Who can permanently delete it?

Who transfers ownership?

Collaboration answers

Who can edit?

Who can view?

Who accepted the invite?

Who can comment?

Therefore

Note

ownerUserId

...

and

NoteCollaborator

noteId

userId

permission

Two relationships.

Different business meaning.

---

# Alternative Design

Suppose business says

Nobody owns the document.

Everyone is equal.

Then

ownerUserId disappears.

Instead

NoteParticipant

noteId

userId

role

Where role is

OWNER

EDITOR

VIEWER

Ownership itself becomes a relationship.

Large collaboration systems often use this approach.

---

# Step 8 - Favorite Notes

First instinct

Add

isFavorite

inside Note.

Question.

If Bob favorites the note

Should Alice automatically see it as favorite?

No.

Favorites belong to users.

Not notes.

Therefore

UserNotePreference

userId

noteId

isFavorite

isPinned

lastViewed

Lesson

Ask

Is this property of the entity

or

property of the relationship?

Examples

title

Property of Note.

lastModified

Property of Note.

permission

Property of User ⇄ Note.

isFavorite

Property of User ⇄ Note.

---

# Step 9 - Soft Delete

Question

Why keep

isDeleted

instead of deleting the row?

Because sometimes business wants recovery.

Apple Notes has

Recently Deleted.

When user deletes

Don't remove the row.

Instead

deletedAt

or

isDeleted

is updated.

Queries become

WHERE deletedAt IS NULL

Benefits

Recover deleted notes.

Audit.

Undo.

Retention policies.

Eventually

A background job permanently removes rows after retention period.

Hard delete is still acceptable if business doesn't require recovery.

Always ask

Does the product need Recently Deleted?

If yes

Soft delete.

If no

Hard delete.

---

# Step 10 - Version History

Without history

Editing overwrites content.

Old version disappears forever.

If business requires

Version History

Create

NoteVersion

versionId

noteId

versionNumber

title

content

editedBy

editedAt

Question

When should versions be created?

Not every keystroke.

Possible policies

User presses Save.

User closes note.

Periodic auto-save.

Significant edits.

All are business decisions.

---

# Two Common Version Designs

Design A

Current version stored in Note.

Old versions stored in NoteVersion.

Workflow

User edits.

Archive previous state.

Update Note.

Advantages

Very fast reads.

Simple implementation.

Good for most systems.

---

Design B

Every save creates a new immutable version.

Note only stores

currentVersionId

All historical data lives inside

NoteVersion.

Advantages

Immutable history.

Simple rollback.

Excellent audit.

Better collaboration.

More storage.

---

# Step 11 - Search

Question

Should we index content?

Usually not using normal B-tree indexes.

Title

Yes.

lastModified

Yes.

ownerUserId

Yes.

content

Prefer full-text search.

Think Elasticsearch

or database full-text indexing.

Different workloads require different indexing strategies.

---

# Step 12 - Think Like Apple

Instead of asking

Where do I put this field?

Ask

Why does this field exist?

Examples

Why ownerUserId?

Ownership.

Why NoteCollaborator?

Multiple editors.

Why UserNotePreference?

Preferences belong to users.

Why NoteVersion?

History.

Why deletedAt?

Recovery.

Why NoteFolder?

Many-to-Many relationship.

Every table should exist because a requirement demanded it.

Not because every database has one.

---

# Biggest Lesson

Every time I introduce

a column

or

a table

I should ask

> Is this describing the entity?

or

> Is this describing a relationship between entities?

That one question eliminates a huge number of bad designs.

---

# Interview Checklist

Whenever Apple asks me to model something:

□ Clarify requirements.

□ Identify entities.

□ Identify relationships.

□ Determine cardinality.

□ Identify ownership.

□ Separate entity properties from relationship properties.

□ Think about indexing.

□ Think about access patterns.

□ Think about scale.

□ Think about future requirements.

Only then start drawing tables.

---

# Final Thought

Good data modelers don't memorize schemas.

They understand business concepts.

The schema is simply the result of that understanding.

# Apple Data Modeling Interview Guide
# Part 2 - Relationships, Cardinality and Thinking About Data

---

# Introduction

A database is nothing more than

Entities

and

Relationships.

The biggest mistake candidates make is immediately creating tables.

Instead, identify:

1. What are the entities?
2. How are they related?
3. What is the cardinality?

Once you know the cardinality,
the schema almost writes itself.

---

# Cardinality

Cardinality simply answers

> How many objects can be related?

There are only four possibilities.

1. One-to-One (1:1)

2. One-to-Many (1:N)

3. Many-to-One (N:1)

4. Many-to-Many (M:N)

Almost every interview question can be reduced to one of these.

---

# One-to-One (1:1)

Example

Every user has exactly one profile.

```
User

↓

Profile
```

Tables

User

userId

name

email

Profile

profileId

userId (Unique FK)

dob

bio

photo

Notice

userId is UNIQUE.

Otherwise one user could have multiple profiles.

---

# Apple Example

Apple ID

↓

Apple User Preferences

One preference record.

Exactly one.

Perfect candidate for 1:1.

---

# Another Example

Employee

↓

Parking Spot

Each employee has one assigned parking spot.

Each parking spot belongs to one employee.

1:1.

---

# Should everything become a new table?

No.

Question

Should

Address

be another table?

If

User

always has exactly one address

and

address isn't shared,

then embedding the address columns inside User is perfectly reasonable.

Don't normalize without a reason.

---

# One-to-Many (1:N)

Most common relationship.

Example

One User

↓

Many Notes

```
User
    |
    |
    v
Note
```

Schema

User

userId

...

Note

noteId

ownerUserId

title

content

Relationship

ownerUserId references User.userId

---

# Another Example

Folder

↓

Many Notes

One folder contains many notes.

Each note belongs to one folder.

Simple foreign key.

---

# Apple Music Example

Artist

↓

Albums

Album

↓

Songs

Artist

1:N

Album

1:N

Song

---

# Amazon Example

Customer

↓

Orders

One customer

Many orders.

Easy.

---

# Rule

Whenever you hear

One

↓

Many

The foreign key goes on the MANY side.

Example

User

↓

Note

Store

ownerUserId

inside

Note.

NOT

list of noteIds

inside User.

---

# Why?

Suppose

Saurabh

creates

1 million notes.

Should User table contain

1 million IDs?

Obviously not.

Foreign key belongs on the child.

---

# Many-to-One (N:1)

This is simply One-to-Many viewed from the other direction.

Example

Many Notes

↓

One User

Nothing changes.

Same schema.

Don't overthink it.

---

# Many-to-Many (M:N)

Most important interview topic.

Example

Students

⇄

Courses

One student

Many courses.

One course

Many students.

Impossible using one foreign key.

Need another table.

---

# Junction Table

StudentCourse

studentId

courseId

Now relationship becomes

Student

↓

StudentCourse

↓

Course

Problem solved.

---

# Apple Notes Example

One Note

Many Tags

One Tag

Many Notes

Therefore

Tag

tagId

name

and

NoteTag

noteId

tagId

Classic Many-to-Many.

---

# Another Apple Example

Shared Notes

One Note

Many Users

One User

Many Shared Notes

Need

NoteCollaborator

noteId

userId

permission

Notice

permission belongs here.

Because permission describes

the relationship.

Not User.

Not Note.

---

# Interview Trick

Suppose interviewer asks

Can one note belong to multiple folders?

Immediately think

Folder

⇄

Note

Many-to-Many

Need

NoteFolder.

No memorization.

Just follow cardinality.

---

# Properties of the Relationship

This is where senior engineers stand out.

Example

User

⇄

Note

Relationship

Can Edit

Can View

Accepted Invite

Joined At

Role

Permission

None of these belong to User.

None belong to Note.

They belong to

User ⇄ Note

Therefore

NoteCollaborator

contains

permission

joinedAt

acceptedAt

role

---

# Properties of the Entity

Now compare

title

content

createdAt

lastModified

These belong to

Note.

Not the relationship.

---

# Simple Rule

Ask

Is this describing

the Note?

or

the relationship between User and Note?

Examples

title

Note

content

Note

permission

Relationship

favorite

Relationship

lastViewed

Relationship

ownerUserId

Note

---

# Recursive Relationships

Sometimes

an entity relates to itself.

Example

Folders

Work

↓

Backend

↓

Apple

Schema

Folder

folderId

name

parentFolderId

The foreign key points back to Folder.

This is called a recursive relationship.

Very common.

Categories.

Comments.

Organization hierarchy.

Folders.

---

# Optional Relationships

Question

Can a note exist without a folder?

If yes

folderId

can be NULL.

If no

folderId

must NOT be NULL.

Notice

Requirements determine constraints.

---

# Weak vs Strong Relationships

Example

Attachment

belongs to

Note.

Can an attachment exist without a note?

Probably not.

Then Attachment has a strong dependency.

Deleting Note probably deletes Attachments.

Example

Tag

Can exist without any notes.

Yes.

Tags are independent.

Understanding lifecycle helps determine relationships.

---

# Ownership vs Relationship

Don't confuse these.

Ownership

Who created the object?

Relationship

Who can access the object?

Google Docs

Owner

Saurabh

Editors

Alice

Bob

Owner remains one person.

Editors are relationship entries.

---

# Cardinality Cheat Sheet

1:1

User

↓

Preference

Store FK on dependent table.

Use UNIQUE.

---

1:N

User

↓

Note

Store FK on child.

---

M:N

User

⇄

Role

Need mapping table.

---

Recursive

Folder

↓

Folder

Use parentFolderId.

---

# Interview Flow

Whenever interviewer introduces a new feature

don't think

"New table?"

Instead ask

"What happened to the relationship?"

Examples

Multiple folders

Relationship changed.

Need NoteFolder.

Multiple editors

Relationship changed.

Need NoteCollaborator.

Favorites per user

Relationship changed.

Need UserNotePreference.

Version history

Business requirement changed.

Need NoteVersion.

Recently Deleted

Lifecycle changed.

Need deletedAt.

---

# Golden Rule

Most schemas are not difficult.

Most interview mistakes happen because candidates fail to identify relationships.

Always identify

Entities

↓

Relationships

↓

Cardinality

↓

Foreign Keys

↓

Only then write tables.

Everything else becomes much easier.

# Apple Data Modeling Interview Guide
# Part 3 - Normalization, Denormalization and Tradeoffs

---

# Introduction

One of the most common interview questions is

> Should this be another table?

There is no universal answer.

The answer depends on

- redundancy
- consistency
- performance
- access patterns
- scalability

Normalization is simply a process of organizing data so that information is stored in one place.

The goal is

- reduce duplication
- improve consistency
- simplify updates

Normalization is NOT the goal.

Good system design is the goal.

---

# Example 1

Suppose we store Notes.

```
Note

noteId

title

content

ownerName

ownerEmail
```

Looks simple.

Suppose Saurabh owns

1000 notes.

Now

ownerName

is stored

1000 times.

ownerEmail

is stored

1000 times.

---

# Problem

Suppose user changes email.

Now

1000 rows

must be updated.

If one update fails

database becomes inconsistent.

Some notes show

old email.

Some notes show

new email.

Bad design.

---

# Better Design

Create

User

userId

name

email

Now

Note

contains

ownerUserId

instead.

Information exists only once.

---

# Why?

Ask yourself

Is the owner's email

a property of the Note?

No.

It is a property of User.

Therefore

move it.

---

# Rule

Store information

where it naturally belongs.

---

# First Normal Form (1NF)

Simple rule.

Every column should contain

one value.

Bad

```
tags

Apple,Interview,Backend
```

Multiple values.

Impossible to search efficiently.

---

# Better

Tag

tagId

name

NoteTag

noteId

tagId

One value per column.

Much easier.

---

# Another Example

Bad

```
phoneNumbers

1234,5678,9999
```

Good

PhoneNumber

phoneId

userId

number

---

# Interview Question

Should

attachments

be stored like

image1,image2,image3

inside Note?

Never.

One row

One attachment.

---

# Second Normal Form (2NF)

This mainly matters when

a table has

a composite primary key.

Example

StudentCourse

studentId

courseId

studentName

courseName

grade

Notice

studentName

depends only on

studentId.

courseName

depends only on

courseId.

Only

grade

depends on both.

Therefore

studentName

belongs in Student.

courseName

belongs in Course.

StudentCourse

contains

studentId

courseId

grade

---

# Interview Tip

Most interviews never ask

"Define 2NF."

Instead they ask

"Would you store customer name here?"

Think about

what determines the data.

---

# Third Normal Form (3NF)

Rule

Columns should depend

only on

the primary key.

Example

Employee

employeeId

zipCode

city

state

Notice

city

depends on

zipCode.

Not employee.

Should city be repeated?

Usually not.

Instead

ZipCode

zipCode

city

state

Employee

stores

zipCode

only.

---

# Practical Reality

Would I always create

ZipCode

table?

Probably not.

If application never updates cities

duplicating

city

may be perfectly acceptable.

Interviewers love this answer.

---

# Apple Interview Mindset

Don't normalize

because the textbook says so.

Normalize

because duplication creates problems.

---

# Over-Normalization

Imagine this.

```
User

userId

firstNameId

lastNameId
```

Another table

FirstName

Another table

LastName

Ridiculous.

Every query now joins

three tables

for no benefit.

Normalization has a cost.

---

# Denormalization

Sometimes

we intentionally duplicate data.

Question

Why?

Performance.

---

# Example

User

userId

name

Picture

pictureId

userId

url

Suppose homepage displays

100 friends.

Each query now joins

User

and

Picture.

Millions of times.

Instead

store

profilePictureUrl

inside User.

Duplicate?

Yes.

Faster?

Also yes.

---

# Apple Notes Example

Suppose

content

is

50 MB.

Homepage shows

20 notes.

Should homepage load

20 × 50 MB?

No.

Store

previewText

inside Note.

Store

content

inside NoteContent.

Now homepage loads

only

title

previewText

lastModified

Huge improvement.

This is denormalization driven by access patterns.

---

# Another Example

Suppose

every query asks

recent notes.

Instead of computing

lastModified

from edit history

store it

inside Note.

Small duplication.

Huge performance gain.

---

# Rule

Storage is cheap.

Latency is expensive.

---

# Tradeoff Thinking

Whenever interviewer asks

Would you normalize?

Think

What happens on reads?

What happens on writes?

How often is data updated?

How often is it queried?

---

# Interview Example

Question

Should owner's email be stored inside Note?

Read optimization?

No.

Update cost?

Huge.

Normalize.

---

Question

Should preview text be stored?

Duplication?

Yes.

Read optimization?

Huge.

Denormalize.

---

Question

Should favorite be inside Note?

Different users have different favorites.

Relationship.

Separate table.

---

Question

Should title exist inside Version table?

Yes.

Version should represent

the complete state

of the note

at that point in time.

---

# Interview Framework

Whenever adding a field ask

1.

Who owns this information?

User?

Note?

Folder?

Relationship?

2.

Will this information repeat?

3.

If it changes

how many rows update?

4.

How often is it read?

5.

How often is it written?

6.

Can I derive it instead of storing it?

If yes

maybe don't store it.

---

# Example

Should Note have

isCollaborative?

Question

Can I derive it

from

NoteCollaborator?

If yes

don't duplicate.

---

# Another Example

Should Note have

numberOfCollaborators?

Maybe.

Why?

If homepage shows

collaborator count

millions of times

calculating COUNT(*)

every request

is expensive.

Store

collaboratorCount

inside Note.

Update when collaborators change.

Classic denormalization.

---

# Golden Rule

Never normalize blindly.

Never denormalize blindly.

Every design decision should answer

one question.

> Which choice makes this system simpler, faster, and easier to maintain for the given requirements?

---

# Apple Interview Checklist

When interviewer challenges your schema

don't defend it immediately.

Walk through your reasoning.

Example

"I normalized owner information because it naturally belongs to User and avoids duplication."

"I denormalized previewText because it optimizes the most common read path."

"I created NoteCollaborator because permissions describe the relationship between a User and a Note."

That explanation is usually more valuable than the schema itself.

---

# Biggest Lesson

Normalization is not about passing database exams.

Normalization is about asking

"Where does this information naturally belong?"

Denormalization is about asking

"Is duplicating this information worth the performance benefit?"

Senior engineers balance both.

Great engineers know when to choose each.

# Apple Data Modeling Interview Guide
# Part 4 - Access Patterns Drive the Data Model

---

# Introduction

One sentence changed the way I think about data modeling.

> Databases are not designed to store data.

They are designed to answer queries efficiently.

This means that before creating a schema, I should ask

> What queries need to be fast?

The schema, indexes, normalization and even denormalization
should all support those queries.

---

# Apple Interview

Suppose the interviewer says

Design Apple Notes.

Don't immediately draw tables.

Instead ask

> What are the common access patterns?

For example

- Open recent notes
- Search notes
- Open one note
- Share a note
- Restore deleted note
- Open folder
- List notes inside folder
- Show favorite notes
- Show shared notes
- View version history

Notice

These are queries.

Not tables.

---

# Rule

Always identify

READS

before

WRITES.

Why?

Because most applications perform far more reads than writes.

Optimizing common reads usually provides the biggest benefit.

---

# Example 1

Requirement

User opens Apple Notes.

Question

What does the home screen display?

Probably

- title

- preview

- last modified

- favorite

Not

- full 50 MB content

Not

- attachment blobs

Not

- version history

Immediately this tells us

Maybe Note should not contain huge content.

---

# Design

Instead of

Note

noteId

title

content

Store

Note

noteId

title

previewText

lastModified

Then

NoteContent

noteId

content

Now opening the homepage requires reading

only

title

previewText

lastModified

Much faster.

Notice

The query changed the schema.

---

# Example 2

Requirement

Show

Recent Notes.

Query

```
SELECT *
FROM Note
WHERE ownerUserId=?
ORDER BY lastModified DESC
LIMIT 20
```

Immediately think

Index

(ownerUserId,lastModified)

Not because indexes are cool.

Because this query happens constantly.

---

# Example 3

Requirement

Open a note.

Query

```
SELECT *
FROM Note
WHERE noteId=?
```

Need

Primary Key Index.

Simple.

---

# Example 4

Requirement

Show notes inside a folder.

Schema

Folder

Note

NoteFolder

Query

```
SELECT noteId
FROM NoteFolder
WHERE folderId=?
```

Index

folderId

Then fetch Notes.

Again

Query first.

Schema second.

---

# Example 5

Requirement

Search

"Kafka"

inside notes.

Would a normal B-Tree index help?

No.

Need

Full Text Search

Maybe

SQLite FTS

Elasticsearch

OpenSearch

Database full-text index.

The workload determines the technology.

---

# Example 6

Requirement

Show favorite notes.

Suppose

UserNotePreference

contains

userId

noteId

isFavorite

Query

```
SELECT noteId
FROM UserNotePreference
WHERE userId=?
AND isFavorite=true
```

Index

(userId,isFavorite)

Very common.

---

# Example 7

Requirement

Show all shared notes.

Schema

NoteCollaborator

Query

```
SELECT noteId
FROM NoteCollaborator
WHERE userId=?
```

Need

Index

userId

Simple.

---

# Example 8

Requirement

Restore deleted notes.

Query

```
SELECT *
FROM Note
WHERE ownerUserId=?
AND deletedAt IS NOT NULL
```

Need

Index

(ownerUserId,deletedAt)

Again

Access pattern

↓

Index

---

# Example 9

Requirement

Open Version History.

Query

```
SELECT *
FROM NoteVersion
WHERE noteId=?
ORDER BY versionNumber DESC
```

Index

(noteId,versionNumber)

---

# Notice Something

We never started with indexes.

We started with

"What query needs to be fast?"

That question produced

the index.

---

# Read-heavy vs Write-heavy

Another interview favorite.

Question

How often is this operation performed?

Example

Opening notes

Millions of times.

Sharing notes

Occasionally.

Creating folders

Rarely.

Deleting notes

Rarely.

Optimize

opening notes.

Not

creating folders.

---

# Hot Path

Every system has

critical paths.

Apple Notes

Open Notes

Search Notes

Edit Notes

These deserve optimization.

Version History

might be much less frequent.

Don't optimize everything equally.

---

# Derived Data

Suppose homepage shows

Number of Collaborators

Option 1

Every request

```
COUNT(*)
FROM NoteCollaborator
```

Option 2

Store

collaboratorCount

inside Note.

Question

Should we duplicate data?

Depends.

If

homepage reads

100 million/day

and

sharing happens

10 thousand/day

Denormalizing is probably worth it.

---

# Think About Scale

Interviewer

How many notes?

Suppose

100 million users

Each user

1000 notes.

100 billion notes.

Now ask

Will this query still work?

Thinking about scale often changes the design.

---

# Pagination

Never return

every note.

Always ask

How many?

Use

LIMIT

OFFSET

or

cursor-based pagination.

Apple Notes

shows maybe

20

notes initially.

Not

100,000.

---

# Caching

Question

Would you cache?

Recent notes

Yes.

Version history

Maybe not.

Search results

Depends.

Think about

what changes frequently.

---

# Interview Framework

Whenever interviewer introduces a feature

follow this sequence.

Step 1

What is the query?

↓

Step 2

How often is it executed?

↓

Step 3

What tables are needed?

↓

Step 4

Do I need indexes?

↓

Step 5

Should I normalize?

↓

Step 6

Should I denormalize?

↓

Step 7

Any caching?

↓

Done.

---

# Example Walkthrough

Interviewer

Users want favorite notes.

Instead of immediately creating

isFavorite

Ask

What is the query?

```
Show all favorite notes for user 42
```

Now ask

Which table naturally owns this information?

UserNotePreference

Now ask

Which index?

(userId,isFavorite)

Notice

One query

determined

the table

and

the index.

---

# Biggest Lesson

Senior engineers don't design tables.

They design

efficient query paths.

The tables are simply one part of that solution.

---

# Apple Interview Checklist

Whenever I hear a new requirement

I will ask

✓ What is the common query?

✓ What columns are returned?

✓ How often is it executed?

✓ Which index supports it?

✓ Is normalization helping or hurting?

✓ Should I denormalize?

✓ Should I cache?

If I answer these questions first,

the schema becomes much easier to design.

---

# Final Thought

A database is successful not because it stores data.

A database is successful because it answers the right queries quickly.

That is the mindset Apple interviewers want to see.

# Apple Data Modeling Interview Guide
# Part 5 - Indexing (The Apple Way)

---

# Introduction

One of the biggest misconceptions about indexes is

> "Indexes make everything faster."

They don't.

Indexes make

READS

faster

but they make

WRITES

slower.

Every index has a cost.

Whenever I create an index I should ask

> Is this query important enough to justify the cost?

---

# What is an Index?

Imagine a 1000-page book.

Suppose I ask

Find

"Normalization"

Option 1

Read every page.

Option 2

Open the index.

Go directly to page 642.

Databases work exactly the same way.

Without an index

Database scans every row.

With an index

Database jumps directly to the matching rows.

---

# Example

Table

Note

| noteId | ownerUserId | title |
|---------|-------------|-------|
|1|10|Apple|
|2|20|Kafka|
|3|10|Interview|

Query

SELECT *

FROM Note

WHERE ownerUserId=10

Without index

Read

every row.

With index

Jump directly to

ownerUserId=10

---

# Primary Key Index

Every primary key automatically has an index.

```
Note

noteId (PK)
```

Query

```
SELECT *

FROM Note

WHERE noteId=100
```

Very fast.

No extra index needed.

---

# Should every column be indexed?

No.

Indexes consume

Memory

Disk

CPU during updates

Every INSERT

UPDATE

DELETE

must update

every relevant index.

Too many indexes

slow writes significantly.

---

# Interview Question

Should title be indexed?

Depends.

Do users search by title?

If yes

Probably.

If not

No.

Always relate indexes

to access patterns.

---

# Composite Index

This is one of the most common interview topics.

Suppose query is

```
SELECT *

FROM Note

WHERE ownerUserId=10

ORDER BY lastModified DESC

LIMIT 20
```

Question

Should we create

Index(ownerUserId)

and

Index(lastModified)

Not necessarily.

Better

(ownerUserId,lastModified)

One composite index

matches

both

WHERE

and

ORDER BY.

---

# Why?

Imagine

ownerUserId

10

contains

1 million notes.

Database finds

ownerUserId=10

already sorted

by

lastModified.

No extra sorting.

Very efficient.

---

# Leftmost Prefix Rule

Composite index

(ownerUserId,lastModified,title)

Can support

ownerUserId

✅

ownerUserId + lastModified

✅

ownerUserId + lastModified + title

✅

Can it support

lastModified alone?

No.

Because ownerUserId

comes first.

Always put

the most selective

or

most common filter

first.

---

# Apple Example

Query

Show my recent notes.

```
WHERE ownerUserId=?

ORDER BY lastModified DESC
```

Perfect index

(ownerUserId,lastModified)

---

# Covering Index

Suppose homepage only displays

title

previewText

lastModified

Query

```
SELECT

title,

previewText,

lastModified

FROM Note

WHERE ownerUserId=?
```

Some databases can satisfy this query

using only

the index

without reading the table.

This is called

a covering index.

Very fast.

---

# Unique Index

Question

Can two users have

the same email?

No.

Create

UNIQUE(email)

Benefits

Fast lookups.

Enforces uniqueness.

---

# Low Selectivity

Should

isDeleted

be indexed?

Suppose

99.9%

rows have

isDeleted=false

Database still needs

almost every row.

The index provides little value.

Low selectivity

usually means

poor index candidate.

---

# High Selectivity

email

Excellent.

phoneNumber

Excellent.

noteId

Excellent.

Very few rows match.

Indexes shine.

---

# Sorting

Suppose

```
ORDER BY

createdAt
```

millions of rows.

Without index

Database sorts everything.

With index

Already sorted.

Huge improvement.

---

# Range Queries

Query

```
WHERE

createdAt

BETWEEN

Jan 1

AND

Jan 31
```

Indexes work well.

Good candidate.

---

# LIKE Queries

Suppose

```
WHERE

title LIKE

'Apple%'
```

Index can help.

Suppose

```
LIKE

'%Apple%'
```

Usually

normal B-tree indexes

cannot help.

Need

Full Text Search

or

specialized indexes.

---

# Too Many Indexes

Imagine

10 indexes.

Insert

one row.

Database updates

10 indexes.

Write latency increases.

Always ask

How frequently is data written?

---

# Apple Notes Examples

Open Note

```
WHERE noteId=?
```

Primary key index.

---

Show Recent Notes

```
WHERE ownerUserId=?

ORDER BY lastModified
```

Composite index

(ownerUserId,lastModified)

---

Favorites

```
WHERE userId=?

AND isFavorite=true
```

Composite index

(userId,isFavorite)

---

Folder

```
WHERE folderId=?
```

Index

folderId

---

Version History

```
WHERE noteId=?

ORDER BY versionNumber DESC
```

Composite index

(noteId,versionNumber)

---

Shared Notes

```
WHERE collaboratorId=?
```

Index

userId

inside

NoteCollaborator

---

Search

Searching

content

Normal index?

No.

Use

Full Text Search.

---

# Clustered vs Non-Clustered

Interviewers occasionally ask this.

Clustered

The table itself

is stored

in index order.

Only one.

Example

Primary Key.

---

Non-clustered

Separate structure.

Points back

to table rows.

Can have many.

---

Don't memorize database-specific behavior.

Simply know

Clustered

determines physical ordering.

Non-clustered

does not.

---

# Tradeoffs

Every index gives

Faster reads

Slower writes

More storage

Maintenance cost

Always mention

the tradeoff.

Interviewers love hearing this.

---

# Interview Framework

Whenever I hear

a new query

I ask

1.

What columns are filtered?

↓

WHERE

2.

What columns are sorted?

↓

ORDER BY

3.

What columns are returned?

↓

SELECT

4.

Can one composite index satisfy all three?

If yes

Excellent.

---

# Biggest Lesson

Don't create indexes

because every table needs indexes.

Create indexes

because a query needs to be fast.

Queries drive indexes.

Indexes drive performance.

---

# Apple Interview Checklist

For every feature

I will ask

✓ What query is executed?

✓ Is this read-heavy?

✓ Which columns are filtered?

✓ Which columns are sorted?

✓ Should I use a composite index?

✓ Is selectivity high?

✓ Does the write cost justify this index?

Only then

create the index.

---

# Final Thought

Junior engineers say

"I'll add an index."

Senior engineers say

"I'll add a composite index on (ownerUserId, lastModified) because the home screen frequently executes a query filtering by owner and sorting by modification time. This avoids a table scan and an additional sort while accepting a small write overhead."

That explanation is far more valuable than simply drawing an index.

# Apple Music Data Modeling Interview

## Problem Statement

Design the backend data model for Apple Music.

Focus only on the data model. We are not designing APIs or distributed systems.

---

# 1. Clarifying Questions

Before jumping into entities, clarify the business requirements.

### Questions I Asked

- Can users create their own albums?
- Can a song have multiple artists?
- Can albums have multiple artists (compilation albums)?
- Do users have a personal library?
- Can playlists be collaborative?
- Do we need to model subscriptions?
- Do songs expire after some time?

### Interviewer's Assumptions

- Users **cannot** create albums.
- Albums are created by publishers.
- Albums contain multiple songs.
- Users have a personal music library.
- Playlists can be collaborative.
- Ignore subscriptions and licensing.
- Songs never expire.
- Support:
    - Favorites
    - Recently Played
    - Downloads
    - Albums
    - Artists
    - Playlists
- Ignore recommendation algorithms.

---

## Interview Tip

Always spend 2-3 minutes understanding the requirements before identifying entities.

Most data modeling mistakes happen because candidates assume requirements instead of asking them.


# 2. Core Entities

The first step is to identify the primary business entities.

At this stage, don't worry about foreign keys or relationship tables. Focus only on the main objects in the system.

## Core Entities

```text
User
Song
Album
Artist
Playlist
```

### User

Represents an Apple Music subscriber.

Stores user profile information and owns user-specific data such as playlists, favorites, downloads, and play history.

---

### Song

Represents an individual piece of music.

A song belongs to an album (based on our assumptions) and can have one or more artists.

---

### Album

Represents a collection of songs released together.

Albums are publisher-managed and cannot be created by users.

Depending on the business requirements, an album may have one or more artists.

---

### Artist

Represents a musician or band.

An artist can perform many songs, and a song may have multiple artists (featured artists, collaborations, duets, etc.).

---

### Playlist

Represents a user-created collection of songs.

Each playlist has one owner.

Later, we'll extend the model to support collaborative playlists.

---

# Why Didn't We Create a Library Entity?

Although the requirements mention that users have a personal library, I would not immediately create a `Library` table.

The reason is that "Library" may simply be a logical collection of:

- Favorite Songs
- Downloaded Songs
- User Playlists
- Saved Albums

Until the interviewer explicitly states that a library has its own behavior or metadata, I would avoid introducing another entity.

This is an important interview principle:

> Don't create entities unless the business requirements justify them.

---

# Table Naming Convention

I prefer using **singular** table names.

```text
User
Song
Album
Artist
Playlist
```

instead of

```text
Users
Songs
Albums
Artists
Playlists
```

Reason:

Each row represents a single business object.

For example,

```text
Song
------
songId
title
duration
```

Each row is **one Song**, making the model easier to discuss during interviews.

There is no universally correct convention. The important thing is to stay consistent throughout the design.

# 3. Relationship Analysis

Once the core entities are identified, the next step is to determine how they relate to one another.

The relationship cardinality (1:1, 1:N, M:N) should always be driven by business requirements.

---

## Playlist ↔ Song

### Initial Thought

A playlist contains many songs.

A song can belong to many playlists.

This is clearly a **Many-to-Many** relationship.

```
Playlist
      ↕
 PlaylistSong
      ↕
Song
```

### Schema

```text
PlaylistSong
-------------
playlistId (FK)
songId (FK)
position
addedAt

PK (playlistId, songId)
```

### Why a Junction Table?

Since both entities can independently exist and relate to multiple instances of each other, a junction table is required.

Notice that this table also stores relationship-specific information such as:

- position
- addedAt

These attributes belong to the relationship, not to either entity.

---

## Song ↔ Artist

### Initial Thought

One artist can perform many songs.

One song can have multiple artists.

Examples:

- Featured artists
- Collaborations
- Duets

This is also a **Many-to-Many** relationship.

```
Artist
      ↕
 SongArtist
      ↕
Song
```

### Schema

```text
SongArtist
------------
songId (FK)
artistId (FK)

PK(songId, artistId)
```

---

## Album ↔ Artist

This relationship depends entirely on the business requirements.

### Option 1 (Simple)

If every album belongs to exactly one artist,

```text
Album
------
albumId
artistId
title
...
```

This is a **One-to-Many** relationship.

One Artist → Many Albums.

---

### Option 2 (Recommended)

Suppose the interviewer says:

> We need to support compilation albums with multiple artists.

Now the relationship becomes:

```
Album
      ↕
 AlbumArtist
      ↕
Artist
```

Schema:

```text
AlbumArtist
-------------
albumId (FK)
artistId (FK)

PK(albumId, artistId)
```

---

## Interview Tip

Don't guess the relationship.

Ask the interviewer.

For example:

> "Should we support albums that contain songs from multiple artists?"

If the answer is **yes**, model it as Many-to-Many.

If the answer is **no**, a simple foreign key is sufficient.

This demonstrates that you're letting the **business requirements drive the schema**.

---

## User ↔ Playlist (Ownership)

Initially, this is a simple **One-to-Many** relationship.

One user can create many playlists.

Each playlist has exactly one owner.

```
User (1) ---------- (N) Playlist
```

Schema:

```text
Playlist
----------
playlistId
ownerUserId (FK)
name
description
createdAt
```

At this stage, no junction table is required.

---

## Interview Principle

Not every relationship needs a separate table.

Use a junction table only when the relationship is **Many-to-Many** or when the relationship itself has important attributes.

# 4. Ownership vs Collaboration

Initially, a playlist has exactly one owner.

Therefore, the relationship is:

```
User (1) -------- (N) Playlist
```

The Playlist table simply stores the owner.

```text
Playlist
----------
playlistId (PK)
ownerUserId (FK)
name
description
createdAt
updatedAt
```

---

## Requirement Change

The interviewer now changes the requirement.

> Multiple users can collaborate on a playlist.

A common mistake is to immediately convert the relationship between User and Playlist into Many-to-Many.

Instead, recognize that **ownership** and **collaboration** are two different business concepts.

Ownership answers:

> Who created this playlist?

Collaboration answers:

> Who is allowed to modify this playlist?

These should be modeled separately.

---

## Ownership

Ownership remains unchanged.

```
User (1) -------- (N) Playlist
```

Each playlist still has exactly one owner.

Therefore,

```text
ownerUserId
```

remains inside the Playlist table.

---

## Collaboration

Collaboration introduces a new Many-to-Many relationship.

```
User
      ↕
PlaylistCollaborator
      ↕
Playlist
```

Schema:

```text
PlaylistCollaborator
----------------------
playlistId (FK)
userId (FK)
permission
joinedAt

PK (playlistId, userId)
```

Possible permissions:

```text
EDITOR
VIEWER
```

Additional metadata such as:

- invitedBy
- acceptedAt
- lastModifiedBy

could also belong here because they describe the collaboration relationship.

---

## Why Not Remove ownerUserId?

Even though multiple users can edit a playlist, only one user owns it.

Ownership is a property of the Playlist itself.

Collaboration is a relationship between User and Playlist.

Keeping them separate makes the model much easier to understand and extend.

---

## Interview Tip

Whenever new requirements are introduced, don't immediately redesign existing relationships.

Instead, ask yourself:

> Is this a completely new business relationship?

If the answer is yes, add a new relationship instead of changing an existing one.

---

## Key Learning

Ownership and Collaboration are independent concepts.

Model them independently.

```
Ownership

User (1) -------- (N) Playlist


Collaboration

User
      ↕
PlaylistCollaborator
      ↕
Playlist
```

This same pattern appears in many real-world systems:

- Google Docs
- Apple Notes
- GitHub Repositories
- Shared Calendars
- Slack Channels

Whenever one user owns a resource but multiple users can access or modify it, separate **ownership** from **sharing/collaboration**.

# 5. Modeling User-Specific Features

The interviewer now introduces the following requirements:

- Users can favorite songs.
- Users have a Recently Played list.
- Users can download songs for offline listening.

The first instinct might be to add these attributes directly to the `Song` table.

For example:

```text
Song
------
songId
title
isFavorite
isDownloaded
lastPlayed
```

This is **incorrect**.

These attributes are not properties of a Song.

They are properties of the relationship between a User and a Song.

---

## Interview Principle

Before adding a field to an entity, always ask:

> Is this property true for the entity itself, or is it true only for a specific user?

If different users can have different values, it belongs in a relationship table.

---

## Favorite Songs

Consider two users.

Alice favorites:

```
Shape of You
```

Bob does not.

If `isFavorite` is stored inside the Song table,

```text
Song
------
songId
title
isFavorite
```

whose favorite is it?

The model cannot answer this question.

Instead, create a relationship table.

```text
UserFavoriteSong
------------------
userId (FK)
songId (FK)
favoritedAt

PK(userId, songId)
```

Now every user maintains their own list of favorite songs.

---

## Recently Played

The same reasoning applies.

Every user has a different listening history.

Instead of storing

```text
lastPlayed
```

inside Song,

store the entire play history.

```text
UserPlayHistory
-----------------
historyId (PK)
userId (FK)
songId (FK)
playedAt
device
```

---

### Why Not Create UserRecentlyPlayed?

During the interview I initially suggested:

```text
UserRecentlyPlayed
```

A better design is

```text
UserPlayHistory
```

Reason:

"Recently Played" is not data.

It is simply a query over the play history.

For example,

```sql
SELECT *
FROM UserPlayHistory
WHERE userId = ?
ORDER BY playedAt DESC
LIMIT 20;
```

By storing the complete history, we can derive:

- Recently Played
- Most Played
- Listening Statistics
- Weekly Recap

without changing the schema.

---

## Downloaded Songs

Downloads are also user-specific.

One user may download a song while another streams it online.

Model it separately.

```text
UserDownloadedSong
--------------------
userId (FK)
songId (FK)
downloadedAt
downloadStatus

PK(userId, songId)
```

Possible values for downloadStatus:

```text
DOWNLOADING

DOWNLOADED

FAILED

REMOVED
```

---

# Common Pattern

Notice that all three features follow exactly the same design pattern.

```
User
      ↕
Relationship Table
      ↕
Song
```

Examples:

```
User
      ↕
UserFavoriteSong
      ↕
Song
```

```
User
      ↕
UserPlayHistory
      ↕
Song
```

```
User
      ↕
UserDownloadedSong
      ↕
Song
```

---

## Interview Tip

A useful question to ask yourself is:

> If two users interact with the same entity differently, should this attribute really belong to the entity?

If the answer is **yes**, it almost always belongs in a relationship table.

This simple rule helps identify many modeling mistakes during interviews.

---

## Key Learning

There are two kinds of properties:

### Entity Properties

These describe the entity itself.

Examples:

```text
Song
------
title
duration
genre
releaseDate
```

These values are the same for every user.

---

### Relationship Properties

These describe how one entity relates to another.

Examples:

```text
favoritedAt

playedAt

downloadedAt

permission

joinedAt
```

These values depend on both entities participating in the relationship.

Recognizing this distinction is one of the most important skills in data modeling interviews.

# 6. Designing Favorites

The interviewer now changes the requirements.

Users can now favorite:

- Songs
- Albums
- Artists
- Playlists

The question becomes:

Should we create separate tables for each type of favorite, or should we create one generic `Favorite` table?

---

## Option 1 (Recommended)

Create separate tables.

```text
UserFavoriteSong
------------------
userId
songId
favoritedAt
```

```text
UserFavoriteAlbum
-------------------
userId
albumId
favoritedAt
```

```text
UserFavoriteArtist
--------------------
userId
artistId
favoritedAt
```

```text
UserFavoritePlaylist
----------------------
userId
playlistId
favoritedAt
```

---

## Why I Prefer This Approach

Each table models one business relationship.

This provides:

- Strong referential integrity
- Simpler queries
- Easy indexing
- Clear foreign key relationships
- Independent evolution of each entity

For example,

```sql
SELECT *
FROM UserFavoriteSong
WHERE userId = ?;
```

The query is simple and the database guarantees that every `songId` references an existing Song.

---

## Option 2

Create one generic Favorite table.

```text
Favorite
-----------
favoriteId
userId
entityType
entityId
favoritedAt
```

Example data:

```text
userId = 10
entityType = SONG
entityId = 25
```

or

```text
userId = 10
entityType = PLAYLIST
entityId = 42
```

---

## Advantages

- Easier to extend.

If Apple introduces new entities such as:

- Podcasts
- Radio Stations
- Concerts

the schema does not change.

Only a new `entityType` is added.

---

## Disadvantages

### 1. Loss of Referential Integrity

Suppose

```text
entityType = SONG
entityId = 25
```

How does the database know that

```text
25
```

exists inside the Song table?

Now suppose

```text
entityType = PLAYLIST
entityId = 25
```

The same value refers to an entirely different table.

A relational database cannot enforce foreign keys in this design.

---

### 2. More Complex Queries

Instead of

```sql
SELECT *
FROM UserFavoriteSong
WHERE userId = ?;
```

we now write

```sql
SELECT *
FROM Favorite
WHERE userId = ?
AND entityType = 'SONG';
```

Every query must now filter by entity type.

---

### 3. Different Metadata

Imagine future requirements.

Albums need

```text
favoriteSource
```

Artists need

```text
followReason
```

Playlists need

```text
isPinned
```

Now one generic table starts accumulating nullable columns or requires additional tables, making the model harder to maintain.

---

## My Recommendation

For this problem, I would choose **separate tables**.

The entity types are well-defined and unlikely to change frequently.

This gives us:

- Better data integrity
- Simpler queries
- Cleaner schema
- Easier maintenance

If the application frequently introduced new favorite-able entity types, I would revisit a generic design.

---

## Interview Tip

There isn't a universally correct answer.

The interviewer is evaluating whether you can discuss the tradeoffs.

A strong answer would be:

> "I would initially choose separate tables because they preserve referential integrity, keep queries simple, and allow each relationship to evolve independently. If the product later introduced many additional entity types, I would consider a generic polymorphic design, understanding that it trades stronger database constraints for greater flexibility."

Showing this reasoning is more valuable than simply choosing one design over the other.

---

## Key Learning

When evaluating two designs, compare them using common design principles:

- Simplicity
- Maintainability
- Extensibility
- Referential Integrity
- Query Complexity

Senior engineers are expected to explain **why** they chose a design, not just present one.


# 7. Requirement Change - Supporting Podcasts

The interviewer now introduces a new requirement.

> Apple Music should now support Podcasts.

Users should be able to:

- Subscribe to podcasts
- Listen to podcast episodes
- Download episodes
- See podcast episodes in Recently Played
- Podcasts can have one or more hosts

The first design decision is:

> Should Podcasts reuse the existing Album and Song entities?

or

> Should Podcasts have their own data model?

---

## Initial Thought

At first glance, the structures look similar.

```
Album
    |
 Songs
```

and

```
Podcast
      |
 Episodes
```

Both represent collections of audio content.

However, data modeling is not just about similar attributes.

It is about modeling the **business domain**.

---

## Recommended Design

Create separate entities.

```text
Podcast
Episode
Host
PodcastHost
```

---

## Why Not Reuse Album?

Albums and Podcasts represent different business concepts.

Although they both contain audio, they have different business behavior and will evolve independently.

For example,

### Album

```text
Album
------
albumId
title
releaseDate
genre
coverImage
```

---

### Podcast

```text
Podcast
---------
podcastId
title
description
language
rssFeed
publishSchedule
```

These concepts already have different metadata.

---

## Future Requirements

Imagine Product later introduces:

Podcast Episode

- transcript
- chapter markers
- explicit flag
- sponsor advertisements
- episode number
- season number

Now compare that to Song.

Songs don't have these attributes.

Trying to force both concepts into the same table would introduce many nullable columns and make the model harder to maintain.

---

## Podcast Schema

### Podcast

```text
Podcast
----------
podcastId (PK)
title
description
language
createdAt
```

---

### Episode

```text
Episode
----------
episodeId (PK)
podcastId (FK)
title
duration
episodeNumber
publishDate
audioUrl
```

Relationship:

```
Podcast (1) -------- (N) Episode
```

---

### Host

```text
Host
-------
hostId (PK)
name
bio
```

---

### PodcastHost

A podcast can have multiple hosts.

A host may host multiple podcasts.

This is a Many-to-Many relationship.

```text
PodcastHost
-------------
podcastId (FK)
hostId (FK)

PK(podcastId, hostId)
```

---

## Why Separation is Better

Keeping Podcasts separate provides:

- Better separation of concerns
- Cleaner schema
- Independent evolution
- Easier maintenance
- Simpler future enhancements

Instead of trying to fit every audio concept into one model, each business domain evolves independently.

---

## Interview Tip

One of the principles I used here was:

> Don't model based only on today's attributes.

Instead, model based on the **business concept**.

Even if two entities look similar today, ask yourself:

> Will these concepts evolve independently?

If the answer is yes, they usually deserve separate entities.

---

## Key Learning

Good data models represent **business concepts**, not just similar structures.

Although Albums and Podcasts are both collections of audio content, they have different metadata, behavior, lifecycle, and future requirements.

Keeping them separate results in a cleaner and more maintainable design.


# 8. Final Data Model

After incorporating all the requirements discussed during the interview, the final data model consists of the following entities.

---

# Core Entities

## User

```text
User
-----
userId (PK)
name
email
createdAt
```

---

## Artist

```text
Artist
-------
artistId (PK)
name
bio
```

---

## Album

```text
Album
------
albumId (PK)
title
releaseDate
coverImage
```

---

## Song

```text
Song
-----
songId (PK)
albumId (FK)
title
duration
genre
releaseDate
```

---

## Playlist

```text
Playlist
---------
playlistId (PK)
ownerUserId (FK)
name
description
createdAt
updatedAt
```

---

# Relationship Tables

## SongArtist

Supports collaborations and featured artists.

```text
SongArtist
-----------
songId (FK)
artistId (FK)

PK(songId, artistId)
```

---

## AlbumArtist

Supports compilation albums and multiple artists.

```text
AlbumArtist
------------
albumId (FK)
artistId (FK)

PK(albumId, artistId)
```

---

## PlaylistSong

Maintains song ordering inside playlists.

```text
PlaylistSong
--------------
playlistId (FK)
songId (FK)
position
addedAt

PK(playlistId, songId)
```

---

## PlaylistCollaborator

Allows multiple users to edit playlists.

```text
PlaylistCollaborator
----------------------
playlistId (FK)
userId (FK)
permission
joinedAt

PK(playlistId, userId)
```

---

# User-Specific Relationship Tables

## Favorite Songs

```text
UserFavoriteSong
------------------
userId (FK)
songId (FK)
favoritedAt

PK(userId, songId)
```

---

## Listening History

```text
UserPlayHistory
-----------------
historyId (PK)
userId (FK)
songId (FK)
playedAt
device
```

---

## Downloaded Songs

```text
UserDownloadedSong
--------------------
userId (FK)
songId (FK)
downloadedAt
downloadStatus

PK(userId, songId)
```

---

# Podcast Domain

## Podcast

```text
Podcast
---------
podcastId (PK)
title
description
language
createdAt
```

---

## Episode

```text
Episode
---------
episodeId (PK)
podcastId (FK)
title
duration
episodeNumber
publishDate
audioUrl
```

---

## Host

```text
Host
------
hostId (PK)
name
bio
```

---

## PodcastHost

```text
PodcastHost
-------------
podcastId (FK)
hostId (FK)

PK(podcastId, hostId)
```

---

# High-Level ER Diagram

```
                  Artist
                     |
              +------+------+
              |             |
        SongArtist     AlbumArtist
              |             |
             Song ------ Album
               |
               |
         PlaylistSong
               |
           Playlist
               |
         ownerUserId
               |
              User
               |
     +---------+----------+----------------+
     |                    |                |
FavoriteSong      PlayHistory      DownloadedSong
     |                    |                |
    Song                Song             Song

---------------------------------------------------

Podcast ----< Episode

Podcast ----< PodcastHost >---- Host
```

---

# Design Decisions

### Many-to-Many Relationships

- Song ↔ Artist
- Album ↔ Artist
- Playlist ↔ Song
- Playlist ↔ Collaborator
- Podcast ↔ Host

---

### One-to-Many Relationships

- User → Playlist
- Album → Song
- Podcast → Episode

---

### Relationship Tables

Created whenever:

- Both entities can relate to many instances of each other, or
- The relationship itself has important attributes.

Examples:

- position
- permission
- favoritedAt
- playedAt
- downloadedAt

---

### Ownership vs Collaboration

Ownership remains inside Playlist.

```text
ownerUserId
```

Collaboration becomes a separate relationship.

---

### User-Specific Features

Do **not** store:

```text
isFavorite
lastPlayed
isDownloaded
```

inside the Song table.

These belong to the relationship between User and Song.

---

### Separate Business Domains

Although Podcasts resemble Albums, they represent a different business concept.

Separate entities make the model easier to evolve.

---

# Senior-Level Interview Takeaways

Throughout the interview, consistently apply these principles:

1. **Clarify requirements before modeling.**
   - Ask questions about business rules instead of making assumptions.

2. **Model business concepts, not just similar structures.**
   - Albums and Podcasts are different domains despite both containing audio.

3. **Let business requirements determine cardinality.**
   - Don't assume one-to-many or many-to-many.

4. **Separate ownership from collaboration.**
   - They represent different business relationships.

5. **Distinguish entity properties from relationship properties.**
   - User-specific state belongs in relationship tables.

6. **Discuss tradeoffs openly.**
   - Explain why you choose separate tables versus a generic polymorphic design.

7. **Optimize for maintainability and future evolution.**
   - A clean, extensible schema is often better than over-generalizing early.

---

# Final Thought

A strong data modeling interview isn't about drawing the most entities—it's about demonstrating sound reasoning. Interviewers care less about whether your schema exactly matches theirs and more about whether you:

- Ask the right clarifying questions.
- Choose relationships based on business needs.
- Explain tradeoffs clearly.
- Adapt your design as new requirements emerge.

# Apple TV Data Modeling Interview

# Step 1 - Requirement Gathering

The very first thing a senior engineer should do is clarify the requirements before drawing any schema.

Never start creating tables immediately.

The interviewer is evaluating whether you understand the business domain before designing the database.

---

# Functional Requirements

Support the following:

- Movies
- TV Shows
- Seasons
- Episodes
- Search by Actor
- User Watchlist
- Favorite Movies
- Favorite TV Shows
- Watch History
- Continue Watching

---

# Out of Scope

For this interview we will ignore:

- Subscription/Billing
- DRM
- Video Streaming Infrastructure
- CDN
- Encoding Pipeline
- Recommendation Engine
- Search Index Implementation

---

# Clarifying Questions

## Q1. Can users upload their own videos?

Answer:

No.

All content is owned and managed by Apple.

Implication:

Movies and Shows are created by administrators, not users.

---

## Q2. Can users create multiple watchlists?

Answer:

No.

Each user has one personal watchlist.

Implication:

We only need one logical watchlist per user.

---

## Q3. Can watchlists be shared?

Answer:

No.

Watchlists are private.

Therefore no collaborator table is required.

---

## Q4. Can users favorite content?

Answer:

Yes.

Users can favorite:

- Movies
- TV Shows

Episodes cannot be favorited.

---

## Q5. Should we support Continue Watching?

Answer:

Yes.

The application should remember where the user left off.

This is one of the most important user-facing features.

---

## Q6. Can users search by actors?

Answer:

Yes.

An actor may appear in multiple movies and multiple TV shows.

This immediately suggests a Many-to-Many relationship.

---

# Key Takeaways

Before creating any tables we have already identified several important business rules:

- Movies and Shows are different business entities.
- Shows contain Seasons.
- Seasons contain Episodes.
- Users have one Watchlist.
- Favorites are user-specific.
- Continue Watching requires tracking playback progress.
- Actors participate in both Movies and TV Shows.

These rules will drive the database design.	

# Apple TV Data Modeling Interview

# Step 2 - Identify Core Business Entities

After gathering the requirements, the next step is to identify the core business entities.

A common mistake is to immediately create tables for every feature (Favorites, Watchlist, Continue Watching, etc.).

Instead, first identify the **main business objects**.

---

# Core Entities

From the requirements, we identify the following entities:

```
User

Movie

Show

Season

Episode

Actor
```

These represent the primary business concepts in the system.

---

# Why is User a Core Entity?

The application is personalized.

Every feature depends on the user.

Examples:

- Favorites
- Watchlist
- Continue Watching
- Watch History

Without a User, none of these features exist.

---

# Why Movie is a Separate Entity?

A Movie has its own lifecycle.

Attributes include:

```
Movie

movieId
title
description
releaseDate
duration
rating
posterUrl
genre
```

A movie exists independently of any user.

---

# Why Show is a Separate Entity?

A TV Show is fundamentally different from a Movie.

A Show contains Seasons.

Example:

```
Breaking Bad
```

The show itself has metadata:

```
showId
title
description
genre
posterUrl
releaseYear
```

A Show is **not** just a collection of Episodes.

It is its own business entity.

---

# Why Season is a Separate Entity?

Many candidates skip this entity.

That is incorrect.

A Season has its own identity.

Example:

```
Breaking Bad

Season 1

Season 2

Season 3
```

Season-specific attributes may include:

```
seasonId
showId
seasonNumber
title
releaseDate
```

Future requirements may include:

- Season artwork
- Season trailers
- Season descriptions

Therefore Season deserves its own table.

---

# Why Episode is a Separate Entity?

Episodes belong to Seasons.

Each episode has unique information.

Example:

```
episodeId
seasonId
episodeNumber
title
duration
releaseDate
description
```

Users watch episodes individually.

Continue Watching also tracks progress at the episode level.

---

# Why Actor is a Separate Entity?

Actors participate in many pieces of content.

Example:

```
Robert Downey Jr.

appears in:

Iron Man
Avengers
Sherlock Holmes
```

Similarly,

```
Breaking Bad

contains

Bryan Cranston
Aaron Paul
```

Actors clearly have their own lifecycle.

Possible attributes:

```
actorId
name
birthDate
biography
profileImage
```

---

# Should Favorites or Watchlist be Core Entities?

No.

These are **relationships between User and Content**.

They describe user interactions.

They are not business entities.

Bad design:

```
Favorite
```

Good approach:

Identify the core business entities first.

Later we model user-specific relationships separately.

---

# Should Continue Watching be a Core Entity?

No.

Continue Watching is derived from playback progress.

The underlying business concept is:

```
Watch Progress
```

Continue Watching is simply one way of viewing that data.

---

# Core Entity Diagram

```
                User

Movie     Show      Actor

             |

          Season

             |

          Episode
```

At this stage, we intentionally ignore relationships.

We only identify the important business concepts.

Relationships are modeled in the next step.

---

# Interview Tip

A senior engineer separates:

**Business Entities**

from

**User Actions**

Business Entities:

- User
- Movie
- Show
- Season
- Episode
- Actor

User Actions (modeled later):

- Favorite
- Watchlist
- Watch History
- Watch Progress
- Continue Watching

This separation leads to a cleaner and more extensible design.

---

# Key Takeaways

- Start with business entities before designing relationship tables.
- Movies, Shows, Seasons, Episodes, Actors, and Users all have independent lifecycles.
- Features such as Favorites and Watchlists are user-specific relationships, not standalone entities.
- Continue Watching is derived from playback progress and should not be modeled as a primary business entity.		


# Apple TV Data Modeling Interview

# Step 3 - Model Relationships

Now that we have identified the core business entities, the next step is to determine how they relate to each other.

A common mistake is to immediately create foreign keys without first identifying the relationship cardinality.

For every pair of entities, ask yourself:

- One-to-One?
- One-to-Many?
- Many-to-Many?

---

# Relationship 1

## Show → Season

Question:

Can one Show have multiple Seasons?

Yes.

Example:

```
Breaking Bad

Season 1
Season 2
Season 3
Season 4
Season 5
```

Can one Season belong to multiple Shows?

No.

Therefore:

```
Show (1)

↓

Season (N)
```

Database Design

```
Season

seasonId
showId (FK)
seasonNumber
title
```

The foreign key is stored in the Season table.

---

# Relationship 2

## Season → Episode

Question:

Can one Season have multiple Episodes?

Yes.

Example:

```
Season 1

Episode 1

Episode 2

Episode 3
```

Can one Episode belong to multiple Seasons?

No.

Therefore:

```
Season (1)

↓

Episode (N)
```

Database Design

```
Episode

episodeId
seasonId (FK)
episodeNumber
title
duration
```

---

# Relationship 3

## Movie ↔ Actor

Question:

Can a Movie have multiple Actors?

Yes.

Example:

```
Avengers

Robert Downey Jr.

Chris Evans

Scarlett Johansson
```

Can an Actor appear in multiple Movies?

Yes.

Example:

```
Robert Downey Jr.

Iron Man

Avengers

Sherlock Holmes
```

Therefore:

```
Movie (N)

↔

Actor (N)
```

This is a Many-to-Many relationship.

---

## Junction Table

```
MovieActor

movieId
actorId
characterName
billingOrder
```

Why a separate table?

Because a Movie can have many Actors, and an Actor can appear in many Movies.

Additionally, the relationship itself has attributes.

For example:

```
characterName

Tony Stark

billingOrder

1
```

These belong to the relationship, not to either Movie or Actor.

---

# Relationship 4

## Show ↔ Actor

The same logic applies.

Example:

```
Breaking Bad

Bryan Cranston

Aaron Paul

Dean Norris
```

Similarly,

```
Bryan Cranston

Breaking Bad

Malcolm in the Middle

Your Honor
```

Again:

```
Show (N)

↔

Actor (N)
```

Relationship table:

```
ShowActor

showId
actorId
characterName
billingOrder
```

---

# Why Not One Generic ContentActor Table?

A common proposal is:

```
ContentActor

contentType
contentId
actorId
```

While this reduces duplication, it introduces a polymorphic association.

Problems:

- Cannot enforce foreign keys.
- We lose referential integrity.
- The database cannot guarantee whether contentId refers to a Movie or a Show.
- Queries become more complicated.

---

# Separate Tables vs Generic Table

Recommended

```
MovieActor

ShowActor
```

Advantages

- Strong foreign keys.
- Better referential integrity.
- Simpler queries.
- Consistent with the rest of the model.

Tradeoff

- Slight schema duplication.

For this interview, preserving referential integrity is the better choice.

---

# Current Entity Relationship Diagram

```
             Show
               |
               | 1
               |
               N
            Season
               |
               | 1
               |
               N
            Episode


Movie  N -------- N  Actor

Show   N -------- N  Actor
```

---

# Interview Tip

Always identify the cardinality first.

Ask yourself:

1. Can one A have many B?
2. Can one B have many A?

If both answers are yes:

You need a junction table.

---

# Common Mistakes

❌ Putting actorId inside Movie

```
Movie

movieId
title
actorId
```

This assumes only one actor per movie.

Incorrect.

---

❌ Storing a comma-separated list

```
actors

"Tom Hanks, Tim Allen"
```

Violates First Normal Form (1NF).

Cannot query efficiently.

---

❌ Using a polymorphic ContentActor table without a Content abstraction

```
contentType
contentId
```

The database cannot enforce referential integrity because contentId may reference either Movie or Show.

---

# Key Takeaways

- Show → Season is One-to-Many.
- Season → Episode is One-to-Many.
- Movie ↔ Actor is Many-to-Many.
- Show ↔ Actor is Many-to-Many.
- Many-to-Many relationships require junction tables.
- Relationship tables can store additional relationship attributes such as character name and billing order.
- Prefer explicit relationship tables over polymorphic associations when strong referential integrity is important.

# Apple TV Data Modeling Interview

# Step 4 - Model User-Specific Relationships

So far we have identified the core business entities:

- User
- Movie
- Show
- Season
- Episode
- Actor

Now we model **how users interact with the content.**

This is an important distinction.

Core entities represent the business.

User-specific tables represent user actions.

---

# User Features

From the requirements, users can:

- Favorite Movies
- Favorite Shows
- Add content to Watchlist
- Watch Movies
- Watch Episodes
- Resume watching later

Notice something interesting.

All of these are relationships between:

```
User

↓

Content
```

None of them are standalone business entities.

---

# Requirement 1 - Favorites

Users can favorite:

- Movies
- TV Shows

Users cannot favorite Episodes.

---

## Option 1 - Generic Favorite Table

```
Favorite

userId
contentType
contentId
favoritedAt
```

Example:

| userId | contentType | contentId |
|---------|-------------|-----------|
|101|MOVIE|25|
|101|SHOW|7|

Advantages

- Only one table

Problems

- No foreign keys
- Cannot preserve referential integrity
- More conditional logic

---

## Option 2 - Separate Tables (Recommended)

```
UserFavoriteMovie

userId
movieId
favoritedAt
```

```
UserFavoriteShow

userId
showId
favoritedAt
```

Advantages

- Strong foreign keys
- Cleaner queries
- Better referential integrity
- Consistent with the rest of the schema

Tradeoff

Two tables instead of one.

For this interview, this is the preferred design.

---

# Why Not Add favorite = true Inside Movie?

Incorrect

```
Movie

movieId
title
favorite
```

Question:

Favorite for whom?

One movie can be favorited by millions of users.

Therefore Favorite is **not** a Movie attribute.

It is a relationship between User and Movie.

---

# Requirement 2 - Watchlist

Each user has one personal watchlist.

Users can add:

- Movies
- TV Shows

---

## Option 1

Create:

```
Watchlist
```

This sounds reasonable, but what does it actually represent?

A watchlist has no independent lifecycle.

It simply stores:

User

↓

Content

Therefore we model it as relationship tables.

---

## Recommended Design

```
UserWatchlistMovie

userId
movieId
addedAt
```

```
UserWatchlistShow

userId
showId
addedAt
```

Again,

these tables represent the relationship.

Not a business entity.

---

# Why Separate Favorites and Watchlist?

A common question:

"Couldn't we just have one UserContent table?"

Example:

```
UserContent

userId
movieId

favorite

watchlist
```

Possible?

Yes.

Recommended?

Usually no.

Reason:

Favorites and Watchlists are different business concepts.

Future requirements differ.

Favorites may include:

- notifications
- favorite ranking

Watchlist may include:

- reminder date
- priority
- expiration

Keeping them separate keeps the design cleaner.

---

# Relationship Diagram

```
                 User

          /          \

Favorite            Watchlist

   |                    |

Movie / Show      Movie / Show
```

---

# Common Mistake

Incorrect

```
Movie

favoriteCount
```

Question:

Does this tell us whether User 101 favorited it?

No.

It only stores an aggregate.

We still need:

```
UserFavoriteMovie
```

Aggregates can always be computed later.

Relationships cannot.

---

# Referential Integrity

By using

```
UserFavoriteMovie
```

the database guarantees

```
userId

↓

User
```

and

```
movieId

↓

Movie
```

Likewise,

```
UserWatchlistMovie
```

guarantees

```
movieId

↓

Movie
```

No orphan records can exist.

---

# Current User Relationship Diagram

```
                 User

         /                 \

Favorites              Watchlist

    |                       |

Movie / Show         Movie / Show
```

---

# Interview Tip

Whenever you hear:

> "A user can..."

ask yourself:

Is this:

1. A new business entity?

or

2. A relationship with an existing entity?

Most user-facing features are actually **relationship tables**, not new entities.

---

# Key Takeaways

- Favorites and Watchlists are relationships between User and Content.
- They should not be modeled as attributes of Movie or Show.
- Prefer explicit relationship tables over polymorphic tables when referential integrity is important.
- Separate business concepts even if they look structurally similar.
- Think about future extensibility rather than minimizing the number of tables.

# Apple TV Data Modeling Interview

# Step 5 - Watch Progress vs Watch History

One of the most common mistakes in media streaming interviews is treating **Watch Progress** and **Watch History** as the same thing.

They are different business concepts and should be modeled separately.

Before creating tables, ask:

> What question is this table trying to answer?

---

# Watch Progress

Watch Progress answers:

> "Where should playback resume?"

Examples:

```
Continue Watching

Avatar

Resume from

01:12:35
```

There is only **one current position** for a user watching a movie.

If the user watches another 10 minutes, we don't create another row.

We simply update the existing one.

---

# Characteristics of Watch Progress

- Represents current state
- Frequently updated
- One row per user-content combination
- Used by Continue Watching

---

# Movie Watch Progress

```
UserMovieWatchProgress

userId
movieId

lastPositionSeconds

lastWatchedAt

completed

completedAt

lastDevice
```

Primary Key

```
(userId, movieId)
```

Meaning

One user has one current progress record for one movie.

---

# Episode Watch Progress

Similarly,

```
UserEpisodeWatchProgress

userId
episodeId

lastPositionSeconds

lastWatchedAt

completed

completedAt

lastDevice
```

Primary Key

```
(userId, episodeId)
```

Again,

only one active progress record exists.

---

# Example

User watches:

```
Avatar
```

Initially

```
Position

0
```

Later

```
20 minutes
```

Database

```
UPDATE

lastPosition = 1200
```

Later

```
55 minutes
```

Database

```
UPDATE

lastPosition = 3300
```

We keep updating the same row.

---

# Why Store completed?

Question:

Couldn't we calculate

```
completed

=

lastPosition >= duration
```

Yes.

But Continue Watching is a very frequently accessed feature.

Instead of calculating completion every time,

we store

```
completed = true/false
```

Advantages

- Faster reads
- Simpler queries
- Easier filtering

Example

```sql
WHERE completed = false
```

Tradeoff

Slight denormalization.

Worth it for a read-heavy feature.

---

# Watch History

Now consider a different question.

Suppose the user asks:

> What did I watch last month?

Progress cannot answer this.

Progress only stores the latest position.

We need another concept.

---

# Watch History

History answers:

> What has the user watched over time?

Unlike Progress,

History never updates.

It only grows.

Every viewing session creates a new record.

---

# Movie Watch History

```
UserMovieWatchHistory

historyId

userId

movieId

startedAt

endedAt

watchedDuration

device
```

Every viewing session inserts a new row.

Example

```
Yesterday

Avatar

20 minutes
```

Today

```
Avatar

45 minutes
```

Tomorrow

```
Avatar

Completed
```

History contains three records.

---

# Episode Watch History

```
UserEpisodeWatchHistory

historyId

userId

episodeId

startedAt

endedAt

watchedDuration

device
```

Again,

append-only.

---

# Progress vs History

Progress

```
UPDATE
```

History

```
INSERT
```

Progress stores

Current State

History stores

Historical Events

---

# Why Not One Generic WatchHistory Table?

Possible design

```
UserWatchHistory

userId

contentType

contentId
```

Problems

- No foreign keys
- No referential integrity
- More conditional logic

Recommended

```
UserMovieWatchHistory

UserEpisodeWatchHistory
```

This keeps the schema consistent with the rest of our design.

---

# Why Separate Movie and Episode Progress?

One proposal is

```
UserWatchProgress

contentType

contentId
```

Again,

this creates a polymorphic association.

Problems

- Cannot enforce foreign keys.
- Database cannot determine whether contentId references Movie or Episode.
- Queries become more complex.

Instead we keep

```
UserMovieWatchProgress

UserEpisodeWatchProgress
```

Strong foreign keys.

Cleaner schema.

Consistent modeling.

---

# Current Relationship Diagram

```
                  User

              /          \

      Watch Progress    Watch History

        /      \          /      \

     Movie   Episode   Movie   Episode
```

---

# Interview Discussion

Interviewer:

Why do you need both Progress and History?

Good Answer:

Progress represents the user's latest playback state and is updated as the user watches content. It powers features like Continue Watching.

History represents every viewing session and is append-only. It supports features like Recently Watched, viewing analytics, and watch history.

These serve different business requirements and therefore should be modeled separately.

---

# Common Mistakes

❌ Using History for Continue Watching

Problem:

Finding the latest playback position requires scanning multiple records.

---

❌ Using Progress for Watch History

Problem:

Every update overwrites the previous viewing session.

Historical information is lost.

---

❌ Combining Movie and Episode into one polymorphic table

Problem:

Loses referential integrity.

Introduces conditional logic.

Harder to maintain.

---

# Key Takeaways

- Progress and History represent different business concepts.
- Progress stores the current playback state.
- History stores every playback session.
- Progress is updated.
- History is append-only.
- Continue Watching is powered by Progress.
- Watch History is powered by History.
- Store `completed` in Progress to optimize read-heavy queries.
- Prefer separate Movie and Episode tables over polymorphic associations to preserve referential integrity.

# Apple TV Data Modeling Interview

# Step 6 - Continue Watching (Normalized vs Denormalized Design)

Now that we have designed:

- UserMovieWatchProgress
- UserEpisodeWatchProgress

the interviewer introduces a new requirement.

---

# New Requirement

When a user opens the Apple TV home page, they should immediately see

```
Continue Watching

Avatar

Breaking Bad S2E5

Interstellar
```

The interviewer now asks:

> How would you implement this feature?

This is not only a database modeling question.

It is also a discussion about:

- Read optimization
- Write optimization
- Denormalization
- Read Models
- Event-driven architecture

---

# Option 1 - Build Continue Watching from Progress Tables

Use the existing tables.

```
UserMovieWatchProgress

userId
movieId
lastPositionSeconds
lastWatchedAt
completed
```

```
UserEpisodeWatchProgress

userId
episodeId
lastPositionSeconds
lastWatchedAt
completed
```

Query:

```sql
SELECT movieId,
       lastPositionSeconds,
       lastWatchedAt
FROM UserMovieWatchProgress
WHERE userId = ?
AND completed = false

UNION ALL

SELECT episodeId,
       lastPositionSeconds,
       lastWatchedAt
FROM UserEpisodeWatchProgress
WHERE userId = ?
AND completed = false

ORDER BY lastWatchedAt DESC
LIMIT 20;
```

---

# Why This Works

Notice what the query is doing.

For one user:

- Read movie progress
- Read episode progress
- Combine the results
- Sort by most recently watched
- Return the first 20

This is inexpensive because it only scans the current user's rows.

Most users only have a small number of active progress records.

Example

```
Movies

8

Episodes

25
```

Sorting

```
33 rows
```

is extremely cheap.

---

# Recommended Index

Create a composite index.

```
(userId,
 completed,
 lastWatchedAt)
```

Why?

The query

1. Filters by

```
userId
```

2. Filters by

```
completed = false
```

3. Sorts by

```
lastWatchedAt
```

The index supports all three operations efficiently.

---

# Advantages

- Single source of truth
- No duplicated data
- Strong consistency
- Simple writes
- Easy maintenance

This is the design I would start with.

---

# Option 2 - Denormalized ContinueWatching Table

Suppose the homepage becomes the highest traffic endpoint.

We might introduce a read-optimized table.

Example

```
ContinueWatching

userId

contentId

contentType

title

posterUrl

showTitle

seasonNumber

episodeNumber

durationSeconds

lastPositionSeconds

remainingSeconds

lastWatchedAt

completed
```

Notice that data from multiple tables has been copied.

Movie

↓

ContinueWatching

Episode

↓

ContinueWatching

This is called

**Denormalization.**

---

# Why Denormalize?

Without a read model

```
Home Page

↓

Read Progress

↓

Join Movie

↓

Join Episode

↓

Calculate Remaining Time

↓

Return
```

With a read model

```
Home Page

↓

SELECT ContinueWatching

↓

Return
```

The table is already shaped exactly like the UI.

No joins.

No UNION.

No calculations.

---

# Tradeoffs

## Normalized Design

Pros

- One source of truth
- No duplicated data
- Easy consistency
- Simpler writes

Cons

- Reads perform joins and calculations

---

## Denormalized Design

Pros

- Extremely fast reads
- Perfect for very high-QPS endpoints

Cons

- Duplicate data
- More expensive writes
- Eventual consistency

---

# Read-heavy vs Write-heavy

Understanding this tradeoff is important.

---

## Writes

A write occurs whenever playback changes.

Example

```
User watches Avatar

0 min

↓

20 min

↓

55 min

↓

Completed
```

Each update writes

```
UserMovieWatchProgress
```

---

## Reads

Every time the user opens

```
Home Page
```

Apple TV loads

```
Continue Watching
```

These are read operations.

---

# Read-heavy System

```
Reads

>>

Writes
```

Example

```
Reads

500 million/day

Writes

10 million/day
```

Continue Watching is generally a read-heavy feature.

---

# Does Read-heavy Automatically Mean Denormalization?

No.

This is a common misconception.

Always ask:

**How expensive is the read?**

Our query is

```
WHERE userId = ?
```

Each user has very few progress rows.

Therefore

the read is already inexpensive.

Creating another table would actually increase complexity.

---

# When Should ContinueWatching Be Populated?

This is a favorite interview question.

---

## Incorrect Approach

Populate it every time the homepage loads.

```
Read Progress

↓

Compute ContinueWatching

↓

Write ContinueWatching

↓

Read ContinueWatching

↓

Return
```

This defeats the purpose.

You still pay the cost of computing the data.

Now you also pay for an unnecessary write.

---

## Correct Approach

Populate the read model when the underlying data changes.

Example

```
User watches movie

↓

Update UserMovieWatchProgress

↓

Update ContinueWatching
```

Now the homepage simply performs

```sql
SELECT *
FROM ContinueWatching
WHERE userId = ?
ORDER BY lastWatchedAt DESC
LIMIT 20;
```

The homepage only reads.

It never updates the read model.

---

# Production Architecture

In practice,

this update is usually asynchronous.

```
              User watches movie

                      │

                      ▼

        Update UserMovieWatchProgress

                      │

                      ▼

          Publish ProgressUpdated Event

                      │

                      ▼

                    Kafka

                      │

                      ▼

      ContinueWatching Consumer Service

                      │

                      ▼

       Update ContinueWatching Read Model

                      │

                      ▼

               Home Page API

                      │

                      ▼

        SELECT ContinueWatching
```

Notice

The Home Page API never computes Continue Watching.

It simply reads precomputed data.

---

# Why Asynchronous?

Instead of

```
Update Progress

↓

Update ContinueWatching

↓

Return
```

we do

```
Update Progress

↓

Return Immediately

↓

Kafka Event

↓

Background Consumer

↓

Update ContinueWatching
```

Advantages

- Faster writes
- Better user experience
- Homepage remains fast
- Slight delay is acceptable

Continue Watching does not need millisecond consistency.

If it updates within a few hundred milliseconds,

the user experience is still excellent.

---

# When Would I Build a ContinueWatching Table?

I would **not** build it initially.

I would first deploy the normalized solution.

Then measure:

- Query latency
- Database load
- Home page QPS

Only if profiling shows the query becoming a bottleneck would I introduce a denormalized read model.

This avoids premature optimization.

---

# Senior Interview Answer

Interviewer:

Why didn't you create a ContinueWatching table?

Good Answer

> I would start with the normalized progress tables because the query is scoped to a single user and is already inexpensive. This keeps the design simple and avoids duplicate data. If future profiling showed that the homepage became a bottleneck due to expensive joins, richer metadata, or significantly higher read costs, I would introduce a denormalized Continue Watching read model maintained asynchronously using events. The normalized progress tables would remain the source of truth while the read model would optimize the highest-read endpoint.

---

# Key Takeaways

- Continue Watching is derived from Watch Progress.
- Start with the normalized design.
- Read-heavy does not automatically justify denormalization.
- Always evaluate the cost of the read before optimizing.
- Denormalized tables should be maintained when data changes, not when it is read.
- Event-driven architectures (Kafka) are commonly used to maintain read models.
- The normalized tables remain the source of truth.
- Read models exist only to optimize read performance.

# Apple TV Data Modeling Interview

# Step 6 - Continue Watching (Normalized vs Denormalized Design)

Now that we have designed:

- UserMovieWatchProgress
- UserEpisodeWatchProgress

the interviewer introduces a new requirement.

---

# New Requirement

When a user opens the Apple TV home page, they should immediately see

```
Continue Watching

Avatar

Breaking Bad S2E5

Interstellar
```

The interviewer now asks:

> How would you implement this feature?

This is not only a database modeling question.

It is also a discussion about:

- Read optimization
- Write optimization
- Denormalization
- Read Models
- Event-driven architecture

---

# Option 1 - Build Continue Watching from Progress Tables

Use the existing tables.

```
UserMovieWatchProgress

userId
movieId
lastPositionSeconds
lastWatchedAt
completed
```

```
UserEpisodeWatchProgress

userId
episodeId
lastPositionSeconds
lastWatchedAt
completed
```

Query:

```sql
SELECT movieId,
       lastPositionSeconds,
       lastWatchedAt
FROM UserMovieWatchProgress
WHERE userId = ?
AND completed = false

UNION ALL

SELECT episodeId,
       lastPositionSeconds,
       lastWatchedAt
FROM UserEpisodeWatchProgress
WHERE userId = ?
AND completed = false

ORDER BY lastWatchedAt DESC
LIMIT 20;
```

---

# Why This Works

Notice what the query is doing.

For one user:

- Read movie progress
- Read episode progress
- Combine the results
- Sort by most recently watched
- Return the first 20

This is inexpensive because it only scans the current user's rows.

Most users only have a small number of active progress records.

Example

```
Movies

8

Episodes

25
```

Sorting

```
33 rows
```

is extremely cheap.

---

# Recommended Index

Create a composite index.

```
(userId,
 completed,
 lastWatchedAt)
```

Why?

The query

1. Filters by

```
userId
```

2. Filters by

```
completed = false
```

3. Sorts by

```
lastWatchedAt
```

The index supports all three operations efficiently.

---

# Advantages

- Single source of truth
- No duplicated data
- Strong consistency
- Simple writes
- Easy maintenance

This is the design I would start with.

---

# Option 2 - Denormalized ContinueWatching Table

Suppose the homepage becomes the highest traffic endpoint.

We might introduce a read-optimized table.

Example

```
ContinueWatching

userId

contentId

contentType

title

posterUrl

showTitle

seasonNumber

episodeNumber

durationSeconds

lastPositionSeconds

remainingSeconds

lastWatchedAt

completed
```

Notice that data from multiple tables has been copied.

Movie

↓

ContinueWatching

Episode

↓

ContinueWatching

This is called

**Denormalization.**

---

# Why Denormalize?

Without a read model

```
Home Page

↓

Read Progress

↓

Join Movie

↓

Join Episode

↓

Calculate Remaining Time

↓

Return
```

With a read model

```
Home Page

↓

SELECT ContinueWatching

↓

Return
```

The table is already shaped exactly like the UI.

No joins.

No UNION.

No calculations.

---

# Tradeoffs

## Normalized Design

Pros

- One source of truth
- No duplicated data
- Easy consistency
- Simpler writes

Cons

- Reads perform joins and calculations

---

## Denormalized Design

Pros

- Extremely fast reads
- Perfect for very high-QPS endpoints

Cons

- Duplicate data
- More expensive writes
- Eventual consistency

---

# Read-heavy vs Write-heavy

Understanding this tradeoff is important.

---

## Writes

A write occurs whenever playback changes.

Example

```
User watches Avatar

0 min

↓

20 min

↓

55 min

↓

Completed
```

Each update writes

```
UserMovieWatchProgress
```

---

## Reads

Every time the user opens

```
Home Page
```

Apple TV loads

```
Continue Watching
```

These are read operations.

---

# Read-heavy System

```
Reads

>>

Writes
```

Example

```
Reads

500 million/day

Writes

10 million/day
```

Continue Watching is generally a read-heavy feature.

---

# Does Read-heavy Automatically Mean Denormalization?

No.

This is a common misconception.

Always ask:

**How expensive is the read?**

Our query is

```
WHERE userId = ?
```

Each user has very few progress rows.

Therefore

the read is already inexpensive.

Creating another table would actually increase complexity.

---

# When Should ContinueWatching Be Populated?

This is a favorite interview question.

---

## Incorrect Approach

Populate it every time the homepage loads.

```
Read Progress

↓

Compute ContinueWatching

↓

Write ContinueWatching

↓

Read ContinueWatching

↓

Return
```

This defeats the purpose.

You still pay the cost of computing the data.

Now you also pay for an unnecessary write.

---

## Correct Approach

Populate the read model when the underlying data changes.

Example

```
User watches movie

↓

Update UserMovieWatchProgress

↓

Update ContinueWatching
```

Now the homepage simply performs

```sql
SELECT *
FROM ContinueWatching
WHERE userId = ?
ORDER BY lastWatchedAt DESC
LIMIT 20;
```

The homepage only reads.

It never updates the read model.

---

# Production Architecture

In practice,

this update is usually asynchronous.

```
              User watches movie

                      │

                      ▼

        Update UserMovieWatchProgress

                      │

                      ▼

          Publish ProgressUpdated Event

                      │

                      ▼

                    Kafka

                      │

                      ▼

      ContinueWatching Consumer Service

                      │

                      ▼

       Update ContinueWatching Read Model

                      │

                      ▼

               Home Page API

                      │

                      ▼

        SELECT ContinueWatching
```

Notice

The Home Page API never computes Continue Watching.

It simply reads precomputed data.

---

# Why Asynchronous?

Instead of

```
Update Progress

↓

Update ContinueWatching

↓

Return
```

we do

```
Update Progress

↓

Return Immediately

↓

Kafka Event

↓

Background Consumer

↓

Update ContinueWatching
```

Advantages

- Faster writes
- Better user experience
- Homepage remains fast
- Slight delay is acceptable

Continue Watching does not need millisecond consistency.

If it updates within a few hundred milliseconds,

the user experience is still excellent.

---

# When Would I Build a ContinueWatching Table?

I would **not** build it initially.

I would first deploy the normalized solution.

Then measure:

- Query latency
- Database load
- Home page QPS

Only if profiling shows the query becoming a bottleneck would I introduce a denormalized read model.

This avoids premature optimization.

---

# Senior Interview Answer

Interviewer:

Why didn't you create a ContinueWatching table?

Good Answer

> I would start with the normalized progress tables because the query is scoped to a single user and is already inexpensive. This keeps the design simple and avoids duplicate data. If future profiling showed that the homepage became a bottleneck due to expensive joins, richer metadata, or significantly higher read costs, I would introduce a denormalized Continue Watching read model maintained asynchronously using events. The normalized progress tables would remain the source of truth while the read model would optimize the highest-read endpoint.

---

# Key Takeaways

- Continue Watching is derived from Watch Progress.
- Start with the normalized design.
- Read-heavy does not automatically justify denormalization.
- Always evaluate the cost of the read before optimizing.
- Denormalized tables should be maintained when data changes, not when it is read.
- Event-driven architectures (Kafka) are commonly used to maintain read models.
- The normalized tables remain the source of truth.
- Read models exist only to optimize read performance.

# Apple TV Data Modeling Interview

# Step 7 - Modeling Audio and Subtitle Tracks

Until now we have focused on the content itself.

However, movies and TV episodes often support multiple:

- Audio Tracks
- Subtitle Tracks

For example,

Avatar may support

Audio

- English
- Spanish
- French
- Japanese

Subtitles

- English
- Spanish
- French
- German
- Japanese

The interviewer now asks:

> How would you model this?

---

# First Observation

A Movie does not have only one audio track.

It has many.

Likewise,

an Episode may also have multiple audio tracks.

This immediately suggests a

One-to-Many

relationship.

---

# Audio Track

Movie

```
Movie

movieId

title

duration
```

Audio Tracks

```
MovieAudioTrack

audioTrackId

movieId

language

codec

channels
```

Relationship

```
Movie (1)

↓

MovieAudioTrack (N)
```

One movie can have many audio tracks.

Each audio track belongs to exactly one movie.

---

# Episode Audio Tracks

Similarly,

```
EpisodeAudioTrack

audioTrackId

episodeId

language

codec

channels
```

Relationship

```
Episode (1)

↓

EpisodeAudioTrack (N)
```

---

# What is a Codec?

A codec defines how the audio is encoded and decoded.

Examples

- AAC
- Dolby Digital
- Dolby Atmos
- MP3
- Opus

The application may choose a codec depending on

- Device capability
- Subscription tier
- Available bandwidth

---

# What are Channels?

Channels describe the speaker configuration.

Examples

```
Mono

Stereo

5.1 Surround

7.1 Surround

Dolby Atmos
```

Different devices may choose different audio tracks.

---

# Subtitle Tracks

Movies also support multiple subtitle tracks.

Example

```
MovieSubtitleTrack

subtitleTrackId

movieId

language

format

isClosedCaption
```

Relationship

```
Movie (1)

↓

MovieSubtitleTrack (N)
```

---

# Episode Subtitle Tracks

```
EpisodeSubtitleTrack

subtitleTrackId

episodeId

language

format

isClosedCaption
```

---

# Why Separate Audio and Subtitle Tables?

A common proposal is

```
MediaTrack

trackType

language

codec

format

channels
```

Question

Is this a good design?

Usually not.

Audio tracks and subtitle tracks represent different business concepts.

They have different attributes.

Audio

```
codec

channels

bitrate
```

Subtitle

```
format

isClosedCaption

encoding
```

Combining them creates many nullable columns.

Example

```
MediaTrack

codec

channels

format

isClosedCaption
```

Subtitle rows would have

```
codec = NULL

channels = NULL
```

Audio rows would have

```
format = NULL

isClosedCaption = NULL
```

This violates good modeling principles.

Separate tables are cleaner.

---

# Why Separate MovieAudioTrack and EpisodeAudioTrack?

Another proposal is

```
AudioTrack

contentType

contentId
```

Example

```
contentType

MOVIE

contentId

100
```

or

```
contentType

EPISODE

contentId

250
```

Question

Can we create a foreign key?

No.

The database cannot determine whether

contentId

references

Movie

or

Episode.

We lose referential integrity.

---

# Better Design

```
MovieAudioTrack

movieId (FK)

↓

Movie
```

```
EpisodeAudioTrack

episodeId (FK)

↓

Episode
```

The database guarantees that every referenced Movie or Episode exists.

This preserves referential integrity.

---

# Could We Reduce to Two Tables?

Yes.

If the entire application were designed around a common

```
Content
```

entity.

Example

```
Content

contentId

contentType
```

Movie

↓

Content

Episode

↓

Content

Then

```
AudioTrack

contentId (FK)

language

codec
```

This reduces duplication.

However,

our current design intentionally models

Movie

and

Episode

as separate business entities.

Introducing a Content abstraction now would require redesigning much of the schema.

Therefore,

for this interview,

keeping four tables is the cleaner and more consistent approach.

---

# User Preferences

The available audio tracks are different from the user's preferred language.

User preferences belong in their own table.

Example

```
UserPreference

userId

preferredAudioLanguage

preferredSubtitleLanguage

autoplayEnabled
```

Notice the separation of responsibilities.

AudioTrack stores

```
What is available
```

UserPreference stores

```
What the user prefers
```

The application chooses the best matching track.

---

# Playback Example

Suppose Avatar supports

Audio

- English
- Spanish
- Japanese

User Preference

```
Preferred Audio

Spanish
```

Playback flow

```
Read UserPreference

↓

Read MovieAudioTrack

↓

Find Spanish Track

↓

Play Movie
```

The database stores the data.

The application decides which track to use.

---

# Common Mistakes

❌ Store

```
audioLanguage
```

inside Movie.

A movie supports multiple languages.

---

❌ Combine Audio and Subtitle into one table.

This introduces many nullable columns.

---

❌ Use

```
contentType

contentId
```

without a Content abstraction.

This sacrifices referential integrity.

---

# Interview Follow-up

Interviewer

Why did you choose four tables instead of two?

Good Answer

> Audio tracks and subtitle tracks are different business concepts with different attributes, so they deserve separate tables. Although MovieAudioTrack and EpisodeAudioTrack have similar schemas, I intentionally kept Movies and Episodes as separate business entities throughout the design. Using separate tables preserves strong foreign keys and keeps the schema consistent. If the application had been designed around a common Content abstraction from the beginning, I would instead model AudioTrack and SubtitleTrack against Content.

---

# Key Takeaways

- A Movie or Episode can have many audio and subtitle tracks.
- Audio and Subtitle tracks are different business concepts.
- Separate tables avoid nullable columns.
- Separate Movie and Episode track tables preserve referential integrity.
- User preferences should be modeled separately from available tracks.
- The database stores available options; the application chooses the best one during playback.

# Apple TV Data Modeling Interview

# Step 8 - Final ER Diagram & Complete Schema

At this point we have modeled:

- Core business entities
- Relationships
- User-specific features
- Watch Progress
- Watch History
- Continue Watching
- Audio Tracks
- Subtitle Tracks

Now let's consolidate everything into one complete schema.

This is typically how you should conclude a data modeling interview.

---

# Core Business Entities

```
User

userId
name
email
createdAt
```

---

```
Movie

movieId
title
description
duration
releaseDate
genre
posterUrl
rating
```

---

```
Show

showId
title
description
genre
releaseDate
posterUrl
```

---

```
Season

seasonId
showId (FK)
seasonNumber
title
releaseDate
```

---

```
Episode

episodeId
seasonId (FK)
episodeNumber
title
duration
releaseDate
description
```

---

```
Actor

actorId
name
birthDate
profileImage
biography
```

---

# Relationship Tables

## Movie ↔ Actor

```
MovieActor

movieId (FK)

actorId (FK)

characterName

billingOrder
```

---

## Show ↔ Actor

```
ShowActor

showId (FK)

actorId (FK)

characterName

billingOrder
```

---

# User Relationships

## Favorite Movies

```
UserFavoriteMovie

userId (FK)

movieId (FK)

favoritedAt
```

---

## Favorite Shows

```
UserFavoriteShow

userId (FK)

showId (FK)

favoritedAt
```

---

## Movie Watchlist

```
UserWatchlistMovie

userId (FK)

movieId (FK)

addedAt
```

---

## Show Watchlist

```
UserWatchlistShow

userId (FK)

showId (FK)

addedAt
```

---

# Playback Progress

## Movie Progress

```
UserMovieWatchProgress

userId (FK)

movieId (FK)

lastPositionSeconds

lastWatchedAt

completed

completedAt

lastDevice
```

Primary Key

```
(userId, movieId)
```

---

## Episode Progress

```
UserEpisodeWatchProgress

userId (FK)

episodeId (FK)

lastPositionSeconds

lastWatchedAt

completed

completedAt

lastDevice
```

Primary Key

```
(userId, episodeId)
```

---

# Watch History

## Movie History

```
UserMovieWatchHistory

historyId

userId (FK)

movieId (FK)

startedAt

endedAt

watchedDuration

device
```

---

## Episode History

```
UserEpisodeWatchHistory

historyId

userId (FK)

episodeId (FK)

startedAt

endedAt

watchedDuration

device
```

---

# Audio Tracks

## Movie Audio

```
MovieAudioTrack

audioTrackId

movieId (FK)

language

codec

channels
```

---

## Episode Audio

```
EpisodeAudioTrack

audioTrackId

episodeId (FK)

language

codec

channels
```

---

# Subtitle Tracks

## Movie Subtitle

```
MovieSubtitleTrack

subtitleTrackId

movieId (FK)

language

format

isClosedCaption
```

---

## Episode Subtitle

```
EpisodeSubtitleTrack

subtitleTrackId

episodeId (FK)

language

format

isClosedCaption
```

---

# User Preferences

```
UserPreference

userId (FK)

preferredAudioLanguage

preferredSubtitleLanguage

autoplayEnabled
```

---

# Complete Relationship Diagram

```
                           User
                             |
      ---------------------------------------------------------
      |            |             |             |              |
      |            |             |             |              |
Favorites      Watchlist     Progress      History     Preferences
      |            |             |             |              |
      |            |             |             |              |
      |            |             |             |              |
 Movie/Show   Movie/Show   Movie/Episode Movie/Episode       |
                                                             |
                                                             |
                                                         Playback
                                                         Settings


                  Movie -----------------------+
                    |                          |
                    |                          |
                    |                          |
                    |                          |
                 MovieActor              MovieAudioTrack
                    |                          |
                    |                          |
                    |                     MovieSubtitleTrack
                    |
                  Actor
                    |
                    |
                 ShowActor
                    |
                    |
                   Show
                     |
                     |
                  Season
                     |
                     |
                  Episode
                     |
        ----------------------------
        |                          |
        |                          |
EpisodeAudioTrack        EpisodeSubtitleTrack
```

---

# Why This Design Works

Notice that every table has one clear responsibility.

Movie

Stores movie metadata.

Episode

Stores episode metadata.

Progress

Stores the user's current playback state.

History

Stores historical playback sessions.

Favorites

Stores favorite relationships.

Watchlist

Stores watchlist relationships.

AudioTrack

Stores available audio options.

UserPreference

Stores user playback preferences.

Every table answers exactly one business question.

This is a hallmark of good data modeling.

---

# Normalization Review

The schema follows Third Normal Form (3NF).

Examples

Movie

```
title

duration

posterUrl
```

exist only inside Movie.

UserFavoriteMovie

contains only

```
userId

movieId

favoritedAt
```

No unnecessary duplication exists.

The only deliberate denormalization is

```
completed
```

inside the Progress tables.

This is a conscious optimization for a read-heavy feature.

---

# Why No Generic Content Table?

A common question is

"Why didn't you create a Content table?"

Example

```
Content

contentId

contentType
```

While this reduces duplication, it introduces additional abstraction and affects almost every relationship.

For this interview, Movies and Episodes have distinct business identities.

Keeping them separate results in:

- Better referential integrity
- Simpler queries
- Easier reasoning
- Cleaner schema

If future requirements introduced many additional content types (Documentaries, Sports, Live TV, Podcasts), then introducing a common Content abstraction would become more attractive.

---

# Interview Wrap-up

A good way to conclude the interview is:

> We started by understanding the business requirements before designing the schema. We identified the core entities first, then modeled their relationships. User-specific features such as Favorites and Watchlists were represented as relationship tables rather than attributes. Playback Progress and Watch History were separated because they solve different business problems. Finally, we discussed how Continue Watching could initially be derived from normalized Progress tables and later evolve into a denormalized read model if production profiling justified it. Throughout the design, I prioritized referential integrity, simplicity, and extensibility while avoiding premature optimization.

---

# Final Takeaways

- Model business entities before user actions.
- Relationship tables represent user interactions.
- Separate current state from historical events.
- Preserve referential integrity whenever possible.
- Keep the schema normalized until profiling justifies denormalization.
- Design for today's requirements while acknowledging how the model could evolve in the future.

# Apple TV Data Modeling Interview

# Step 9 - Advanced Interview Follow-ups (Senior Level)

Once the schema is complete, interviewers usually explore your design decisions.

These questions have no single "correct" answer.

The goal is to evaluate your engineering judgment and tradeoff analysis.

---

# Question 1

Why did you choose SQL instead of NoSQL?

Good Answer

The data has strong relationships.

Examples:

- Show → Season → Episode
- Movie ↔ Actor
- User → Watch Progress
- User → Watch History

These relationships benefit from:

- Foreign keys
- Referential integrity
- ACID transactions
- Joins

A relational database is therefore a natural fit.

NoSQL may be introduced later for specialized read models or caching.

---

# Question 2

Would you use UUIDs or Auto Increment IDs?

Auto Increment

Pros

- Smaller indexes
- Faster joins
- Better cache locality

Cons

- Harder to merge data across regions.

UUID

Pros

- Globally unique
- Easier distributed generation

Cons

- Larger indexes
- Poorer index locality

Good interview answer

Internal databases often use numeric IDs for performance, while external APIs expose UUIDs or opaque identifiers.

---

# Question 3

How would you index Watch Progress?

Typical query

```sql
SELECT *
FROM UserMovieWatchProgress
WHERE userId=?
AND completed=false
ORDER BY lastWatchedAt DESC
LIMIT 20;
```

Recommended index

```
(userId, completed, lastWatchedAt)
```

Reason

Matches the filtering and ordering pattern.

---

# Question 4

Why store completed?

Could calculate

```
lastPosition >= duration
```

Instead we store

```
completed
```

Reason

Continue Watching is read frequently.

We optimize reads by storing the derived state.

This is intentional denormalization.

---

# Question 5

Should completed movies be deleted?

No.

Simply mark

```
completed=true
```

Reasons

- Resume later
- Restart movie
- Analytics
- Viewing statistics

Continue Watching simply filters

```
completed=false
```

---

# Question 6

What happens if the user watches Avatar again?

Do not insert another Progress row.

Instead

```
UPDATE

lastPosition=0

completed=false
```

Progress represents current state.

History records another viewing session.

---

# Question 7

Why separate Progress and History?

Progress

Current playback state

UPDATE

History

Playback events

INSERT

Different business requirements.

Different data models.

---

# Question 8

Why not one generic UserContent table?

Example

```
userId

contentId

favorite

watchlist

completed
```

Although this reduces tables,

it mixes unrelated business concepts.

Favorites

Watchlist

Progress

History

all evolve independently.

Separate tables provide cleaner modeling.

---

# Question 9

Why not one generic Content table?

Possible

```
Content

contentId

contentType
```

Advantages

- Less duplication

Disadvantages

- Additional abstraction
- More joins
- Existing relationships become more complicated

Since Movies and Episodes have distinct business meaning,

keeping them separate is cleaner.

---

# Question 10

How would you scale Continue Watching?

Phase 1

Normalized Progress tables.

Phase 2

Denormalized ContinueWatching table.

Phase 3

Redis cache.

Phase 4

CDN/API cache if appropriate.

Always optimize incrementally.

---

# Question 11

Why Kafka?

Progress updates generate events.

Example

```
ProgressUpdated
```

Consumers

- ContinueWatching
- Analytics
- Recommendations
- Notifications

Kafka decouples producers from consumers.

New consumers can be added without modifying playback services.

---

# Question 12

Why asynchronous?

Instead of

```
Update Progress

↓

Update ContinueWatching

↓

Return
```

Use

```
Update Progress

↓

Return

↓

Kafka

↓

Consumer

↓

Update ContinueWatching
```

Advantages

- Lower latency
- Better scalability
- Loose coupling

---

# Question 13

What if Kafka is down?

Progress remains the source of truth.

Events should be persisted or retried.

The ContinueWatching read model may become temporarily stale,

but playback itself should never fail.

Critical writes should not depend on Kafka availability.

---

# Question 14

Is eventual consistency acceptable?

Yes.

Example

User pauses at

```
1:10:30
```

Continue Watching briefly displays

```
1:10:10
```

This is acceptable.

The feature is not financially critical.

---

# Question 15

How would you shard Progress?

Shard by

```
userId
```

Reason

Nearly every query filters by

```
WHERE userId=?
```

All of a user's progress remains on the same shard.

---

# Question 16

Would you partition Watch History?

Yes.

History grows continuously.

Partition by

- Month
- Year

Benefits

- Faster queries
- Easier archival
- Better maintenance

---

# Question 17

How would you archive History?

Move old partitions to cheaper storage.

Examples

- S3
- Cold database
- Data warehouse

Keep recent history in the primary database.

---

# Question 18

How would you cache Movie metadata?

Movie information changes infrequently.

Good cache candidates

- Movie details
- Show details
- Posters
- Actor information

Redis works well here.

---

# Question 19

What should NOT be cached aggressively?

User Progress.

Because it changes frequently.

Incorrect caching may display stale playback positions.

---

# Question 20

How would you support Live TV later?

Introduce a new entity.

```
LiveChannel

channelId

name
```

Reuse

- Favorites
- Watch History
- Progress (if DVR supported)

Good schemas evolve instead of being rewritten.

---

# Question 21

Would you use optimistic locking?

Yes.

Suppose two devices update progress simultaneously.

TV

↓

50 min

Phone

↓

48 min

Optimistic locking or timestamps prevent older updates from overwriting newer progress.

---

# Question 22

Would you store playback every second?

No.

That would generate excessive writes.

Instead

- Every 30 seconds
- On Pause
- On Exit
- On App Background

---

# Question 23

How would you handle duplicate progress updates?

Updates should be idempotent.

Receiving the same progress event twice should not corrupt state.

---

# Question 24

How would recommendations use this model?

Recommendations should consume

Watch History

not

Watch Progress.

History represents completed viewing behavior.

Progress represents temporary playback state.

---

# Question 25

How do you know when to denormalize?

Never start with denormalization.

Follow this process.

```
Can normalized tables answer the query?

↓

Yes

↓

Measure latency

↓

Is it a bottleneck?

↓

No

↓

Keep normalized.

↓

Yes

↓

Introduce read model.

↓

Cache if necessary.
```

---

# Final Interview Summary

A senior engineer does not optimize every query immediately.

Instead,

they:

- Understand the business domain.
- Design a normalized schema first.
- Preserve referential integrity.
- Separate business concepts.
- Optimize only after measuring.
- Introduce denormalization when justified.
- Use event-driven architecture to maintain read models.
- Consider operational concerns such as indexing, partitioning, caching, sharding, concurrency, and eventual consistency.

---

# Senior Engineering Mindset

The interviewer is not looking for the most complex schema.

They are looking for someone who can answer:

- Why this table?
- Why not another design?
- What are the tradeoffs?
- How will this evolve at scale?

The strongest answers demonstrate thoughtful tradeoff analysis rather than memorized patterns.


# Apple Sports Data Modeling Interview (Part 1)

> Goal: Design the data model for an Apple Sports application.

---

# Interview Approach

Instead of jumping directly into tables, follow a structured approach.

```
Requirements
    ↓
Entities
    ↓
Relationships
    ↓
Cardinality
    ↓
Attributes
    ↓
Validate Against Requirements
```

---

# Step 1 - Gather Requirements

Never assume requirements.

Start by asking clarification questions.

### Questions Asked

### Q1

> Should the application support a single sport or multiple sports?

Answer

- Multiple sports
    - Soccer
    - Basketball
    - Baseball

---

### Q2

> What features are supported?

Answer

Users can

- Browse Sports
- Browse Leagues
- Browse Teams
- Browse Scheduled Matches
- Browse Completed Matches

Not supported (yet)

- Live Scores
- Favorites
- Notifications
- Player Statistics
- Fantasy Sports

---

### Q3

> Can a Team participate in multiple Leagues?

Answer

Yes.

Examples

- Manchester City
    - Premier League
    - Champions League
    - FA Cup

Therefore

League ↔ Team is Many-to-Many.

---

### Q4

> Does every Match belong to one League?

Answer

Yes.

Every Match belongs to exactly one League.

---

### Q5

> How many Teams participate in one Match?

Answer

Exactly two.

- Home Team
- Away Team

---

# Step 2 - Identify Core Entities

Only model entities required by the business.

Current requirements mention

- Sport
- League
- Team
- Match

Do NOT introduce entities that haven't been requested.

Examples

❌ Player

❌ Venue

❌ Referee

❌ Coach

These can be added later if requirements evolve.

Current entities

```
Sport

League

Team

Match
```

---

# Step 3 - Discover Relationships

Do NOT start creating tables yet.

First discover business relationships.

Current relationship diagram

```
Sport (1) ---- (N) League <------> Team
                     |
                     |
                  (1)|----(N)
                     |
                   Match
                  /     \
             Home       Away
               |          |
             Team       Team
```

Notice

- Sport → League
- League ↔ Team
- League → Match
- Match references two Teams

---

# Important Learning #1

Only model relationships explicitly required by the business.

Example

Business says

Sport has many Leagues.

Therefore

```
Sport ---- League
```

Business never says

```
Sport ---- Match
```

Do not invent relationships.

---

# Important Learning #2

Model Fundamental Relationships

Example

```
Sport
   |
League
```

This is fundamental.

But

```
Sport
   |
League
   |
Match
```

already tells us the Sport for every Match.

Therefore

```
Sport -------- Match
```

is unnecessary.

It is a derived relationship.

---

# Important Learning #3

Avoid modeling derivable relationships.

Instead ask

> Can I already reach this entity through another relationship?

Example

```
Match

↓

League

↓

Sport
```

Sport is already known.

Therefore

Sport → Match is unnecessary.

Same for

```
Team

↓

League

↓

Sport
```

No need for

Sport → Team.

---

# Step 4 - Cardinality

Don't ask

> What's the cardinality?

Instead ask business questions.

Example

Relationship

```
Sport ---- League
```

Questions

> Can one Sport have multiple Leagues?

Yes.

> Can one League belong to multiple Sports?

No.

Result

```
Sport (1) ---- (N) League
```

Repeat this for every relationship.

---

League ↔ Team

Questions

> Can one League have multiple Teams?

Yes.

> Can one Team participate in multiple Leagues?

Yes.

Result

```
League (M) <----> (N) Team
```

---

League → Match

Questions

> Can one League have multiple Matches?

Yes.

> Can one Match belong to multiple Leagues?

No.

Result

```
League (1) ---- (N) Match
```

---

Match → Team

Questions

> How many Teams participate in one Match?

Exactly two.

Question

> Can one Team play multiple Matches?

Yes.

Implementation

Match stores

- homeTeamId
- awayTeamId

---

# Step 5 - Attributes

Now define the minimum attributes.

## Sport

```
Sport
------
sportId (PK)
sportName
```

Removed

❌ numPlayers

Reason

Different formats of the same sport can have different player counts.

---

## League

```
League
-------
leagueId (PK)
leagueName
sportId (FK)
```

---

## Team

```
Team
----
teamId (PK)
teamName
```

Removed

❌ teamCaptain

Reason

Captain is a Player.

Player entity doesn't exist yet.

---

## Match

```
Match
------
matchId (PK)
leagueId (FK)
homeTeamId (FK)
awayTeamId (FK)
matchDate
matchStatus
homeScore
awayScore
```

Removed

❌ winner

Reason

Winner is derived from

- homeScore
- awayScore

Avoid storing derivable information.

---

# Many-to-Many Relationship

Since

```
League ↔ Team
```

is Many-to-Many,

we introduce a junction table.

```
LeagueTeam
------------
leagueId (PK, FK)
teamId (PK, FK)
```

Composite Primary Key

```
(leagueId, teamId)
```

---

# Why Composite Key?

Because LeagueTeam is a pure relationship.

It simply says

"This Team participates in this League."

No additional identity is needed.

Advantages

- Prevents duplicates
- Simpler schema
- Naturally represents the relationship

---

# When Would We Use a Surrogate Key?

If the relationship itself becomes a business entity.

Example

```
LeagueTeam

leagueTeamId (PK)

leagueId

teamId

joinedDate

division

ranking

approvedBy

isActive
```

Now the relationship has its own lifecycle.

A surrogate key becomes reasonable.

Still enforce

```
UNIQUE (leagueId, teamId)
```

---

# Final Schema

```
Sport
------
sportId (PK)
sportName

League
-------
leagueId (PK)
leagueName
sportId (FK)

Team
-----
teamId (PK)
teamName

LeagueTeam
----------
leagueId (PK, FK)
teamId (PK, FK)

Match
------
matchId (PK)
leagueId (FK)
homeTeamId (FK)
awayTeamId (FK)
matchDate
matchStatus
homeScore
awayScore
```

---

# Final Relationship Diagram

```
Sport (1) ---- (N) League <------> Team
                     |
                     |
                  (1)|----(N)
                     |
                   Match
                  /     \
             Home       Away
               |          |
             Team       Team
```

---

# Interview Takeaways

## 1. Don't jump into tables.

Think

Requirements

↓

Entities

↓

Relationships

↓

Cardinality

↓

Attributes

---

## 2. Model only what the requirements ask for.

Don't invent

- Player
- Venue
- Referee

until they're requested.

---

## 3. Model only fundamental relationships.

Avoid redundant relationships.

---

## 4. Derived relationships should not be modeled directly.

Example

Match

↓

League

↓

Sport

Therefore

Sport → Match is unnecessary.

---

## 5. Derived attributes should not be stored.

Example

Winner

Instead store

- homeScore
- awayScore

Winner can always be calculated.

---

## 6. Use Composite Keys for pure junction tables.

Example

LeagueTeam

Composite PK

```
(leagueId, teamId)
```

If the relationship later gains business meaning,

introduce a surrogate key.


---

# Step 6 - Validate the Model Against Requirements

A senior engineer should validate the schema against every business requirement before moving on.

This helps identify missing entities, relationships, or attributes early.

---

## Requirement 1

> Browse all Sports

Query

```sql
SELECT *
FROM Sport;
```

Supported?

✅ Yes

---

## Requirement 2

> Browse all Leagues for a Sport

Example

Show all Soccer leagues.

Query

```sql
SELECT *
FROM League
WHERE sportId = ?;
```

Supported?

✅ Yes

Relationship used

```
Sport (1) ---- (N) League
```

---

## Requirement 3

> Browse all Teams in a League

Example

Show all teams participating in the Premier League.

Query

```sql
SELECT t.*
FROM Team t
JOIN LeagueTeam lt
ON t.teamId = lt.teamId
WHERE lt.leagueId = ?;
```

Supported?

✅ Yes

Relationship used

```
League (M) <----> (N) Team
```

---

## Requirement 4

> Browse all Scheduled Matches in a League

Example

Show all upcoming Premier League matches.

Query

```sql
SELECT *
FROM Match
WHERE leagueId = ?
AND matchStatus = 'SCHEDULED';
```

Supported?

✅ Yes

Relationship used

```
League (1) ---- (N) Match
```

---

## Requirement 5

> Browse all Completed Matches in a League

Query

```sql
SELECT *
FROM Match
WHERE leagueId = ?
AND matchStatus = 'COMPLETED';
```

Supported?

✅ Yes

---

## Requirement 6

> View the Teams Playing in a Match

Example

Liverpool vs Arsenal

Query

```sql
SELECT
    homeTeamId,
    awayTeamId
FROM Match
WHERE matchId = ?;
```

Supported?

✅ Yes

Implementation

```
Match
------
homeTeamId
awayTeamId
```

---

## Requirement 7

> Determine the Winner of a Match

Current Design

```
homeScore
awayScore
```

Winner is calculated.

Example

```
Home Score = 3
Away Score = 1

Winner = Home Team
```

Supported?

✅ Yes

No need to store a Winner column.

---

# Validation Summary

| Requirement | Supported | Notes |
|-------------|-----------|-------|
| Browse Sports | ✅ | Sport table |
| Browse Leagues for a Sport | ✅ | League.sportId |
| Browse Teams in a League | ✅ | LeagueTeam junction table |
| Browse Scheduled Matches | ✅ | Match table |
| Browse Completed Matches | ✅ | MatchStatus |
| View Match Participants | ✅ | homeTeamId / awayTeamId |
| Determine Winner | ✅ | Derived from scores |

---

# Why Validate?

Before adding new features, verify that every current requirement can be satisfied.

This helps identify:

- Missing entities
- Missing relationships
- Missing attributes
- Redundant data
- Incorrect normalization

Only after the current requirements are fully satisfied should the schema evolve to support new business features.

---

# Step 7 - Evolving the Model

One of the biggest mistakes candidates make is trying to design the final schema upfront.

Instead, evolve the schema only when new requirements justify it.

Example progression

```
Version 1

Sport
League
Team
Match

↓

Users can follow Teams

↓

User
Follow

↓

Users can view Match Timeline

↓

MatchEvent

↓

Show who scored

↓

Player
```

Every new entity should be introduced because the business requires it.

---

# Live Score Feature

## New Requirement

> Users should be able to see live scores.

Never jump into the schema.

Start with clarification questions.

---

### Clarification Question

> Are scores simply updated as they change, or do we need a play-by-play timeline?

Answer

The application should display

- Current score
- Complete timeline of events
- Historical events after the match ends

Example

```
12' Goal - Liverpool

28' Yellow Card - Arsenal

45' Goal - Arsenal

61' Red Card - Liverpool

72' Goal - Liverpool

90' Match Finished
```

---

# New Entity

Because the business wants a timeline,

a new entity naturally emerges.

```
Match (1) -------- (N) MatchEvent
```

---

# Initial MatchEvent Design

```
MatchEvent
----------
eventId (PK)
matchId (FK)
eventType
eventTime
eventDescription
```

Example

| eventType | eventDescription |
|------------|------------------|
| GOAL | Salah scores |
| YELLOW_CARD | Rice booked |
| RED_CARD | Van Dijk sent off |
| SUBSTITUTION | Diaz replaces Gakpo |

---

# Important Design Discussion

Question

> Should MatchEvent store only a description?

Answer

No.

If everything is stored inside eventDescription,

queries become difficult.

For example

```
Show all Goals

Count all Yellow Cards

Show all Substitutions
```

Instead,

store

```
eventType
```

and use

```
eventDescription
```

only for display.

---

# Requirement Evolution

Next clarification question

> Does every Match Event belong to a Team?

Answer

Not always.

Examples

Belongs to Team

- Goal
- Corner
- Yellow Card
- Red Card
- Substitution

No Team

- Match Started
- Half Time
- Full Time
- Rain Delay

---

Next clarification question

> Should we know which Player caused the event?

Answer

Yes.

Examples

- Goal
- Yellow Card
- Red Card
- Substitution

Need Player.

---

# New Entity

Business requirement introduces

```
Player
```

Relationship

```
Team (1) -------- (N) Player
```

One Team has many Players.

One Player belongs to one Team.

---

# Player Discussion

Question

> Where should the foreign key be stored?

Answer

```
Player
------
playerId
teamId (FK)
```

NOT

```
Team
-----
playerId
```

Reason

In a One-to-Many relationship,

the Foreign Key always goes on the Many side.

---

# Important Rule

## One-to-Many

```
A (1) -------- (N) B
```

Foreign Key goes inside

```
B
```

Examples

```
Sport (1) ------ (N) League

League stores sportId
```

```
League (1) ------ (N) Match

Match stores leagueId
```

```
Team (1) ------ (N) Player

Player stores teamId
```

```
Match (1) ------ (N) MatchEvent

MatchEvent stores matchId
```

---

## Many-to-Many

Neither side stores the Foreign Key.

Create a Junction Table.

Example

```
League <------> Team
```

becomes

```
LeagueTeam

leagueId
teamId
```

Composite Primary Key

```
(leagueId, teamId)
```

---

## One-to-One

Usually,

the Foreign Key is stored in the dependent (optional) entity.

Example

```
User (1) -------- (1) UserProfile
```

```
User
----
userId

UserProfile
-----------
userId (PK, FK)
bio
photo
address
```

---

# Important Rule

## Derived Relationships

Never store information that can already be derived.

Example

```
Sport
   |
League
   |
Match
```

Do NOT store

```
Match

sportId ❌
```

because

```
leagueId

↓

League

↓

sportId
```

already gives the Sport.

---

# Another Derived Relationship Discussion

Question

Should MatchEvent store

```
teamId
playerId
```

or only

```
playerId
```

Observation

```
MatchEvent

↓

playerId

↓

Player

↓

teamId

↓

Team
```

Knowing the Player already tells us the Team.

Therefore,

initially

```
MatchEvent
----------
eventId
matchId
playerId (Nullable)
eventType
eventTime
eventDescription
```

is sufficient.

---

# Trade-off Discussion

However,

future requirements may introduce Team-level events.

Examples

- Corner
- Possession Change
- Team Timeout

These events belong to a Team,

but not to a Player.

At that point,

adding

```
teamId (Nullable)
```

becomes reasonable.

This is a conscious trade-off between

- strict normalization
- supporting business semantics

Always discuss the trade-off with the interviewer.

---

# Foreign Key Placement Cheat Sheet

## One-to-Many

```
1 : N

↓

Foreign Key goes on N
```

---

## Many-to-Many

```
M : N

↓

Create Junction Table
```

---

## One-to-One

```
1 : 1

↓

Foreign Key goes on dependent entity
```

---

## Derived Relationship

```
Already reachable?

↓

Do NOT store it.
```

---

# Interview Takeaway

Never memorize where Foreign Keys belong.

Instead,

identify

1. The relationship
2. The cardinality

Then apply

```
1 : N

↓

FK on Many side
```

This single rule correctly models the vast majority of relational database designs.

---

# Step 8 - One-to-One (1:1) Relationships

One-to-One relationships are common in system design interviews.

Examples

- User → UserProfile
- Employee → Payroll
- Customer → CustomerPreferences
- Player → PlayerProfile

---

# New Requirement

Apple wants every Player to have an optional profile.

The profile contains

- Bio
- Height
- Weight
- Preferred Foot
- Instagram Handle

Not every player has completed their profile.

---

# Step 1 - Ask Clarifying Questions

Never assume it is a 1:1 relationship.

Ask business questions first.

### Question 1

> Can a Player have more than one Profile?

Answer

No.

---

### Question 2

> Is a Profile mandatory for every Player?

Answer

No.

A player may never create a profile.

---

### Question 3

> Can a Profile exist without a Player?

Answer

No.

A profile cannot exist independently.

---

# Relationship

From the answers we conclude

```
Player (1) -------- (0..1) PlayerProfile
```

Meaning

- Every Profile belongs to exactly one Player.
- A Player may or may not have a Profile.

---

# First Design Option

Create a separate Profile ID.

```
Player
------
playerId (PK)
name
teamId

PlayerProfile
-------------
profileId (PK)
playerId (FK)
bio
height
weight
instagramHandle
```

This is a perfectly valid design.

However,

to enforce one profile per player,

we must also add

```
UNIQUE(playerId)
```

Otherwise this would be allowed

| profileId | playerId |
|-----------|----------|
|101|1|
|102|1|

which violates the business rule.

---

# Better Design

Instead of creating a separate Profile ID,

reuse the Player's Primary Key.

```
Player
------
playerId (PK)
name
teamId

PlayerProfile
-------------
playerId (PK, FK)
bio
height
weight
instagramHandle
```

Notice

There is no

```
profileId
```

The

```
playerId
```

is simultaneously

- Primary Key
- Foreign Key

---

# Why is this Better?

Because a Profile has no independent identity.

Ask yourself

> What identifies this profile?

Answer

The Player.

If someone asks

> "Show me Player 25's Profile"

there is only one possible Profile.

The identity is already

```
playerId = 25
```

Creating

```
profileId
```

adds another identifier that has no business meaning.

---

# Automatic One-to-One Enforcement

Suppose

```
PlayerProfile

playerId (PK)
```

Since Primary Keys are unique,

this is impossible

| playerId | bio |
|----------|-----|
|1|Bio A|
|1|Bio B|

The database rejects it automatically.

No additional UNIQUE constraint is required.

---

# Comparison

## Option 1

```
profileId (PK)
playerId (FK)
```

Requires

```
UNIQUE(playerId)
```

to guarantee One-to-One.

---

## Option 2

```
playerId (PK, FK)
```

No extra constraint needed.

The Primary Key itself guarantees

One Player

↓

One Profile

---

# Why Not Store profileId in Player?

Example

```
Player
------
playerId
profileId
```

Technically possible.

However,

it models the dependency backwards.

The business says

```
Profile depends on Player
```

NOT

```
Player depends on Profile
```

The child should reference the parent,

not the other way around.

---

# Compare with MatchEvent

```
Match (1) -------- (N) MatchEvent
```

```
MatchEvent
----------
eventId (PK)
matchId (FK)
```

Why does MatchEvent need its own ID?

Because one Match has many Events.

```
Match

↓

Goal

↓

Yellow Card

↓

Corner

↓

Goal
```

Each Event has its own identity.

---

# Compare with PlayerProfile

```
Player

↓

One Profile
```

There is never more than one Profile.

The Player already uniquely identifies it.

Therefore

```
playerId
```

is sufficient.

---

# Shared Primary Key Pattern

This design is called a

**Shared Primary Key One-to-One Relationship**

```
Player
------
playerId (PK)

PlayerProfile
-------------
playerId (PK, FK)
```

The child shares the parent's Primary Key.

---

# When Should You Use This Pattern?

Use a shared Primary Key when

- The child cannot exist without the parent.
- There is exactly one child per parent.
- The child has no separate business identity.

Examples

```
User
↓

UserProfile
```

```
Employee
↓

Payroll
```

```
Person
↓

PassportDetails
```

```
Player
↓

PlayerProfile
```

---

# When Should You Use a Separate Primary Key?

Use a separate ID when

- The child has its own lifecycle.
- The child may later have relationships of its own.
- There can eventually be multiple child records.
- The child has an independent business identity.

Examples

```
Match

↓

MatchEvent
```

```
Order

↓

OrderItem
```

```
Blog

↓

Comment
```

Each child needs its own identity.

---

# Interview Takeaway

When designing a One-to-One relationship,

don't immediately create another surrogate key.

Ask yourself

1. Can the child exist without the parent?
2. Does the child have its own business identity?
3. Is there exactly one child per parent?

If the answers are

- No
- No
- Yes

then a **Shared Primary Key (PK = FK)** is usually the cleanest and most normalized design.

---

# Mental Model

```
1 : N

↓

FK goes on the Many side.
```

```
M : N

↓

Create a Junction Table.
```

```
1 : 1

↓

If the child depends completely on the parent,

consider using the parent's Primary Key
as both the PK and FK.
```

This is a common pattern in enterprise database design and frequently discussed in senior backend interviews.

# Apple Data Modeling Interview Notes – Weak Entities, Composite Keys & Schema Evolution

---

# Key Principle

> **Never choose the Primary Key first.**
>
> First ask:
>
> **"What does one row represent?"**

Once the business meaning is clear, the Primary Key usually becomes obvious.

---

# Scenario 1 – Player Statistics

## Requirement

Store the following for every player:

- Goals
- Assists
- Yellow Cards
- Red Cards
- Minutes Played

---

# Step 1 – Clarifying Questions

Before creating tables, ask:

1. Are these lifetime statistics?
2. Are they per league?
3. Are they per season?
4. Are they per match?

---

## Interviewer Response

Statistics are **per match**.

---

# Step 2 – Identify Relationships

Questions to ask:

Can a player have statistics without playing a match?

**No.**

Can a match have statistics for multiple players?

**Yes.**

Relationship:

```text
Player (1) -------- (N) PlayerStat

Match  (1) -------- (N) PlayerStat
```

Visual:

```text
        Player
           |
           |
      PlayerStat
           |
           |
         Match
```

---

# Step 3 – Determine Identity

Ask yourself:

Can playerId uniquely identify a row?

No.

One player plays many matches.

Example:

| Player | Match | Goals |
|---------|-------|-------|
| Salah | Match 1 | 2 |
| Salah | Match 2 | 1 |
| Salah | Match 3 | 0 |

---

Can matchId uniquely identify a row?

No.

One match has many players.

---

Therefore:

The business uniquely identifies one statistics record by:

```text
(playerId, matchId)
```

---

# PlayerStat Table

```text
PlayerStat
-----------
playerId      (PK, FK)
matchId       (PK, FK)

goals
assists
yellowCards
redCards
minutesPlayed
```

Notice:

There is **no statId**.

---

# Why No statId?

Suppose we create:

```text
PlayerStat
-----------
statId
playerId
matchId
```

Interviewer may ask:

> What does statId = 742 represent?

Nothing meaningful.

The business identifies the record using:

- Player
- Match

Therefore the natural key already exists.

---

# Weak Entity

PlayerStat is a **Weak Entity** because it depends on:

- Player
- Match

Without either one, the statistics have no meaning.

---

# Rule

Ask:

> Can this entity exist independently?

If the answer is **No**, it is usually a weak entity.

---

# Scenario 2 – Introducing Seasons

## New Requirement

Store statistics by season.

---

# First Clarification

Never assume what "Season" means.

Ask:

> When you say season, do you mean a sports season, league, or calendar year?

---

## Interviewer Response

Season means:

Examples:

- Premier League 2025–26
- Champions League 2025–26
- La Liga 2025–26

---

# Next Clarification

Can one League have multiple Seasons?

Interviewer:

Yes.

Example:

```text
Premier League
      |
      +---- 2023-24
      |
      +---- 2024-25
      |
      +---- 2025-26
```

Relationship:

```text
League (1) -------- (N) Season
```

---

# Next Clarification

Does every Match belong to a Season?

Answer:

Yes.

Therefore:

Old Model:

```text
Sport
   |
League
   |
Match
```

New Model:

```text
Sport
   |
League
   |
Season
   |
Match
```

---

# Updated Tables

## Season

```text
Season
-------
seasonId
leagueId (FK)

seasonName
startDate
endDate
```

---

## Match

```text
Match
------
matchId
seasonId (FK)

homeTeamId
awayTeamId
matchDate
status
```

Notice:

The Match no longer directly references League.

League is derived through Season.

---

# Common Mistake

Many candidates immediately change PlayerStat to:

```text
(playerId, seasonId)
```

This is **incorrect** if statistics are still **per match**.

Example:

Season = 2025-26

Salah plays:

- Match 1
- Match 2
- Match 3

All belong to the same season.

Using:

```text
(playerId, seasonId)
```

would produce duplicate primary keys.

Example:

| playerId | seasonId |
|----------|----------|
|25|10|
|25|10|
|25|10|

Impossible.

---

# Correct Design

PlayerStat remains:

```text
(playerId, matchId)
```

because statistics are still recorded **per match**.

Season is already derivable.

```text
PlayerStat
     |
matchId
     |
Match
     |
seasonId
```

Never duplicate derivable information.

---

# If Requirement Changes

Suppose interviewer now says:

Store **aggregate statistics for the entire season**.

Example:

Salah

Premier League

2025-26

Goals = 27

Assists = 14

Minutes = 3020

Now each row represents:

> One Player in One Season.

Primary Key becomes:

```text
(playerId, seasonId)
```

---

# New Entity

```text
PlayerSeasonStat
----------------
playerId   (PK, FK)
seasonId   (PK, FK)

goals
assists
minutesPlayed
yellowCards
redCards
```

Notice:

This is **not** the same entity.

The business meaning changed.

Therefore the entity changed.

---

# More Examples

## Per Match

One row represents:

One Player in One Match.

```text
PlayerMatchStat

PK = (playerId, matchId)
```

---

## Per Season

One row represents:

One Player in One Season.

```text
PlayerSeasonStat

PK = (playerId, seasonId)
```

---

## Per League

One row represents:

One Player in One League.

```text
PlayerLeagueStat

PK = (playerId, leagueId)
```

---

## Career Statistics

One row represents:

One Player.

```text
PlayerCareerStat

PK = playerId
```

---

# Biggest Interview Lesson

Never ask:

> What should the Primary Key be?

Instead ask:

> **What does one row represent?**

Once that answer is clear, the Primary Key almost always becomes obvious.

---

# Interview Takeaways

## ✔ Ask clarification questions before modeling.

Never assume business meaning.

---

## ✔ Discover relationships before creating tables.

Business relationships drive the schema.

---

## ✔ Natural Keys first.

If the business already uniquely identifies a record, prefer using that natural key.

Example:

```text
(playerId, matchId)
```

instead of introducing an unnecessary surrogate key.

---

## ✔ Weak Entities depend on parent entities.

Examples:

- PlayerStat
- MatchEvent
- PlayerProfile (using Shared Primary Key)

---

## ✔ The Primary Key follows the business meaning.

Examples:

Per Match

```text
(playerId, matchId)
```

Per Season

```text
(playerId, seasonId)
```

Per League

```text
(playerId, leagueId)
```

Career

```text
playerId
```

---

## Golden Rule

> **Never design the key first.**
>
> First ask:
>
> **"What does one row represent?"**
>
> The Primary Key is a consequence of that answer, not the starting point.


# Apple Data Modeling Interview Notes – Lookup Tables, Translation Tables, Normalization & Denormalization

---

# Topic 1 – Lookup Tables vs ENUM vs String

## Requirement

Apple wants to support Match Statuses.

Examples:

- Scheduled
- Live
- Completed
- Cancelled
- Postponed

---

## Option 1 – Store as String

```text
Match
------
matchId
...
status VARCHAR
```

### Advantages

- Very simple
- No joins

### Disadvantages

Possible inconsistent values:

```text
Completed
completed
COMPLETED
Done
Finished
```

No database validation.

Not recommended.

---

## Option 2 – Store as ENUM

```text
status ENUM(
    Scheduled,
    Live,
    Completed,
    Cancelled
)
```

### Advantages

- Database validates values.
- Faster than joins.
- Good for truly fixed values.

### Disadvantages

Suppose Apple adds:

- Suspended
- Rain Delay
- Abandoned

The schema must change.

Requires:

```sql
ALTER TABLE ...
```

Schema changes become deployment changes.

---

## Option 3 – Lookup Table (Recommended)

```text
MatchStatus
------------
statusId (PK)
statusName
```

```text
Match
------
matchId
statusId (FK)
```

---

### Advantages

Adding a new status requires only:

```sql
INSERT INTO MatchStatus
VALUES (6,'Suspended');
```

No schema change.

No application redeployment.

---

### Future Extensibility

Later Apple may add:

```text
MatchStatus
------------
statusId
statusName
displayOrder
displayColor
isTerminal
```

Example:

| Status | isTerminal |
|---------|------------|
| Scheduled | No |
| Live | No |
| Completed | Yes |
| Cancelled | Yes |
| Postponed | No |

This metadata is much easier to manage than hardcoding it.

---

# Interview Answer

> I would model Match Status as a lookup table because statuses are business data rather than application logic. This allows adding new statuses without schema changes and supports future metadata like colors, display order, or terminal state.

---

# Topic 2 – Supporting Multiple Languages

## New Requirement

Support:

- English
- Spanish
- French
- Japanese

Future:

- German
- Hindi
- Italian

---

## Option 1 – Multiple Columns

```text
MatchStatus
------------
statusId
englishName
spanishName
frenchName
japaneseName
```

### Problem

Adding German requires:

```sql
ALTER TABLE MatchStatus
ADD germanName;
```

Every new language changes the schema.

Not scalable.

---

## Option 2 – Separate Table Per Language

```text
EnglishStatus

SpanishStatus

FrenchStatus

JapaneseStatus
```

Problems:

- New table for every language.
- New repositories.
- New APIs.
- New migrations.
- Difficult maintenance.

Not recommended.

---

## Option 3 – Translation Table (Recommended)

```text
MatchStatus
------------
statusId
statusCode
```

Notice:

Store a stable language-independent code.

Example:

| statusId | statusCode |
|----------|------------|
|1|SCHEDULED|
|2|LIVE|
|3|COMPLETED|

---

Translation table:

```text
MatchStatusTranslation
-----------------------
statusId      (PK, FK)
languageCode  (PK)

displayName
```

Example:

| statusId | languageCode | displayName |
|----------|--------------|-------------|
|1|en|Scheduled|
|1|es|Programado|
|1|fr|Planifié|
|1|ja|予定|

---

Relationship

```text
Match
   |
   |
MatchStatus
      |
      |
MatchStatusTranslation
```

---

Primary Key

One row represents:

> One Status in One Language.

Therefore:

```text
(statusId, languageCode)
```

---

Adding German

Simply insert:

| statusId | languageCode | displayName |
|----------|--------------|-------------|
|1|de|Geplant|

No schema change.

---

# Interview Principle

Ask yourself:

> Is this business concept data or schema?

If new values should not require changing the database structure, model them as rows rather than new columns or tables.

---

# Examples

| Requirement | Correct Modeling |
|-------------|------------------|
| New Team | New Row |
| New League | New Row |
| New Language | New Row |
| New Match Status | New Row |
| New Player | New Row |
| New Season | New Row |

If adding another instance requires a schema change, the design is usually not normalized.

---

# Topic 3 – Normalization vs Denormalization

## Existing Normalized Model

```text
Sport
   |
League
   |
Season
   |
Match
   |
PlayerStat
```

```text
Player
   |
PlayerStat
```

---

## Requirement

Display Match Details:

```text
Liverpool vs Arsenal

Premier League 2025-26

Status: Live

Players

Salah
Goals: 2

Diaz
Goals: 1
```

---

## Tables Required

Typical joins:

- Match
- Team (Home)
- Team (Away)
- Season
- League
- MatchStatus
- PlayerStat
- Player

Approximately 7–8 tables.

---

# Interview Question

Millions of users are opening this page.

Should we continue performing all these joins?

Answer:

No.

Create denormalized read models.

---

# Denormalized Match Summary

```text
MatchSummary
-------------
matchId (PK)

homeTeamName
awayTeamName

leagueName
seasonName

status

homeScore
awayScore

lastUpdated
```

Now:

```sql
SELECT *
FROM MatchSummary
WHERE matchId = ?
```

One table.

No joins.

---

# Player Read Model

```text
PlayerMatchSummary
-------------------
matchId
playerId

playerName

goals
assists
minutesPlayed
yellowCards
```

Instead of joining:

```text
Player
      \
       \
    PlayerStat
```

every request,

the application simply reads:

- MatchSummary
- PlayerMatchSummary

---

# Read Architecture

Write Path

```text
Normalized Tables

Player
Match
Season
League
PlayerStat
```

Read Path

```text
Denormalized Read Models

MatchSummary

PlayerMatchSummary
```

---

# Source of Truth

Always remember:

```text
Normalized Tables
        ↓
Source of Truth

Denormalized Tables
        ↓
Optimized Read Models
```

Never treat the denormalized tables as the primary source of data.

---

# Keeping Read Models Updated

## Option 1 – Synchronous Transaction

Suitable when:

Strong consistency is required.

Example:

```text
Goal Scored

↓

BEGIN TRANSACTION

Update PlayerStat

Update MatchSummary

Update PlayerMatchSummary

COMMIT
```

### Advantages

- No stale reads.
- Users always see latest data.
- Simple consistency model.

### Disadvantages

- Longer transactions.
- Higher write latency.
- Every write touches multiple tables.
- Less scalable as more read models are added.

---

## Option 2 – Event-Driven Updates (Recommended when Eventual Consistency is Acceptable)

Example:

```text
Goal Scored

↓

Update PlayerStat

↓

Publish Kafka Event

↓

Consumer

↓

Update MatchSummary

↓

Update PlayerMatchSummary
```

### Advantages

- Fast writes.
- Services are loosely coupled.
- Easy to add more consumers.
- Highly scalable.

### Trade-off

Summary tables may briefly become stale.

---

# Kafka Failure Scenario

Suppose:

```text
Goal Scored

↓

PlayerStat Updated

↓

Kafka is temporarily unavailable
```

Result:

The normalized tables are still correct.

The summary tables may be stale temporarily.

Once Kafka recovers, events are retried or replayed and the read models catch up.

This is why the normalized model remains the source of truth.

---

# Strong Consistency vs Eventual Consistency

## Strong Consistency

Use synchronous transactional updates.

Example:

Financial systems.

Payment processing.

Inventory deduction.

---

## Eventual Consistency

Use asynchronous event-driven updates.

Example:

Sports scores.

News feeds.

Analytics dashboards.

Leaderboards.

---

# Interview Rule

Never choose an architecture first.

Ask:

> Can the business tolerate stale reads?

If YES

→ Event-driven denormalized read models.

If NO

→ Update normalized and denormalized tables in the same transaction.

---

# Senior Engineering Takeaways

## ✔ Normalize first.

The normalized schema should always be the source of truth.

---

## ✔ Denormalize only for performance.

Create read models to eliminate expensive joins.

---

## ✔ Consistency requirements drive architecture.

- Strong consistency → Transactional updates
- Eventual consistency → Kafka/Event-driven updates

---

## ✔ Business data should not require schema changes.

Examples:

- Languages
- Match Statuses
- Teams
- Seasons

These should become rows, not columns.

---

# Golden Rules

### Rule 1

Normalize for correctness.

Denormalize for performance.

---

### Rule 2

The normalized model is the source of truth.

Read models are disposable and can always be rebuilt.

---

### Rule 3

Choose synchronization based on business requirements.

- Immediate correctness → Transactions
- High scalability with acceptable delay → Events

---

### Rule 4

Always ask:

> Can the business tolerate eventual consistency?

The answer determines whether synchronous or asynchronous updates are appropriate.



# Apple Data Modeling Interview Notes (Part 3)
## Ticket Booking System | Relationship Entities | Composite Keys | Reservation Modeling

---

# Problem Statement

Design the data model for a sports application where:

- Users can browse matches.
- Users can purchase tickets.
- A purchase can contain multiple tickets.
- Each ticket is assigned a seat.
- One purchase is for exactly one match.
- Seats are assigned.
- Payment is handled by another service.
- No cancellations or refunds initially.

---

# Step 1: Clarifying Questions

Before identifying entities, ask questions.

### Can a user purchase multiple tickets?

**Yes.**

---

### Can one purchase contain tickets for multiple matches?

**No.**

One purchase belongs to exactly one match.

---

### Are seats assigned?

**Yes.**

---

### Can users buy tickets for friends?

**Yes.**

Attendees are not modeled yet.

---

### Is payment part of this system?

**No.**

Payment is handled externally.

---

# Step 2: Identify Business Entities

Do not think about tables.

Think about business concepts.

Entities:

```
User

Match

Purchase

Ticket

Seat
```

---

# Important Discovery

Initially it looks like:

```
Ticket
   |
 Seat
```

But this is incorrect.

Why?

A physical seat can be sold for many different matches.

Example:

```
Seat A-12-18

↓

Liverpool vs Arsenal

↓

Ticket #1

----------------------

Seat A-12-18

↓

Liverpool vs Chelsea

↓

Ticket #2
```

The physical seat remains the same.

Its availability changes per match.

This introduces a new business entity.

---

# Relationship Entity

```
Match
   |
MatchSeat
   |
Seat
```

Ticket now reserves a MatchSeat.

```
Ticket
   |
MatchSeat
```

---

# Why MatchSeat?

Whenever a relationship has its own attributes, it should become an entity.

Examples:

| Relationship | Entity |
|-------------|--------|
| Student ↔ Course | Enrollment |
| User ↔ Team | Membership |
| Player ↔ Match | PlayerStat |
| Match ↔ Seat | MatchSeat |
| User ↔ Role | UserRole |

---

# Step 3: Relationships

## User → Purchase

One user can make many purchases.

```
User (1)
    |
    |
Purchase (N)
```

---

## Match → Purchase

One purchase belongs to one match.

One match can have many purchases.

```
Match (1)
     |
     |
Purchase (N)
```

---

## Purchase → Ticket

One purchase contains multiple tickets.

```
Purchase (1)
       |
       |
Ticket (N)
```

---

## Match → MatchSeat

One match has many match seats.

```
Match (1)
      |
      |
MatchSeat (N)
```

---

## Seat → MatchSeat

One physical seat appears in many matches.

```
Seat (1)
     |
     |
MatchSeat (N)
```

---

## Ticket → MatchSeat

Each ticket reserves exactly one MatchSeat.

```
Ticket
   |
MatchSeat
```

---

# Step 4: Attributes

## User

```text
User
---------
userId (PK)

name

email
```

---

## Purchase

```text
Purchase
------------
purchaseId (PK)

userId (FK)

matchId (FK)

purchaseTime

status

totalAmount
```

Why?

A purchase is a business transaction.

---

## Ticket

```text
Ticket
-----------
ticketId (PK)

purchaseId (FK)

matchId (FK)

seatId (FK)
```

Since MatchSeat has a composite key `(matchId, seatId)`, Ticket references both.

(Alternative design: introduce a surrogate `matchSeatId`.)

---

## Seat

Represents a physical seat.

```text
Seat
---------
seatId (PK)

section

row

seatNumber
```

Notice:

No price.

No availability.

These depend on the match.

---

## MatchSeat

This is where the relationship-specific information lives.

```text
MatchSeat
----------------
matchId (PK, FK)

seatId (PK, FK)

price

status
```

Example status:

```
AVAILABLE

RESERVED

SOLD
```

---

# Why Price Belongs Here

Suppose:

```
Seat A-12-18
```

Costs:

- $150 today

- $300 for the finals

The physical seat didn't change.

Only its price for a particular match changed.

Therefore:

```
Price belongs to MatchSeat.
```

---

# Reservation Problem

Suppose a customer clicks:

```
Buy Now
```

Immediately changing:

```
status = RESERVED
```

But then closes the browser.

Without additional information, the seat stays reserved forever.

---

# Solution

Store the reservation expiration.

```text
MatchSeat
----------------
matchId (PK)

seatId (PK)

price

status

holdExpiresAt
```

(or `reservedUntil`)

Example:

```
status = RESERVED

holdExpiresAt = 10:15 AM
```

---

# Reservation Lifecycle

Customer clicks Buy:

```
AVAILABLE

↓

RESERVED
```

Payment succeeds:

```
SOLD
```

Payment fails or times out:

```
AVAILABLE
```

---

# Background Job

A scheduled process periodically runs:

```sql
SELECT *
FROM MatchSeat
WHERE status = 'RESERVED'
AND holdExpiresAt < NOW();
```

Those seats become:

```
status = AVAILABLE

holdExpiresAt = NULL
```

---

# Interview Insight

Notice what we modeled.

We did **not** model the scheduler.

We modeled the **data required by the scheduler**.

Good interview answer:

> Store the reservation expiration timestamp (`holdExpiresAt`) in `MatchSeat`. A background job can periodically release expired reservations.

This demonstrates data modeling first, implementation second.

---

# Composite Key Discussion

Current MatchSeat design:

```text
MatchSeat

matchId (PK)

seatId (PK)
```

Advantages:

- Natural key
- Prevents duplicate seat assignments for the same match
- No surrogate key needed

Ticket references:

```
(matchId, seatId)
```

Alternative:

```
matchSeatId
```

Both are acceptable.

---

# Final ER Model

```
User
 |
 | 1
 |
 N
Purchase
 |
 | 1
 |
 N
Ticket
 |
 |
 | references
 |
MatchSeat
 /      \
/        \
Match     Seat
```

---

# Key Interview Takeaways

### 1. Model business concepts first.

Do not jump directly to tables.

---

### 2. Ask clarification questions.

Never assume requirements.

---

### 3. Discover hidden entities.

Examples:

- Enrollment
- Membership
- PlayerStat
- MatchSeat

---

### 4. If a relationship has its own attributes, make it an entity.

Examples of relationship-specific attributes:

- price
- status
- availability
- reservation expiration

---

### 5. Don't store derivable data.

Ticket stores the MatchSeat reference.

Match and Seat can be derived from it.

---

### 6. Model the data before the process.

Instead of saying:

> Run a scheduler.

Say:

> Store `holdExpiresAt`, then a scheduler can release expired reservations.

---

# Common Apple Interview Questions

### Why not store price in Seat?

Because price changes per match.

---

### Why not store status in Seat?

Because availability changes per match.

---

### Why introduce MatchSeat?

Because the relationship between Match and Seat has its own business meaning and attributes.

---

### Why use a composite key?

Because `(matchId, seatId)` naturally identifies one seat allocation for one match and prevents duplicates.

---

# Patterns Learned So Far

- 1:N relationships
- M:N relationships
- Junction tables
- Relationship entities
- Weak entities
- Composite primary keys
- Lookup tables
- Translation tables
- Normalization vs Denormalization
- Time-based state (`holdExpiresAt`)
- Modeling relationship-specific attributes


# Data Modeling Notes - Audit Tables vs Effective Dating (Temporal Modeling)

## 1. Audit Table

### Purpose

An audit table answers:

> **"What happened?"**

It records **events** or **transitions** in the system.

Typical use cases:

- Purchase status changes
- Order status changes
- User profile updates
- Who modified a record
- Compliance and traceability

---

## Example

### Purchase (Current State)

| purchaseId | status |
|------------|---------|
|100|CANCELLED|

This table always stores the **latest state**.

---

### PurchaseAudit

```text
PurchaseAudit
----------------------------
auditId (PK)

purchaseId (FK)

oldStatus

newStatus

changedAt

changedBy
```

Example:

|auditId|purchaseId|oldStatus|newStatus|changedAt|changedBy|
|--------|----------|---------|---------|----------|----------|
|1|100|CREATED|PAYMENT_PENDING|10:02|Payment Service|
|2|100|PAYMENT_PENDING|PAID|10:03|Payment Service|
|3|100|PAID|TICKET_ISSUED|10:05|Ticket Service|
|4|100|TICKET_ISSUED|CANCELLED|10:20|Customer|

Each row represents:

> **An event occurred.**

---

## Why separate Purchase and PurchaseAudit?

Purchase

- Optimized for operational queries
- Contains only current state
- Small and fast

PurchaseAudit

- Complete history
- Customer support
- Compliance
- Investigation
- Reporting

---

## Typical Interview Questions

- Who changed the status?
- When did it change?
- What was the previous status?
- Why was it changed?

Audit tables answer all of these.

---

# 2. Effective Dating (Temporal Modeling)

## Purpose

An effective-dated table answers:

> **"What was true at a specific point in time?"**

Instead of recording events, it records **business validity**.

Typical use cases:

- Employee Salary
- Insurance Policy
- Customer Address
- Tax Rates
- Product Prices
- Employee Department
- Manager History

---

## Example Requirement

Employee receives salary increases.

Need to answer:

- Current salary
- Salary on any historical date
- Schedule future salary changes
- Prevent overlapping salary periods

---

## Clarification Questions

Before designing, ask:

- Do we need to retain every salary change?
- Do we need to store the reason for the change?
- Can salary changes be scheduled for the future?
- Should overlapping salary periods be allowed?
- Do we need to answer salary on any historical date?
- Is salary the only changing attribute?

---

## Entity Design

```text
Employee
-----------------------
employeeId (PK)

name
```

```text
EmployeeSalary
-----------------------------
employeeId (FK)

salary

effectiveFrom

effectiveTo

changeReason
```

Relationship

```
Employee

1
|
|
N

EmployeeSalary
```

---

## Primary Key

Recommended:

```text
(employeeId, effectiveFrom)
```

Reason:

An employee cannot have two salary records starting at the same instant.

---

## Example Data

|employeeId|salary|effectiveFrom|effectiveTo|changeReason|
|-----------|------|-------------|-----------|------------|
|101|120000|2025-01-01|2025-06-30|Annual Review|
|101|130000|2025-07-01|2025-12-31|Promotion|
|101|145000|2026-01-01|NULL|Market Adjustment|

Current salary is simply the row where:

```text
effectiveTo IS NULL
```

(or some systems use 9999-12-31.)

---

## Current Salary Query

```sql
SELECT *
FROM EmployeeSalary
WHERE employeeId = 101
AND effectiveTo IS NULL;
```

---

## Historical Salary Query

```sql
SELECT *
FROM EmployeeSalary
WHERE employeeId = 101
AND '2025-08-15'
BETWEEN effectiveFrom
AND effectiveTo;
```

---

# Audit vs Effective Dating

## Audit

Focus:

> **Something happened.**

Example:

```
10:00 CREATED

↓

10:03 PAID

↓

10:05 ISSUED

↓

10:20 CANCELLED
```

Stores:

- old value
- new value
- changedAt
- changedBy

Questions answered:

- What changed?
- When?
- Who changed it?

---

## Effective Dating

Focus:

> **This value was valid during this period.**

Example:

```
Jan 1 -------- Jun 30

Salary = $120K


Jul 1 -------- Dec 31

Salary = $130K


Jan 1 onward

Salary = $145K
```

Stores:

- effectiveFrom
- effectiveTo

Questions answered:

- What was the salary on March 15?
- What will the salary be next month?
- What is currently valid?

---

## Easy Way to Remember

### Audit

Records

> **Events**

### Effective Dating

Records

> **State over time**

---

# Preventing Overlapping Date Ranges

Example:

Existing row

|employeeId|effectiveFrom|effectiveTo|
|-----------|-------------|-----------|
|101|Jan 1|Jun 30|

Trying to insert

|employeeId|effectiveFrom|effectiveTo|
|-----------|-------------|-----------|
|101|Jun 15|Dec 31|

This should be rejected because the validity periods overlap.

---

## Option 1 (Most Common)

### Application Validation

Before inserting:

```sql
SELECT *
FROM EmployeeSalary
WHERE employeeId = :employeeId
AND effectiveFrom <= :newEffectiveTo
AND effectiveTo >= :newEffectiveFrom;
```

If any row exists, reject the insert.

Most common solution in Spring Boot applications.

---

## Option 2

### Database Constraint

Some databases (e.g., PostgreSQL) support exclusion constraints that prevent overlapping ranges automatically.

Good database-level protection.

Not portable across all databases.

---

## Option 3

### Trigger

A BEFORE INSERT trigger can:

- Check overlap
- Reject insert

Possible, but generally avoided because:

- Business logic becomes hidden
- Harder to maintain
- Harder to test

---

## Option 4 (Preferred in Modern Applications)

Service layer validation inside a transaction.

```java
@Transactional
public void updateSalary(...) {

    // Check overlap

    // Close previous salary period

    // Insert new salary record

}
```

This keeps business logic in the application while ensuring atomic updates.

---

# Interview Summary

## Use an Audit Table when the business asks:

- Who changed it?
- When did it change?
- What changed?
- Why did it change?

Audit tables store **events**.

---

## Use Effective Dating when the business asks:

- What was true on a specific date?
- What is currently valid?
- Schedule future changes
- Maintain business validity over time

Effective-dated tables store **state over time**.

---

# Senior Interview Answer

> Audit tables answer **how the data changed** by recording events and transitions. Effective-dated tables answer **what the data looked like at any point in time** by recording the period during which a value was valid. They solve different business problems, and in some enterprise systems both patterns are used together.

# Data Modeling Notes - Audit Tables vs Effective Dating (Temporal Modeling)

## 1. Audit Table

### Purpose

An audit table answers:

> **"What happened?"**

It records **events** or **transitions** in the system.

Typical use cases:

- Purchase status changes
- Order status changes
- User profile updates
- Who modified a record
- Compliance and traceability

---

## Example

### Purchase (Current State)

| purchaseId | status |
|------------|---------|
|100|CANCELLED|

This table always stores the **latest state**.

---

### PurchaseAudit

```text
PurchaseAudit
----------------------------
auditId (PK)

purchaseId (FK)

oldStatus

newStatus

changedAt

changedBy
```

Example:

|auditId|purchaseId|oldStatus|newStatus|changedAt|changedBy|
|--------|----------|---------|---------|----------|----------|
|1|100|CREATED|PAYMENT_PENDING|10:02|Payment Service|
|2|100|PAYMENT_PENDING|PAID|10:03|Payment Service|
|3|100|PAID|TICKET_ISSUED|10:05|Ticket Service|
|4|100|TICKET_ISSUED|CANCELLED|10:20|Customer|

Each row represents:

> **An event occurred.**

---

## Why separate Purchase and PurchaseAudit?

Purchase

- Optimized for operational queries
- Contains only current state
- Small and fast

PurchaseAudit

- Complete history
- Customer support
- Compliance
- Investigation
- Reporting

---

## Typical Interview Questions

- Who changed the status?
- When did it change?
- What was the previous status?
- Why was it changed?

Audit tables answer all of these.

---

# 2. Effective Dating (Temporal Modeling)

## Purpose

An effective-dated table answers:

> **"What was true at a specific point in time?"**

Instead of recording events, it records **business validity**.

Typical use cases:

- Employee Salary
- Insurance Policy
- Customer Address
- Tax Rates
- Product Prices
- Employee Department
- Manager History

---

## Example Requirement

Employee receives salary increases.

Need to answer:

- Current salary
- Salary on any historical date
- Schedule future salary changes
- Prevent overlapping salary periods

---

## Clarification Questions

Before designing, ask:

- Do we need to retain every salary change?
- Do we need to store the reason for the change?
- Can salary changes be scheduled for the future?
- Should overlapping salary periods be allowed?
- Do we need to answer salary on any historical date?
- Is salary the only changing attribute?

---

## Entity Design

```text
Employee
-----------------------
employeeId (PK)

name
```

```text
EmployeeSalary
-----------------------------
employeeId (FK)

salary

effectiveFrom

effectiveTo

changeReason
```

Relationship

```
Employee

1
|
|
N

EmployeeSalary
```

---

## Primary Key

Recommended:

```text
(employeeId, effectiveFrom)
```

Reason:

An employee cannot have two salary records starting at the same instant.

---

## Example Data

|employeeId|salary|effectiveFrom|effectiveTo|changeReason|
|-----------|------|-------------|-----------|------------|
|101|120000|2025-01-01|2025-06-30|Annual Review|
|101|130000|2025-07-01|2025-12-31|Promotion|
|101|145000|2026-01-01|NULL|Market Adjustment|

Current salary is simply the row where:

```text
effectiveTo IS NULL
```

(or some systems use 9999-12-31.)

---

## Current Salary Query

```sql
SELECT *
FROM EmployeeSalary
WHERE employeeId = 101
AND effectiveTo IS NULL;
```

---

## Historical Salary Query

```sql
SELECT *
FROM EmployeeSalary
WHERE employeeId = 101
AND '2025-08-15'
BETWEEN effectiveFrom
AND effectiveTo;
```

---

# Audit vs Effective Dating

## Audit

Focus:

> **Something happened.**

Example:

```
10:00 CREATED

↓

10:03 PAID

↓

10:05 ISSUED

↓

10:20 CANCELLED
```

Stores:

- old value
- new value
- changedAt
- changedBy

Questions answered:

- What changed?
- When?
- Who changed it?

---

## Effective Dating

Focus:

> **This value was valid during this period.**

Example:

```
Jan 1 -------- Jun 30

Salary = $120K


Jul 1 -------- Dec 31

Salary = $130K


Jan 1 onward

Salary = $145K
```

Stores:

- effectiveFrom
- effectiveTo

Questions answered:

- What was the salary on March 15?
- What will the salary be next month?
- What is currently valid?

---

## Easy Way to Remember

### Audit

Records

> **Events**

### Effective Dating

Records

> **State over time**

---

# Preventing Overlapping Date Ranges

Example:

Existing row

|employeeId|effectiveFrom|effectiveTo|
|-----------|-------------|-----------|
|101|Jan 1|Jun 30|

Trying to insert

|employeeId|effectiveFrom|effectiveTo|
|-----------|-------------|-----------|
|101|Jun 15|Dec 31|

This should be rejected because the validity periods overlap.

---

## Option 1 (Most Common)

### Application Validation

Before inserting:

```sql
SELECT *
FROM EmployeeSalary
WHERE employeeId = :employeeId
AND effectiveFrom <= :newEffectiveTo
AND effectiveTo >= :newEffectiveFrom;
```

If any row exists, reject the insert.

Most common solution in Spring Boot applications.

---

## Option 2

### Database Constraint

Some databases (e.g., PostgreSQL) support exclusion constraints that prevent overlapping ranges automatically.

Good database-level protection.

Not portable across all databases.

---

## Option 3

### Trigger

A BEFORE INSERT trigger can:

- Check overlap
- Reject insert

Possible, but generally avoided because:

- Business logic becomes hidden
- Harder to maintain
- Harder to test

---

## Option 4 (Preferred in Modern Applications)

Service layer validation inside a transaction.

```java
@Transactional
public void updateSalary(...) {

    // Check overlap

    // Close previous salary period

    // Insert new salary record

}
```

This keeps business logic in the application while ensuring atomic updates.

---

# Interview Summary

## Use an Audit Table when the business asks:

- Who changed it?
- When did it change?
- What changed?
- Why did it change?

Audit tables store **events**.

---

## Use Effective Dating when the business asks:

- What was true on a specific date?
- What is currently valid?
- Schedule future changes
- Maintain business validity over time

Effective-dated tables store **state over time**.

---

# Senior Interview Answer

> Audit tables answer **how the data changed** by recording events and transitions. Effective-dated tables answer **what the data looked like at any point in time** by recording the period during which a value was valid. They solve different business problems, and in some enterprise systems both patterns are used together.


# Data Modeling Notes - Polymorphic Associations, Audit vs Versioning vs Effective Dating

# 1. Polymorphic Association

## Definition

A polymorphic association allows a single entity to reference **multiple different entity types** using the same relationship.

Instead of referencing a single table with a foreign key, it stores:

- objectType
- objectId

The application determines which table to query.

---

## Example Requirement

Users can comment on:

- Product
- Order
- Blog Post
- Photo

Each comment belongs to **exactly one** object.

---

## Clarification Questions

Before designing:

- What objects can users comment on?
- Can a comment belong to multiple objects?
- Can users reply to comments?
- Can comments be liked?
- Can users edit comments?
- Can users delete comments?
- Do deleted comments need to be retained?
- Will additional object types be added in the future?

---

## Initial Design

```text
Comment
-------------------------
commentId (PK)

objectType

objectId

commentText

commentedBy

commentedAt

editedAt

deletedAt
```

Example:

|commentId|objectType|objectId|
|----------|----------|--------|
|1|PRODUCT|101|
|2|PHOTO|55|
|3|ORDER|900|
|4|BLOG_POST|25|

The pair

```
(objectType, objectId)
```

uniquely identifies the parent object.

---

## Why not only objectId?

Suppose:

```
objectId = 101
```

How does the database know whether it is:

- Product 101
- Blog Post 101
- Order 101
- Photo 101

It doesn't.

That's why we also store:

```
objectType
```

---

## Advantages

- Flexible
- Easy to support new entity types
- Simple schema
- Common in ORMs (Rails, Django, etc.)

---

## Disadvantages

A single foreign key cannot reference multiple tables.

Therefore:

- No database referential integrity
- Application must validate object existence
- Joins become more complex

---

## Alternative Design

Instead of polymorphism:

```text
Comment
-------------------
commentId

commentText

commentedBy

commentedAt

deletedAt
```

Separate relationship tables:

```text
ProductComment
----------------
commentId
productId
```

```text
OrderComment
----------------
commentId
orderId
```

```text
BlogComment
----------------
commentId
blogId
```

```text
PhotoComment
----------------
commentId
photoId
```

Advantages:

- Strong foreign keys
- Database enforces integrity

Disadvantages:

- More tables
- New entity requires another relationship table

---

## Interview Answer

A polymorphic association allows one entity to reference multiple different entity types using:

- objectType
- objectId

It provides flexibility but sacrifices database-enforced foreign keys.

---

# 2. Editing Comments

If the requirement is simply:

> Users can edit comments

Add:

```text
editedAt
```

Final table:

```text
Comment
-------------------------
commentId

objectType

objectId

commentText

commentedBy

commentedAt

editedAt

deletedAt
```

If the business only needs the latest version, this is sufficient.

---

## Important Clarification Question

Whenever you hear:

> Users can edit...

Ask:

> Do we need to keep previous versions?

If yes, introduce history/versioning.

---

# 3. Audit vs Versioning vs Effective Dating

Although all three store historical information, they solve completely different business problems.

---

# Audit

## Purpose

Answers:

> What happened?

Each row represents an **event**.

Example:

Purchase status

```
CREATED

↓

PAID

↓

SHIPPED

↓

DELIVERED
```

Audit table:

```text
PurchaseAudit
------------------------
auditId

purchaseId

oldStatus

newStatus

changedAt

changedBy
```

Example:

|purchaseId|oldStatus|newStatus|changedAt|
|-----------|----------|----------|---------|
|100|CREATED|PAID|10:03|
|100|PAID|SHIPPED|10:10|
|100|SHIPPED|DELIVERED|11:30|

Audit answers:

- Who changed it?
- When?
- What changed?
- Why?

---

# Versioning

## Purpose

Answers:

> What did Version X look like?

Every row is a **complete snapshot**.

Example:

Google Docs

Version 1

```
Hello
```

Version 2

```
Hello World
```

Version 3

```
Hello World!!
```

Table:

```text
CommentVersion
-------------------------
commentId

versionNumber

commentText

createdAt

createdBy
```

Example:

|commentId|version|text|
|----------|-------|----------------|
|1|1|Hello|
|1|2|Hello World|
|1|3|Hello World!!|

Questions answered:

- Restore Version 2
- Compare Version 3 and Version 5
- Download Version 4

Every row is a complete object snapshot.

---

# Effective Dating (Temporal Modeling)

## Purpose

Answers:

> What was true on a given date?

Stores **business validity**.

Example:

```text
EmployeeSalary
--------------------------
employeeId

salary

effectiveFrom

effectiveTo

changeReason
```

Example:

|salary|effectiveFrom|effectiveTo|
|-------|-------------|-----------|
|120000|Jan 1|Jun 30|
|130000|Jul 1|Dec 31|
|145000|Jan 1|NULL|

Questions answered:

- Current salary
- Salary on March 15
- Future scheduled salary
- Historical salary

Every row represents:

> This salary was valid during this period.

---

# What is "History"?

History is **not** a modeling pattern.

It is only a table name.

Examples:

```
PurchaseHistory
```

may actually be an Audit table.

```
CommentHistory
```

may actually be a Version table.

```
SalaryHistory
```

may actually be an Effective-Dated table.

Always look at the columns.

---

# CommentHistory Example

```text
CommentHistory
-------------------------
historyId

commentId

oldText

newText

editedAt

editedBy
```

This is an Audit table because it stores transitions.

---

# CommentVersion Example

```text
CommentVersion
-------------------------
commentId

versionNumber

commentText

createdAt
```

This is Versioning because each row stores the full object.

---

# EmployeeSalary Example

```text
EmployeeSalary
-------------------------
employeeId

salary

effectiveFrom

effectiveTo
```

This is Effective Dating because each row stores a validity period.

---

# Comparison

| Pattern | Row Represents | Main Question |
|----------|----------------|---------------|
| Audit | Event | What changed? |
| Version | Snapshot | What did Version X look like? |
| Effective Dating | Valid State | What was true on a specific date? |

---

# Real-World Examples

## Audit

- Purchase Status
- Payment Status
- User Profile Changes
- Login History
- Banking

---

## Versioning

- Google Docs
- Microsoft Word
- Git
- Confluence
- Notion
- Wiki Pages

---

## Effective Dating

- Employee Salary
- Tax Rate
- Insurance Premium
- Product Pricing
- Department Assignment
- Customer Address History

---

# Interview Cheat Sheet

## Audit

Stores:

- old value
- new value
- who
- when

Represents:

> Event

---

## Version

Stores:

- Complete object snapshot

Represents:

> Revision

---

## Effective Dating

Stores:

- effectiveFrom
- effectiveTo

Represents:

> Business validity

---

# Senior Interview Summary

Audit, Versioning, and Effective Dating solve different business problems.

- **Audit** records events and transitions.
- **Versioning** stores complete revisions of an object.
- **Effective Dating** models when business data is valid.

Many enterprise systems use **all three together**.

Example:

EmployeeSalary (Effective Dating)

+

EmployeeSalaryAudit (Audit)

or

DocumentVersion (Versioning)

+

DocumentAudit (Audit)

# Data Modeling Interview Notes: Inheritance vs. Role-Based Modeling

## Problem Statement

Model users for an e-commerce platform.

### Shared Attributes

- Name
- Email
- Phone
- Login Credentials

### Current Roles

- Customer
- Seller
- Admin

### Role-Specific Attributes

**Customer**
- Loyalty Points
- Shipping Preference

**Seller**
- Tax ID
- Store Name

**Admin**
- Employee ID
- Access Level

---

# Clarification Questions

Before designing, ask:

1. Can a user have multiple roles?
2. Does every user have at least one role?
3. Can roles be added or removed dynamically?
4. Will new roles be introduced in the future?

The answers determine whether to use **Inheritance** or a **Role Model**.

---

# Option 1: Joined Table Inheritance

Use when a user is fundamentally one subtype.

```
             User
          (Supertype)
               |
     -----------------------
     |         |          |
 Customer   Seller     Admin
```

## User

```text
User
-----
userId (PK)
name
email
phone
password
```

## Customer

```text
Customer
---------
userId (PK, FK)
loyaltyPoints
shippingPreference
```

## Seller

```text
Seller
-------
userId (PK, FK)
taxId
storeName
```

## Admin

```text
Admin
------
userId (PK, FK)
employeeId
accessLevel
```

### Advantages

- Represents an **IS-A** relationship.
- Strong referential integrity.
- Clean for fixed hierarchies.

### Drawbacks

- Every new role requires:
  - A new table
  - Database migration
  - Code changes

Best when subtypes are stable and unlikely to change.

---

# Option 2: Role-Based Model (Recommended)

Use when users can have multiple roles and roles evolve over time.

```
User ----< UserRole >---- Role
```

## User

```text
User
-----
userId (PK)
name
email
phone
password
```

## Role

```text
Role
-----
roleId (PK)
roleName
```

Example data:

| roleId | roleName |
|--------|----------|
|1|Customer|
|2|Seller|
|3|Admin|

## UserRole

```text
UserRole
---------
userId (FK)
roleId (FK)

PK(userId, roleId)
```

Relationship:

```
User
 1
 |
 | N
UserRole
 | N
 |
 1
Role
```

### Advantages

- Multiple roles per user.
- Roles can be added or removed.
- New roles only require inserting a row into the Role table.
- No schema changes.

---

# Role-Specific Data

Some roles have additional information.

## CustomerProfile

```text
CustomerProfile
---------------
userId (PK, FK)
loyaltyPoints
shippingPreference
```

## SellerProfile

```text
SellerProfile
-------------
userId (PK, FK)
taxId
storeName
```

## AdminProfile

```text
AdminProfile
------------
userId (PK, FK)
employeeId
accessLevel
```

---

# Why Not Put Everything in User?

Suppose Bob is only an Admin.

If User contained:

```text
loyaltyPoints
shippingPreference
taxId
storeName
```

Bob would have lots of NULL values.

The User table would mix unrelated concepts.

---

# Why Not Store loyaltyPoints in UserRole?

UserRole represents the relationship between a User and a Role.

Examples of UserRole attributes:

```text
assignedAt
assignedBy
expiresAt
```

These describe the assignment.

However:

```text
loyaltyPoints
shippingPreference
taxId
storeName
```

describe the user's business data while acting in that role.

Therefore they belong in separate profile tables.

---

# Where Does Each Attribute Belong?

## User

Stores identity.

Examples:

- name
- email
- phone
- password

---

## Role

Stores the type of role.

Examples:

- Customer
- Seller
- Admin

---

## UserRole

Stores information about the assignment.

Examples:

- assignedAt
- assignedBy
- expiresAt

---

## CustomerProfile

Stores customer-specific data.

Examples:

- loyaltyPoints
- shippingPreference

---

## SellerProfile

Stores seller-specific data.

Examples:

- taxId
- storeName

---

## AdminProfile

Stores admin-specific data.

Examples:

- employeeId
- accessLevel

---

# Inheritance vs. Role Model

## Inheritance

Think:

> **What is this entity?**

Examples:

```
Vehicle
    |
------------------
|       |        |
Car    Truck   Motorcycle
```

```
Animal
   |
-------------
|     |      |
Dog   Cat   Bird
```

These are fundamentally different types.

Use **Inheritance**.

---

## Role Model

Think:

> **What can this entity do?**

Examples:

```
User
 |
 +-- Customer
 |
 +-- Seller
 |
 +-- Admin
```

The same user can have multiple roles.

Roles can change over time.

New roles can be added.

Use a **Role Model**.

---

# Interview Takeaway

Your first design (User + Customer + Seller + Admin) is called **Joined Table Inheritance**.

It is a correct solution when:

- Users belong to one subtype.
- Subtypes are fixed.
- New subtypes are rare.

For this interview, after the clarification questions revealed that:

- Users can have multiple roles.
- Roles change dynamically.
- New roles will be added.

A **Role-Based Model** becomes the better choice.

A senior-level answer is to explain **both approaches**, then justify why the role-based design better matches the business requirements.