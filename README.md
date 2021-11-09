# RSO: Microservice notifications

Microservice which manages notifications in our service

## Prerequisites

```bash
docker run -d --name pg-notifications -e POSTGRES_USER=dbuser -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=notifications -p 5432:5432 postgres:13
```