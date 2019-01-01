# 스프링 데이터 JPA

**출처 : [스프링 데이터 JPA - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-jpa/)**

#### 목차

1. **스프링 데이터 JPA 시작하기**

   - [**관계형 데이터베이스와 자바**](#1-관계형-데이터베이스와-자바)
   - [**ORM 개요**](#2-ORM-개요)
   - [**ORM 패러다임 불일치**](#3-ORM-패러다임-불일치)
   - [**스프링 데이터 JPA 원리**](#4-스프링-데이터-JPA-원리)

2. JPA의 핵심 개념 (특성) 이해하기

3. 스프링 데이터 JPA 활용 (Common)

4. 스프링 데이터 JPA 활용 (웹 기능)

5. 스프링 데이터 JPA 활용 (JPA)

     

___

## 스프링 데이터 JPA 시작하기

### 1. 관계형 데이터베이스와 자바

#### 1) JDBC

- 스프링에서는 데이터베이스에 접근하고 관리하기 위해 **JDBC**를 사용한다.

- **사용법**

  - **JDBC**의 의존성을 추가한다

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    ```

  - 사용하고자 하는 데이터베이스의 의존성도 추가해준다.

    ```xml
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```

    

  1. `DataSource`, `DriverManager`로 데이터베이스를 사용할 수 있도록 만든 다음에...
  2. `Connection` 을 가져와서 실제로 작업을 해준다.
  3. `PreparedStatement` 로 쿼리를 실행해준다.

  

- **문제 (단점)**

  - **SQL**을 실행하는 비용이 비싸고, 데이터베이스마다 다르게 작성해줘야 한다.
  - 스키마를 바꾸면 코드를 하나하나 수정해줘야 하고, 반복되는 코드도 많아진다.
  - 특정 도메인에 매핑 해줘야 할 경우 타입 변환도 해줘야 하고... 귀찮은 일이 많다.

  

___

### 2. ORM 개요

#### 1) JDBC와 비교하기

- **JDBC**를 사용한다면?

  ```java
  try(Connection connection = DriverManager.getConnection(url, username, password)) {
      System.out.println("Connection created: " + connection);
      String sql = "INSERT INTO ACCOUNT VALUES(1, 'soyoung', 'qwer1234');";
      
      try(PreparedStatement statement = connection.prepareStatement(sql)) {
          statement.execute();
      }
  }
  ```

  이렇게 직접 **JDBC**를 통해서 DB에 접속하고, 쿼리를 작성해줘야 한다.

  

- **ORM**을 사용한다면?

  ```java
  Account account = new Account(“soyoung”, “qwer1234”);
  accountRepository.save(account);
  ```

  우리는 도메인 모델을 기반으로 코딩하고 싶다!



#### 2) ORM : Object-Relation Mapping

- **ORM의 정의**

  > ORM은 애플리케이션의 클래스와 SQL 데이터베이스의 테이블 사이의 
  >
  > **맵핑 정보를 기술한 메타데이터**를 사용하여, 
  >
  > 자바 애플리케이션의 객체를 SQL 데이터베이스의 테이블에 
  >
  > **자동으로 (또 깨끗하게) 영속화** 해주는 기술입니다.

- **맵핑 정보를 기술한 메타데이터**는 우리가 제공해줘야 한다. (클래스와 테이블, 필드와 컬럼 매칭 등 ...)

  

#### 3) 그렇다면 왜 ORM을 쓸까?

- **객체 지향 프로그래밍**의 장점을 활용하기 좋다.
- 각종 **디자인 패턴**을 사용할 수 있다.
- **코드 재사용성**이 좋고, **유지 보수**가 용이하다.
- 비즈니스 로직을 구현하고 **테스트**하는 것이 편해진다.
- **벤더 독립성** : 어떤 데이터베이스를 쓸지만 알려주면 알아서 SQL을 생성해준다.



___

### 3. ORM 패러다임 불일치

#### 1) 이게 무슨 말일까

- **ORM**을 학습하는 데 많은 비용이 든다는 것은, ORM이 해결하고자 하는 것이 어려운 것이기 때문!

- **객체를 릴레이션에 매핑**하자니 많은 문제들이 발생한다

  

#### 2) 다양한 문제들

- **밀도 (Granularity) 문제**

  - 객체는 다양한 데이터 타입을 쓸 수 있고 만들 수도 있지만 릴레이션은 정해져 있다.

- **서브타입 (Subtype) 문제**

  - 객체는 상속 구조를 만들고 다형성으로 참조하기 쉽지만 릴레이션에는 해당 표준 기술이 없다.

- **식별성 (Identity) 문제**

  - 객체에서 같은 객체인지 확인할 때 레퍼런스 동일성 (`==`) 이나 인스턴스 (`equals()`) 동일성을 쓴다.
  - 릴레이션에서는 기본키를 쓴다.

- **관계 (Association) 문제**

  - 객체 사이의 관계에는 방향이 있고, 다대다 관계를 가질 수 있다.
  - 릴레이션에서는 외래키로 관계를 표현할 수 있지만, 방향의 의미가 없으며 다대다 관계의 개념이 없다.

- **데이터 네비게이션 (Navigation) 문제**

  - 가장 복잡하고 어렵고... 성능에도 영향을 주는 문제이다.

  - 객체에서는 레퍼런스를 이용해 **다른 객체로 이동**하고, **컬렉션도 순회** 할 수 있다.

  - 릴레이션에서는 이 방법을 쓰기에 **너무 비효율적**이다.

    - DB에는 요청이 적게 들어갈 수록 성능이 좋기 때문에 **Join**으로 한 번에 끌어온다.

    - 근데 여기서도 너무 많이 한 번에 가져오려고 해도 문제고,

    - **Lazy Loading** 을 하는 것도 문제다 (n+1 select)

      >**즉시로딩** : 엔티티를 조회 할 때 연관된 엔티티도 함께 조회한다.
      >
      >**지연로딩** : 엔티티를 조회 할 때 연관된 엔티티를 실제 사용할 때 조회한다.



___

### 4. 스프링 데이터 JPA 원리

#### 1) `JpaRepository<Entity, Id>` 인터페이스

- 만약에 레포지토리를 직접 만든다면?

  ```java
  @Repository
  @Transactional
  public class PostRepository {
  
      @PersistenceContext
      EntityManager entityManager;
      
      public Post add(Post post) {
          entityManager.persist(post);
          return post;
      }
      
      public void delete(Post post) {
          entityManeger.remove(post);
      }
      
      public List<Post> findAll() {
          return entityManager.createQuery(
          	"SELECT p FROM Post As p")
              .getResultList();
      }
  }
  ```

  SQL마저도 포함되어 있고, 테스트 짜기고 힘들고 귀찮다.

  > 이런 문제 때문에 제네릭한 레포지토리를 만들어 쓰는 프레임워크가 유행하기도 했음

- 이러지 말고, `JpaRepository`를 상속받은 인터페이스를 만들어 쓰자!

  ```java
  public interface {$이름} extends 
  	JpaRepository<{$엔티티 클래스 이름}, {$Id 타입}> {}
  ```

  여기에 `@Repository` 애노테이션도 안 붙여줘도 된다!

- 그리고 갖다 쓰면 된다. 이미 구현되어 있는 많은 메소드들이 있다!

  이미 스프링에서 만들어 놓은 것이기 때문에 따로 테스트를 작성할 필요도 없다.

  

#### 2) `@EnableJpaRepositores`

- 만약 스프링 부트를 쓰지 않고 직접 환경을 잡아줘야 한다면

- `@SpringBootApplication` 클래스에 `@EnableJpaRepositories`를 붙여줘야 함!

  **`@Configuration` 클래스에 붙여줘야 한다고 함!!!** *스프링 어려워*

  - `@EnableJpaRepositories`에 들어가보면,

    `@Import(JpaRepositoriesRegister.class)`가 있다.

    이 친구가 `JpaRepositories`를 빈으로 등록해주는 역할을 한다.

    `ImportBeanDefinitionRegister` 인터페이스가 등장하는데...

    - 프로그래밍적 방법으로 빈을 등록해주는 역할을 한다고 함 ...
    - **`JpaRepository`를 상속받은 클래스들을 모두 빈으로 등록한다!**

