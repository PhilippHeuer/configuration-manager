# MySQL

## Local

```bash
docker run -it --rm --env MYSQL_RANDOM_ROOT_PASSWORD="true" --env MYSQL_USER="user" --env MYSQL_PASSWORD="password" --env MYSQL_DATABASE="db" -p 3306:3306 mysql:8.0.13
```
