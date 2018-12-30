# 스프링 부트 개념과 활용

**출처 : [스프링 부트 개념과 활용 - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기

2. 스프링 부트의 원리

3. 스프링 부트의 핵심 기능

4. 스프링 부트의 기술 (웹 MVC)

5. **스프링 부트의 기술 (데이터베이스)**

   - 인메모리 데이터베이스
   - MySQL과 PostgreSQL
   - 스프링 데이터 JPA
   - 데이터베이스 초기화와 마이그레이션
   - NoSQL (Redis, MongoDB, Neo4j)

6. 스프링 부트의 기술 (보안 및 REST)

7. 스프링 부트의 운영 (Actuator)

  ​    

___

## 스프링 부트의 기술 (데이터)

### 1. 인메모리 데이터베이스

#### 1) 종류

- **H2** : 콘솔이 있어서 이걸 많이 쓴다고 한다!

  - 의존성부터 추가하자!

    ```xml
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```

    - `DataSourceAutoConfiguration`
    - `JdbcTemplateAutoConfiguration`

- **HSQL**

- **Derby** 



#### 2) 특징

- `Spring-JDBC`가 클래스패스에 있으면, 자동 설정이 필요한 빈을 설정해준다.

  - `DataSource`, `JdbcTemplate`

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-jdbc</artifactId>
  </dependency>
  ```

- 아무런 `DataSource` 관련 설정을 해주지 않으면 부트에서 자동으로 인메모리 DB를 생성해준다.



#### 3) 사용법

- **인메모리 데이터베이스 연결 정보 확인하기**

  - `DataSourceProperties`에서 확인할 수 있다.

    ```
    URL: "testdb"
    username: "sa"
    password: ""
    ```

    

- **콘솔 사용하기**

  - `spring-boot-devtools` 추가
  - `application.properties`에 `spring.h2.console.enabled = ture` 추가
    - 위 작업을 해준 후 `http://localhost:{port}/h2-console` 로 접속해준다.



___

### 2. MySQL과 PostgreSQL

#### 1) DBCP

- **DBCP** (DataBase Connection Pool)
  - 커넥션을 만드는 과정에 상당히 많은 과정이 필요하다. 
  - 때문에 **커넥션을 미리 만들어놓고** 필요할 때마다 만들어져있는 커넥션을 가져다가 쓴다.
  - 몇 개 만들어 놀 것인지, 최소 몇개를 유지할 건지 등등을 설정해줘야 한다.
  - **[주의]** : 잘 알지 못하고 쓰면 예상치 못한 에러들이 많이 발생할 수 있으므로, 충분히 숙지하고 쓰자

- **지원하는 DBCP**

  - `HikariCP` [reference](https://github.com/brettwooldridge/HikariCP#frequently-used)
    - 스프링 부트는 기본적으로 `HicariCP`를 쓴다.
  - `Tomcat CP` [reference](https://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html)
  - `Commons DBCP2` [reference](https://commons.apache.org/proper/commons-dbcp/)

- **DBCP 설정** : `application.properties`에 추가해준다.

  - `spring.datasource.hikari.{key} = {value}`

  - `spring.datasource.tomcat.{key} = {value}`

  - `spring.datasource.dbcp2.{key} = {value}`

- **[주의]** `spring.datasource.url`로 사용할 DB를 설정해주지 않으면, 

  스프링 부트에서는 **자동으로 인메모리 데이터베이스를 사용하려 한다**

  

#### 2) MySQL

- **사용법**

  1. 일단 호스트에 **MySQL**부터 설치해줘야 한다!

     만약 귀찮으면 **Docker**로 설치하자

     ```sh
     docker run -p 3306:3306 --name mysql_boot -e MYSQL_ROOT_PASSWORD={pw} -e
     MYSQL_DATABASE={dbname} -e MYSQL_USER={username} -e
     MYSQL_PASSWORD={pw} -d mysql
     
     docker exec -i -t mysql_boot bash
     
     mysql -u root -p
     ```

     **우분투**에서 설치하기

     ```sh
     sudo apt-get update
     sudo apt-get install mysql-server
     ```

  2. **의존성 추가**

     ```xml
     <dependency>
         <groupId>mysql</groupId>
         <artifactId>mysql-connector-java</artifactId>
     </dependency>
     ```

     이는 **MySQL**에 접속할 수 있는 커넥터를 추가한 것!

- **설정** : `applicaion.properties`에 추가한다.

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/{dbname}?useSSL=false
  spring.datasource.username={username}
  spring.datasource.password={password}
  ```

  

- **주의** : `GPL license` 이기도 하고... 상용 앱에서 쓰면 문제가 될 수 있으므로 **MariaDB**를 쓰자!

  

#### 3) PostgreSQL

- **PostgreSQL**은 **MySQL**과 다르게 저작권으로 인해서 문제될 일이 없다고 함!!!

- **사용법**

  1. 역시 호스트에 **PostgreSQL**부터 설치해준다.

     귀찮을 땐 **Docker**!

     ```sh
     docker run -p 5432:5432 -e POSTGRES_PASSWORD={password} -e
     POSTGRES_USER={username} -e POSTGRES_DB={dbname} --name postgres_boot -d
     postgres
     
     docker exec -i -t postgres_boot bash
     ```

     **우분투**에서는 설치가 복잡하지 않다

     ```sh
     sudo apt-get update
     sudo apt-get install postgresql postgresql-contrib
     ```

  2. **의존성 추가**

     ```xml
     <dependency>
         <groupId>org.postgresql</groupId>
         <artifactId>postgresql</artifactId>
     </dependency>
     ```

- **설정** : `applicaion.properties`에 추가한다.

  ```properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/{dbname}
  spring.datasource.username={username}
  spring.datasource.password={password}
  ```

- **CLI 명령어**

  - `\list` : 데이터베이스 조회하기
  - `\dt` : 해당 데이터베이스의 테이블 조회하기
  - 쿼리는 **MySQL**과 거의 유사한 듯!



___

### 3. 스프링 데이터 JPA

#### 1) 왜 쓰는 걸까?

- **정의**

  - **ORM** (Object Relational Mapping) : 객체와 릴레이션을 매핑해주는 프레임워크!

    - 자바의 클래스는 여러 멤버 변수나 메소드를 가지지만, 릴레이션의 테이블은 컬럼들만 가진다.
    - 클래스에 쓰이는 자료 구조들과 릴레이션 타입의 크기에도 차이가 있다.
    - 클래스는 상속 관계를 가질 수 있지만, 릴레이션은 가질 수 없다. 등등...

  - **JPA** (Java Persistence API) : ORM을 위한 자바 (EE) 표준

    - 대부분의 자바 표준은 `hibernate` 기반으로 만들어져 있다.
    - 하지만 **JPA**에서 `hibernate`의 모든 기능을 가져온 건 아니어서, 직접 설정해줘야 할 때도 있다.

  - **스프링 데이터 JPA** : 그런 JPA 표준 스펙을 아주 쉽게 사용할 수 있도록 **스프링 데이터로 추상화** 시킨 것

    - 밑의 구현체는 `hibernate`를 사용하고, 이것을 `EntityManager`로 감싸서 사용한다.

    > **DataSource** < **Hibernate** < **JPA** < **Spring Data JPA**

- **기능**

  - `@Repository` 빈 자동 생성
  - **쿼리 메소드 자동 구현**
  - `@EnableJpaRepositories` : 스프링 부트가 자동으로 설정 해줌

- **사용법**

  - 먼저 의존성을 추가해준다

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    ```



#### 2) 스프링 데이터 JPA 써보기

- `@DataJpaTest` : 슬라이스 테스트. **Repository**와 관련된 빈만 등록한다.

  - 비어 있는 테스트이더라도 빈이 잘 등록되는지 등등을 확인할 수 있어 유용하다.

  - **embeded-database**가 없는 오류가 발생할 수 있어, **인메모리 데이터베이스를 추가해줘야 한다!**

    ```xml
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    ```

    **H2**를 **테스트용** 인메모리 데이터베이스로 넣어준다.

    테스트용이기 때문에 `hibernate`에서 기존 테이블들을 자동으로 Drop하고 새로 만들어준다.

    ```sh
    Hibernate: drop table account if exists
    Hibernate: drop sequence if exists hibernate_sequence
    ```

- 만약 이렇게 안하고 `@SpringBootTest`를 쓰게 되면,

  - 속도도 다소 느려지고,
  - 테스트용 DB도 따로 줘야 한다. `@SpringBootTest(properties = "spring.datasource.url={url}")`

- 만약 **직접 쿼리문을 쓰고 싶다면?** Native Query 사용!

  ```java
  @Query(nativeQuery = true, value = "SELECT * FROM account")
  Account findAll();
  ```

  

___

### 4. 데이터베이스 초기화와 마이그레이션

#### 1) 데이터베이스 초기화

- 애플리케이션 초기에 필요한 데이터를 만들어준다.

- **JPA**로 초기화하기

  - **스키마 자동 생성** : `application.properties`에 추가!

    1. `spring.jpa.generate-ddl = true`로 **먼저 설정해 준 뒤에**

    2. `spring.jpa.hibernate.ddl-auto = {validate | update | create | create-drop}`

       - `validate` 실제로 스키마를 생성하지는 않고, 유효성만 검사해준다.

       - `create-drop`은 초기에 생성하고 꺼지기 전에 삭제하고,
       - `create`는 초기에 일단 삭제부터 한 뒤에 생성한다
       - `update`는 기존 스키마는 두고, 추가된 것만 변경해준다. (**데이터가 유지되어 편하다**)

  - **스키마 생성되는 로그** : `spring.jpa.show-sql = true`

    

- **SQL 스크립트**로 초기화하기

  - 미리 스크립트를 만들어두면, 애플리케이션이 시작될 때마다 실행해서 초기화할 수 있다.

  - `hibernate`를 사용하지 않고 초기화 하는 방법!

    - `schema.sql` 또는 `schema-${platform}.sql` 

    - `data.sql` 또는 `data-${platform}.sql`

      > `schema.sql`이 먼저 실행되고, 다음에 `data.sql`이 실행된다!

    - `${platform}` 값은 `spring.datasource.platform = { dev... }` 으로 설정 가능

      > 각 플랫폼을 정의해서, 플랫폼에 맞는 쿼리를 실행시킬 수 있다.



- **[주의]** : 만약 컬럼의 이름을 바꾸고 **update** 한다면???

  **컬럼의 이름이 바뀌는 게 아니라 새 컬럼으로 추가된다!** 

  - 안 쓰는 컬럼이 계속 남아 있게 되기 때문에.. 개발할 때는 편하지만 **운영할 때는 주의해야 한다**

  만약 버전 관리 하듯이 스키마도 해주고 싶다면 **마이그레이션**을 해주자!

  

#### 2) 데이터베이스 마이그레이션

- **종류 및 목적**

  - `Flyway`와 `Liquibase`가 대표적이다.
  - DB 스키마나 데이터의 변경을 버전 관리하듯이 차곡차곡 관리해준다.
  - 스트립트를 통해 스키마 뿐 아니라 데이터도 추가해줄 수 있다.

- **사용**

  1.  역시 먼저 **의존성을** 추가하고,

     ```xml
     <dependency>
         <groupId>org.flywaydb</groupId>
         <artifactId>flyway-core</artifactId>
     </dependency>
     ```

  2. 리소스 밑에 **디렉토리**를 만들어준다. `resources/db/migration`

     - `resources/db/migration/{vender}`도 가능하다.
     - 경로를 아예 바꾸고 싶다면 `spring.flyway.locations = {$path}`로 설정해준다.

  3. **SQL 파일을 추가**해준다. 이름은 V숫자\__이름.sql (Ex. `V1__init.sql` )

     - V는 꼭 대문자로 써야 하고, 숫자는 순차적으로 붙인다 (**타임스탬프 추천!**)
     - 숫자와 이름 사이에 언더바 두 개를 붙인다.
     - 마지막으로, 이름은 어떤 목적인지 알기 편하게 서술적으로 써준다.

- **응용**

  - 먼저 `flyway`로 스키마를 생성해 준 뒤에,
  - `spring.jpa.hibernate.ddl-auto = validate` 로 해주면 잘 생성됐는지 확인까지 해줄 수 있다.

- **주의**

  - 한 번 적용이 된 스크립트는 수정하지 않는다.
  - 스키마의 변경 사항이 생기면 **새 파일을 만들어줘야** 한다.



___

### 5. NoSQL

#### 1) Redis

- 캐시, 메시지 브로커, key/value 스토어 등으로 사용 가능.

- **초기 설정**

  1. 호스트에 **Redis**부터 설치해준다.

     **Docker**!

     ```sh
     docker run -p 6379:6379 --name redis_boot -d redis
     docker exec -i -t redis_boot redis-cli	
     ```

     **우분투** 설치

     ```sh
     sudo apt-get update
     sudo apt-get install redis-server
     ```

     +) 안될 경우 [**askubuntu**](https://askubuntu.com/questions/868848/how-to-install-redis-on-ubuntu-16-04) 참고

  2. **의존성 추가**

     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-data-redis</artifactId>
     </dependency>
     ```

     이렇게만 해주면, 스프링 부트가 설정 다 해주니까 갖다 쓰기만 하면 된다!

     

- **사용법**

  - **Redis**를 사용하는 데에는 두 가지 방법이 있다.

    - `StringRedisTemplate` : String 타입에 특화된`RedisTamplate`

    - `CrudRepository` : `JPARepository`와 비슷하게 사용할 수 있다.

      - 먼저 레파지토리를 생성해준다.

        ```java
        public interface AccountRepository 
        	extends CrudRepository<Account, String> {}
        ```

      - 해당 레파지토리를 가져다가 JPA처럼 그냥 쓰면 된다!

        ```java
        @RedisHash("accounts")
        public class Account {}
        
        Account account = new Account();
        account.setEmail("soyoungpark.me@gmail.com");
        account.setUsername("soyoung");
        
        accountRepository.save(account);
        ```

      - 추가하고 **CLI**로 확인해보면,

        ```sh
        127.0.0.1:6379> keys *
        1) "accounts"
        2) "accounts:e5daedbb-e63e-4ebf-8621-3a571b1712d1"
        
        127.0.0.1:6379> hgetall accounts:e5daedbb-e63e-4ebf-8621-3a571b1712d1
        1) "_class"
        2) "me.soyoung.jdbc.redis.Account"
        3) "id"
        4) "e5daedbb-e63e-4ebf-8621-3a571b1712d1"
        5) "username"
        6) "soyoung"
        7) "email"
        8) "soyoungpark.me@gmail.com"
        ```

        이렇게 값이 들어가 있는 것을 확인할 수 있다.

        

- **설정** : `applicaion.properties`에 추가한다.

  ```properties
  spring.redis.{key} = value // 이런 식으로 작성하면 된다!
  spring.redis.url = http://localhost
  spring.redis.port = 1234
  ```

- **CLI 명령어**

  주요 커맨드는 [**reference**](http://spring.io/projects/spring-data-redis) 를 참고하자!

  

#### 2) MongoDB

- JSON 기반의 도큐먼트 데이터베이스로, 스키마가 없다.

- **초기 설정**

  1. 호스트에 **MongoDB**설치

     **Docker!**

     ```sh
     docker run -p 27017:27017 --name mongo_boot -d mongo
     docker exec -i -t mongo_boot bash
     mongo
     ```

     **우분투 설치**

     ```sh
     sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
     echo "deb http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list
     sudo apt-get update
     sudo apt-get install -y mongodb-org
     sudo systemctl start mongod
     ```

     

  2. **의존성 추가**

     ```xml
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-mongodb</artifactId>
     </dependency>
     ```

- **사용법**

  - `MongoTemplate`와 `mongoRepository`를 이용해서 자유롭게 쓸 수 있다.

    - 템플릿으로 바로 쓰면 된다!

      ```java
      @Document(collection = "accounts")
      public class Account {}
      
      @Autowired
      MongoTemplate mongoTemplate;
      
      @Autowired
      MongoRepository mongoRepository;
      
      Account account = new Account();
      account.setEmail("soyoungpark.me@gmail.com");
      account.setUsername("soyoungpark");
      
      mongoTemplate.insert(account);
      ```

    - 레파지토리로 쓰고 싶다면

      ```java
      public interface AccountRepository extends MongoRepository<Account, String> {}
      
      @Autowired
      MongoRepository mongoRepository;
      
      mongoRepository.save(account);
      ```

  

- **설정** : `applicaion.properties`에 추가한다.

  ```properties
  spring.data.mongodb.port = 27017
  spring.data.mongodb.database = test
  ```

  

- **테스트**

  - 테스트용 DB를 따로 파는 것은 번거롭기 때문에  부트에서 내장형 봉고 DB를 써도 된다.

  - 사용법은 다음과 같다!

    1. **의존성 추가**

       ```xml
       <dependency>
           <groupId>de.flapdoodle.embed</groupId>
           <artifactId>de.flapdoodle.embed.mongo</artifactId>
           <scope>test</scope>
       </dependency>
       ```

    2. **슬라이스 테스트**로 사용하기

       ```java
       @RunWith(SpringRunner.class)
       @DataMongoTest
       public class AccountRepositoryTest {
           @Autowired
           AccountRepository accountRepository;
           
           ...
       }
       ```

       이렇게 사용해도 실제 데이터베이스에는 영향을 주지 않는다.

       

#### 3) Neo4j

- 노드들의 연관 관계를 영속화하는데 유리한 그래프 데이터베이스이다.

  ![이미지](http://i.imgur.com/U9SPDyM.png)

- **초기 설정**

  1. 호스트에 **Neo4j** 설치

     - **도커**

       ```sh
       docker run -p 7474:7474 -p 7687:7687 -d --name noe4j_boot neo4
       ```

     - **우분투 설치**

       ```sh
       wget --no-check-certificate -O - https://debian.neo4j.org/neotechnology.gpg.key | sudo apt-key add -
       echo 'deb http://debian.neo4j.org/repo stable/' | sudo tee /etc/apt/sources.list.d/neo4j.list
       sudo apt update
       sudo apt install neo4j
       sudo systemctl start neo4j
       ```

       

  2. **의존성 추가**

     ```xml
     <dependency>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-starter-data-neo4j</artifactId>
     </dependency>
     ```

     버전에 따라 여러 빈들을 설정해주지만, **하위 호환성이 없다**는 것에 주의해야 함...

     `SessionFactory` 빈만 사용이 가능하다고 함! (`Neo4jTemplate`은 Deprecated 되었다)

- **사용법**

  - 따로 템플릿이나 레포지토리를 쓰지 않는다.

    ```
    @NodeEntity
    public class Account {}
    
    @Autowired
    SessionFactory sessionFactory;
    
    Session session = sessionFactory.openSession();
    session.save(account);
    sessionFactory.close(); // 닫아줘야 앱이 뜬다!
    ```

  - 레파지토리로 쓰고 싶다면

    ```java
    public interface Neo4jAccountRepository extends Neo4jRepository<Account, Long> {}
    
    @Autowired
    Neo4jAccountRepository neo4jAccountRepository;
    
    neo4jAccountRepository.save(account);
    ```

  - 저장이 끝나면 다음과 같이 웹으로 확인할 수 있다! (주소는 http://localhost:7474/browser/)

    ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzBfMjUg/MDAxNTQ2MTc3NTYwNTQ2.WzHoFxLP7vOzoqQa_mEe5SZLzpRBbJ7Lqv0FHypal1Ag.1cMDGaKsKWWhCdYBzHxmyiUigJQ8R0yw4sy1xnrWALkg.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-30_22-42-54.png)

- **설정**

  ```properties
  spring.data.neo4j.username = neo4j
  spring.data.neo4j.password = qwer1234
  ```