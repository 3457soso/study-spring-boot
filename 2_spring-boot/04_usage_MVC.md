# 스프링 부트 개념과 활용

**출처 : [스프링 부트 개념과 활용 - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기

2. 스프링 부트의 원리

3. 스프링 부트의 핵심 기능

4. **스프링 부트의 기술 (웹 MVC)**

   - 스프링 부트 MVC 소개
   - HttpMessageConverters와 ViewResolver
   - 리소스 관리 (정적 리소스, 웹 JAR, index.html, favicon)
   - Thymeleaf와 HtmlUnit
   - ExceptionHandler
   - Spring HATEOAS
   - CORS

5. 스프링 부트의 기술 (데이터베이스)

6. 스프링 부트의 기술 (보안 및 REST)

7. 스프링 부트의 운영 (Actuator)

  ​    

___

## 스프링 부트의 기술 (웹 MVC)

### 1. 스프링 부트 MVC 소개

#### 1) 스프링 부트 MVC

- 스프링 부트에서는 별다른 설정을 해주지 않아도, **자동 설정**을 통해 바로 **스프링 웹 MVC**를 개발할 수 있다.

- 이런 자동 설정은

  `spring-boot-autoconfigure > META-INF > spring.fatories`에 정의되어 있다.

  ```java
  @Configuration
  @ConditionalOnWebApplication(
      type = Type.SERVLET
  )
  @ConditionalOnClass({Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class})
  @ConditionalOnMissingBean({WebMvcConfigurationSupport.class})
  @AutoConfigureAfter({DispatcherServletAutoConfiguration.class, TaskExecutionAutoConfiguration.class, ValidationAutoConfiguration.class})
  public class WebMvcAutoConfiguration { ... }
  ```

  이런 식으로 다 미리 설정이 되어있기 때문에 그냥 바로 쓰면 된다!



#### 2) 스프링 MVC 확장

- 스프링 웹 MVC가 주는 기능을 쓰면서, 추가적으로 확장하고 싶을 경우

```java
@Configuration
public class WebConfig implements WebMvcConfigurer { ... }
```



#### 3) 스프링 MVC 재정의

- 스프링 웹 MVC가 주는 기능을 모두 엎어버리고 처음부터 직접 설정한다!

  ```java
  @Configuration
  @EnableWebMvc // 스프링 웹 MVC가 제공하는 애들이 다 사라진다!
  public class WebConfig implements WebMvcConfigurer { ... }
  ```

  - 이렇게 해두고 인터페이스가 제공하는 여러가지 콜백 메소드들을 통해 커스터마이징 하게 된다. 
  - **Converter**, **Interceptor**, **CorsMapping**, **Resource Handler** 등등 ... 추가 ... 할 거 ... 많아 ...
    - 굳이 직접 정의해줄 일은 없다. 기본적으로 제공해주는 애들만 써도 충분함!



___

### 2. HttpMessageConverters와 ViewResolver

#### 1) HttpMessageConverter

- **MessageConverter** : 스프링 프레임워크에서 제공하는 인터페이스

- HTTP 요청 본문으로 들어오는 것을 객체로 변환하거나, 객체를 HTTP 응답 본문으로 변환한다.

  `{"username":"soyoung", "password":"qwer1234"} <-> User`

- 다양한 `HttpMessageConverter`가 존재하고, 어떤 걸 받아오고 어떻게 응답하느냐에 따라서 달라진다.

  - Ex) **JSON**을 본문으로 하는 요청이 들어왔다면?
    - `Content-Type` 헤더에 `application/json`이라고 적혀 있을 것이고, 본문도 역시 **JSON**
    - 이러면 `JsonMessageConverter`가 사용되게 된다.
  - Ex) **JSON**을 본문으로 응답하고 싶다면?
    - 현재 가지고 있는 **User** 객체 자체를 응답으로 보낼 수는 없다.
    - 이걸 변환해줘야 하는데...
      - 안에 **여러 가지 property**가 있을 때 : `JsonMessageConverter`가 사용된다.
      - 단순히 **문자열 하나**일때 : `StringMessageConverter`가 사용된다.

- 보통 `@RequestBody`, `@ResponseBody`와 함께 사용된다.

  ```java
  @PostMapping("/user")
  public @ResponseBody User create(@RequestBody User user) {
  	return null;
  }
  ```

  - 클래스에 `@RestController`가 붙어있으면 `@ResponseBody` **생략 가능**

  - 그냥 `@Controller`인 경우에는 붙여줘야 한다! 

    안 그러면 `ViewNameResolver`를 사용해서, return 하는 값을 **이름으로 하는 뷰**를 찾으려고 하게 될 것!

  

#### 2) ViewResolver

- `ContentNegotiatingViewResolver` : `ViewResolver` 중 하나로, 들어오는 요청에 따라 응답이 달라진다.

  - 클라이언트는 `Accept` 헤더를 통해 어떤 타입의 응답을 원하는지 알려준다. 이에 따라 달라짐!

    1. 어떤 요청이 들어오면 그 요청에 응답할 수 있는 모든 뷰를 찾는다.
    2. 뷰의 타입을  `Accept` 헤더와 비교해 최종적으로 선택해 리턴한다.

  - 근데 클라이언트가 `Accept` 헤더를 주지 않았다면?

    - 이에 대비해서 `format`이라는 매개변수를 이용해 원하는 응답의 타입을 전해줄 수도 있다.

  - 만약에 받고 싶은 **응답을 다른 타입으로 고쳐도** 컨트롤러에서 고쳐야 할 내용은 없다!

    ```java
    mockMvc.perform(post("/users")
               .contentType(MediaType.APPLICATION_JSON)
               .accept(MediaType.APPLICATION_XML) // XML로 고쳐도 상관X
               ...
    ```

    - 만약  `406 not acceptable` 이 발생하면, 해당 타입을 처리할 `HttpMessageConverter`가 없는 것!

    - `HttpMessageConvertersAutoConfiguration`를 추가해주면 된다.

      ```xml
      <dependency>
          <groupId>com.fasterxml.jackson.dataformat</groupId>
          <artifactId>jackson-dataformat-xml</artifactId>
          <version>2.9.6</version>
      </dependency>
      ```

- 포스트맨으로 테스트 해보면 다음과 같다!

  `Content-Type`은 `application/json`으로 주되,

  - `Accept`를 `application/json`으로 주거나 공란으로 두면

    ```json
    {
        "id": null,
        "username": "soyoung",
        "password": "qwer1234"
    }
    ```

  - `Accept`를 `application/xml`로 주면

    ```xml
    <User>
        <id/>
        <username>soyoung</username>
        <password>qwer1234</password>
    </User>
    ```

    

---

### 3. 리소스 관리 (정적 리소스, 웹 JAR, index.html, favicon)

#### 1) 정적 리소스

- **정적 리소스란?**

  - 동적으로 생성하지 않는 것.
  - 클라이언트에서 요청이 들어왔을 때, 해당하는 리소스가 이미 만들어져 있어 그대로 보내주면 되는 것

- **정적 리소스 매핑** : 기본적으로 다음 4가지 위치에 있는 리소스들은 `/**` 경로에 매핑되어 제공된다.

  - `classpath:/static`
  - `classpath:/public`
  - `classpath:/resources/`
  - `classpath:/META-INF/resources`

  Ex) `/hello.html`을 요청하면 `/static/hello.html`이 매핑된다.

  - `Last-Modified` : 리소스 변경 후 다시 빌드하면, `Last-Modified` 값도 변경된다.
    - 갱신이 필요한 경우 (`If-Modified-Since` 이후에 바뀌었으면) `200 OK` 보내고 **새로 전송**
    - 반대인 경우 `304 Not Modified` 보내고 **다시 보내지 않음** (더 빠르다)

  - **디렉토리 설정**

    1. `application.properties`에 다음과 같이 설정한다.

       -  `spring.mvc.static-locations = 위치`

       - `spring.mvc.static-path-pattern = 위치` : 전부 해당 위치로 요청해야 한다!

         > 설정하고 나면 위 디렉토리가 모두 **오버라이딩** 된다.

    2. 위 방법 보다는 새로 **리소스 핸들러**를 추가하는 것이 낫다.

       - 기존의 스프링 부트가 제공하는 **리소스 핸들러**는 그대로 유지하면서, 
       - 내가 원하는 **리소스 핸들러**만 따로 추가한다!

       ```java
       @Configuration
       public class WebConfig implements WebMvcConfigurer {
           @Override
           public void addResourceHandlers(ResourceHandlerRegistry registry) {
               registry.addResourceHandler("/m/**")
                       .addResourceLocations("classpath:/m/") // 꼭 /로 끝나야 함
                       .setCachePeriod(20);
           }
       }
       ```



#### 2) 웹 JAR

- 스프링부트는 웹 JAR에 대한 기본 매핑도 제공한다!

- **웹 JAR란?**

  - 클라이언트에서 쓰는 라이브러리들은 굉장히 많다 (자바스크립트, jQuery 등등...)

  - 이런 애들까지 모두 다 JAR 파일로 의존성을 추가해서 정적 리소스로 참조할 수 있다.

  - **MVNRepository** 같은데서 찾아다가 넣어도 되고... 어쨌든 `pom.xml`에 추가하면 된다.

    ```xml
    <dependency>
        <groupId>org.webjars.bower</groupId>
        <artifactId>jquery</artifactId>
        <version>3.3.1</version>
    </dependency>
    ```

    - 만약 이 친구를 HTML 파일에서 참조하고 싶다면

      `<script src="/webjars/jquery/3.3.1/dist/jquery.min.js"></script>` 이런 식으로!

  - 근데 그럼 버전을 올릴 때마다 저 경로도 바꿔줘야 할까? **webjars-locator-core** 추가!

    ```xml
    <dependency>
        <groupId>org.webjars</groupId>
        <artifactId>webjars-locator-core</artifactId>
        <version>0.35</version>
    </dependency>
    ```

    +) **리소스 핸들러**와 **리소스 트랜스포머**를 **체이닝**하는 기능을 해주어 가능하게 된다.



#### 3) index 페이지와 favicon

- **welcome page** : 루트로 요청했을 때 보여주는 페이지!
  1. 정적 페이지로 보여주기
     - 위에서 기술한 4가지 정적 리소스의 위치 중 하나에  `index.html`를 생성하면 된다.
  2. 동적 페이지로 보여주기
- **favicon** : 탭의 왼쪽에 보이는 작은 로고 이미지!
  - 교체하려면 **favicon** 파일도 똑같이 리소스 디렉토리 중 하나에 두면 된다.
  - **잘 안 바뀌는 경우**
    - 기본 **favicon**은 스프링 부트의 JAR에서 제공해준다. 
    - 한 번 캐싱이 되면 다시 요쳥하지 않아서 갱신이 되지 않을 수도 있다.



___

### 4. Thymeleaf와 HtmlUnit



___

### 5. ExceptionHandler

#### 1) 스프링 @MVC의 예외 처리 방법

- `@ControllerAcvice`

- `@ExchangeHandler`

  

#### 2) 스프링 부트의 기본 예외 처리기

- 스프링 부트를 실행하면, 기본적으로 **에러 핸들러**가 등록되어 있다.

- 그 **에러 핸들러**에 의해 에러를 처리하게 된다.

  > **Whitelabel Error Page** ... 404 Not Found  ... 이렇게 생긴 ...

- `BasicErrorController` : 웬만한 요청, 응답 및 별 에러와 관련된 에러들은 여기서 다 처리한다.

- 만약 커스텀하고 싶다면 `ErrorController`를 구현한 뒤에 빈으로 등록한다. 

  - **[추천]** 만들 때 `BasicErrorController`를 상속받아서 만들어도 된다.



#### 3) 에러 페이지 커스터마이징

- 에러가 발생했을 때, 응답의 상태 값에 따라 다른 웹 페이지를 보여준다.
- `/resource` 밑의 `/static`이나 `/template` 중 아무 곳에나 정적인 에러 페이지를 만들어준다.
  - `/error` 폴더를 만들고, **상태 코드와 같은 이름으로 페이지를 생성한다**
  - `404.html`, `5xx.html`
- 아예 `ErrorViewResolver`를 새로 구현해줘도 된다!
  - 좀 더 많은 것들을 커스터마이징 하고 싶다면!
  - 좀 더 동적인 컨텐츠 뷰로 ... 이전보다 다양한 뷰를 만들어줄 수 있다.



___

### 6. Spring HATEOAS

#### 1) HATEOAS란?

- **H**ypermedia **A**s **T**he **E**ngine **O**f **A**pplication **S**tate

  - **RESTful API**에서 리소스에 대한 정보를 제공할 때, 서버에서 리소스와 **연관된 링크 정보**까지 같이 제공하고,

  - 클라이언트는 같이 제공받은 연관된 링크 정보를 바탕으로 리소스에 접근한다.

    - 해당 리소스와 연결은 링크 정보에는 이런 게 있구나! 하고 파악하고, 

    - 해당 리소스 (**Rel**ation)와 관련된 링크 (**H**ypertext **Ref**erence)로 요청을 보낸다.

      

- 의존성을 먼저 추가하자

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-hateoas</artifactId>
  </dependency>
  ```

+) 깃허브 API에는 이미 추가되어 있다고 한다...!



#### 2) 기능

- 추가하고 나면, 다음과 같은 기능이 제공된다.

- `ObjectMapper` : 제공하는 리소스를 JSON으로 변환할 때 사용해서, 자주 쓴다.

  - `spring-boot-starter`만 등록되어 있어도 기본적으로 빈으로 등록이 된다.
  - `spring.jackson.*`이 포함되어 있다.
  - 만약 커스터마이징 하고 싶다면, `application.properties`에서 `spring.jackson.*`으로!

- `LinkDiscovers` : 클라이언트 쪽에서 링크 정보를 Rel 이름으로 찾을때 사용할 수 있는 XPath 확장 클래스

  - `Hateoas`용 클라이언트 API라고 보면 된다.

  - `Hateoas`로 만들어진 남의 서버에서 응답을 받았는데, 링크 정보들이 그 안에 들어있다면...

    이걸 더 잘 갖다 쓸 수 있도록 도와주는 듯!



#### 3) 방법

- 링크 정보를 추가하는 것에는 다양한 방법이 있다.

- `Hateoas`의 리소스는 우리가 제공하는 리소스에 **링크 정보**를 추가한다.

  ```java
  @RestController
  public class SampleController {
      @GetMapping("/hello")
      public Resource<Hello> hello() {
          Hello hello = new Hello();
  
          Resource<Hello> helloResource = new Resource<>(hello);
          helloResource.add(linkTo(methodOn(SampleController.class).hello())
          			 .withSelfRel());
          
          return helloResource;
      }
  }
  ```

  - 먼저 컨트롤러에서 제공하는 hello() 라는 메소드의 링크를 따서,

  - 그 링크를 `self`라는 릴레이션으로 만들어서 추가해준다.

  - 그리고 `Hateoas`에서 만든 **리소스를 반환해야 한다!** 

    

- 이 상태로 포스트맨으로 결과를 보면 다음과 같이 나온다.

  ```json
  {
  	...
      "_links": {
          "self": {
              "href": "http://localhost:8765/hello"
          }
      }
  }
  ```

  위와 같이 `_link`가 추가되어 있음을 볼 수 있다.

  

#### +) Reference

- https://spring.io/understanding/HATEOAS
- https://spring.io/guides/gs/rest-hateoas/
- https://docs.spring.io/spring-hateoas/docs/current/reference/html



___

### 7. CORS

#### 1) CORS란?

- **Origin** : URI 스키마 + 호스트 + 포트를 조합한 것

- **SOP** : **S**ingle-**O**rigin **P**olicy (동일 출처 정책) : 문서나 스크립트가 다른 출처의 리소스와 통신하는 것을 제한하는 것

- **CORS** : **C**ross-**O**rigin **R**esource **S**haring (교차 출처 리소스 공유) : SOP를 우회하기 위한 기술

  - Cross-Site Http Request를 가능하게 해주는 것!

- **CORS**를 설정하지 않은 채로 교차 HTTP 요청을 시도하면,

  `No 'Access-Control-Allow-Origin' header is present on the requested resource` 라고 함!

  - 따라서 이 `Access-Control-Allow-Origin` 헤더를 서버에서 보내줘야 한다.

  - 어떤 Origin에서 자신에게 접근할 수 있는지를 설정해주는 역할을 한다.

    

#### 2) 스프링 부트에서 해주는 것

- 원래 스프링 MVC에서 **CORS**를 사용하려면 여러 빈 설정을 해줬어야 하는데, 이를 부트가 해준다.
- 아무런 빈 설정 없이도 **CORS** 기능을 이용할 수 있다.



#### 3) 사용하기

- 가장 간단한 방법은 애노테이션을 이용하는 것이다.

  ```java
  @CrossOrigin(origins = "http://locahost:8080")
  @GetMapping("/hello")
      public String hello() {
      return "hello";
  }
  ```

  이렇게 메소드 위에 `@CrossOrigin`을 붙여주면 **CORS**가 적용이 된다.

- `@CrossOrigin` 애노테이션은 컨트롤러에도 붙일 수 있다.

- 만약 여러 컨트롤러에 붙여주고 싶다면?

  ```java
  public class WebConfig implements WebMvcConfigurer {    
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/hello")
                  .allowedOrigins("Http://localhost:8080");
      }
  }
  ```

  이렇게 `WebMvcConfigurer`를 상속받아서 설정해주면 된다!