# MusicVault — CS 157A Final Project

MusicVault is a web-based music rating and discovery platform. Registered users can browse and search a catalog of albums, rate albums on a half-star scale, write reviews, build ordered album lists, save albums to their collection (owned / favorite / wishlist), and follow other users. Administrators manage the catalog (artists, albums, tracks, genres, labels, release types, streaming services) and moderate reviews. It is backed by a normalized 20-table MySQL database.

## Tech Stack

- **Database:** MySQL 8
- **Back end:** Java (Spring Boot), Spring Security, Spring `JdbcTemplate`
- **Front end:** HTML, Bootstrap, Thymeleaf templates
- **Build:** Maven (via the bundled `mvnw` wrapper)

## Prerequisites

Install these before running the app:

- **JDK 17 or newer** (the project targets Java 17). Check with `java -version`.
- **MySQL 8**, with a running local server. Check with `mysql --version`.

## Setup

### 1. Initialize the database

From the repository root, run the three SQL scripts **in order**. The first script creates the `musicvault` database automatically, so you do not need to create it by hand. Use `-p` and enter your MySQL password when prompted:

```bash
mysql -u root -p < sql/01_schema.sql     # creates the database + all 20 tables
mysql -u root -p < sql/02_seed.sql       # populates tables with sample data
mysql -u root -p < sql/03_indexes.sql    # creates indexes
```

### 2. Configure the database connection

Copy the example properties file, then edit it to add your MySQL credentials:

```bash
cp app/src/main/resources/application-example.properties app/src/main/resources/application.properties
```

Open `app/src/main/resources/application.properties` and set your MySQL username and password:

```properties
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

(Leave `spring.datasource.password=` empty only if your MySQL root account has no password.) This file is git-ignored so credentials are never committed.

### 3. Run the application

```bash
cd app
./mvnw spring-boot:run        # macOS / Linux / Git Bash
mvnw.cmd spring-boot:run      # Windows (Command Prompt / PowerShell)
```

Wait for `Started MusicVaultApplication`, then open:

**http://localhost:8080/albums**

## Demo Accounts

All demo accounts use the password `password`.

| Username | Password | Role |
|----------|----------|------|
| `alice` / `bob` / `carol` | `password` | Registered user |
| `admin_mod` | `password` | Administrator |

## Testing Key Features

1. **Register & log in** — create a new account, or log in with a demo account above.
2. **Browse & search** — browse the catalog at `/albums`; search by title, artist, genre, release year, or record label.
3. **Album detail** — open any album to see its tracks, credited artists and roles, genres, label, and streaming links.
4. **Rate & review** — on an album page, submit a half-star rating (the community average updates) and post, edit, or delete a review.
5. **Lists** — create an album list at `/lists` and add albums in a chosen order.
6. **Saves** — save an album as Owned, Favorite, or Wishlist and view them at `/saves`.
7. **Follow** — browse users at `/users` and follow/unfollow another user.
8. **Admin** — log in as `admin_mod`, open `/admin`, add/edit/delete catalog entries, and moderate a review. Confirm that a regular (non-admin) user is blocked from `/admin`.

## Division of Work

- **Andrew Young** — ER/EER diagram and relational schema design; SQL scripts (schema), normalization and indexing decisions, database testing and verification.
- **Arsen Keneshbekov** — SQL scripts(seed data, indexing, and demo queries), Spring Boot application (controllers, services, repositories, and Thymeleaf templates), authentication and role-based access control, admin catalog-management and review-moderation system.
- **Mallika Natarajan** — initial project proposal.

## Project Structure

```
CS157A_Final_Project/
├── sql/          # 01_schema, 02_seed, 03_indexes, 04_queries
├── app/          # Spring Boot application (source under src/main)
├── docs/         # ER diagram, report, meeting notes
└── README.md
```
