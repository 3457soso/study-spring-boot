# 스프링 부트 입문

**출처 : [백기선의 스프링 부트- 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기 (소개 및 프로젝트 시작)

2. 스프링 부트의 원리 (의존성 관리와 자동 설정)

3. **스프링 부트의 원리 (내장 웹 서버)**

4. 스프링 부트의 핵심 기능 (SpringApplication)

5. 스프링 부트의 핵심 기능 (외부 설정)

6. 스프링 부트의 핵심 기능 (프로파일, 로깅, 테스트)

7. 스프링 부트의 기술 (웹 MVC)

8. 스프링 부트의 기술 (데이터베이스)

9. 스프링 부트의 기술 (보안 및 REST)

10. 스프링 부트의 운영 (Actuator)

    ​    

___

## 스프링 부트의 원리 (내장 웹 서버)

### 1. 내장 웹 서버 (톰캣)

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

  

### 2. 내장 웹 서버 업그레이드

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

>  **HTTP/2**를 쓰려면 **SSL**은 기본적으로 적용이 되어있어야 한다!

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

  

### 3. 기타 (추가 사항)

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

