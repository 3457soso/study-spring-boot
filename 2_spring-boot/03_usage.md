# 스프링 부트 개념과 활용

**출처 : [스프링 부트 개념과 활용 - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기

2. 스프링 부트의 원리

3. **스프링 부트의 핵심 기능**

   - [**SpringApplication**](#1-SpringApplication)
   - [**이벤트, 타입, 인자**](#2-이벤트,-타입,-인자)
   - [**외부 설정 (Properties)**](#3-외부-설정-Properties)
   - [**타입-세이프 프로퍼티 (@ConfigurationProperties)**](#4-타입-세이프-프로퍼티-@ConfigurationProperties)
   - [**프로파일**](#5-프로파일)
   - [**로깅**](#6-로깅)
   - [**테스트**](#7-테스트)

   

4. 스프링 부트의 기술 (웹 MVC)

5. 스프링 부트의 기술 (데이터베이스)

6. 스프링 부트의 기술 (보안 및 REST)

7. 스프링 부트의 운영 (Actuator)

  ​    

___

## 스프링 부트의 핵심 기능

### 1. SpringApplication

#### 1) 방법

- **기존 방법**으로는 커스텀하기 쉽지 않다.

  ```java
  SpringApplication.run(SpringInitApplication.class, args);
  ```

- 그래서 인스턴스를 먼저 만든 다음에 `run()`을 호출하는 방법을 사용한다.

  ```java
  SpringApplication app = new SpringApplication(SpringInitApplication.class);
  
  app.run(args);
  ```

- **빌더 패턴 사용하기**

  ```java
  new SpringAPplicationBuilder()
      .sources(SpringInitApplication.class)
      .run(args)
      .banner(...);
  ```



#### 2) 가능한 설정들

- 아무 설정도 바꾸지 않으면 기본적으로 **INFO** 레벨의 로그로 찍힌다. *(추후 더 배운다)*

  +) **디버그 모드** : 디버그모드로 앱이 동작하면서, **DEBUG** 레벨의 로그도 찍힌다.

  - 어떤 자동 설정이 적용이 됐는지, 왜 적용이 안됐는지도 나온다.

  - **설정 방법**
    - 실행 환경 설정에서 `VM options`에 `-Ddebug`를 쓰거나,

    - `Program arguments`에 `--debug`를 쓰면 된다.

      

- **배너 바꾸기** : `src/main/resource/banner.text`를 작성해주면 된다.

  - 온라인의... ASCII generator 같은거 써서 만들면 된다.

  - 가능한 변수들... : MENIFEST가 생성되어야 찍히는 애들도 있음!

    -  `${application.version}` : 현재 스프링 버전

  - 이미지 파일을 사용하고 싶다면? GIF 파일 같은것도 사용 가능하다.

  - **다른 위치**에 있는 걸 가져오고 싶으면?

     `application.properties`에서  `spring.banner.location`으로 설정해준다.

  - **java 코드로도 커스텀이 가능하다!**

    ```java
    app.setBanner(new Banner() {
        @Override
        public void printBanner(Environment environment, Class<?> source out) {
            out.println("뭐시기");
        }
    })
    app.setBannerMode(Banner.Mode.OFF); // 배너 모드 끄기
    ```

    만약 두 가지 방법으로 동시에 세팅한다면 **text파일이 우선한다**



___


### 2. 이벤트, 타입, 인자

#### 1) ApplicationEvent

- 애플리케이션의 상태와 관련된 다양한 기본적인 이벤트들이 있다.

- **생성 방법**

  ```java
  @Component // 사실 얘는 빈으로 등록해봤자 의미가 없다. 이유는 후술!
  public class SampleListener implements ApplicationListener<ApplicationStartingEvent> {
      @Override
      public void onApplicationEvent(ApplicationStartingEnvent e) {
          ...
      }
  }
  ```

- 리스너를 빈으로 등록해놓으면, 등록된 빈 중에 해당하는 이벤트의 리스터를 알아서 실행하게 된다.

  **!!!!근데 그 이벤트가 언제 발생하느냐가 중요한 기점이 된다!!!!**

  - `ApplicationContext`가 생성된 이후에 발생하는 이벤트는, 해당 리스너가 빈이면 호출된다.

  - 하지만 이후에 발생하는 이후에는 호출되지 않는다.

    > 이 예제 코드에서도, 해당 리스너가 발동할 때, `ApplicationContext`가 생성되지 않았기 때문에 빈으로 등록해도 동작하지 않는다.

  - **이럴때는 직접 실행해줘야 한다.**

    ```java
    app.addListener(new SampleLister()); // SpringApplication 커스터마이징
    ```

  - 만약 `ApplicationStartingEvent`가 아니라 `ApplicationStartedEvent`라면...

    이미 `ApplicationContext`가 생성된 이후이므로 그냥 빈으로 등록해서 쓴다.

- **[주의]** : `ApplicationListener`의 **제네릭 부분**에 꼭 타입을 줘야 함!

- **[주의]** : `ApplicationContext`의 **전후 생성 시점**에 주의해서 사용해야 함!

  

#### 2) WebApplicationType

- `WebApplicationType`은 조정 가능하다.

  - `NONE`

  - `SERVLET` : 기본값. 서블릿이 있으면 무조건! 스프링 웹 MVC로 돈다.

  - `REACTIVE` : 서블릿이 없고 웹플럭스가 있다면 이걸로 돈다.

    +) 만약 서블릿과 웹플럭스 둘 다 있다면 **SERVLET**으로 돈다.

    +) **REACTIVE**로 설정하고 싶다면 명시해줘야 한다.

    ```java
    app.setWebApplicationType(WebApplicationType.REACTIVE);
    ```



#### 3) ApplicationArguments

- ApplicationArguments는  **``--``으로 들어오는 것들**을 말한다.
- `VM options`와 다르다는 것을 구별하고 있어야 한다.
  - 둘 다 콘솔로 들어오는 거긴 하지만, **-옵션**과 **--옵션**으로 들어온다는 점이 다르다.
- `ApplicationArguments`로 빈으로 등록되어 있기 때문에 가져다 쓰면 된다.
- 해당 인자들에 대해서 추상화되어있는 메소드들이 있기 떄문에 갖다 쓰면 됨!



#### 4) ApplicationRunner (+ CommandLineRunner)

- 애플리케이션이 실행된 이후에 무언가를 실행하고 싶을 때 사용한다.

- `ApplicationRunner`는 `ApplicationArguments`의 메소드를 그대로 갖다 쓸 수 있다.

  ```java
  @Component
  public class SampleRunner implements ApplicationRunner {
      @Override
      public void run(ApplicationArguments args) throws Exception {
          
      }
  }
  ```

  만약 `AppliationRunner`가 **여러개**라면?

  - `@Order(1)` : 어노테이션을 써서 순서를 정해준다 (숫자가 낮을수록 우선!)

    

- `CommandLineRunner`는 ... 쓰기 복잡하다. 출력만 해도 다음과 같이 써야한다.

  ```java
  @Override
  public void run(String... args) throws Exception {
      Arrays.stream(args).forEach(System.out::println);
  }
  ```



___

### 3. 외부 설정 Properties

#### 1) 정의

- 애플리케이션에서 사용하는 **여러가지 설정 값**들을 애플리케이션의 밖, 또는 안에 지정해놓는 것.

- 주로 `application.properties`로 사용한다. 

  - 스프링부트가 애플리케이션을 구동할 때 **자동으로 로딩**하는 파일 이름임!

  - 정의되어 있는 파일 안에 **key-value** 형식으로 정의해 놓고,

    `@Vaule("${key}")` 이런 식으로 가져다가 쓰면 됨!

- 프로퍼티에는 보통 `Environment` 빈을 통해서 접근할 수 있다.

  `environment.getProperty("key");`

- **properties**, **YAML**, **Environment Arguments**, **Command Line Arguments** 등이 있음!



#### 2) 설정 팁

- 랜덤 값 설정하기 : `${random.*}`
- 플레이스 홀더 : `fullName = ${name} Park`



#### 3) 프로퍼티 우선 순위

- **필요한 이유**

  - 프로퍼티를 정의할 수 있는 수단은 다양하다.
  - 이 값들은 다른 방법으로 **오버라이딩**이 가능하기 때문에, 무엇을 우선으로 하는지 참고해야 함!

  

- **우선 순위 정리**

  1. 유저 홈 디렉토리에 있는 `spring-boot-dev-tools.properties `

  2. **테스트**에 있는 `@TestPropertySource` > `@SpringBootTest`의 properties 속성

     - 테스트 폴더 내에 `resource` 폴더를 만들어 `application.properties`를 직접 지정

     - 이 때는 Project Structure - Modules - Sources 에서 해당 폴더를 **Test Resources**로 바꿔줘야 함!

     - 이렇게 쓰면, 기존 **main의 properties**보다 **test의 properties**가 우선하게 된다.

     - **[주의]** 테스트 프로퍼티엔 없고 메인 프로퍼티에는 있을 경우, **테스트 코드가 깨질 수 있음!**

       > - 그러면... 테스트 프로퍼티 자체를 없애고, 바꾸고 싶은 값은 어노테이션으로 바꾸자.
       > - `test.properties` 등 다른 이름으로 만들어서 추가하자.
       > - `application.properties`의 경로를 서로 다르게 하자.

  3. **커맨드 라인 아규먼트** 

     - ``--server.port=8080` 처럼 cmd 인자로 들어오는 것.
     - 끄고 싶으면 `SpringApplication.setAddCommandLineProperties(false);`

  4. SPRING_APPLICATION_JSON (환경 변수 또는 시스템 프로퍼티) 에 들어있는 프로퍼티 

  5. ServletConfig 파라미터 > ServletContext 파라미터 

  6. java:comp/env JNDI 애트리뷰트 

  7. System.getProperties() 자바 시스템 프로퍼티 

  8. OS 환경 변수 

  9. RandomValuePropertySource 

  10. **application.properties** 자체

      JAR 밖 ( **>** 안) 에 있는 특정 프로파일용 application properties **>** 

      JAR 밖 ( **>** 안) 에 있는 application properties 

  11. **@PropertySource** 

  12. **기본 프로퍼티 (SpringApplication.setDefaultProperties)**

      - 아무 것도 안 써도 자동으로 들어오는 것들 (Ex. 자동 설정)

  

------

### 4. 타입-세이프 프로퍼티 @ConfigurationProperties

#### 1) 목적 및 장점

- `application.properties`가 여러 곳에 분산되어 있으면 귀찮으니까, 한 곳에 모으자!

- 프로퍼티 자체를 **빈으로 등록**해서 다른 빈에 주입해줄 수 있도록 하자!

- **융통성 있게 바인딩** 해줄 수 있다.

  - `application.properties`에 다양한 케이스로 작성해도 바인딩해준다.
  - **camelCase**, **snake_case**, **kebab-case**, **CAPITALIZATION**

- **프로퍼티 타입 컨버전** : 프로퍼티 자체는 문자열이지만, 이게 int형 등으로 **변환**되어 들어간다.

  - `@DurationUnit` : 시간 정보를 받고 싶을 때 사용 가능

    ```java
    @DurationUnit(ChronoUnit.MINUTES)
    private Duration duration = Duration.ofSeconds(300);
    
    >> PT5M
    ```

- **프로퍼티 값 검증** : 각 값들의 유효성을 검사해줄 수 있다.

  - `@Validated`를 붙여주고, 각 필드에 `@NotEmpty`나 `@NotNull` 등 검사하고 싶은 걸 붙여주면 된다.

    

#### 2) 방법

- 이 때, `@ConfigurationProperties`를 추가하고 나면 오류가 난다.

  > **Spring Boot Configuration Annotation Processor not found in classpath**
  >
  > - 어노테이션이 달려 있는 클래스를 조사해서, 자동 완성을 이용할 수 있도록 
  > - 프로젝트를 빌드할 때 **메타 정보를 생성해 주는 플러그인**을 추가해주라는 것!

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
  	<artifactId>spring-boot-configuration-processor</artifactId>
  	<optional>true</optional>
  </dependency>
  ```

  > **Not registered via @EnableConfigurationProperties or marked as Spring component**
  >
  > - 해당 클래스를 빈으로 등록해야 어디서든 가져다 쓸 수 있다.
  > - 아래 두 방법 중 하나만 쓰면 되는데, 사실 그냥 후자로 하면 된다.

  ```java
  @EnableConfigurationProperties(SoyoungProperties.class)
  public class SpringInitApplication { ... }
  
  @Component
  @ConfigurationProperties("soyoung")
  public class SoyoungProperties { ... } 
  ```

  

#### 3) Third-party Configuration

- 클래스가 외부에 있거나... 따로 JAR 파일 안에 있을 경우에는 여기에 `@Component`를 붙일 수 없다.

- 이런 경우에는 `@Bean`으로 만들고 그 위에 `@ConfigurationProperties`를 붙일 수 있다

  +) *흔한 경우는 아님!*



------

### 5. 프로파일

#### 1) ApplicationEvent

- 애플리케이션의 상태와 관련된 다양한 기본적인 이벤트들이 있다.



___

### 6. 로깅

#### 1)

- 



___

### 7. 테스트

#### 1) 

- 