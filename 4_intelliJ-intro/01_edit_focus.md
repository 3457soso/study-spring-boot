# 인텔리제이 사용법

**출처 : [IntelliJ를 시작하시는 분들을 위한 IntelliJ 가이드 - 이동욱 님](https://www.inflearn.com/course/intellij-guide/)**

#### 목차

1. **코드 Edit, 포커스**
   - 프로젝트 생성 관련
   - 코드 Edit
   - 포커스
2. 검색, 자동완성
3. 리팩토링, 디버깅
4. Git, 플러그인



___

## 코드 Edit, 포커스

### 1. 프로젝트 생성 관련

#### 1) 의존성 관리

- 프로젝트는 되도록 `Gradle`이나 `Maven`으로 만드는 것이 좋다.
- 라이브러리 사용 시 의존성 관리를 편하게 할 수 있다.



#### 2) 프로젝트 생성

- **GroupId** : 프로젝트 그룹 (ex. Spring)
- **ArtifactId** : 해당 그룹의 하위 모듈 (ex. Spring security)
- **Version** : 그냥 내버려 두면 된다^^



___

### 2. 코드 Edit

#### 1) 새로 만들고 메인 메소드 실행하기

- 새로 만들기 : `command + N` (`Alt + Insert`)
  - 특정 클래스 내에서 하면 **생성자, Getter, Setter, toString...** 생성 가능!
- 파일명, 디렉토리명에 슬래시를 쓰면 자동으로 하위 디렉토리 잡아줌
- **코드 템플릿 (라이브 템플릿)** : 자주 쓰는 메소드들을 만들어주는 것
  - `psvm` : `public static void main(String[] args)` 생성
  - `sout` : `System.out` 관련 메소드들 생성
- **포커스를 둔 곳에서** 실행 : `ctrl + shift + R` (`Shift + Ctrl + F10`)
- **최근에 실행한** 환경 실행 : `ctrl + R` (`Shift + F10`)
  - 우측 상단의 `Edit Configuration`에 최근 환경들이 밑에 나온다.



#### 2) 라인 수정하기

- **라인 복제** : `Command + D` (`Ctrl + D`)
- **라인 삭제** : `Command + 백스페이스` (`Ctrl + Y`)
- **문자열 라인 병합** : `Ctrl + Shift + J` (동일) 아래로 나뉜 문자열을 위로 합쳐옴.
- **라인 단위 이동**
  - **위치 무관** : `Option + Shift + 방향키` (`Shift + Alt + 방향키`)
  - **구문 여부** : `Shift + Command + 방향키` (`Shift + Ctrl + 방향키`)
- **Element 단위 이동**
  - `Option + Shilt + Command + 방향키` (`Shift + Ctrl + Alt + 방향키`)
  - **XML**이나 **HTML**에서 엘리멘트 단위로 좌우 위치를 바꿀 수 있다.
- **위치 무관하게 개행** : `Command + Enter` (`Ctrl + Enter`)

#### 3) 코드 즉시보기

- **인자값 즉시 보기** : `Command + P` (`Ctrl + P`) 
  - 생성자나 메소드에서 모두 가능! 
  - 해당 클래스에서 포커싱된 상태여야 함

- **코드 구현부** : `Option + Space` (`Ctrl + Shift + I`)
  - 코드 내부가 즉시 보인다. (창을 바꿔주지 않아도 됨!)
  - **클래스에서** 호출하면 클래스 코드가 보이고,
  - **인스턴스에서** 호출하면 객체를 생성하는 부분이 보이고
  - **메소드에서** 호출하면 메소드 구현 내용이 보인다.
  
  +) 얼티밋 버전에서는 자바가 아닌 언어에서도 쓸 수 있다고 함!

- **Doc** : `F1` (`Ctrl + Q`)  *Java Doc의 내용을 확인하고 싶을 때.*

  

___

### 3. 포커스
