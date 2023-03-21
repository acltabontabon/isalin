# Isalin

![isalin-version](https://img.shields.io/badge/version-0.1.0-blue)
![isalin-license](https://img.shields.io/github/license/acltabontabon/isalin)

Isalin is a spring-boot library that aims to provide a less boilerplate and convenient way of using the Google translate API.

# Usage

### Setting Up
##### Apache Maven 
```xml
<dependency>
    <groupId>com.acltabontabon</groupId>
    <artifactId>isalin</artifactId>
    <version>0.1.0</version>
</dependency>
```

##### Gradle
```
implementation group: 'com.acltabontabon', name: 'isalin', version: '0.1.0'
```

### Using `@Translate`
#### Translating simple text
```java
@Translate(from = Language.ENGLISH, to = Language.FILIPINO)
public String getGreetings() {
  return "Hello world!";
}
```

#### Translating simple text with auto detection of source language
```java
@Translate(to = Language.FILIPINO)
public String getGreetings() {
  return "Hello world!";
}
```

#### Translating text within a custom object
```java
@Translate(value = "$.body.content", to = Language.FILIPINO)
public CustomMessage getGreetings() {
  CustomMessage msg = new CustomMessage();
  msg.setSource("Tarzan");
  msg.setBody(new Content("Welcome to the jungle!"));

  return msg;
}
```

# Motivation
Created this library for fun and learning. If you somehow find this helpful and/or useful, I'd be grateful for a cup of coffee. :grin: :coffee:


<a href='https://ko-fi.com/acltabontabon' target='_blank'><img style='height:30px;' src='https://az743702.vo.msecnd.net/cdn/kofi3.png?v=1' border='0' alt='Buy Me a Coffee at ko-fi.com'></a>

# Request / Issues
For feature request or found an issues please [open a ticket](https://github.com/acltabontabon/isalin/issues).
