---
layout: page
title: "How to start guide"
description: "How to start using DAS"
group: quickstart
---

# How to start using SmartActors

## Terminology

Let start from some terms.

__SmartActors__, __DAS__, __Distributed Actor System__ — the framework you're going to use here.

__das__ — the command line tool `das` which helps to initialize the Server and creates source code templates and the project structure.

__Server__ — you need a Server where you put your compiled code and it'll run it.

__Core__, __Core Pack__ — the initial set of classes (in jars) necessary to start the Server from scratch.

__Project__ — it's presumed you work in some Project, to create some Features and Actors to run they in the Server. The source code of the Project is located in a folder, separated from the SmartActors sources or where you run your Server. The Project can be built by a single command.

__Feature__ — a named set of functionality (Actors, Plugins and Maps) to run in the Server. Typically are distributed as zip archives, are extracted to separated folders by the Server. A Feature may depend on other Features.

__Core Feature__ — a Feature, provided by the SmartActors developers. As any Feature, it gives a new functionality to the Server. It's downloaded automatically when the Server starts. Core Features provide some basic functionality to the Server, while ordinary Features add some specific business logic.

__Actor__ — a Java class which provides the minimal, independent and atomic set of functionality. Actors should be combined to Maps (or Chains) to do something useful within a Feature. Actors in the map receives and sends messages to each other.

__Wrapper__ — an object which is passed to the methods of an Actor. It wraps the actual message passed in the Chain into the interface provided by the author of the Actor. This interface is also called Wrapper.

__Config__ — a `config.json` file, a part of a Feature. Configs from all loaded Features are joined together and form an aggregate config of the Server. The config defines: set of objects to create in the Server's IOC, typically they are Actors; Maps; other metadata and handlers of system events.

__Map__, __Message Map__, __Chain__ — the list of actors in the specified order to process a message step by step. On each step the message is wrapped and passed to the Actor's handler, the Actor makes some modifications in the message atomically, then the message is passed to the next Actor. This is the core idea, how independent Actors work together.

__Plugin__ — a part of a Feature, contains code to initialize the Feature, in most cases registers new strategies in IOC. Plugins may depend on other plugins.

__IOC__, __Inversion of Control__, __IOC Container__ — global "storage" of all objects in the system. The preferable way to take an object in the system is to ask IOC to resolve some parameters and get the object reference.

__Message Receiver__, __Receiver__ — an entity in the system able to receive and process messages. Technically Actor is also such kind of entity, but Actors also support Wrappers and some thread-safety guarantees.

__Endpoint__ — point of the server to receive incoming messages, for HTTP it's a TCP port accepting POSTed JSON documents.

## Requirements

You need Debian-base Linux distribution.

You need [OpenJDK](http://openjdk.java.net/install/index.html) 8 or later to run and compile. Note you need `jdk` packages, not `jre`.

You need [Apache Maven](https://maven.apache.org/install.html) 3 or later to build your Project.

You need access to http://archiva.smart-tools.info to download necessary packages.

Download and install `das` utility.

* Download [deb package](http://archiva.smart-tools.info/repository/server-dev-tools/info/smart_tools/smartactors/das/0.3.3/das-0.3.3.deb).
* Install it with `dpkg` command.
    ```console
    $ sudo dpkg -i das-0.3.3.deb
    ```

You need a good text editor or an IDE like [IntelliJ IDEA](https://www.jetbrains.com/idea/) to write a code.

## Create the Project

Make sure the `das` utility is installed correctly.

```console
$ das
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
```

This Project is to make a simple application to keep in memory a list of items. The application has methods to add new items and list all of them. Think of items as a simplest abstraction for, for example, blog posts, etc...

### Create the Project folder

Go to some folder where you'll put the Server folder and the Project folder.

Create the new Project folder, use `cp` subcommand. The Project name is "Items". The Maven groupId for the Project is "info.smart_tools.examples.items".

```console
$ das cp -pn Items -g info.smart_tools.examples.items
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
Creating project ...
Project has been created successful.
```

The new folder "Items" is created. Check it's content.

```console
$ ls
Items
$ ls Items
das.data  pom.xml
```

`das.data` file contains some metadata necessary for `das` utility to work correctly.

`pom.xml` file is the Maven parent module for your Project.

### Create the Feature

Go to the Project folder.

```console
$ cd Items
```

Create the new Feature, use `cf` subcommand. The Feature name is "ItemsFeature".

```console
$ das cf -fn ItemsFeature
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
Creating feature ...
Feature has been created successful.
```

The new folder "ItemsFeature" is created. Check it's content.

```console
$ ls
das.data  ItemsFeature  pom.xml
$ ls ItemsFeature
config.json  ItemsFeatureDistribution  pom.xml
$ ls ItemsFeature/ItemsFeatureDistribution
bin.xml  pom.xml
```

`config.json` is the initial configuration file for the Feature.

The Feature is another Maven module with it's own `pom.xml`.

"ItemsFeatureDistribution" folder contains another Maven module necessary to build zip archive for the Feature. `bin.xml` contains instructions to Maven plugins how to zip.

Note that Project's `pom.xml` references Feature's `pom.xml` as a submodule. And also Feature references Distribution.

### Create the Actor

Create the new Actor, use `ca` subcommand. The actor name is "ItemsActor". It's located in the "ItemsFeature".

```console
$ das ca -an ItemsActor -fn ItemsFeature
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
Creating actor ...
Actor has been created successful.
```

The new folder "ItemsActor" is created under the Feature folder. Check it's content.

```console
$ ls ItemsFeature
config.json  ItemsActor  ItemsFeatureDistribution pom.xml
$ ls ItemsFeature/ItemsActor
pom.xml  src
$ ls ItemsFeature/ItemsActor/src
main  test
$ ls ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/*
ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/ItemsActor.java

ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/exception:
ItemsActorException.template

ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/wrapper:
ItemsActorWrapper.template
```

The new Maven module is created for the Actor. This module is a submodule of the Feature.

### Write the code of the Actor

It's time to open the whole Project in your IDE. Open the `pom.xml` in the Project folder as a Maven IDE project.

Our Actor will keep the list of items in memory (as it's private field) and gives access to the list.
It'll have two handlers (methods): to retrieve the whole list and to add a new item.

Note in this case the Actor is the _owner_ of the items list. All operations over the list should be performed through this Actor.

#### Write exceptions

Typically each actor has it's own exception type thrown from its methods.

Rename `ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/exception/ItemsActorException.template` into `ItemsActorException.java` and fill it with the content.

```java
package info.smart_tools.examples.items.items_feature.items_actor.exception;

public class ItemsActorException extends Exception {

    public ItemsActorException(final String message) {
        super(message);
    }

    public ItemsActorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ItemsActorException(final Throwable cause) {
        super(cause);
    }
}
```

Note that you can define more than one exception, specific for each exceptional case in your Actor.

#### Write wrappers

Wrappers are interfaces to the data the Actor need access to or provides.

Because our Actor has two handlers, we should define two wrappers: to get all items and to add a new item.

Copy and rename `ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/wrapper/ItemsActorWrapper.template` into `GetAllItemsWrapper.java` and `AddNewItemWrapper.java`.

`GetAllItemsWrapper` is the interface with a method to set a list of items. These are data going _out from_ the Actor, so the Actor should _set_ the list to the processing message.

```java
package info.smart_tools.examples.items.items_feature.items_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;

import java.util.List;


public interface GetAllItemsWrapper {

    /**
     * The Actors sets of list of all items here.
     * @throws ChangeValueException when the set fails
     */
    void setAllItems(final List<String> items)
            throws ChangeValueException;

}
```

`AddNewItemWrapper` is the interface with a method to _get_ a new item name. These are data going _into_ the Actor, so the Actor should _get_ them from the processing message.

```java
package info.smart_tools.examples.items.items_feature.items_actor.wrapper;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface AddNewItemWrapper {

    /**
     * The gets the new item name here.
     * @return the new item name
     * @throws ReadValueException when the get fails
     */
    String getNewItemName()
            throws ReadValueException;
}
```

Note getters and setters of the Wrapper must throw `ReadValueException` and `ChangeValueException` respectively.

#### Write the Actor

Take the file `ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/ItemsActor.java` and modify it.

It's necessary to add two methods: to retrieve all items and to add a new item. Each method takes one argument with the necessary Wrapper type and returns `void`. Each method throws the specific exception.

```java
package info.smart_tools.examples.items.items_feature.items_actor;

import info.smart_tools.examples.items.items_feature.items_actor.exception.ItemsActorException;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.AddNewItemWrapper;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.GetAllItemsWrapper;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemsActor {

    private final List<String> items = new ArrayList<>();

    /**
     * Retrieves the list of all items.
     * @param wrapper the wrapper where to set the list
     * @throws ItemsActorException if something goes wrong
     */
    public void getAllItems(final GetAllItemsWrapper wrapper) throws ItemsActorException {
        try {
            wrapper.setAllItems(Collections.unmodifiableList(items));
        } catch (ChangeValueException e) {
            throw new ItemsActorException("Failed to set list", e);
        }
    }

    /**
     * Adds the new item to the list.
     * @param wrapper the wrapper where to get the name of the new item
     * @throws ItemsActorException if something goes wrong
     */
    public void addNewItem(final AddNewItemWrapper wrapper) throws ItemsActorException {
        try {
            items.add(wrapper.getNewItemName());
        } catch (ReadValueException e) {
            throw new ItemsActorException("Failed to get item name", e);
        }
    }

}
```

#### Test the Actor

Note the Actor is just a Java class which uses some specific interfaces.
It's possible to test it completely independently by mock implementations of the wrappers.
You can use [Mockito](http://site.mockito.org/) for it.

Modify `ItemsFeature/ItemsActor/src/test/java/info/smart_tools/examples/items/items_feature/items_actor/ItemsActorTest.java`.

```java
package info.smart_tools.examples.items.items_feature.items_actor;

import info.smart_tools.examples.items.items_feature.items_actor.exception.ItemsActorException;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.AddNewItemWrapper;
import info.smart_tools.examples.items.items_feature.items_actor.wrapper.GetAllItemsWrapper;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ItemsActorTest {

    private ItemsActor actor;

    @Before
    public void init() {
        actor = new ItemsActor();
    }

    private List getListFromWrapper(final GetAllItemsWrapper mock) throws ChangeValueException {
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(mock).setAllItems(captor.capture());
        return captor.getValue();
    }

    @Test
    public void testAddOneItem() throws ItemsActorException, ChangeValueException, ReadValueException {
        GetAllItemsWrapper getAllWrapperBefore = mock(GetAllItemsWrapper.class);
        actor.getAllItems(getAllWrapperBefore);
        assertEquals(Collections.emptyList(), getListFromWrapper(getAllWrapperBefore));

        AddNewItemWrapper newItemWrapper = mock(AddNewItemWrapper.class);
        when(newItemWrapper.getNewItemName()).thenReturn("new item");
        actor.addNewItem(newItemWrapper);
        verify(newItemWrapper).getNewItemName();

        GetAllItemsWrapper getAllWrapperAfter = mock(GetAllItemsWrapper.class);
        actor.getAllItems(getAllWrapperAfter);
        List<String> expected = new ArrayList<>();
        expected.add("new item");
        assertEquals(expected, getListFromWrapper(getAllWrapperAfter));
    }

}
```

Note `das` already added all dependencies necessary for the test.

### Create the Plugin

A Plugin is necessary to make your actor available in the system IOC. Typically each Actor has a corresponding Plugin.

Create the new Plugin, use `cpl` subcommand. The Plugin name is "ItemsActorPlugin". It's located in the "ItemsFeature".

```console
$ das cpl -pln ItemsActorPlugin -fn ItemsFeature
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
Creating plugin ...
Plugin has been created successful.
```

The new folder "ItemsActorPlugin" is created under the Feature folder. Check it's content.

```console
$ ls ItemsFeature
config.json  ItemsActor  ItemsActorPlugin  ItemsFeatureDistribution  pom.xml
$ ls ItemsFeature/ItemsActorPlugin
pom.xml  src
```

The new Maven module is created for the Plugin. This module is a submodule of the Feature.

#### Write the Plugin code

Modify `ItemsFeature/ItemsActorPlugin/src/main/java/info/smart_tools/examples/items/items_feature/items_actor_plugin/ItemsActorPlugin.java`. You should add a code to register a strategy of retrieving of your Actor instance from IOC. In this simplest case the strategy just creates a new instance when IOC is queried for the Actor. You must define the name of the Actor, how it's visible in IOC, "ItemsActor" in this case.

```java
package info.smart_tools.examples.items.items_feature.items_actor_plugin;

import info.smart_tools.examples.items.items_feature.items_actor.ItemsActor;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class ItemsActorPlugin extends BootstrapPlugin {

     /**
     * Constructs the plugin.
     * @param bootstrap the bootstrap instance
     */
    public ItemsActorPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("items-actor-plugin")     // the unique name of the plugin item, the items may depend on each other
    public void init()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getOrAdd("ItemsActor"),    // the unique name of the actor in IOC
                new ApplyFunctionToArgumentsStrategy(
                        a -> {
                            try {
                                return new ItemsActor();
                            } catch (Exception e) {
                                throw new FunctionExecutionException(e);
                            }
                        }
                )
        );
    }
}
```

The Plugin Maven module depends on the Actor Maven module. So, add the dependency to `ItemsFeature/ItemsActorPlugin/pom.xml`.

```xml
        <dependency>
            <groupId>info.smart_tools.examples.items</groupId>
            <artifactId>items-feature.items-actor</artifactId>
            <version>0.1.0-SNAPSHOT</version>
        </dependency>
```

### Define the message Map

How it's time to create the Config for the Feature. You should define the Actor, how to get it from IOC, and the message Map, the chain of actors to process the message.

We define two Maps: one to get all items, another to add a new item. Our ItemsActor appears in both Maps, but the different handlers are used.

Modify the config of the Feature: `ItemsFeature/config.json`.

```json
{
  "featureName": "info.smart_tools.examples.items:items-feature",
  "afterFeatures": [],
  "objects": [
    {
      "name": "items-actor",
      "kind": "actor",
      "dependency": "ItemsActor"
    }
  ],
  "maps": [
    {
      "id": "get-all-items",
      "externalAccess": true,
      "steps": [
        {
          "target": "items-actor",
          "handler": "getAllItems",
          "wrapper": {
            "out_setAllItems": "response/items"
          }
        },
        {
          "target": "sendResponse"
        }
      ],
      "exceptional": [
      ]
    },
    {
      "id": "add-new-item",
      "externalAccess": true,
      "steps": [
        {
          "target": "items-actor",
          "handler": "addNewItem",
          "wrapper": {
            "in_getNewItemName": "message/name"
          }
        },
        {
          "target": "sendResponse"
        }
      ],
      "exceptional": [
      ]
    }
  ]
}
```

Because the Maps will serve external HTTP requests we explicitly allow external access to them with "externalAccess" property.

For each Actor in the Map it's necessary to define mapping of the message fields to the Wrapper methods. Note, "out" parameters correspond to setters in the Wrapper and transfers data _out from_ the Actor, while "in" parameters correspond to getters in the Wrapper and transfers data _into_ the Actor.

The "response/" prefix of the mapping values means we work with the response object, it'll pass back to the HTTP client at the end of the Map.

The "message/" prefix of the mapping values means we work with the fields of the Message passing between the Actors, here the HTTP request comes in.

Because our server will serve HTTP requests and it should return some responses, we add to the end of each Map the system message receiver named "sendResponse".

The "exceptional" property for each Map defines how to process exceptions during the processing. Here it's empty array.

### Build the Feature

Use `make` subcommand to build all the Features in the Project.

```console
$ das make
make project
[INFO] Scanning for projects...

... many output from Maven ...

[INFO] --- maven-assembly-plugin:3.0.0:single (default) @ items-feature-distribution ---
[INFO] Reading assembly descriptor: bin.xml
[INFO] Building zip: /home/gelin/work/7bits/smart-tools/tutorials/src/how_to_start/Items/project-distribution/items-feature-0.1.0-SNAPSHOT-archive.zip
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] items .............................................. SUCCESS [  0.570 s]
[INFO] items-feature ...................................... SUCCESS [  0.016 s]
[INFO] items-feature.items-actor .......................... SUCCESS [  3.652 s]
[INFO] items-feature.items-actor-plugin ................... SUCCESS [  0.874 s]
[INFO] items-feature-distribution ......................... SUCCESS [  0.802 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 6.149 s
[INFO] Finished at: 2017-06-30T13:03:43+06:00
[INFO] Final Memory: 22M/286M
[INFO] ------------------------------------------------------------------------
```

The result is zip archives of the Features in "project-distribution" folder.

```console
$ ls project-distribution
items-feature-0.1.0-SNAPSHOT-archive.zip
```

## Configure Endpoint

You have to provide some configuration for the HTTP endpoint of the Server.

Create the new Feature named "EndpointConfiguration".

```console
$ das cf -fn EndpointConfiguration
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
Creating feature ...
Feature has been created successful.
```

This Feature contains only config file. Modify `EndpointConfiguration/config.json`.

```json
{
  "featureName": "info.smart_tools.examples.items:endpoint-configuration",
  "afterFeatures": [],
  "objects": [
    {
      "name": "router",
      "kind": "raw",
      "dependency": "info.smart_tools.smartactors.message_processing.chain_call_receiver.ChainCallReceiver",
      "strategyDependency": "chain choice strategy"
    },
    {
      "name": "sendResponse",
      "kind": "raw",
      "dependency": "response sender receiver"
    }
  ],
  "maps": [
    {
      "id": "routing_chain",
      "steps": [
        {
          "target": "router"
        }
      ],
      "exceptional": [
      ]
    }
  ],
  "endpoints": [
    {
      "name": "mainHttpEp",
      "type": "http",
      "port": 9909,
      "startChain": "routing_chain",
      "maxContentLength": 4098,
      "stackDepth": 5
    }
  ]
}
```

You should add some message receivers. "router" is to forward the incoming requests to specified message maps. "sendResponse" is to transfer the message back to the HTTP client.

You should define the initial "routing_chain" where the HTTP requests start processing.

You should define the "endpoints" section with the TCP port the server will listen on and some other parameters.

Build all Features with `das`.

```console
$ das make
make project

... many output from Maven ...

[INFO] Reactor Summary:
[INFO]
[INFO] items .............................................. SUCCESS [  0.503 s]
[INFO] items-feature ...................................... SUCCESS [  0.015 s]
[INFO] items-feature.items-actor .......................... SUCCESS [  3.506 s]
[INFO] items-feature.items-actor-plugin ................... SUCCESS [  0.920 s]
[INFO] items-feature-distribution ......................... SUCCESS [  1.010 s]
[INFO] endpoint-configuration ............................. SUCCESS [  0.008 s]
[INFO] endpoint-configuration-distribution ................ SUCCESS [  0.085 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 6.304 s
[INFO] Finished at: 2017-06-30T17:17:18+06:00
[INFO] Final Memory: 24M/322M
[INFO] ------------------------------------------------------------------------
```

## Run the Server

It's time to deploy our Features to the Server.

### Create the Server folder

Go back to the initial folder outside of the Project hierarchy.

Create here the folder of the Server. Use `cs` subcommand. The Server name is "ItemsServer".

```console
$ das cs -sn ItemsServer
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
Creating server ...
... some warnings skipped ...
Server has been created successful.
```

The new folder "ItemsServer" is created. Check it's content.

```console
$ ls
Items  ItemsServer
$ ls ItemsServer
configuration.json  core  corefeatures  features  server.jar
```

The `server.jar` is the main entry point for the Server.

The `configuration.json` is the main configuration file of the Server.

### Download core

Use subcommand `dc` to download core jars for the Server. Point the path to the Server folder.

```console
$ das dc -path ItemsServer
Distributed Actor System. Design, assembly and deploy tools.
Version 0.3.3.
Download server core ...
... some warnings skipped ...
Server core has been downloaded successful.
```

Now the Server's "core" folder contains downloaded core libraries.

```console
$ ls ItemsServer/core
base-0.3.3                           feature-management-0.3.3         iobject-plugins-0.3.3            message-processing-interfaces-0.3.3       task-plugins.non-blocking-queue-0.3.3
configuration-manager-0.3.3          field-0.3.3                      ioc-0.3.3                        message-processing-plugins-0.3.3          utility-tools-0.3.3
configuration-manager-plugins-0.3.3  field-plugins-0.3.3              ioc-plugins-0.3.3                on-feature-loading-service-starter-0.3.3
core-service-starter-0.3.3           iobject-0.3.3                    ioc-strategy-pack-0.3.3          scope-0.3.3
dumpable-interface-0.3.3             iobject-extension-0.3.3          ioc-strategy-pack-plugins-0.3.3  scope-plugins-0.3.3
feature-loading-system-0.3.3         iobject-extension-plugins-0.3.3  message-processing-0.3.3         task-0.3.3
```

### Add core Features

We want our server to receive HTTP requests and reply with a response. So we need to add "http-endpoint" core Feature and it's dependencies.

Where to download the core Features and ids of their artifacts must be listed in `ItemsServer/corefeatures/features.json` file. Create this file.

```json
{
  "repositories": [
    {
      "repositoryId": "archiva.smartactors-features",
      "type": "default",
      "url": "http://archiva.smart-tools.info/repository/smartactors-features/"
    }
  ],
  "features": [
    {
      "group": "info.smart_tools.smartactors",
      "name": "http-endpoint",
      "version": "0.3.3"
    },
    {
      "group": "info.smart_tools.smartactors",
      "name": "http-endpoint-plugins",
      "version": "0.3.3"
    },
    {
      "group": "info.smart_tools.smartactors",
      "name": "endpoint",
      "version": "0.3.3"
    },
    {
      "group": "info.smart_tools.smartactors",
      "name": "endpoint-plugins",
      "version": "0.3.3"
    },
    {
      "group": "info.smart_tools.smartactors",
      "name": "endpoint-service-starter",
      "version": "0.3.3"
    },
    {
      "group": "info.smart_tools.smartactors",
      "name": "message-bus",
      "version": "0.3.3"
    },
    {
      "group": "info.smart_tools.smartactors",
      "name": "message-bus-service-starter",
      "version": "0.3.3"
    }
  ]
}
```

The server will download all listed Features during it's start.

Currently the server cannot download dependencies of the Features. So you have to list in `features.json` not only "http-endpoint", but also it's dependencies: "endpoint" and "message-bus", and some additional Features with the names ending with "-plugins" and "-service-starter".

### Add custom Features

Copy the zip archives of the Features you built to the "features" folder of the Server.

```console
$ cp Items/project-distribution/*.zip ItemsServer/features
$ ls ItemsServer/features
endpoint-configuration-0.1.0-SNAPSHOT-archive.zip  items-feature-0.1.0-SNAPSHOT-archive.zip
```

### Run the Server

Go to the Server's folder and start it from the command line.

```console
$ cd ItemsServer
$ java -jar server.jar

... skipped some warnings and stacktraces ...

[OK] Stage 1: server core has been loaded successful.


[INFO] Start downloading feature - 'http-endpoint-plugins'.
[INFO] Start downloading feature - 'http-endpoint'.
[INFO] Start downloading feature - 'message-bus'.
[INFO] Start downloading feature - 'endpoint-plugins'.
[OK] -------------- Feature 'endpoint-plugins' has been downloaded successful.
[OK] -------------- Feature 'message-bus' has been downloaded successful.
[INFO] Start downloading feature - 'endpoint-service-starter'.
[INFO] Start downloading feature - 'endpoint'.
[OK] -------------- Feature 'http-endpoint-plugins' has been downloaded successful.
[INFO] Start downloading feature - 'message-bus-service-starter'.
[OK] -------------- Feature 'message-bus-service-starter' has been downloaded successful.
[OK] -------------- Feature 'endpoint-service-starter' has been downloaded successful.
[INFO] Start unzipping feature - 'message-bus'.
[INFO] Start unzipping feature - 'http-endpoint-plugins'.
[OK] -------------- Feature 'http-endpoint-plugins' has been unzipped successful.
[OK] -------------- Feature 'message-bus' has been unzipped successful.
[INFO] Start unzipping feature - 'message-bus-service-starter'.
[INFO] Start unzipping feature - 'endpoint-service-starter'.
[OK] -------------- Feature 'endpoint-service-starter' has been unzipped successful.
[OK] -------------- Feature 'message-bus-service-starter' has been unzipped successful.
[INFO] Start unzipping feature - 'endpoint-plugins'.
[INFO] Start loading feature - 'endpoint-service-starter'.
[OK] -------------- Feature 'endpoint-plugins' has been unzipped successful.
[INFO] Start loading feature - 'message-bus-service-starter'.
[OK] -------------- Feature - 'info.smart_tools.smartactors:message-bus-service-starter' has been loaded successful.
[INFO] Start loading feature - 'message-bus'.
[OK] -------------- Feature - 'info.smart_tools.smartactors:endpoint-service-starter' has been loaded successful.
[OK] -------------- Feature - 'info.smart_tools.smartactors:message-bus' has been loaded successful.
[OK] -------------- Feature 'endpoint' has been downloaded successful.
[INFO] Start unzipping feature - 'endpoint'.
[OK] -------------- Feature 'endpoint' has been unzipped successful.
[INFO] Start loading feature - 'endpoint'.
[OK] -------------- Feature 'http-endpoint' has been downloaded successful.
[INFO] Start unzipping feature - 'http-endpoint'.
[OK] -------------- Feature 'http-endpoint' has been unzipped successful.
[OK] -------------- Feature - 'info.smart_tools.smartactors:endpoint' has been loaded successful.
[INFO] Start loading feature - 'endpoint-plugins'.
[INFO] Start loading feature - 'http-endpoint'.
[OK] -------------- Feature - 'info.smart_tools.smartactors:endpoint-plugins' has been loaded successful.
[OK] -------------- Feature - 'info.smart_tools.smartactors:http-endpoint' has been loaded successful.
[INFO] Start loading feature - 'http-endpoint-plugins'.
[FAILED] ---------- Feature 'info.smart_tools.smartactors:http-endpoint-plugins' loading has been broken with exception:
info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException:

Error occurred processing item "CreateHttpClient".
Processed items are: actor:http_request_sender, response, response_content_strategy, EndpointPlugin, CreateHttpEndpoint.
All items are: actor:http_request_sender, response, response_content_strategy, EndpointPlugin, CreateHttpEndpoint, CreateHttpClient, GetHeaderFromRequestRulePlugin, GetCookieFromRequestRulePlugin, GetQueryParameterFromRequestRulePlugin



[INFO] Feature group has been loaded: [
info.smart_tools.smartactors:message-bus - (OK),
info.smart_tools.smartactors:http-endpoint-plugins - (Failed),
info.smart_tools.smartactors:http-endpoint - (OK),
info.smart_tools.smartactors:endpoint-plugins - (OK),
info.smart_tools.smartactors:endpoint-service-starter - (OK),
info.smart_tools.smartactors:endpoint - (OK),
info.smart_tools.smartactors:message-bus-service-starter - (OK)]


[INFO] Start unzipping feature - 'endpoint-configuration'.
[INFO] Start unzipping feature - 'items-feature'.
[OK] -------------- Feature 'endpoint-configuration' has been unzipped successful.
[INFO] Start loading feature - 'endpoint-configuration'.
[OK] -------------- Feature 'items-feature' has been unzipped successful.
[INFO] Start loading feature - 'items-feature'.
[OK] -------------- Feature - 'info.smart_tools.examples.items:endpoint-configuration' has been loaded successful.
[OK] -------------- Feature - 'info.smart_tools.examples.items:items-feature' has been loaded successful.


[INFO] Feature group has been loaded: [
info.smart_tools.examples.items:items-feature - (OK),
info.smart_tools.examples.items:endpoint-configuration - (OK)]
```

### Test the Server

Our server listens on port 9909 and receives POST requests. You must provide "messageMapId" in each request.

```console
$ curl -X POST http://localhost:9909/ \
  -H 'content-type: application/json' \
  -d '{
    "messageMapId": "get-all-items"
  }'
{"items":[]}

$ curl -X POST http://localhost:9909/ \
  -H 'content-type: application/json' \
  -d '{
    "messageMapId": "add-new-item",
    "name": "new item"
}'
{}

$ curl -X POST http://localhost:9909/ \
  -H 'content-type: application/json' \
  -d '{
    "messageMapId": "get-all-items"
  }'
{"items":["new item"]}
```

You see the server accepts a POSTed JSON document as an incoming message. This message is passed to the Map defined in "messageMapId" property. The response, created in the Map is returned as a JSON body.
