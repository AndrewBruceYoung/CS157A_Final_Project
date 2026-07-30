# MusicVault — CS157A Final Project

Music catalog platform with album browse/search, ratings, and user accounts.

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

Demo users: `alice`, `bob`, `carol` — password `password`
