# 스프링 부트 입문

**출처 : [백기선의 스프링 부트- 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기 (소개 및 프로젝트 시작)

2. 스프링 부트의 원리 (의존성 관리와 자동 설정)

3. 스프링 부트의 원리 (내장 웹 서버)

4. **스프링 부트의 핵심 기능 (SpringApplication)**

5. 스프링 부트의 핵심 기능 (외부 설정)

6. 스프링 부트의 핵심 기능 (프로파일, 로깅, 테스트)

7. 스프링 부트의 기술 (웹 MVC)

8. 스프링 부트의 기술 (데이터베이스)

9. 스프링 부트의 기술 (보안 및 REST)

10. 스프링 부트의 운영 (Actuator)

  ​    

___

## 스프링 부트의 핵심 기능 (SpringApplication)

### 1. SpringApplication 커스터마이징

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

  
