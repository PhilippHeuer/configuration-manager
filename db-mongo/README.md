# MongoDb Module

## Run a local mongo-db for testing

```bash
docker run -it --rm --env MONGO_INITDB_ROOT_USERNAME=root --env MONGO_INITDB_ROOT_PASSWORD=example --publish 27017:27017 mongo:4.1
```
