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
   - [**Spring-Boot-Devtools**](#8-Spring-Boot-Devtools)

4. 스프링 부트의 기술 (웹 MVC)

5. 스프링 부트의 기술 (데이터베이스)

6. 스프링 부트의 기술 (스프링 시큐리티)

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

#### 1) 정의

- 스프링 프레임워크에서 제공해주는 기능으로,

  특정 프로파일에서만 **특정 기능을 동작**시키거나, **특정 빈을 등록**하는 등의 역할을 한다.

  

#### 2) 사용법

- **어노테이션 사용하기**

  1. 먼저 `@Profile` 어노테이션을 통해 언제 사용할 지 설정해준다.

     ```java
     @Profile("prod") // 이 설정 파일 자체가 prod 상태일 때 사용된다.
     @Configuration
     public class BaseConfiguration {
         @Bean
         public String hello() {
             return "hello";
         }
     }
     ```

  2. 그리고 어떤 프로파일을 활성화 할 것인지를 `application.properties`에 설정해준다.

     ```properties
     spring.profiles.active = prod
     ```

- **프로파일용 프로퍼티 생성하기**

  - `application-{profile}.properties`을 생성한다.

  - 프로파일용 프로퍼티는 기존 `application.properties`보다 우선한다.

  - 아래와 같은 방법으로 **다른 프로파일도 활성화** 시킬 수 있다.

    ```properties
    spring.profiles.include = profileName
    ```

    

___

### 6. 로깅

#### 1) 종류

- 스프링 부트는 기본적으로 **Commons Logging**을 사용하지만, 보통 **SLF4j**를 쓰게 된다.

  +) 스프링 프레임워크에서는 **Commons Logging**을 써왔다고 함!

  - 보통 **Commons Logging**을 exclude 시키고 **SLF4j**를 써야 했었는데, 이젠 안 그래도 된다고 함!
  - 스프링 부트에서는 최종적으로 **SLF4j**가 쓰이고, 로그는 **Logback**이 찍는다고 보면 된다!

- **로킹 퍼사드 VS 로거**

  - 로깅 퍼사드 : **Commons Logging**와 **SLF4j**

    - 로깅을 하는 친구들이 아니고, 로거 API를 **추상화**해 둔 인터페이스이다.
    - 쓰면 추후 로거를 바꿔 쓸 수 있기 때문에 편한다.

  - 로거 : **JUL**, **Log4J2**, **Logback** 등등 ...

    

#### 2) 내용

- **날짜 시간 레벨 PID --- 스레드이름 패키지경로 : 메시지** 순으로...

  ```
  2018-12-27 13:30:01.503  INFO 7883 --- [           main] 
  m.s.bootproject.SpringInitApplication    : 
  Starting SpringInitApplication on ubuntu-900X5N with PID 7883
  ```

  

- 만약 더 많은 내용을 주고 싶으면 `-Ddebug`나 `--debug`를 준다.

  - 이걸 쓰면 코어 로거들 (**embeded container**, **Hibernate**, **Spring Boot**) 만 디버그로 찍어준다.

- 모든 메시지를 다 찍어주고 싶으면 `--trace`를 찍는다.



#### 3) 설정

- 아래 설정들은 모두 `application.properties`에 추가한다.

- **로그를 컬러로**

  ```properties
  spring.output.ansi.enabled = always
  ```

- **로그 파일 출력** : 크기나 그런 건 따로 설정해줄 수 있다.

  ```properties
  logging.file = log.txt // 로그 파일을 설정하는 것
  logging.path = /logs   // 디렉토리를 설정하는 것
  ```

- **로그 레벨 조정** : 패키기별로 로그의 레벨을 설정해 줄 수 있다.

  ```properties
  logging.level.me.soyoungpark.springboot = DEBUG
  logging.level.org.springframework = DEBUG // 몽땅 디버그로 찍어버리기!
  ```



#### 4) 커스텀

- 실제로 쓸 때는 사내의 로그 시스템과 연동을 해야 하기 때문에 **특점 시점에 특정 위치에 특정 포맷으로** 남겨야!

- 커스텀 로그 설정 파일을 만들자

  - **Logback** *(추천)* : `logback-spring.xml`

    - **Logback extension** : 스프링에서는 **Logback**에 대한 추가 설정을 지원해준다.

      - 사내에 적합한 로그 설정으로 바꿔 설정해주면 된다.

      - **프로파일** 설정 : `springProfile name="프로파일">`

        특정 프로파일에서는 어떤 모드로 찍을건지 등등을 각자 설정해줄 수 있다.

      - **Environment 프로퍼티** 설정 `<springProperty>`

  - **Log4J2** : `log4j2-spring.xml`

    - 로거를 **Log4J2**로 변경하고 싶다면... 기존 로깅 빼주고 **log4j2**를 넣어주면 된다.

      ```xml
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-web</artifactId>
          <exclusions>
              <exclusion>
                  <groupId>org.springframework.boot</groupId>
                  <artifactId>spring-boot-starter-logging</artifactId>
              </exclusion>
          </exclusions>
      </dependency>
      
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-log4j2</artifactId>
      </dependency>
      ```

  - **JUL** *(비추하신단다)* : `logging.properties`

  

___

### 7. 테스트

#### 1) 스프링 부트의 테스트 

- 테스트의 시작은 의존성부터!

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
  </dependency>
  ```

- `@SpringBootTest`는 무슨 일을 할까?

  1. `@SpringBootApplication`을 찾아간다.  여기서부터 몽땅 **빈을 스캔**한다.

  2. 해당 빈들을 테스트용 `ApplicationContext`를 만들면서 **빈으로 등록**한다.

  3. 만약 `@MockBean`이 있다면, 해당 빈은 **Mock**으로 교체해준다. `@Test` 마다 갱신도 해준다.

     

#### 2) 테스트 환경의 종류

- 이후 테스트에 사용할 클래스에 어노테이션을 붙여준다.

  ```java
  @RunWith(SpringRunner.class)
  @SpringBootTest
  public class SampleControllerTest { ... }
  ```

  빈 설정 파일은 `@SpringBootApplication`에서 알아서 찾아준다.

  - `webEnvironment` : `@SpringBootTest(webEnvironment = 뭐시기)` 식으로 쓴다.

    - **MOCK** : 내장 톰캣을 띄우지 않고, 서블릿을 모킹. 

      - `DispatcherServlet`에 요청을 보내는 것 같은 느낌은 들지만 아니다!

      - 쓰기 위해서는 `@AutoConfigureMockMvc`를 붙이고, `MockMvc`를 주입받으면 된다.

        ```java
        @RunWith(SpringRunner.class)
        @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
        @AutoConfigureMockMvc
        public class SampleControllerTest {
            @Autowired
            MockMvc mockMvc;    
        }
        ```

        

    - **RANDOM_PORT, DEFINED_PORT** : 실제로 내장 톰캣을 사용하고, 서블릿이 뜬다. 

      - `TestRestTemplate`나 테스트용 `webTestClient`를 써야 한다.

      - 실제로 내장 톰캣 서버에 요청을 보내고 그 결과를 받아온다.

      - **WebTestClient** : Async하다. 콜백으로 처리 결과를 받아올 수 있다.

        +) 쓰려면 spring webflux와 관련된 dependency를 가지고 있어야 한다. 

        ```xml
        <dependency>
        	<groupId>org.springframework.boot</groupId>
        	<artifactId>spring-boot-starter-webflux</artifactId>
        </dependency>
        ```

        +) 이게 여러모로 편해서 많이 쓴다고 함!

    - **NONE**

#### 3) @MockBean

- `ApplicationContext`에 들어있는 빈을 `MockBean` 어노테이션을 붙여서 가져오면,

  - 해당 빈을 **Mock**으로 만든 객체로 교체해서 쓴다.
  - 모든 `@Test` 마다 자동으로 새로 리셋된다.

  ```java
  public class SampleControllerTest {
      @MockBean
      SampleService mockSampleService;    
      ...
  }
  ```



#### 4) 슬라이스 테스트

- 레이어별로 잘라서 테스트를 하고 싶을 때 사용한다.

- 다음 어노테이션들을 `@SpringBootTest` 대신 써주면 된다.

  - `@JsonTest` : 우리가 가지고 있는 모델이 Json일 때 어떤 형태로 나갈 건지를 테스트한다.

    ```java
    @RunWith(SpringRunner.class)
    @JsonTest
    public class SampleControllerTest {
        @Autowired
        JacksonTester<Sample>
        ...
    }
    ```

  - `@WebMvcTest` : 컨트롤러 하나만 테스트한다. 해당 컨트롤러와 웹과 관련된 빈들만 등록한다!

    - 사용하는 의존성이 있다면 **Mock**으로 추가해줘야 한다.
    - 얘는 꼭 `MockMvc`로 테스트해줘야 한다.

    ```java
    @RunWith(SpringRunner.class)
    @WebMvcTest(SampleController.class)
    public class SampleControllerTest {
        @MockBean
        SampleService mockSampleService;
        
        @Autowired
        MockMvc mockMvc;
        ...
    }
    ```

  - `@WebFluxTest`

  - `@DataJpaTest` : 레파지토리만 등록된다.



#### 5) 테스트 유틸리티

- `OutputCapture`

  - public으로 만들어줘야 함.

  - 로그를 비롯해 콘솔에 찍히는 모든 것을 다 캡처한다. 

    > 로그 메시지를 중간 중간 중요한 포인트에 놓고, 그 로그 메시지가 찍혔는지 안찍혔는지로 테스트 ... ?



___

### 8. Spring Boot Devtools

#### 1) 개념 및 사용

- 스프링 부트가 제공하는 Optional한 툴. 꼭 써야 하는 건 아니다.

- **의존성 추가부터!**

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-devtools</artifactId>
  </dependency>
  ```



#### 2) 기능

- 의존성을 추가하고 나면 아무것도 안해도 설정이 바뀌는 게 있다. [**(내용 링크)**](https://github.com/spring-projects/spring-boot/blob/v2.0.3.RELEASE/spring-boot-project/spring-boot-devtools/src/main/java/org/springframework/boot/devtools/env/DevToolsPropertyDefaultsPostProcessor.java)

- 열어보면 주로 **캐시**를 안쓰도록 하는 게 많다.

  개발할 때 캐시를 쓰게 되면, 그떄그때 바로 바뀌지 않기 때문에 브라우저의 캐시를 비워줘야 한다.

- **restart** : 클래스패스에 있는 파일이 변경될 때마다 자동으로 재시작해줌.

  - 그 속도가 톰캣을 껐다가 키는 속도보다 빠르다.
  - 스프링부트는 클래스로더를 2개 사용한다. 
    - `base classloader` : 바뀌지 않는, 라이브러리같은 애들을 올리는 애고,
    - `restart classloader` : 실제로 개발자가 개발하는 클래스는 이 친구가 올린다.

- **Live reload** : 리스타트 했을 때, 브라우저도 자동으로 리프레시를 해 주는 기능

  - 대신 얘는 **브라우저의 플러그인**을 설치해줘야 함.

  - 기타 설정

    ```properties
     spring.devtools.restart.exclude = 이름		// 리스타트 제외
     spring.devtools.restart.enabled = false	 // 리스타트 기능 끄기
    ```

- **글로벌 설정** : 우선 순위 1등! 위치는 `~/.spring-boot-devtools.properties`