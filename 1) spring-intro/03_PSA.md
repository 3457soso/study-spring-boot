# 스프링 프레임워크 입문

**출처 : [스프링 프레임워크 입문 - 백기선 님](https://www.inflearn.com/course/spring/)**

#### 목차

1. IoC (Inversion of Control, 제어의 역전)

2. AOP (Aspect Oriented Programming, 관점 지향 프로그래밍)

3. **PSA (Portable Service Abstraction, 서비스 추상화)**

   

___

## PSA (Portable Service Abstraction, 서비스 추상화)

### 1. PSA란?

#### 1) 정의

- 굳이 번역하자면... **이식 가능한(?) 서비스 추상화**

- **잘 만든 인터페이스**

  - 보통 내 코드는 확장성이 좋지 못한 코드이거나, 특정 기술에 특화된 코드일 가능성이 높다.

    테스트도 어렵고, 특정 기술이 바뀔 때마다 내 코드도 바뀌게 된다.

  - 잘 만든 인터페이스를 갖다 쓰면, 기술이 바뀌어도 **내 코드까지 바꿀 필요가 없다.**

- 스프링이 제공해주는 대부분의 API는 **PSA** 라고 볼 수 있다.

  Ex) `Events`, `Resources`, `i18n`, `Validation`, `Data Binding`, `Type Conversion`, `Tranactions`, `DAO`, `JDBC`, `ORM`, `Marshalling XML `...

- 다르게 생각하면, 스프링이 제공해주는 게 워낙 많다 보니까 **어렵게 느껴질 수 있다**

  

### 2. 예시

#### 1) 스프링 트랜잭션

- 트랜잭션은 **AOP**의 예제가 되기도 하지만, **PSA**의 예시로도 볼 수 있다.

- `PlatformTransactionManager`라는 추상화된 인터페이스를 통해서 트랜잭션을 구현한다.

  - `JpaTransactionManager`, `DatasourceTransactionManager`, `HibernamteTransactionManager` 등 기술의 종류가 바뀌더라도, 내 코드 상에서는 바꿀 필요가 없다.

  - 내 코드에서 **JPA**를 사용하면, 자동으로 `JpaTransactionManager`가 빈으로 등록되어 이를 사용하게 됨!

    

#### 2) 캐시

- 다양한 캐시매니저들은 `CacheManager`로 추상화 되어있다.

  

#### 3) 웹 MVC

- 우리의 코드는 컨트롤러와 GetMapping을 사용해서 해당 메소드의 내용을 호출한다.
- 이 코드는 서블릿일 수도 있고, 리액티브를 쓰는 걸 수도 있다.
- 무엇을 쓰는지는 코드상에서는 확인할 수 없고, 의존성을 직접 확인해줘야 한다...
- **따라서 기술에 독립적으로 만들어준다고 볼 수 있다! 추상화다!**
- **밑단에 있는 기술을 바꾸더라도, 기존 코드에는 영향을 끼치지 않는다.**