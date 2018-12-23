# 스프링 프레임워크 입문

**출처 : [스프링 프레임워크 입문 - 백기선 님](https://www.inflearn.com/course/spring/)**

#### 목차

1. IoC (Inversion of Control, 제어의 역전)

2. **AOP (Aspect Oriented Programming, 관점 지향 프로그래밍)**

3. PSA (Portable Service Abstraction, 서비스 추상화)

   

___

## AOP (Aspect Oriented Programming, 관점 지향 프로그래밍)

### 1. AOP란?

#### 1) 정의

- **흩어진 코드를 한 곳으로 모으자!**

- **Before** : 각 메소드 내부에 AAAA, BBBB가 계속 등장한다.

  ```java
  class A {
      method a () {
          AAAA
          흩어진
          BBBB
      }
      
      method b () {
          AAAA
          코드를
          BBBB
      }
  }
  
  class B {
      method c() {
          AAAA
          모아보자
          BBBB
      }
  }
  ```

  

- **After** : 코드가 훨씬 깔끔해진다!

  ```java
  class A {
      method a () {
          흩어진
      }
      
      method b () {
          코드를
      }
  }
  
  class B {
      method c() {
          모아보자
      }
  }
  
  class AB {
      method ab(Joinpoint point) {
          AAAA
          point.execute();
          BBBB
      }
  }
  ```



#### 2) 하는 법

- **바이트코드 조작** : 컴파일 한 후 생기는 클래스 파일에 추가해준다.

  실제 코드와 컴파일 후 실행되는 내용이 달라지게 되는 것!

- **프록시 패턴 사용** : 내부적으로 프록시 오브젝트를 만들어서 상속하게 한다.

  > 스프링에서는 이 방법을 사용한다!



### 2. 예제 및 사용법

#### 1) 트랜잭션

- 개요

  > **AAAA** : 트랜잭션 매니저를 생성하고, 오토커밋을 false로 바꾼다.
  >
  > **BBBB** : 변경된 부분을 커밋해준다.
  >
  > 그리고 위 부분이 try-catch로 묶여있다.

- 그런데 이런 트랜잭션은 한 부분에만 나타나지 않는다. 필요한 부분에는 다 붙어있다. 

- 같은 코드들을... 이걸 하나하나 다 붙여주는 건 너무 번거롭고 비효율적이다.

- `@Transactional`만 붙이면 관련 작업들을 자동으로 처리해주는 것! 

  

#### 2) 로깅 예제

- 특정 메소드 전 후로 로깅과 관련된 코드들이 들어가야 할 때

- 일단 해당 프록시가 어디에 붙어야 하는지를 표시하기 위해 **어노테이션**을 만들고

  ```java
  @Target(ElementType.METHOD) // 메소드에 붙는다
  @Retention(RetentionPolicy.RUNTIME) // 실행 중까지는 유지한다
  public @interface LogExecution {}
  ```

- 해당 프록시의 내용을 작성해 준 뒤에,

  ```java
  @Component
  @Aspect
  publcic class LogAspect {
      // 로그 생성하고,
      
      @Around("@annotation(LogExecution)") // 적용 범위
      public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
          // 로깅 시작
          
          Object proceed = joinPoint.proceed(); // 각 메소드 별로 실행할 부분
          
          // 로깅 끝
          return proceed;
      }
  }
  ```

- 실제로 로깅 작업이 필요한 클래스 (메소드)에 선언한 어노테이션을 붙여준다.

- **중복되는 코드를 작성할 필요가 없게 된다!!!**
