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

__Map__, __Chain__ — the list of actors in the specified order to process a message step by step. On each step the message is wrapped and passed to the Actor's handler, the Actor makes some modifications in the message atomically, then the message is passed to the next Actor. This is the core idea, how independent Actors work together.

__Plugin__ — a part of a Feature, contains code to initialize the Feature, in most cases registers new strategies to IOC. Plugins may depend on other plugins.

__IOC__, __Inversion of Control__, __IOC Container__ — global "storage" of all the objects in the system. The preferable way to take an object in the system is to ask IOC to resolve some parameters and get the object reference.

## Requirements

You need Debian-base Linux distribution.

You need [OpenJDK](http://openjdk.java.net/install/index.html) 8 or later to run and compile. Note you need `jdk` packages, not `jre`.

You need [Apache Maven](https://maven.apache.org/install.html) 3 or later to build your project.

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

`pom.xml` file is the Maven parent module for your project.

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

Note that Project's `pom.xml` references Feature's `pom.xml` as a submodule. And Feature references Distribution.

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

Note in this case the Actor is the owner of the items list. All operations over the list should be performed through this Actor.

#### Write exceptions

Typically each actor has it's own exception type throwing from its methods.

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

Copy and rename `Items/ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/wrapper/ItemsActorWrapper.template` into `GetAllItemsWrapper.java` and `AddNewItemWrapper.java`.

`GetAllItemsWrapper` is the interface with a method to set a list of items. These are data going _out_ of the Actor, so the Actor should _set_ the list to the processing message.

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

Take the file `Items/ItemsFeature/ItemsActor/src/main/java/info/smart_tools/examples/items/items_feature/items_actor/ItemsActor.java` and modify it.

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
     * Add the new item to the list.
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

Modify `/home/gelin/work/7bits/smart-tools/tutorials/src/how_to_start/Items/ItemsFeature/ItemsActor/src/test/java/info/smart_tools/examples/items/items_feature/items_actor/ItemsActorTest.java`.

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

### Build the Feature

## Run the Server

### Download core

### Define core Features

### Add custom Features

### Run the Server

