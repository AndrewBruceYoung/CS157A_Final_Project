# MusicVault — CS157A Final Project

Music catalog platform with album browse/search, ratings, reviews, lists, saves, follows, and admin catalog tools.

## Setup

```bash
mysql -u root < sql/01_schema.sql
mysql -u root < sql/02_seed.sql
mysql -u root < sql/03_indexes.sql
```

```bash
cp app/src/main/resources/application-example.properties app/src/main/resources/application.properties
cd app && ./mvnw spring-boot:run
```

Open http://localhost:8080/albums

## Demo accounts

| Username | Password | Role |
|----------|----------|------|
| `alice` / `bob` / `carol` | `password` | Registered user |
| `admin_mod` | `password` | Administrator |

## Features to try

- Browse/search albums, rate and review (album detail page)
- Save albums as Owned / Favorite / Wishlist (`/saves`)
- Create and manage album lists (`/lists`)
- Browse users and follow/unfollow (`/users`)
- Admin catalog + review moderation (`/admin`) — login as `admin_mod`
