# Isalin

![isalin-version](https://img.shields.io/badge/version-1.0.1-blue)
![isalin-java-comp](https://img.shields.io/badge/java-17%2B-blue)
![isalin-spring-comp](https://img.shields.io/badge/spring--boot-3.x-blue)
![isalin-license](https://img.shields.io/github/license/acltabontabon/isalin)
[![CodeQL](https://github.com/acltabontabon/isalin/actions/workflows/codeql.yml/badge.svg)](https://github.com/acltabontabon/isalin/actions/workflows/codeql.yml)
[![Dependency Review](https://github.com/acltabontabon/isalin/actions/workflows/dependency-review.yml/badge.svg)](https://github.com/acltabontabon/isalin/actions/workflows/dependency-review.yml)

Isalin is a spring-boot library that aims to provide a less boilerplate and convenient way of using the Google translate API.

# Features
- Text translation
- Document translation
- Provides `@Translate` annotation to translate the response of a method
- Provides spring bean named `IsalinService` for in-line usage
- Supported all languages listed [here](https://cloud.google.com/translate/docs/languages)
- Works with Google SDK V3

# Usage

### Setting Up Your Project
##### Apache Maven 
```xml
<dependency>
    <groupId>com.acltabontabon</groupId>
    <artifactId>isalin</artifactId>
    <version>1.0.1</version>
</dependency>
```

##### Gradle
```
implementation group: 'com.acltabontabon', name: 'isalin', version: '1.0.1'
```

### Using `@Translate`

To start using Isalin library, you need to setup first your [Google Authentication credentials](https://cloud.google.com/docs/authentication/provide-credentials-adc).

#### Single text translation
```java
@Translate(from = Language.ENGLISH, to = Language.FILIPINO)
public String getGreetings() {
  return "Hello world!";
}
```

#### Single text translation with auto detection of source language
```java
@Translate(to = Language.FILIPINO)
public String getGreeting() {
  return "Hello world!";
}
```

#### Single text translation within a custom object
```java
@Translate(value = "$.body.content", from = Language.ENGLISH, to = Language.FILIPINO)
public CustomMessage getGreeting() {
  CustomMessage msg = new CustomMessage();
  msg.setSource("Tarzan");
  msg.setBody(new Content("Welcome to the jungle!"));

  return msg;
}
```

#### Multiple text translation
```java
@Translate(from = Language.ENGLISH, to = Language.FILIPINO)
public List<String> getGreetings() {
  return List.of("Hello!", "Hi");
}
```

#### Single file translation
```java
@Translate(from = Language.ENGLISH, to = Language.FILIPINO)
public File getDocument() {
  return new File("/path/to/my/file.pdf");
}
```

> **Note**
> Supported file formats: `.doc`, `.docx`, `.pdf`, `.ppt`, `.pptx`, `.xls`, `.xlsx`

#### Multiple file translation
```java
@Translate(from = Language.ENGLISH, to = Language.FILIPINO)
public List<File> getDocuments() {
  return List.of(new File("/path/to/my/file.pdf"), new File("/path/to/my/file2.pdf"));
}
```


#### Inline translation
```java
@Autowired
private IsalinService isalinService;
    
private void translate() {
  String text  = isalinService.translateText("Hello", Language.ENGLISH, Language.FILIPINO);
  List<String> texts  = isalinService.translateTexts(List.of("Hi","Hello"), Language.ENGLISH, Language.FILIPINO);
  
  File doc  = isalinService.translateDocument("/path/to/my/file.pdf", Language.ENGLISH, Language.FILIPINO);
  List<File> docs  = isalinService.translateDocuments(List.of("/path/file.ppt","/path/file.pdf"), Language.ENGLISH, Language.FILIPINO);
  
  // auto detection of source language
  String text2  = isalinService.translateText("Hello", Language.ANY, Language.FILIPINO);
  File doc2  = isalinService.translateDocument("/path/to/my/file.pdf", Language.ANY, Language.FILIPINO);
}
 ```

# Motivation
Created this library for fun and learning. If you somehow find this helpful and/or useful, I'd be grateful for a cup of coffee. :grin: :coffee:


<a href='https://ko-fi.com/acltabontabon' target='_blank'><img style='height:30px;' src='https://az743702.vo.msecnd.net/cdn/kofi3.png?v=1' border='0' alt='Buy Me a Coffee at ko-fi.com'></a>

# Request / Issues
For feature request or found an issues please [open a ticket](https://github.com/acltabontabon/isalin/issues).
