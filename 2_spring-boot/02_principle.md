# 스프링 부트 개념과 활용

**출처 : [스프링 부트 개념과 활용 - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기

2. **스프링 부트의 원리**

   - [**의존성 관리**](#1-의존성-관리)

   - [**자동 설정 (EnableAutoConfiguration)**](#2-자동-설정-EnableAutoConfiguration)

   - [**내장 웹 서버 (Tomcat)**](#3-내장-웹-서버-Tomcat)

   - [**내장 웹 서버 업그레이드**](#4-내장-웹-서버-업그레이드)

   - [**기타**](#5-기타)

3. 스프링 부트의 핵심 기능

4. 스프링 부트의 기술 (웹 MVC)

5. 스프링 부트의 기술 (데이터베이스)

6. 스프링 부트의 기술 (스프링 시큐리티)

7. 스프링 부트의 운영 (Actuator)

    

___

## 스프링 부트의 원리

### 1. 의존성 관리

#### 1) 소개 및 장점

- 스프링 부트를 쓸 때는, **dependency**들을 이름만 적어주고, **심지어 버전도 적지 않았는데** 자동으로 가져온다.

  이는 스프링 부트의 **의존성 관리** 때문임!

  `spring-boot-dependencies.pom`을 보면, 

  - **properties**에 각종 모듈들과 버전들이 정리되어 있다.
  - `pom.xml`에 `spring-boot-starter-parent`를 포함하면 따로 설정해주지 않아도 됨!

  > 이 의존성 내용들은 Maven의 **Dependencies**를 통해서 확인할 수 있다.

- **장점 :** 직접 관리해야 할 의존성의 수가 줄어든다. *(일이 줄어든다)*

  

#### 2) 방법

- 보통 `<parent>`로 추가해서 쓴다.

  ```xml
  <parent>
  	<groupId>org.springframework.boot</groupId>
  	<artifactId>spring-boot-starter-parent</artifactId>
  	<version>2.0.3.RELEASE</version>
  </parent>
  ```

  +) 만약 폼에서 지원하지 않는 의존성을 추가해야 한다면 **버전까지 명시해야 한다.**

  

- 만약 `<parent>`를 절대 바꿀 수 없다면?

  ```xml
  <dependencyManagement>
  		<dependencies>
  		<dependency>
  			<!-- Import dependency management from Spring Boot -->
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-dependencies</artifactId>
  			<version>2.0.3.RELEASE</version>
  			<type>pom</type>
  			<scope>import</scope>
  		</dependency>
  	</dependencies>
  </dependencyManagement>
  ```

  - parent는 의존성만 가져오는 게 아니라, 자바 버전이나 컴파일러 위치 등등... 다양한 것들이 설정되어 있다.

  - 따라서 이걸 빼고 의존성만 가져오게 되면 이것도 따로 명시해줘야 해서 불편할 수 있다.

    

#### 3) 활용

- 만약 **JPA**를 쓰고 싶다면?

  ```xml
  <dependency>
  	<groupId>org.springframework.boot</groupId>
  	<artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>
  ```

  이렇게만 쳐주면, 관련 의존성들을 자동으로 끌어온다!

- 스프링 부트에서 관리해주지 않는 버전일 경우에는 **maven**이든 어디든... 복붙하면 된다.

  이럴 경우에는 **버전을 꼭 명시**해준다!!!



___

### 2. 자동 설정 EnableAutoCongifuration 

#### 1) 기초

- `@SpringBootApplication`은 3가지 빈을 포함하고 있다.

  - `@Configuration` : 빈을 등록하는 설정 파일!

  - `@ComponentScan` : `@Component` 를 가진 클래스들을 찾아 빈으로 등록한다.

  - `@EnableAutoConfiguration`

    +) 여기서 **빈을** **등록하는** 과정에는 

    - `@ComponentScan`으로 먼저 빈들을 모두 등록하고,
    - `@EnableAutoConfiguration`으로 추가적인 빈들을 2차로 등록해준다. 

    > 따라서 `@AutoConfiguration`으로 등록하면
    >
    > 기존에 `@ComponentScan`으로 등록한 빈을 덮어쓰게 될 수 있음에 주의해야 한다.

- `@EnableAutoConfiguration`을 등록해주지 않아도 사용은 가능하다.

  

#### 2)  `@EnableAutoConfiguration`은 뭘 하는 걸까?

- 스프링의 메타 파일들...

  자바 리소스의 메타 파일들 안에 `spring.fatories` 라는 파일이 있다.

- `spring.fatories`를 보면, `org.springframework.boot.autoconfigure.EnableAutoConfiguration`

  밑에 다양한 설정 파일들이 있다.

  - 이 키 값 밑에 설정되어 있는 설정 파일들을 가져오는 역할을 한다.
  - 또한, 조건에 따라 어떤 빈을 등록하거나 안하거나, 이 설정 파일을 쓸건지 말건지 등등... 상황에 따라 등록한다.

- **직접 구현하고 싶다면?**

  1) 의존성 추가하고

  2) `@Configuration` 파일을 작성한 뒤에

  3) `src/main/resource/META-INF/spring.factories` 파일을 생성해 자동 설정 파일을 추가한다.

  4) `mvn install`로 설치해준다.

  

#### 3) 커스터마이징

- `@ConfitionalOnMissingBean` : `@ComponentScan`으로 등록된 빈이 **없을 때만** 새로 생성한다.

  - 이는 스프링부트에서 제공하는 기능들을 커스텀하는데에 많이 쓰이게 된다.

- **만약 `application.properties`**로 내용만 바꿔주고 싶다면? **(빈을 재정의하기 싫다)**

  1) `@ConfigurationProperties` : 해당 빈에 그 프로퍼티에 대한 내용을 정의해줘야 한다.

  2) `@EnableConfigurationProperties(클래스이름.class)`로 해당 프로퍼티를 주입받는다.

  3) properties의 Getter 함수로 값을 가져와서 써주면 된다.



___

### 3. 내장 웹 서버 Tomcat

#### 1) 이해

- **스프링 부트는 웹 서버가 아니다.**

  ```java
  SpringApplication aplication = new SpringAPplication(Application.class);
  application.setWebApplicationType(WebApplicationType.NONE);
  ```

  이렇게 실행하면 일반 자바 클래스 실행하듯이 된다.

  +) `application.properties`에서 `spring.main.web-application-type=none`을 해줘도 동일.

- 서버는 **Tomcat**, **Jetty**, **Undertow** 이런 친구들...

- 의존성을 찾아보면 `tomcat-embeded-XXX` 이런 친구들이 있다.

  

#### 2) 톰캣 예제 코드

```java
Tomcat tomcat = new Tomcat();
tomcat.setPort(8080);

tomcat.addContext("/", "/"));
tomcat.start();
```

원래는 이런 식으로 서버 설정을 해줘야 하는데, 이런 부분들이 자동으로 설정이 되어 있는 것!

+) `TomcatServletWebServerFactory`에서 이런 일을 다 해준다!

+) `TomcatServletWebServerFactoryCustomizer` 여기서 커스터마이징을 해 줄 수도 있음!



#### 3) 다른 서버를 쓰고 싶다면?

- 스프링 부트를 쓸 때는, 웹 서버를 기본적으로 **톰캣**을 쓴다. 만약 다른 서버를 쓰고 싶다면?

  1) 먼저 **dependency**에서 톰캣을 제외시킨다.

  2) 새로 쓰고 싶은 서버를 추가해주고, 빌드하면 된다!

  ```xml
  <dependencies>
  	<dependency>
  		<groupId>org.springframework.boot</groupId>
  		<artifactId>spring-boot-starter-web</artifactId>
  		<exclusions>
  			<groupId>org.springframework.boot</groupId>
  			<artifactId>spring-boot-starter-tomcat</artifactId>
          </exclusions>
      </dependency>
      
      <dependency>
          <groupId>org.springframework.org</groupId>
          <artifactId>spring-boot-starter-undertow</artifactId>
      </dependency>
  </dependencies>
  ```

  +) 만약 새로 추가하지 않은 채로 실행하면, 웹 서버가 없기 때문에 일반 자바 타입으로 실행될 것!



#### 4) 포트 설정 및 확인

- **포트 변경** : `application.properties`에서 `server.port=8080`

- **랜덤 포트** : `application.properties`에서 `server.port=0` *(사용 가능한 포트를 찾아서 띄워준다.)*

- **포트 확인** : 리스너를 하나 생성해서 콜백 함수를 이용해 확인한다.

  ```java
  @Component
  public class PortListener implements ApplicationListener<ServletWebServerInitializedEvent> {
      @Override
      public void onApplicationEvent(ServletWebServerInitializedEvent e) {
          // 이 함수는 웹 서버가 생성이 될 때 호출되는 콜백 함수
          ServletWebServerApplicationContext context = e.getApplicationContext();
          context.getWebServer().getPort(); // 이 함수로 포트 번호를 가져올 수 있음!
      }
  }
  ```



___

### 4. 내장 웹 서버 업그레이드

#### 1) HTTPS 설정하기

- 먼저 **keystore**를 생성해준다. 조직 이름 그런건 알아서 입력해준다.

  ```sh
  keytool -genkey 
    -alias tomcat 
    -storetype PKCS12 
    -keyalg RSA 
    -keysize 2048 
    -keystore keystore.p12 
    -validity 4000
  ```

- 이후 만든 **keystore**를 `application.properties`에 설정해준다.

  ```properties
  server.ssl.key-store: keystore.p12
  server.ssl.key-store-password: ???
  server.ssl.keyStoreType: PKCS12
  server.ssl.keyAlias: ???
  ```

- 기본적으로 톰캣이 사용하는 커넥터는 하나만 등록된다. 

  - 해당 커넥터에 **SSL**을 등록해줘서, 모든 요청이 **https**로 오고 가도록 만들어준다.

    ᐳ 이럴 경우에는 기존 **http**로 요청하면 **400** 에러 코드를 받게 됨...!

  - 하지만 이 상태에서는 공인된 인증서가 아니기 때문에... 브라우저에 **Not Secure**로 뜨게 된다.

  - **HTTPS**이긴 하지만 어떤 웹 사이트인지는 알 수가 없는 상태...

    > 공인된 인증서가 필요하면 GoDaddy 같은 데서 구매하거나... Letsencrypt를 쓰자!

- (필요하다면) **HTTP** 커넥터도 따로 추가해주자!

  ```java
  @Bean // 새 톰캣 서버를 생성한 뒤에, 커넥터를 붙여서 반환한다.
  public ServletWebServerFactory serverFactory() {
      TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
      tomcat.addAdditionalTomcatConnectors(createHttpConnector());
      return tomcat;
  }
  
  private createHttpConnector() {
      Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
      connector.setPort(8080); // 기존 포트와 겹치면 안됨!
      return connector;
  }
  ```

  

#### 2) HTTP/2 설정하기

> **HTTP/2**를 쓰려면 **SSL**은 기본적으로 적용이 되어있어야 한다!

- `application.properties`에서 `server.http2.enabled=true`

- **제약사항이 서버마다 다르다!**

  - **Undertow** : HTTPS만 적용이 되어있으면 따로 추가설정을 해주지 않아도 된다.

  - **Tomcat**

    - 8.5.X 버전에서는, 시스템에 `libtenative` 라이브러리가 있어야 하고... 디렉토리 설정도 해줘야 하고...

    - 9.0.X 버전에서는, JDK 9를 사용할 시에 따로 설정해주지 않아도 된다.

      > **Project Structure - Project - Project SDK**를 **9**로 올려주고,
      >
      > **Project Structure - Module - Module SDK**도 **9**로 바꿔줘야 한다.

- *근데 JDK 9버전을 적용하기 힘들 수도 있고... 실제로 웹 서버로 Undertow를 잘 쓰는 것 같지도 않은데... 굳이 HTTP/2를 써야 하는 건지는 아직 잘 모르겠음!*

  

---

### 5. 기타

#### 1) 독립적인 JAR 파일

- 사실 **Maven** 프로젝트에 관한 이야기...
- 개발 할 때는 IDE에서 하는 것이 편하지만, 실제로 배포할 때 등등에는 **JAR 패키지로 만드는 게 좋다**
  - **패키징** : `mvn package -DskipTest` *(테스트 하지 말자...)*
  - **실행** : `java -jar .jar파일 이름` (이후 생기는 *.jar* 파일 하나로 앱이 돌아간다!)
- **근데, 수많은 패키지와 모듈들 (의존성들)은 어디로 간 걸까?**
  - 생성된 jar 파일 안에 해당 의존성들이 다 들어있다.
  - 그런데 자바에는 jar 파일 안에 들어가 있는 의존성들을 읽어 들일 수 있는 **표준적인 방법이 없다**
- **스프링 부트는 어떻게 jar 내부를 열어볼까?**
  - 내장 JAR : JAR 안에 내장 JAR를 만들고, 해당 파일들을 읽어들이는 로더를 만든다.
    - org.springframework.boot.**loader.jar.JarFile** : 내장 JAR를 읽는다. 
    - org.springframework.boot.**loader.Launcher** : JAR들을 실행한다.
  - 이 부분도 스프링 부트의 중요한 특징 중 하나이다!

