---
layout: page
title: "PostgreSQL configuration"
description: "How to configure PostgreSQL"
group: quickstart
---

# PostgreSQL configuration

## Dependencies

To start using database, especially PostgreSQL in your project you have to add this features in the `corefeatures/features.json` file:

- database
- database-plugins
- database-postgresql
- database-postgresql-plugins
- database-postgresql-connection-options
- database-postgresql-create-collection-if-not-exists

## Configurations

Create your custom feature, for example `SetupPostgresConnectionOptions`. We will not write some code in this feature but add actions to the `config.json`:

```json
{
  "featureName": "com.my-project:setup-postgres-connection-options",
  "afterFeatures": [
        "info.smart_tools.smartactors:database-postgresql-connection-options"
  ],
  "onFeatureLoading": [
    {
      "chain": "registerPostgresJsonConnectionOptions",
      "messages": [
        {
          "connectionOptionsRegistrationName": "MyConnectionOptions",
          "url": "jdbc:postgresql://localhost:5432/example",
          "username": "example",
          "password": "example",
          "maxConnections": 250
        }
      ]
    }
  ]
}
```

Here we see, that when our feature being loaded it sends a message to the chain `registerPostgresConnectionOptions`. This is the server's inner chain defined inside the `database-postgresql-connection-options` feature. You can set db url, username, password and maxConnections in the message. To register this options inside IOC we will use our custom key name `connectionOptionsRegistrationName` so we'll be able in the project to select the proper connection. It might be useful when you have multiple databases in a project.

## Create collections

In our framework we don't need any migrations but we have to create collections in a DB. For this purposes we can use `database-postgresql-create-collection-if-not-exists` feature which allows us to specify collection names and create them on our feature being loaded.

To use it, create a new feature, for example `CreateCollections` and fill the `config.json` with this:

```json
{
  "featureName": "com.my-project:create-collections",
  "afterFeatures": [
    "info.smart_tools.smartactors:database-postgresql-create-collection-if-not-exists",
    "com.my-project:setup-postgres-connection-options"
  ],
  "onFeatureLoading": [
    {
      "chain": "createCollections",
      "messages": [
        {
          "collectionName": "example_collection",
          "connectionOptionsRegistrationName": "MyConnectionOptions"
        }
      ]
    }
  ]
}
```

You can see, that this feature allows us to send messages to the some inner chain that creates collections. **Important** thing is that this feature **MUST** depends on our previous feature that creates connection options `com.my-project:setup-postgres-connection-options`.

You can specify as many messages in `messages` section as many collections you need.

Best practises: add this messages in every feature, that need this collection. Do not create a single feature that creates everything. Here we do this just for education purposes.

## How to use it?

You have registered options and created collections. How to use them?

To do database tasks you need to have an `IPool` object, to get it use this snippet:

```java
final ConnectionOptions options = IOC.resolve(Keys.getOrAdd(message.getConnectionOptionsRegistrationName()));
final IPool pool = IOC.resolve(Keys.getOrAdd("PostgresConnectionPool"), options);
String collectionName = message.getCollectionName();
try (PoolGuard guard = new PoolGuard(pool)) {
    ITask task = IOC.resolve(
            Keys.getOrAdd(SOME_TASK_ID),
            guard.getObject(),
            collectionName,
            message.getOptions()
    );
    task.execute();
} catch (TaskExecutionException | ResolutionException | PoolGuardException e) {
    throw new CreateCollectionActorException(e);
}
```
