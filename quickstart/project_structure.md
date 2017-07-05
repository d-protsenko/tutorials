---
layout: page
title: "Project structure"
description: "Project structure - best practises"
group: quickstart
---

# Project structure

_Before reading this guide, look through this instructions: [How to start using SmartActors](how_to_start.md)_

## Dirs structure

```
|-- Project
    |-- Features
    |   |-- das.data
    |   |-- pom.xml
    |   |-- SomeSuperFeature
    |   |   |-- SomeSuperActor
    |   |   |-- SomeSuperFeatureDistribution
    |   |   |-- pom.xml
    |   |   |-- config.json
    |   |-- ...
    |-- ServerParts
    |   |-- corefeatures
    |   |    |-- features.json
    |   |-- configuration.json
    |   |-- server.jar
    |-- etc
    |   |-- nginx
    |   |   |-- site.conf
    |   |-- systemd
    |   |   |-- project.conf
    |   |-- ...
    |-- Makefile
    |-- README.md
    |-- .gitignore
    |-- .editorconfig
    |-- docker-compose.yml ## this is if you would like to use docker.
```

## .gitignore

Supposing you use git, gitignore must have this lines:

```
*.class
*.jar
**/target/

project-distribution
## put server jar in repo!!!
!ServerParts/server.jar
ServerParts/corefeatures
## do not forget to add corefeatures config
!ServerParts/corefeatures/features.json
ServerParts/downloads/
ServerParts/core
ServerParts/features
*.zip
```

## Makefile

To specify the way of building system strictly we will use `Makefile`. Do not use instructions in the readme, because you'll definitely reach instructions-hell.

```make
all: downloadDependencies start

downloadDependencies:
	cd ServerParts && das dc

build:
	cd Features && das make

run:
	cd ServerParts && java -jar server.jar

copyFeatures:
	mkdir -p ServerParts/features && cp Features/project-distribution/*.zip ServerParts/features

start: build copyFeatures run
```

Thus, when you download a repo first time, you have to run only `make`. To just start the server run `make start`.


## .editorconfig

In our projects I recommend to use this editor configuration.

```ini
# EditorConfig is awesome: http://EditorConfig.org

# top-most EditorConfig file
root = true

# Unix-style newlines with a newline ending every file
[*]
end_of_line = lf
insert_final_newline = true
charset = utf-8

# Tab indentation (no size specified)
[Makefile]
indent_style = tab

# Matches the exact files either package.json or .travis.yml
[*.json]
indent_style = space
indent_size = 2

[*.{xml,java}]
indent_style = space
indent_size = 4
```
