# 스프링 데이터 JPA

**출처 : [스프링 데이터 JPA - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-jpa/)**

#### 목차

1. 스프링 데이터 JPA 시작하기

2. **JPA의 핵심 개념 (특성) 이해하기**

   - [**프로젝트 세팅하기**](#1-프로젝트-세팅하기)
   - [**엔티티 타입 매핑**](#2-엔티티-타입-매핑)
   - [**Value 타입 매핑**](#3-Value-타입-매핑)
   - [**관계 매핑**](#4-관계-매핑)
   - [**엔티티 상태와 Cascade**](#5-엔티티-상태와-Cascade)
   - [**Fetch**](#6-Fetch)
   - [**쿼리**](#7-쿼리)

3. 스프링 데이터 JPA 활용 (Common)

4. 스프링 데이터 JPA 활용 (웹 기능)

5. 스프링 데이터 JPA 활용 (JPA)

  ​    

___

## JPA의 핵심 개념 (특성) 이해하기

### 1. 프로젝트 세팅하기

#### 1) 의존성 추가

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```



#### 2) 환경 변수 설정

```properties
spring.datasource.url = jdbc:mysql://localhost:3306/test?useSSL=false
spring.datasource.url = jdbc:mysql://localhost:3306/test?characterEncoding=UTF-8&serverTimezone=UTC 

spring.datasource.username = {$username}
spring.datasource.password = {$password}

spring.jpa.generate-ddl = true
spring.jpa.hibernate.ddl-auto = {validate | update | create | create-drop}

spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation = true

spring.jpa.show-sql = true
spring.jpa.properties.hibernate.format_sql = true
logging.level.org.hibernate.type.descriptor.sql = true
```

02) **MySQL**에서 **timezone**과 관련된 오류가 발생할 경우

10) 데이터베이스의 오류 로그들이 찍히는 걸 비활성화하고 싶을 경우



___

### 2. 엔티티 타입 매핑

#### 1) 엔티티 매핑이란?

- 도메인 모델을 만들었으면, 해당 모델을 테이블에 **어떻게 매핑시킬지**에 대한 정보를 줘야 한다.

- **애노테이션**을 사용하는 방법과 **XML**을 사용하는 방법이 있다.

- **대부분 애노테이션을 사용한다**

  

#### 2) 주요 애노테이션

- `@Entity` : **객체 세상에서 부르는 이름**

  - 도메인 클래스가 엔티티이다. **자동으로 테이블을 만들어준다**
  - 기본적으로 테이블 이름은 클래스의 이름과 같게 된다.

- `@Table` : **릴레이션 세상에서 부르는 이름**

  - `@Table(name = "{$table_name}")`으로 테이블 이름을 바꿔줄 수 있다.
  - 테이블의 이름은 **SQL**에서 쓰이게 된다.

- `@Id` : **엔티티의 기본 키를 매핑**
  - 모든 `primitive` 타입과 그 `Wrapper` 타입을 모두 지원한다.
  - 보통 `reference`타입으로 쓰신다고 함!
    - 실제로 0의 값이 필요할 수 있는데, primitive 타입으로 하면 값을 주지 않았을 시 0이 된다.

- `@GeneratedValue` : **기본 키의 생성 방법을 매핑**
  - 아무 옵션을 주지 않았다면, **DB에 따라** 생성 방법이 달라진다.
    - 기본적으로는 `auto`이고, `table`, `sequence`, `identity` 중 하나가 된다.
    - `@generatedValue(strategy = {$type})`으로 직접 설정할 수도 있다.

- `@Column` : 모든 필드에 Column은 자동으로 붙는다. 

  - `name`, `unique`, `nullable (기본 true)`, `length`, `columnDefinition (SQL 명시)`)

- `@Temporal` : `Date`와 `Calender`를 지원해준다.

  - TIME, DATE, DATETIME 중 하나를 선택해서 사용할 수 있다.

  - **JPA 2.2** 부터는 **JAVA 8**의 `LocalDate` 같은 애들도 지원해준다.

    ```java
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();
    ```

- `@Transient` : **컬럼으로 매핑하고 싶지 않는 멤버 변수**에 사용한다.



#### 3) 설정

- **SQL 내용**을 로그에 찍어달라 : `spring.jpa.show-sql = true`
- 읽기 힘드니까 **편하게** 보여달라 : `spring.jpa.properties.hibernate.format_sql = true`



___

### 3. Value 타입 매핑

#### 1) Entity 타입과 Value 타입의 구분

- **Entity 타입** : 고유한 식별자를 가지고 있다. (Id 같은...) 

  - 독립적으로 존재하기 때문에 다른 엔티티에서 해당 엔티티로 참조할 수도 있다.

- **Value 타입** : 엔티티에 속하는 멤버 변수들... 특정 엔티티를 통해서 접근할 수 있다.

  *Ex) 주소는 엔티티일까?*

  - 위치 정보를 다루는 서비스라면 주소 자체가 엔티티가 될 수 있다. 독립적으로 레퍼런스 되어야 하는 경우.

  - 특정 유저의 주소를 나타내는 경우라면, 해당 주소의 생명 주기는 특정 유저 (엔티티)에게 속해 있다.

    

#### 2) Value 타입 종류

- **기본 타입** : `String`, `Date`, `Boolean`

- **Composite Value** 타입 :  **복합 기본 키**

- **Collection Value** 타입 : 기본 타입의 콜렉션과 컴포짓 타입의 콜렉션...

  

#### 3) Composite Value 타입 매핑

- `@Embadable` : 기간 (시작 날짜, 끝 날짜)나 좌표 (X, Y) 등의 값들을 **객체로 묶어** 필드로 씬다.

  - 해당 객체에 붙여줘야 한다.

- `@Embadded` : 엔티티의 필드에 붙는다. `@Embadable`이 붙은 객체를 매핑하겠다는 뜻.

- `@AttributeOverrides` : 같은 Value 타입이 여러 곳에서 매핑 되는 경우

- `@AttributeOverride` : 어떤 컬럼에 어떤 이름으로 매핑할 건지 써준다.

  ```java
  @Embedded
  @AttributeOverrides({
      @AttributeOverride(
          name = "street", 
          column = @Column(name = "home_street")
      )
  })
  private Address address;
  ```

  

___

### 4. 관계 매핑

#### 1) 관계란?

- 관계에는 항상 **두 가지 엔티티**가 존재한다.
- 하나는 그 관계의 **주인 (owning)**이고, 다른 쪽은 **종속된 (non-owning)** 쪽이다.
  - 관계의 주인은, 그 관계를 설정했을 때 해당 값을 반영받는 것!

#### 2) 단방향

- `@ManyToOne` : N-1 상태일 경우... 관계의 주인은 나. **기본 키를 만들어준다**

  Ex) 현재 스터디가 **누구에게 소속되어 있는지** 표시한다.

  ```java
  public class Study {
  	...
      @ManyToOne
      private Account owner;
      ...
  }
  ```

  이렇게 해당 필드에 붙여주기면 하면,

  ```sql
  create table study ( ... owner_id bigint, ... );
  
  alter table study 
         add constraint FK210g5r7wftvloq2ics531e6e4 
         foreign key (owner_id) 
         references account (id)
  ```

  자동으로 이렇게 외래 키까지 설정해준다! 그래서 이렇게 설정해주면 된다!

  ```java
  study.setOwner(account);
  ```

  

- `@OneToMany` : 1-N 상태일 경우! **Join 테이블으로 만들어준다.**

  Ex) 해당 유저가 **어떤 스터디들을 가지고 있는지** 표시한다.

  ```java
  public class Account {
  	...
  	@OneToMany
      private Set<Study> studies = new HashSet<>();
      ...
  }
  ```

  **SQL 쿼리문** 결과는 다음과 같이 나온다.

  ```sql
  create table account_studies (
      account_id bigint not null,
      studies_id bigint not null,
      primary key (account_id, studies_id)
  ) engine=MyISAM
      
  alter table account_studies 
      add constraint FKem9ae62rreqwn7sv2efcphluk 
      foreign key (studies_id) 
      references study (id)
      add constraint FK4h3r1x3qcsugrps8vc6dgnn25 
      foreign key (account_id) 
      references account (id)
  ```

  이렇게 아예 아예 새로 테이블을 생성해준 뒤, 외래 키로 각자 연결한다.

  저장해주기 위해 컬렉션 목록에 추가해준다.

  ```java
  account.getStudies().add(study);
  ```

  

#### 3) 양방향

- 두 관계가 상호 간 참고하고 싶을 수도 있다. 이럴 때는 양방향으로 만들어준다.

  > 그런데 양 테이블에 각자 `@ManyToOne`과 `@OneToMany`를 추가해주는 것은 양방향이 아니다!
  >
  > 이건 그저 서로 무관한 **두 개의 단방향 관계가 생성된 것**

  **주인이 아닌 쪽에서 ** 이 관계가 반대편에 어떻게 매핑이 되어 있는지를 명시해줘야 한다.

- `@ManyToOne` : 이 쪽이 주인 일 경우에는 그대로 써준다.

- `@OneToMany(mappedBy = {$누구})` 

  누구랑 물려 있는 관계인지를 알려주는 것. 

  {$누구} 자리에는 어떤 필드와 참조 관계인지를 써주면 된다.

  ```java
  public class Accoutn {
      @OneToMany(mappedBy = "owner")
      private Set<Study> studies = new HashSet<>();
  }
  
  public class Study {
      @ManyToOne
      private Account owner;
  }
  ```

  **[대박 주의사항]** : **주인에게 관계를 설정해줘야** DB에 반영이 된다! 

  Ex) 현재 관계의 주인은 **Study** 이므로 **Study** 에게 설정해줘야 반영됨!

  ```sql
  account.getStudies().add(study); 		(X)
  
  mysql> select * from study;
  +----+-----------------+----------+
  | id | name            | owner_id |
  +----+-----------------+----------+
  |  1 | String Data JPA |     NULL |
  +----+-----------------+----------+
  1 row in set (0.00 sec)
  
  ______________________________________________
  study.setOwner(account);				(O)
  account.getStudies().add(study);		
  
  mysql> select * from study;
  +----+-----------------+----------+
  | id | name            | owner_id |
  +----+-----------------+----------+
  |  1 | String Data JPA |        2 |
  +----+-----------------+----------+
  1 row in set (0.00 sec)
  
  mysql> select * from account_studies;
  +------------+------------+
  | account_id | studies_id |
  +------------+------------+
  |          2 |          1 |
  +------------+------------+
  1 row in set (0.00 sec)
  ```

  +) `account.getStudies().add(study)` 이 친구는 사실 없어도 **DB에는 반영이 된다**

  - 하지만 **ORM을 통해 객체에서 사용하기 위해서는** 이 친구도 붙여줘야 한다.
  - 두 라인을 함께 포함하는 **함수를 하나 만드는 걸 추천**

  ```java
  public void addStudy(Study study) {
      this.getStudies().add(study);
      study.setOwner(this);
  }
  
  public void removeStudy(Study study) {
      this.getStudies().remove(study);
      study.setOwner(null);
  }
  ```

  

___

### 5. 엔티티 상태와 Cascade

#### 1) 엔티티의 상태란?

![이미지](https://www.objectdb.com/files/images/manual/jpa-states.png)

- **Transient **(New) : JPA가 **모르는 상태**

  - 객체 자체는 만들어졌지만, JPA에게 전달되지 않아 관리가 불가능함!

- **Persistent** (Managed) : JPA가 **관리 중인 상태**

  - 만약 그 상태에서 `save()` 해주면 이 상태가 된다.

  - `save()` 해줬다고 바로 DB에 저장되는 건 아니다.

  - JPA가 **계속 관리**하고 있다가, 이쯤 되면 저장해야겠다! 싶을 때 저장한다. (트랜잭션 끝날 때?)

    - **1차 캐시** : 캐싱해놓고 추후 가져다 쓴다! , Write Behind ...

      ```java
      Account account = new Account(); // 만들고
      session.save(account);			 // 저장한 뒤에
      Account selected = session.load(Account.class, account.getId()) 
      ```

      이렇게 저장하고 다시 불러도 **SELECT** 쿼리를 한 번 더 하지 않는다.

    - **Dirty Checking** : JPA가 변경 사항을 계속 감지하는 것
    - **Write Behind** : 객체의 상태 변화를 DB에 최대한 늦게 반영하는 것

    

- **Detached** : JPA가 더 이상 **관리하지 않는 상태** (세션이 끝남)

  - 트랜잭션이 끝난 이후에는 이미 DB에 들어간 객체이기 때문에 더 이상 관리하지 않음!
  - 위에서 언급한 **1차 캐시**, **Dirty Checking**, **Write Behind**, **Lazy Loading** 발생 X!
  - 이런 장점을 다시 이용하고 싶다면 `reattach` 해줘야 한다.

- **Removed** : JPA가 관리하긴 하지만 **삭제하기로 한 상태**

  - 실제로 커밋이 일어날 때 삭제한다.



#### 2) Parent - Child 관계

- 위의 예시인 **Account**와 **Study**는 이 관계가 아니다. 서로가 약간 독립적임!

  - **Account**가 지워진다고 **Study**가 모두 삭제될 필요는 없다.

- 좋은 예시로 **Post**와 **Comment**의 관계가 있다.

  

#### 3) Cascade 사용하기

- **cascade** : 엔티티의 상태 변화를 전파 시키는 옵션
- `@OneToMany`나 `@ManyToOne`에 `cascade` 옵션을 줄 수 있다.
- 특정 엔티티의 상태가 A에서 B로 변할 때, 이와 관계가 있는 엔티티의 상태도 옮겨 주고 싶은 경우!



- 만약 `@OneToMany`와 `@ManyToOne`이 설정된 상태에서, 종속된 쪽만 저장하면...

  반대 쪽의 데이터는 DB에 저장되지 않는다.

  **이 상태에서 PETSIST 타입을 적용해주면**

  ```java
  @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST)
  private Set<Comment> comments = new HashSet<>();
  ```

  실제로 DB를 조회해 봤을 때 잘 저장되는 것을 볼 수 있다!!!

  ```sql
  mysql> select * from post;
  +----+-------+
  | id | title |
  +----+-------+
  |  1 | TITLE |
  +----+-------+
  1 row in set (0.00 sec)
  
  mysql> select * from comment;
  +----+-----------+---------+
  | id | contents  | post_id |
  +----+-----------+---------+
  |  2 | COMMENT 1 |       1 |
  |  3 | COMMENT 2 |       1 |
  +----+-----------+---------+
  2 rows in set (0.00 sec)
  ```

- 이런 식으로 여러 개의 조건을 한 번에 붙여줄 수도 있다. **보통은 `CascadeType.ALL`로 한 번에!**

  ```java
  @OneToMany(mappedBy = "post", 
             cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private Set<Comment> comments = new HashSet<>();
  ```

  

___

### 6. Fetch

#### 1) Fetch란?

- 연관 관계의 엔티티에 대한 정보를 어떻게 가져올 것인가에 대한 설정
- 지금 당장 가져올 건지, 나중에 가져올 건지를 뜻한다!



#### 2) Fetch의 종류

- **Lazy** (나중에) : `@OneToMany`의 기본값이다.

  - 몇 개나 있는지도 모르고, 당장 필요하지도 않는데 왕창 가져오는 건 비효율적이다.

  Ex) 포스트에 대한 정보를 가져올 때, **댓글은 바로 가져오지 않는다**

- **Eager** (지금) : `@ManyToOne`의 기본값이다.

  - 데이터가 하나 밖에 안되니까, 나중에 또 **SELECT** 하지 말고 그냥 **JOIN**해서 미리 가져오자.

  Ex) 댓글에 대한 정보를 가져올 때, **소속된 포스트 정보는 바로 가져온다**

  

#### 3) 설정해주기

- 타입을 바꿔주기 위해서는 `fetch` 속성 값을 바꿔주면 된다.

  ```java
  @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
  private Set<Comment> comments = new HashSet<>();
  ```

- 쿼리를 찍어보면 실제로 **JOIN** 이 발생하는 걸 알 수 있다!

  ```sql
  select 
  	post0_.id as id1_2_0_,
  	post0_.title as title2_2_0_,
  	comments1_.post_id as post_id3_1_1_,
  	comments1_.id as id1_1_1_,
  	comments1_.id as id1_1_2_,
  	comments1_.contents as comment2_1_2_,
  	comments1_.post_id as post_id3_1_2
  from
  	post post0_
  left outer join
  	comment comments1_
  		on post0_.id=comments1_.post_id
  where
  	post0_id=?	
  ```

- 성능하고 직결되는 문제이다. 잘 선택해줘야 함!

- **[주의]** toString 찍을 때 쓰이면, `fetch.Lazy` 더라도 바로 끌어와버릴 수 있다!



___

### 7. 쿼리

- 우리는 `EntityManager`를 이용해서 직접 다양한 DB 작업을 해줄 수 있었다!

- 이를 통해서 직접 쿼리를 날리는 방식으로 사용할 수도 있다!

  

#### 1) JPQL (HQL) [(REF)](**https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#hql**)

- Java Persistence Query Language / Hibernate Query Language

- 데이터베이스 테이블이 아닌, **엔티티 객체 모델 기반**으로 쿼리 작성.

  > 따라서 데이터베이스에 독립적으로 작성할 수 있다.

- JPA 또는 하이버네이트가 해당 쿼리를 SQL로 변환해서 실행함.

  ```java
  TypedQuery<Post> query = 
      entityManager.createQuery("SELECT p FROM Post As p", Post.class);
  List<Post> posts = query.getResultList();
  ```

  

#### 2) Criteria [(REF)](**https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#criteria**)

- 타입 세이프 쿼리, *근데 너무 복잡해서 쓰고 싶지 않아 보임 ㅠㅠ*

  ```java
  CriteriaBuilder builder = entityManager.getCriteriaBuilder();
  
  CriteriaQuery<Post> criteria = builder.createQuery(Post.class);
  Root<Post> root = criteria.from(Post.class);
  criteria.select(root);
  
  List<Post> posts = entityManager.createQuery(criteria).getResultList();
  ```

  

#### 3) Native Query [(REF)](**https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#sql**)

- SQL 쿼리 실행하기

  ```java
  List<Post> posts = entityManager
  	.createNativeQuery("SELECT * FROM Post", Post.class)
      .getResultList();
  ```

- 이렇게 안하고 해당 엔티티에 `@NamedQueries` 애노테이션을 붙여서 가져다 써도 된다.

  ```java
  @NamedQueries({
      @namedQuery(name = "all_posts", query = "SELECT p FROM Post")
  })
  @Entity
  public class Post { ... }
  ```

  