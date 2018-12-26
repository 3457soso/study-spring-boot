# 스프링 부트 입문

**출처 : [백기선의 스프링 부트- 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기 (소개 및 프로젝트 시작)
2. **스프링 부트의 원리 (의존성 관리와 자동 설정)**
3. 스프링 부트의 원리 (내장 웹 서버)
4. 스프링 부트의 핵심 기능 (SpringApplication)
5. 스프링 부트의 핵심 기능 (외부 설정)
6. 스프링 부트의 핵심 기능 (프로파일, 로깅, 테스트)
7. 스프링 부트의 기술 (웹 MVC)
8. 스프링 부트의 기술 (데이터베이스)
9. 스프링 부트의 기술 (보안 및 REST)
10. 스프링 부트의 운영 (Actuator)

___

## 스프링 부트의 원리 (의존성 관리와 자동 설정)

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

  

### 2. 자동 설정 (EnableAutoCongifuration) 

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