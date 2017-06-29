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

__Project__ — it's presumed you work in some project, to create some Features and Actors to run they in the Server. The source code of the project is located in a folder, separated from the SmartActors sources or where you run your Server.

__Feature__ — a named set of functionality (Actors, Plugins and Maps) to run in the Server. Typically are distributed as zip archives, are extracted to separated folders by the Server. A Feature may depend on other Features.

__Core Feature__ — a Feature, provided by the SmartActors developers. As any Feature, it gives a new functionality to the Server. It's downloaded automatically when the Server starts. Core Features provide some basic functionality to the Server, while ordinary Features add some specific business logic.

__Actor__ — a Java class which provides the minimal, independent and atomic set of functionality. Actors should be combined to Maps (or Chains) to do something useful within a Feature. Actors in the map receives and sends messages to each other.

__Wrapper__ — an object which is passed to the methods of an Actor. It wraps the actual message passed in the Chain into the interface provided by the author of the Actor. This interface is also called Wrapper.

__Config__ — a `config.json` file, a part of a Feature. Configs from all loaded Features are joined together and form an aggregate config of the Server. The config defines: set of objects to create in the Server's IOC, typically they are Actors; Maps; other metadata and handlers of system events.

__Map__, __Chain__ — the list of actors in the specified order to process a message step by step. On each step the message is wrapped and passed to the Actor's handler, the Actor makes some modifications in the message atomically, then the message is passed to the next Actor. This is the core idea, how independent Actors works together.

__Plugin__ — a part of a Feature, contains code to initialize the Feature, in most cases registers new strategies to IOC. Plugins may depend on other plugins.

__IOC__, __Inversion of Control__, __IOC Container__ — global "storage" of all the objects in the system. The preferable way to take an object in the system is to ask IOC to resolve some parameters and get the object reference.

## Requirements

## Create the Project

### Create the Feature

### Create the Actor

### Create one more Actor

### Create the Plugin

### Build the Feature

## Run the Server

### Download core

### Define core Features

### Add custom Features

### Run the Server

