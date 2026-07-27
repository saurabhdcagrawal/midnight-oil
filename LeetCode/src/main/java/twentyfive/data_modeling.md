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