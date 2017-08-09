# Postgresql Setup

## Postgres 9.5

```
sudo su postgres
psql
```

```sql
create user example login password 'example';
create database example owner example;
```

## Server

Just run `make`.