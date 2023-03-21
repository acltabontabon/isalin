# Isalin
Is a spring-boot library that aims to provide a less boilerplate and convenient way of using the Google translate API.


# Motivation
Created this library for fun and learning. If you find this helpful and/or useful I'd be really grateful if you can <a href='https://ko-fi.com/acltabontabon' target='_blank'><img height='35' style='border:0px;height:26px;' src='https://az743702.vo.msecnd.net/cdn/kofi3.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' />


# Usage

### Translating simple text
```java
@Translate(from = Language.ENGLISH, to = Language.FILIPINO)
public String getGreetings() {
  return "Hello world!";
}
```

### Translating simple text with auto detection of source language
```java
@Translate(to = Language.FILIPINO)
public String getGreetings() {
  return "Hello world!";
}
```

### Translating text within a custom object
```java
@Translate(value = "$.body.content", to = Language.FILIPINO)
public CustomMessage getGreetings() {
  CustomMessage msg = new CustomMessage();
  msg.setSource("Tarzan");
  msg.setBody(new MockPayload.Body("Welcome to the jungle!"));

  return msg;
}
```
