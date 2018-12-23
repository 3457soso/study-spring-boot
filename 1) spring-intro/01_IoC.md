# 스프링 프레임워크 입문

**출처 : [스프링 프레임워크 입문 - 백기선 님](https://www.inflearn.com/course/spring/)**

#### 목차

1. **IoC (Inversion of Control, 제어의 역전)**

2. AOP (Aspect Oriented Programming, 관점 지향 프로그래밍)

3. PSA (Portable Service Abstraction, 서비스 추상화)

   

___

## IoC (Inversion of Control, 제어의 역전)

### +) petClinic 프로젝트

#### 1) 개요

- 스프링을 기반으로 한 간단한 동물병원 웹사이트 애플리케이션

- 고객, 고객의 애완동물, 수의사 등의 정보를 관리할 수 있다.

- 간단한 CRUD 들으로 이루어져 있음!

  

#### 2) 사용 기술

- Spring Framework XML configuration
- JSP pages
- JDBC, JPA, Spring Data JPA



### 1. IoC란?

- 대상은 무관하지만, 주로 **의존성**에 대한 제어를 말한다.

  - 원래 의존성에 대한 제어권은 자신이 가진다. 

  - **기존의 의존성** : 본인이 직접 필요한 것을 만들어서 사용한다.

    ```java
    class UserController {
        private UserRepository userRepository = new UserRepository();
    }
    ```

    - 이렇게 직접 만들어서 쓰지만, **직접 관리해야 하기 때문에** 계속 관리해줘야 한다.

  - **역전된 의존성** : 내가 직접 만들지 않고, **누가 주겠지** ... 가정하고 쓴다.

    ```java
    class UserController {
        private UserRepository userRepository;
        
        public UserController(UserRepository userRpository) {
            this.userRepository = userRepository;
        }
    }
    ```

    - 이렇게 누군가 만들어서 갖다 줄 거라고 가정하고 쓴다.

- 이를 의존성에만 국한하지 않고 말하는 이유는, **의존성 외에도 역전되는 것**이 있는 것 같다...

  Ex) 서블릿 또한 컨테이너가 생성하고 관리하기 때문에 역전되었다고 볼 수 있음.



### 2. IoC 컨테이너란? (ApplicationContext)

#### 1) 정의

- **ApplicationContext** : IoC 컨테이너라고 불린다. 
- 직접 쓸 일은 없다... 예시 코드를 봐도 어디에도 없다.



#### 2) 하는 일

- **그럼 무슨 일을 할까?**

  1. `UserController`가 IoC 컨테이너 내부에 객체로 생성한다.

  2. 이유는 약간 다르지만 `UserRepository` 객체도 생성된다.

  3. 생성된 빈들의 **의존성을 관리**해주는 역할을 한다.

     > 즉 `UserController`에게 `UserRepository`를 주입해준다.

  **+) IoC 컨테이너가 직접 하는 일을 보고 싶다면?**

  IoC 컨테이너 자체가 빈으로 등록되어 있기 때문에 직접 `@Autowired`로 가져와서 쓸 수 있음!

  ```java
  @Autowired
  ApplicationContext applicationContext;
  
  @GetMapping("/context")
  public String context() {
  	applicationContext.getBean("...");    
      ...
  }
  ```

  

### 3) 빈 (Bean)이란?

#### 1) 정의

- **스프링 IoC컨테이너가 관리하는 객체**를 말한다.
- 그러므로 `User`나 `Owner`같은 엔티티들은 빈이라고 할 수 없다.
- **오로지 Bean만 Bean을 쓸 수 있다**



#### 2) 등록 방법

1. **Component Scanning** : 어노테이션을 통해 자동으로 등록되게 한다.

   - `@Component`, `@Repository`, `@Service`, `@Controller` ...
   - **어노테이션 자체에는 기능이 없다.** 이걸 처리하는 과정이 있을 뿐...
   - `@Component` 어노테이션이 붙은 클래스들을 모두 찾아서 빈으로 등록하는 친구가 있음!

2. **설정 파일**이나 **XML**에 직접 등록

   1.  일단 빈으로 등록하고 싶은 클래스에 `@Bean` 어노테이션을 붙이는데, 

      빈으로 등록할때는 `@Configuration` 어노테이션이 붙은 클래스 내부에서 정의해야 한다.

   2. 가져다 쓸 때는 `@Autowired`를 붙인 다음에 가져다가 쓰면 된다.



### 4) 의존성 주입 (DI)

- 필요한 의존성을 받아오는 다양한 방법들

- **생성자** : 어떤 빈의 생성자가 오직 하나만 있고, 해당 생성자의 매개변수 타입이 빈이라면, 

  - **이 빈은 `@Autowired` 어노테이션이 없어도 자동으로 주입해준다.**
  - 결국 점점 `@Autowired` 어노테이션을 안 쓰게 될 거긴 하지만, 이런 이유를 모른다면 이해하기 어려울 듯!

  ```java
  private UserRepository userRepository;
      
  public UserController(UserRepository userRepository) {
      this.userRepository = userRepository;
  }
  ```

- **필드** : 필요한 필드에 `@Autowired` 어노테이션을 붙인다.

  ```java
  @Autowired
  private UserRepository userRepository;
  ```

- **Setter** : 

  - `setter`가 있다는 얘기는 `setter`로 의존성을 주입하겠다는 뜻이니까...
  - 되도록 `setter`가 있으면 `setter`에 붙이되, 없으면 그냥 필드에 붙이자.
  - 없다고 `@Autowired`를 위해 `setter`를 추가하는건... 과하게 보일 수도 있는 듯!

  ```java
  @Autowired
  public void setUserRepository(UserRepository userRepository) {
      this.userRepository = userRepository;
  }
  ```

  > With `@Autowired` annotation, you don't need a setter method. Once your bean's constructor is done with allocating/creating the object, Spring will scan for this annotation and would inject the object instances that you annotated.
  >
  > While if you have setter and if you are still using xml config, you would explicitly set properties.
  >
  > Having said that, You could annotate your constructor and setter method with autowired annotation which i would prefer as this would give me flexibility later on to move away from Spring (although i wont do it).
  >
  > **[출처 : 관련 답안 (stackoverflow)](https://stackoverflow.com/questions/33562731/spring-autowire-property-vs-setter)**

