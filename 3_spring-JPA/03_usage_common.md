# 스프링 데이터 JPA

**출처 : [스프링 데이터 JPA - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-jpa/)**

#### 목차

1. 스프링 데이터 JPA 시작하기

2. JPA의 핵심 개념 (특성) 이해하기

3. **스프링 데이터 JPA 활용 (Common)**

   - 리포지토리
   - 인터페이스 정의하기
   - Null 처리
   - 쿼리 만들기
   - 비동기 쿼리 메소드
   - 커스텀 리포지토리 만들기
   - 기본 리포지토리 커스터마이징
   - 도메인 이벤트
   - QueryDSL 연동

4. 스프링 데이터 JPA 활용 (웹 기능)

5. 스프링 데이터 JPA 활용 (JPA)

  ​    

___

## 스프링 데이터 JPA 활용 (Common)

### 1. 리포지토리

#### 1) Repository의 구조

- `JpaRepository` > `PagingAndSortingRepository` > `CrudRepository` > `Repository`
- `JpaRepository` : JPA와 관련된 기능들을 주로 제공한다.
- `PagingAndSortingRepository` : `Common`에 소속되어 있다. 
  - 페이징과 소팅을 지원해준다.
- `CrudRepository` : `save`(), `findAll()` 등등 다양한 메소드들이 정의되어 있다.

- `Repository` : 마커. 실질적으로 특정 기능을 하진 않는다.

  - `@NoRepositoryBean` : 다른 저장소용 리포지토리가 이 클래스로 실제 빈을 등록하는 것을 방지한다. *(무슨 말이지...)*

  - 실제 리포지토리가 아니라는 걸 표시하는 거라고 함!



#### 2) 인터페이스 정의하기

- 지금까지는 스프링 데이터 JPA가 제공하는 인터페이스를 상속받았다.

- **직접 정의해서 쓰고 싶다면?**

  1. 일단 인터페이스를 반들고, `RepositoryDefinition`을 상속받게 한다.
  2. 이후로 구현하고 싶은 내용을 직접 구현하면 된다!

-  대신 이 방법으로 하면 테스트도 구현하고 해야할 일이 많다.

- 먄약 공통되는 메소드가 많아 **추상화하고 싶다면?**

  - 인터페이스를 만들고 `@NoRepositoryBean`을 붙여준 다음,  상속 받게 한다.

    ```java
    @NoRepositoryBean
    public interface MyRepository<T, Id extends Serializable> extends Repository<T, Id> {}
    ```

    

___

### 3. Null 처리

#### 1) Optional : 단일 값의 null 처리

- return 값이 1개일 경우에는 `Optional`을 쓸 수 있다.
  - return 되는 값이 null이어도, `Optional`이 주는 메소드를 통해 확인할 수 있다.
  - `isEmpty()` :`Optional` 객체의 내용이 비어 있는지 확인.
  - `orElse()` : 해당 객체가 없을 때.
  - `orElseThrow()` : 해당 객체가 없을 때 에러를 던짐.



#### 2) List

- List의 경우 `Optional`을 사용할 수 없다.
- 내용물이 없을 때는 비어 있는 리스트가 나온다!
- **때문에 컬렉션을 null 체크하는 것은 의미가 없다.**



#### 3) Null 애노테이션

- 스프링 프레임워크 5.0부터 지원하는 **Null 애노테이션**을 사용할 수 있다.
- `@NonNullApi`, `@NonNull`, `@Nullable`
  - **파라미터**의 null 체크는 파라미터 앞에 쓰면 되고,
  - **리턴타입**의 null 체크는 메소드 위에 써주면 된다.
- 런타임 체크도 지원한다!

___

### 4) 쿼리 만들기



___

### 5) 비동기 쿼리 메소드



___

### 6) 커스텀 리포지토리 만들기



___

### 7) 기본 리포지토리 커스터마이징



___

### 8) 도메인 이벤트



___

### 9) QueryDSL 연동