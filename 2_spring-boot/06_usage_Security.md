# 스프링 부트 개념과 활용

**출처 : [스프링 부트 개념과 활용 - 백기선 님](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81%EB%B6%80%ED%8A%B8//)**

#### 목차

1. 스프링 부트 시작하기

2. 스프링 부트의 원리

3. 스프링 부트의 핵심 기능

4. 스프링 부트의 기술 (웹 MVC)

5. 스프링 부트의 기술 (데이터베이스)

6. **스프링 부트의 기술 (스프링 시큐리티)**

   - [**스프링 시큐리티 시작하기**](#1-스프링-시큐리티-시작하기)
   - [**시큐리티 설정 커스터마이징**](#2-시큐리티-설정-커스터마이징)

7. 스프링 부트의 운영 (Actuator)

  ​    

___

## 스프링 부트의 기술 (스프링 시큐리티)

### 1. 스프링 시큐리티 시작하기

#### 1) Starter-Security

- 일단 쓰기 위해서는 의존성을 추가해줘야 한다

  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
  </dependency>
  ```

  의존성을 추가하고 나면, 바로 모든 요청에서 **인증 절차**를 요구하게 된다.

  ```sh
  MockHttpServletResponse:
             Status = 401
      Error message = Unauthorized
            Headers = {WWW-Authenticate=[Basic realm="Realm"], X-Content-Type-Options=[nosniff], X-XSS-Protection=[1; mode=block], Cache-Control=[no-cache, no-store, max-age=0, must-revalidate], Pragma=[no-cache], Expires=[0], X-Frame-Options=[DENY]}
  ```

  **Basic realm** : 이 값을 받으면, 브라우저는 내장하고 있는 기본 인증 폼을 보여준다.

  ![이미지](https://blogfiles.pstatic.net/MjAxODEyMzFfMjEg/MDAxNTQ2MjQyMzA3MDM0.zxM-sY8YB-a2CGg-IvJZhd6ETaV5wQpSjGdwQPJzn1Mg.HS7V1EpjROQzuvHf5S1EF80py5BhnVqlJJVks9Irr58g.PNG.3457soso/%EC%8A%A4%ED%81%AC%EB%A6%B0%EC%83%B7%2C_2018-12-31_16-41-17.png)

  이 내용?은 요청 헤더에서 받는 `Accept` 헤더에 따라 다르다. (어떤 정보를 받을 건지)



#### 2) 자동 설정 및 이벤트

- **자동 설정**

  - 초기에 기본 사용자를 생성해준다.

    - Username: user

    - Password: 애플리케이션을 실행할 때 마다 랜덤 값 생성 (콘솔에 출력 됨)

      `Using generated security password: 67bc4dbd-dc51-47fc-b6ea-226544a7e7cc`

    - 바꾸고 싶다면 `application.properties`에 추가한다.

      ```properties
      spring.security.user.name = {$username}
      spring.security.user.password = {$password}
      ```

      

- **인증 관련 이벤트**

  - `SpringBootWebSecurityConfiguration`에는 `DefaultAuthenticationEventPublisher`가 있다.
    - 여기에는 다양한 인증과 관련된 **이벤트 핸들러**들이 등록되어 있다.
    - 스프링 부트를 쓰지 않아도, **스프링 시큐리티**에 다 구현이 되어 있으므로 **빈으로만 등록하면** 사용 가능!
    - Ex) 비밀번호가 틀림, 해당 유저가 없음 등등 ...



- 그럼 스프링 부트는 뭘 해주냐! `UserDetailsServiceAutoConfiguration`을 해준다.
  - 인 메모리의 초기 유저 하나를 처음으로 만들어 주는 것!
  - 이건 `UserDetailsServlce`나 `AuthenticationManager`, `AuthenticationProvider`가 없을 때 적용



#### 3) 테스트

- 깨진 테스트를 복구해주기 위해서는 먼저 의존성을 추가하고

  ```xml
  <dependency>
      <groupId>org.springframework.security</groupId>
      <artifactId>spring-security-test</artifactId>
      <scope>test</scope>
  </dependency>
  ```

- 다음으로 클래스나 메소드에 `@WithMockUser` 애노테이션을 붙여준다. (가짜 유저 사용)

  ```java
  @Test
  @WithMockUser
  public void hello() throws Exception {}
  ```

- 아니면 `with(user())` 메소드를 이용해도 된다.

  ```java
  mockMvc.perform(get("/hello")
                  .with(user("soyoung")))
                  ...
  ```

  

___

### 2. 시큐리티 설정 커스터마이징

#### 1) WebSecurityConfigureAdapter

- **원본**

  ```java
  	protected void configure(HttpSecurity http) throws Exception {
  		http
  			.authorizeRequests()
  				.anyRequest().authenticated()
  				.and()
  			.formLogin().and()
  			.httpBasic();
  	}
  ```

  이렇게 생겼기 때문에 **모든 요청**에서 인증을 요구하게 된다.

  이 빈이 등록되면 실제로 `SpringBootWebSecurityConfiguration`이 등록되지 않는다.

  ```java
  @Configuration
  public class WebSecurityConfig extends WebSecurityConfigurerAdapter {}
  ```

  이렇게 등록하면 스프링의 시큐리티를 이용하면서, 커스터마이징을 해줄 수 있다!

  > 대신 스프링 부트에서 제공하는 **AutoConfiguration**은 이용할 수 없게 된다.

  

#### 2) 웹 시큐리티 설정

- `configure(HttpSecurity http)`를 오버라이딩 해준다.

  ```java
  @Override
      protected void configure(HttpSecurity http) throws Exception {
          http.authorizeRequests()
              .antMatchers("/", "/hello").permitAll() // 얘네는 모두 볼 수 있다
              .anyRequest().authenticated()           // 나머지는 인증을 필요
                  .and()
              .formLogin()                            // 폼 로그인과
                  .and()
              .httpBasic();                            // httpBasicAuthentication 사용
      }
  ```



#### 3) UserDetailsService 구현

- 보통 유저 정보를 관리하는 서비스가 `UserDetailsService`를 구현하도록 만든다.

  ```java
  @Service
  public class AccountService implements UserDetailsService {}
  ```

- 그럼 `loadUserByUsername()` 메소드가 호출된다. 

  - 로그인할 때 입력받은 유저 정보를 가져와서, 입력 받은 비밀번호가 해당 유저의 정보와 일치하는지를 확인한다. 
  - 그리고 이에 맞는 응답을 보내준다.
  - **핵심적인 인터페이스라고 볼 수 있다**

  ```java
  @Service
  public class AccountService implements UserDetailsService {
      ...
  	@Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          Account account = accountRepository.findByUsername(username)
                  .orElseThrow(() -> new UsernameNotFoundException(username));
  
          return new User(account.getUsername(), 
                          ccount.getPassword(), authorities());
      }
  
      private Collection<? extends GrantedAuthority> authorities() {
          // 해당 유저에게 이 권한들을 부여해준다.
          return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
      }
  }
  ```

  

#### 4) PasswordEncoder 설정 및 사용

- 위 방법대로 따라와도 로그인이 안된다! 스프링 시큐리티 특정 버전부터 **패스워드 인코더가 복잡해졌다**

- **비밀번호를 인코딩하지 않고 그대로 DB에 넣으면 안된다.**

- 스프링 시큐리티 버전이 올라가면서 **다양한 인코딩을** 지원하고 있다.

  ```html
  {bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG 1
  {noop}password 2
  {pbkdf2}5d923b44a6d129f3ddf3e3c8d29412723dcbde72445e8ef6bf3b508fbf17fa4ed4d6b99ca763d8dc 3
  {scrypt}$e0801$8bWJaSu2IKSn9Z9kM+TPXfOc/9bdYSrN1oD9qfVThWEwdRTnO7re7Ei+fUZRJ68k9lTyuTeUp4of4g24hHnazw==$OAOec05+bXxvuu/1qZ6NUR+xQYvYv7BeL1QxwRpY5Pc=  4
  {sha256}97cde38028ad898ebc02e690819fa220e88c62e0699403e94fff291cfffaf8410849f27605abcbc0 5
  ```

  여기서 **noop** 타입 (인코딩을 안 함)으로 비밀번호를 쓰면 **스프링 시큐리티에서 안 받아준다**

  이 과정을 점검하는 빈 자체를 비활성 시켜버리는 방법도 있지만 *쓰면 안 된다...*

- `PasswordEncoder`를 사용하자!

  ```java
  @Configuration
  public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
      @Bean
      public PasswordEncoder passwordEncoder() {
          return PasswordEncoderFactories.createDelegatingPasswordEncoder();
      }    
  }
  ```

  이렇게 하면 유저를 만들 때도, 유저 정보를 저장하기 전에 인코더를 주입받아서 **인코딩 해줘야 한다.**

  ```java
  @Autowired
  private PasswordEncoder passwordEncoder;
  ...
  account.setPassword(passwordEncoder.encode(password));
  return accountRepository.save(account);
  ```

  결과적으로 DB에는 다음과 같이 **인코딩된 값**이 들어가게 된다.

  `admin password: {bcrypt}$2a$10$XPWeHwCSuKt96sMN0EWLM.XIzeiqO3mUp.M6t1RRfujQQSb0G1Rjy`

  - 하지만 입력할때는 초기에 등록했던 거 그대로 하면 됨! 
  - 스프링에서 자동으로 인코딩, 디코딩을 해준다.



___

### 참고

- [스프링 시큐리티 공식 레퍼런스 docs](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle)

