# 스프링 부트 개념과 활용

**출처 : [스프링 부트 개념과 활용 - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기

2. 스프링 부트의 원리

3. 스프링 부트의 핵심 기능

4. 스프링 부트의 기술 (웹 MVC)

5. 스프링 부트의 기술 (데이터베이스)

6. 스프링 부트의 기술 (스프링 시큐리티)

7. **스프링 부트의 운영 (Actuator)**

  - [**Actuator 소개**](#1-Actuator-소개)
  - [**JMX와 HTTP**](#2-JMX와-HTTP)
  - [**스프링 부트 어드민**](#3-스프링-부트-어드민)

  ​    

___

## 스프링 부트의 운영 (Actuator)

### 1. Actuator 소개

#### 1) 액추에이터란?

- 스프링 부트의 운영 중에 주시할 수 있는 다양한 정보들을 제공해주는 모듈

- 해당 정보들을 **엔드포인트**를 통해서 제공해줌!

- **쓰려면?** : 의존성을 추가한다!

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
  </dependency>
  ```



#### 2) 엔드포인트

- 다양한 엔드포인트를 통해서 정보들을 제공하는데, [**[여기]**](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#production-ready-endpoints) 에서 확인할 수 있다.

- 기본적으로 ID에 해당하는 액추에이터들이 활성화된다.

- `shutdown` 엔드포인트를 제외하고는 모두 **활성화** 되어있다.

  - `auditevents` : 인증 정보들. 인증에 관련된 이벤트들. 누가 인증했고 실패했는지..
  - `bean` : 등록된 빈들
  - `conditions` : 어떤 자동 설정이 어떤 조건에 의해서 설정되고, 설정되지 않았는지
  - `configprops` : `application.properties`에서 정의가 가능한 것들
  - `env` : 스프링의 `Enviroments` 안에 들어 있는 properties를 보여준다
  - `flyway` : 마이그레이션 정보를 보여준다
  - `health` : 헬스 정보. 앱이 잘 굴러가고 있는지
  - `httptrace` : 최근 100개의 HTTP 요청과 응답들을 보여준다
  - `info` : 앱과 관련된 임의의 정보들
  - `loggers` : 어떤 패키지의 어떤 로거를 사용하고 있는지. 로깅 레벨까지 보여주고 운영 중 수정도 가능하다.
  - `liquibase` : flyway랑 비슷하게 데이터베이스를 마이그레이션 해준다.
  - `metrics` : 애플리케이션의 핵심 정보들 (사용 메모리나 CPU등)
    - 연동된 제3의 모니터링 애플리케이션에서 쓸 수 있도록 만들어준다. 
    - 그런 모니터링 애플리케이션에서 특정 수치를 넘으면 alert를 발생하게 만들 수도 있다.
  - `mappings` : 컨트롤러의 매핑 정보
  - `scheduledtasks` : 스프링 프레임워크에서 주기적으로 실행되는 배치 task들을 보여준다.
  - `sessions` : 세션과 관련됨
  - `shutdown` : 애플리케이션을 끌 수 있다. **얘는 위험해서 디폴트로는 비활성화되어있다**
  - `threaddump` : 스레드덤프를 띄울 수 있다.

- 만약 웹 애플리케이션이라면 (`Spring MVC`, `Spring WebFlux`, `Jersey` 등 ...)

  - `heapdump` : `hprof` 힙 덤프 파일을 압축해서 준다.
  - `jolokia` :  JMX 빈을 HTTP 뷰에서도 볼 수 있다. 앱 밖에서도 빈을 호출할 수 있다.
  - `logfile` : 로그 파일의 정보 확인
  - `prometheus` : 메트릭들을 프로메테우스 서버에서 캡처할 수 있는 형태로 변환해준다.

  

#### 3) 이제 써보자!

-  `http://localhost:{$port}/actuator`에 접속하면 **HATEOAS** 형식으로 반환한다.

  ```json
  // 20181230233424
  // http://localhost:8765/actuator
  
  {
    "_links": {
      "self": {
        "href": "http://localhost:8765/actuator",
        "templated": false
      },
      "health": {
        "href": "http://localhost:8765/actuator/health",
        "templated": false
      },
      "health-component": {
        "href": "http://localhost:8765/actuator/health/{component}",
        "templated": true
      },
      "health-component-instance": {
        "href": "http://localhost:8765/actuator/health/{component}/{instance}",
        "templated": true
      },
      "info": {
        "href": "http://localhost:8765/actuator/info",
        "templated": false
      }
    }
  }
  ```

  HTTP를 사용할때는 공개된 정보가 `health`와 `info`밖에 없다 ㅠㅠ..

  **정보의 활성화와 공개 여부는 따로 설정한다**



- **설정** : `application.properties`에서 
  - **활성화** : `management.endpoint.{$id}.enabled = {true | false}`를 통해 설정해줄 수 있다.
  - **공개 여부** : `management.endpoint.{$id}.exclude = {true | false}`



___

### 2. JMX와 HTTP

#### 1) Jconsole 사용하기

- **접속** : 콘솔에서 `jconsole`입력

  ```sh
  soyoung@ubuntu-900X5N$ jconsole
  ```

  그러면 다음과 같은 화면이 뜨는데, 리스트에서 내 애플리케이션을 선택하고 **Connect**

  ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzFfNzAg/MDAxNTQ2MjIzMzA2NjQy.YjU-DDjOKoB6iHrW0Aot5Tu7J29uC3FPSfnL8ohnXckg.c-IAADXPnqb_cZZB7wxHE90jtExLUAijAl2pYM70Ymwg.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-31_11-13-21.png)

  만약 SSL을 적용하지 않았으면 **Insecure connection**으로 접속하면 된다.

  ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzFfMjIy/MDAxNTQ2MjIzMzA3MjIw.16YB4r2YjOyJdVM-eoRRYsKsRvPLLW2Iadhxh45TJY4g.4uCcpoYupjpOS5VQk_RTEzcpkfqBMYbnZ1duPBJ2APwg.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-31_11-16-08.png)

  ▲ 그럼 애플리케이션이 사용한 쓰레드, 클래스 개수, 메모리, CPU  사용률을 시각적으로 확인할 수 있다.

  ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzFfMTIy/MDAxNTQ2MjIzMzA3NTUy.JGjE4D6rOtoB2bSCuusvDwIB-2j0h7wZDh0OL5QcPtwg.pO0ZeMYYtgWZjF7TZEagpucGEsWDSD5c4JP4pHDYyzAg.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-31_11-17-04.png)

  ▲ 이렇게 탭을 통해서 다양하게 살펴볼 수 있다!

  ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzFfMjc4/MDAxNTQ2MjIzMzA3ODc5.wTtBcyrE1PH3m5DT9gw39X3EobCtDcRIIW7mpLc3fTEg.W52rGj4CIT_cscjRWm-GK8b_32m9JfXLs6L2IWqbS8kg.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-31_11-19-57.png)

  ▲이렇게 엔드포인트를 볼 수는 있지만... 사실 보기 쉽지는 않다

  

#### 2) VisualVM 사용하기

- Java 10 부터는 설치해줘야 한다!

  훨씬 보기에 예쁘지만, 사실상 **Jconsole**과 같은 정보이다.

  **MBean** 정보를 보려면 `Tools-Plugins-Available Plugins`에서 추가해줘야 한다.\

  ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzFfMTE3/MDAxNTQ2MjIzMzA4Mjkx.24WpnByXNW_AfQRORrQa4hhQ9uIrWjcxHTN045g9HMcg.iYnGlpChzVzVgamv8Rpi5Ugdq61Ez0sGdTk1fSifsmQg.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-31_11-21-07.png)

  ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzFfMTE3/MDAxNTQ2MjIzMzA4Mjkx.24WpnByXNW_AfQRORrQa4hhQ9uIrWjcxHTN045g9HMcg.iYnGlpChzVzVgamv8Rpi5Ugdq61Ez0sGdTk1fSifsmQg.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-31_11-21-07.png)



#### 3) HTTP 사용하기

- 그래도 웹 상에서 보고싶다면? `application.properties`에

  `management.endpoints.web.exposure.include=*`

  `management.endpoints.web.exposure.exclude=env,beans`

- 하지만 이렇게하면 누구든지 볼 수 있기 때문에.. 스프링 시큐리티로 어드민만 보게 하든가.. 해야함!

  

___

### 3. 스프링 부트 어드민

#### 1) 소개

- 스프링 부트 어드민은 스프링 프레임워크에서 제공하는 게 아니다! **제 3자가 제공!**
- 스프링 부트 액추에이터 정보를 UI로 확인할 수 있다.

#### 2) 사용법

- 어드민 서버 역할을 할 친구가 필요하다! 새로 프로젝트를 파줘야 한다.

- **어드민 서버 설정**

  - 의존성을 추가해준다.

    ```xml
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-starter-server</artifactId>
        <version>2.0.1</version>
    </dependency>
    ```

  - SpringBootApplication 메인 클래스에 `@EnableAdminServer`를 추가해준다.

- **클라이언트 설정**

  - 의존성 추가

    ```xml
    <dependency>
        <groupId>de.codecentric</groupId>
        <artifactId>spring-boot-admin-starter-client</artifactId>
        <version>2.0.1</version>
    </dependency>
    ```

  - `application.properties`에 추가한다

    ```properties
    spring.boot.admin.client.url = http://localhost:{$port}
    management.endpoints.web.exposure.include=*
    ```

    

- **JSON**으로 된 걸로 보는 것 보다는 훨씬 보기 좋은 UI로 볼 수 있다^^!

  ![이미지](https://github.com/codecentric/spring-boot-admin/raw/master/images/screenshot-details.png)

- 이 친구도 마찬가지로 스프링 시큐리티로 어드민만 보게 해줘야 한다.