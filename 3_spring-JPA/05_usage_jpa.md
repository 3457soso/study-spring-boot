# 스프링 데이터 JPA

**출처 : [스프링 데이터 JPA - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-jpa/)**

#### 목차

1. 스프링 데이터 JPA 시작하기

2. JPA의 핵심 개념 (특성) 이해하기

3. 스프링 데이터 JPA 활용 (Common)

4. 스프링 데이터 JPA 활용 (웹 기능)

5. **스프링 데이터 JPA 활용 (JPA)**

  - JpaRepository와 save 메소드
  - JPA 쿼리 메소드와 Sort와 SpEL
  - EntityGraph
  - Projection
  - Specifications와 Query by
  - 트랜잭션
  - Auditing

  ​    

___

## 스프링 데이터 JPA 활용 (JPA)

### 1. JpaRepository와 save 메소드

#### 1) JpaRepository

- `@EnableJpaRepository`가 있어서, 스프링 부트를 쓸 떄는 자동 설정이 된다.
- `@Repository` 애노테이션을 안 붙여도 된다.
- `SQLException`이나 JPA 관련 예외는 `DataAccessException`으로 변환된다.
  - 예외를 이해하기 쉽다.



#### 2. JpaRepository의 save()

- `JpaRepository`의 `save()`는 단순히 새 엔티티를 추가하는 메소드가 아니다!

  - `save()` 메소드는 실제로 저장하기 전에 해당 엔티티의 유무를 확인한다.

  - **Transient** 상태라면 `EntityManager.persist()`

    - 새로 만들어진 객체이기 때문에 `Hibernate`가 관리하지 못한다.
    - 새로운 객체를 **Persistent**한 상태로 만들어준다.

  - **Detached** 상태라면 `EntityManager.merge()`

    - 해당 객체에 매핑되는 내용이 테이블에 이미 존재하는 경우...

    - 기존에 있던 객체를 **Persistent**한 상태로 만들어준다.

      > 전달받은 객체의 복사본을 만들고 해당 복사본을 영속화
      >
      > 전달한 객체는 **Persistent**하게 만들지 않는다.
      >
      > return한 값이 **Persistent**한 상태가 된다!

- **Transient**인지 **Detached**인지...

  - 엔티티의 `@Id` 프로퍼티를 찾아서, 해당 내용이 **null인지 아닌지로 판단**

- **[TIP]** : 실수를 줄이기 위해서는, 파라미터로 준 객체를 사용하지 말고 **반환된 객체를 쓰자**



___

### 2. JPA 쿼리 메소드와 Sort와 SpEL

#### 1. 쿼리 메소드

- 해당 키워드들은 **스프링 데이터 Data**에서 지원한다.

- **Jpa용 키워드들이다!**

  - `And`, `Or`
  - `Is`, `Equals`
  - `LessThan`, `LessThanEqual`, `GreaterThan`, `GreaterThanEqual`
  - `After`, `Before`
  - `IsNull`, `IsNotNull`, `NotNull`
  - `Like`, `NotLike`
  - `StartingWith`, `EndingWIth`, `Containing`
  - `Ordery`
  - `Not`, `In`, `NotIn`
  - `True`, `False`
  - `IgnoreCase`

- **NamedQuery**

  - 해당 엔티티에 미리 쿼리를 정의해놓고 갖다가 쓴다.

  - `@NamedQuery` : **JPQL**을 사용한다

    ```java
    @Entity
    @NamedQuery(name = "{$엔티티}.{$메소드이름}", query = "{$쿼리}")
    
    @Entity
    @NamedQuery(name = "Post.findByTitle",
    	query = "SELECT p FROM Post As p WHERE p.title = ?1")
    ```

  - `@NamedNativeQuery` : 네이티브 쿼리로도 작성이 가능함!

  - 쓸 때는 이름만 맞춰서 가져다 쓰면 된다!

    ```java
    public interface PostRepository extends JpaRepository<Post, Long> {
        List<Post> findByTitle<String title);
    }
    ```

- **그냥 쿼리를 쓰는게 낫다**

  - 메소드 위에 `@Query("{$쿼리}")` 이렇게 써주면 됨!

    ```
    @Query("SELECT p FROM Post AS p WHERE p.title = ?1")
    List<Post> findByTitle(String title);
    ```

  - **Native Query**를 쓰고 싶다면 `@Query(value = "{$쿼리}", nativeQuery = true)`

  

#### 2. 쿼리 메소드 Sort

- 정렬을 하고 싶을 때는, `Pageable`과 비슷하게 `Sort`를 매개변수로 주면 된다.

  - **[주의]** : 그 안에서 사용한 **프로퍼티**나 **alias**가 엔티티에 없으면 예외가 발생한다.

    - **alias** : 쿼리에서 `a AS b`로 지어주는 것

    - 만약 이 상황에서, `LENGTH({$column})`처럼 함수로 정렬하고 싶다면?

      **JpaSort.unsafe()**를 사용한다!

    ```java
    List<Post> all = postRepository.findByTitle("Spring",
    	JpaSort.unsafe("LENGTH(title)"));
    ```

    

- `@Query`와 같이 사용할 때는 제약 사항이 있다.

#### 3. Named Parameter

- `@Query`에서 참조하는 매개변수를 **?1**, **?2** 이렇게 주지 말고 **이름으로 주고 싶다**

  ```java
  @Query("SELECT p FROM Post AS p WHERE p.title = :title")
  List<Post> findByTitle(@Param("title") String keyword, Sort sort);
  ```

  저렇게 매개변수 앞에 `@Param("{$이름}")`을 붙여준다.

#### 4. SpEL

- **SpEL (Spring Expression Language)** : 스프링 표현 언어

- 엔티티의 이름을 직접 적지 않고, `#(#entityName)`으로 표현할 수 있다.

  - 엔티티의 이름을 다른 걸로 바꿔도 해당 쿼리를 변경할 필요가 없음!
  - 스프링에서 객체의 값들을 직접 조회해서 바꿔준다.

  ```java
  @Query("SELECT p FROM #{#entityName} AS p WHERE p.title = :title")
  List<Post> findByTitle(@Param("title") String title, Sort sort);
  ```



#### 5. Update 쿼리

- **Update의 발생 과정**

  1. `PersistentContext`가 관리하다가 이 객체 상태의 변화가 일어났다.
  2. 데이터베이스에 sync 해야겠다 싶을 때 flush를 해서 동기화를 한다.
  3. 이 때 update 쿼리가 자동으로 실행이 된다.

- 그래서 update 쿼리를 직접 만들어서 쓸 필요는 사실 없다.

- 하지만 **직접 정의해서 쓰고 싶다면?**

  ```java
   @Modifying(clearAutomatically = true, flushAutomatically = true)
  @Query("UPDATE Post p SET p.title = ?2 WHERE p.id = ?1")
  int updateTitle(Long id, String title);
  ```

  - *하지만 추천하는 방법은 아니라고 함!*
  - update한 다음에도 기존 객체는 계속 **Persistent** 상태에 남는다.
    - 따라서 해당 객체 (이전 객체)는 캐시에 담긴 상태를 유지한다.
    - 이걸 해결하려면 `clearAutomatically = true` 옵션을 줘서 **캐시를 비워준다**

  

___

### 3. EntityGraph

- 쿼리 메소드 마다 연관 관계의 **Fetch** 모드를 설정할 수 있다

- `@ManyToOne`의 경우, 연관된 데이터를 바로 가지고 온다 (**Eager**)

  - 이걸 바꿔주고 싶어서 `ManyToOne(fetch = FetchType.LAZY)` 이런 식으로도 쓰지만,
  - 트랜잭션 안에서, 캐시를 클리어하지 않은 **Persistent** 상태에서만 가능하다.

- 기본은 **Lazy**로 쓰지만, **필요할 때만 Eager**로 가져오고 싶을 수도 있다!

- **이럴 때 사용하는 게 `EntityGraph`** : `@Entity`에서 재사용할 여러 엔티티 그룹을 정의

  ```java
  @NamedEntityGraph(name = "{$엔티티}.{$필드}",
         attributesNodes = @NamedAttributeNode("{이름}"))
  
  ____EX____
  @NamedEntityGraph(name = "Comment.post",
         attributesNodes = @NamedAttributeNode("post "))
  ```

  연관 관계를 어떻게 쓸지는 레포지토리에 커스텀하게 만들어서 정의해줘야 한다.

  ```java
  @EntityGraph(value = "Comment.post", type = {$type})
  Optional<Comment> loadCommentById(Long id);
  
  @EntityGraph(attributePaths = "post") // 이렇게 써도 됨
  @EntityGraph(attributePaths = {"attr1", "attr2"}) // 배열 가능
  ```

  - (기본값) **FETCH** : 설정한 엔티티 애트리뷰트는 **EAGER** 패치 나머지는 **LAZY** 패치
  - **LOAD** : 설정한 엔티티 애트리뷰트는 **EAGER** 패치 나머지는 기본 패치 전략 따름.

  

___

### 4. Projection

- **모든 컬럼들을 다 가져오지 말고 일부만 가져오자!**

#### 1) 인터페이스 기반 프로젝션

- **Nested** 프로젝션

- **Open** 프로젝션 : 다 가져온 다음에 보고 싶은 것만 보거나 연산한다.

  ```java
  @Value("#{target.up + ' ' + target.down}")
  
  > 10 1
  ```

  - **SpEL**을 사용한다.
  - 타겟의 정보를 일단 다 가져와야 하기 때문에 Open 이라고 한다
  - 성능 최적하는 안 되지만, 해당 문자열로 만들어서 반환받을 수 있다.

  

- **Closed** 프로젝션 : 우리는 딱 이것들만 가져오겠다!

  - 가져오려는 애트리뷰트를 알고 있기 떄문에 쿼리를 최적화할 수 있다.

  - **방법**

    1. 먼저 가져오고자 하는 컬럼을 정의할 인터페이스를 만든다.

    ```java
    public interface CommentSummary() {
        String getComment();
        int getUp();
        int getDown();
    }
    ```

    2. 그리고 정의한 인터페이스를 반환값으로 받도록 메소드를 정의한다.

    ```java
    List<CommentSummary> findByPostId(Long id);
    ```

  - **[추천]** : **JAVA 8**의 디폴트 메소드를 사용해 연산할 수 있다.

    ```java
    public interface CommentSummary() {
        String getComment();
        int getUp();
        int getDown();
        
        default String getVotes() {
            return getUp() + " " + getDown();
        }
    }
    ```

    

#### 2) 클래스 기반 프로젝션

- **DTO** : **Closed** 프로젝션과 유사하다. 쿼리도 똑같지만 뭔가 장황하다...
- `@Value`를 이용해 코드를 줄일 수 있다.



#### 3) 다이나믹 프로젝션

- 같은 메소드인데, 프로젝션한 결과만 다르게 가져오고 싶다.

- 그런데 오버로딩도 딱히 안되고... 이 때 쓰는게 제네릭!

- 프로젝션 용 메소드를 하나만 정의하고, **실제 프로젝션 타입은 타입 인자로 전달**한다.

  ```java
  <T> List<T> findByPost_Id(Long id, Class<T> type);
  
  comments.findByPostId(1L, CommentSummary.class); // 이렇게 쓴다.
  ```

  

___

### 5. Specifications와 Query by



___

### 6. 트랜잭션 [(REF)](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Transactional.html)

#### 1) 스프링의 트랜잭션

- 스프링 데이터 JPA가 제공하는 `Repository`의 모든 메소드에는 **기본적으로** `@Transaction`이 적용되어 있다.
  - 실제 우리가 사용하게 될 리파지토리의 구현체인 `SimpleJpaRepository`를 보면
  - `@Transactional(readOnly = true)`가 붙어 있다.
- 스프링 `@Transactional` : 클래스, 인터페이스, 메소드에 사용 가능하다.
  - 메소드에 가장 가까운 설정이 우선시된다.
    - 메소드에 `@Transactional`이 붙어 있다면 그대로 적용되고,
    - 아무것도 붙어 있지 않다면 `@Transactional(readOnly = true)`가 적용된다.
- **리포지토리의 메소드에도 애노테이션을 붙여줘서 변경해줄 수 있다.**



#### 2) `@Transactional` 옵션

- `rollbackFor`, `rollbackForClass` : 특정 런타임 에러가 발생하면 롤백시킨다.

- `noRollbackFor`, `noRollbackForClass` : 런타임 에러가 발생해도 롤백시키지 않는다!

- `timeout` : 트랜잭션 타임아웃 설정하기

- `isolation` : 여러 트랜잭션이 동시에 접근했을 때 해당 트랜잭션들을 어떻게 처리 (격리) 할까..
  - 동시 접근을 허용하든, 차례차례 시키든 등을 정해준다.

- `Propagation` : 어떻게 전파시킬 건지. 한 트랜잭션이 다른 트랜잭션에도 영향을 수 있다.

  - **Nested Transaction**과 관련된 이야기!

- `readOnly` : 성능 최적화의 여지가 생긴다.

  - 데이터를 변경하는 과정이 없으면 **true**로 주는 게 좋다.

  - **Flush** 모드를 **NEVER**로 설정해 **Dirty Checking**을 하지 않는다.

    > 이 트랜잭션은 readOnly니까 데이터를 변경할 일이 없다고 알려주는 것!
    >
    > DB Sync를 한다는 소리인데, 기존 캐시에서 변경사항이 있는지를 체크하지 X



___

### 7. Auditing



### 