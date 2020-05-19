---
layout: page
title: IOC Dependencies
description: Guide for working with IOC dependencies
group: userguide
---

# IOC-зависимости
## Введение
Данный документ предназначен для описания принципов работы с IOC-зависимостями.

## Регистрация IOC-зависимостей
Все сущности системы (акторы, правила преобразования) на старте системы регистрируются в системном сервис-локаторе (IOC) под тем именем, которое указал разработчик. Общие рекомендации по наименованию следующие:
1. Не используйте имена классов в каком-либо виде, ни краткое имя, ни полное каноничное имя.
2. Имя IOC-зависимости должно полно и явно описывать суть зависимости, оно должно быть достаточно коротким, не более 4-6 слов.
3. Имя IOC-зависимости пишется на английском языке с использованием пробелов в качестве разделителя между словами.
4. Если нужно указать область видимости IOC-зависимости, то сперва пишется имя области, потом `#`, затем имя самой зависимости.

### Примеры
Рассмотрим работу с IOC-зависимостями на трех примерах - актор, правило или стратегия, прикладная зависимость

#### Актор
Предположим, что у нас есть актор `StatusCodeSetter`, который выполняет следующее - ему на вход подается статус код, который необходимо отправить в ответ пользователю, он выставляет его в тело ответа. Поэтому его можно зарегистрировать в IOC под именем `"status code setter"`.

Обратите внимание, что, как было сказано выше, тут не используется имя класса StatusCodeSetter в каком-либо виде. Если по каким-то причинам имя класса актора изменится, то имя IOC-зависимости не изменится, и его все так же можно будет получить из IOC под одним и тем же именем.

#### Правило или стратегия
Правило или стратегия реализуют интерфейс `IStrategy`, но в зависимости от контекста использования, они могут отличаться. Так, стратегия обычно используется где-то в коде плагинов или акторов, а правила - непосредственно в цепочках перед или после вызова метода обертки (например, правило преобразования из одного типа в другой).

Принцип наименования правил или стратегий в общем случае одинаковый, единственное отличие заключается в том, что будет на конце имени. Если мы имеем дело с правилом, то имя IOC-зависимости будет выглядеть так - `"convert integer to string rule"`. Если же мы имеем дело со стратегией, то `rule` заменяется на `strategy`, т.е. `"build iobject strategy"`.

#### Прикладная зависимость
Под прикладной зависимостью тут подразумевается любая зависимость, которая создается непосредственно прикладным разработчиком.

Например, есть интерфейс `IPropertiesReader`, у которого есть метод чтения properties. У этого интерфейса есть две реализации - `FilePropertiesReader`, которая читает properties из файловой системы, и `NetworkPropertiesReader`, которая читает properties с удаленного ресурса.

Поскольку они принадлежат к одному интерфейсу, мы их регистрирует в IOC следующим образом - `"properties reader#file"` и `"properties reader#network"`. Здесь `properties reader#` задает область видимости, чтобы не возникло конфликтов при появлении какой-то другой реализации другого интерфейса, который тоже может читать из файлов.

Теперь для того, чтобы достать из IOC реализацию `IPropertiesReader` мы указываем не только имя самой зависимости, но и область видимости, т.е. `"properties reader#file"` вернет нам `FilePropertiesReader`, и мы сможем считать properties из файла.