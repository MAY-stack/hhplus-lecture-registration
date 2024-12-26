# 요구사항 분석

- 아래 3가지 API 를 구현합니다.
    - 특강 신청 가능 목록 조회 API
    - 특강 신청 API
    - 특강 신청 완료 목록 조회 API
- 각 기능 및 제약 사항에 대해 단위 테스트를 반드시 하나 이상 작성하도록 합니다.
- 다수의 인스턴스로 어플리케이션이 동작하더라도 기능에 문제가 없도록 작성하도록 합니다.
- 동시성 이슈를 고려 하여 구현합니다.
- **DB 는 MySQL / MariaDB 로 제한합니다.**
- **Test 는 (1) 인메모리 DB (2) docker-compose 정도 허용합니다. ( + TestContainers 이용해도 됨 )**

## API Specs

1️⃣ **(핵심)** 특강 신청 **API**

- 특정 userId 로 선착순으로 제공되는 특강을 신청하는 API 를 작성합니다.
  - 없는 사용자의 id 입력 시, 신청에 실패한다.
  - 없는 강좌의 id 입력 시, 신청에 실패한다.
- 동일한 신청자는 동일한 강의에 대해서 한 번의 수강 신청만 성공할 수 있습니다.
  - 한 사용자가 동일한 강좌에 중복해서 수강 신청할 시 신청에 실패한다.
- 특강은 선착순 30명만 신청 가능합니다.
- 이미 신청자가 30명이 초과 되면 이후 신청자는 요청을 실패합니다.

**2️⃣ 특강 신청 가능 목록 API** 

- 날짜별로 현재 신청 가능한 특강 목록을 조회하는 API 를 작성합니다.
  - 날짜값으로 변환할 수 없는 문자열 입력시, 목록 조회에 실패한다.
  - 신청 가능한 강좌가 없으면 빈 배열을 반환한다.
  - 강좌 목록은 강좌 시간 오름차순으로 반환한다.
  - 시간과 날짜가 다른 강좌는 모두 다른 강좌 아이디를 가진다.
- 특강의 정원은 30명으로 고정이며, 사용자는 각 특강에 신청하기 전 목록을 조회해 볼 수 있어야 합니다.
  - 신청 가능한 강좌에대한 강좌 아이디, 강좌명, 강사명, 강의 날짜, 강의 시간, 남은 자리, 정원의 정보를 반환한다.

3️⃣  **특강 신청 완료 목록 조회 API**

- 특정 userId 로 신청 완료된 특강 목록을 조회하는 API 를 작성합니다.
  - 없는 사용자의 id 입력 시, 조회에 실패한다.
  - 신청한 강좌가 없으면 빈 배열을 반환한다.
- 각 항목은 특강 ID 및 이름, 강연자 정보를 담고 있어야 합니다.

# ERD 작성
![ERD](https://github.com/user-attachments/assets/354b45b1-f98c-4cda-8d4c-ffaf6fe99fb8)
구현의 난이도가 높을 것 같아서 요구사항을 충족하는 최소한의 필드로 구성해서 객체를 설계해 보았다.  사용자가 여러 강의에 등록할 수 있고, 각 강의는 다수의 사용자를 수용할 수 있도록 관계를 정의했다.

- user 테이블: 사용자의 기본 정보(user_id, user_name)를 저장합니다. 사용자는 여러 강의에 등록할 수 있으므로, registration 테이블과 1:N 관계를 가진다.
- lecture 테이블
    - 강의의 기본 정보(lecture_id, lecture_title, instructor_name, capacity, lecture_date, lecture_time)를 저장한다.
    - 하나의 강의는 여러 사용자가 등록할 수 있으므로, registration 테이블과 1:N 관계를 가진다.
    - 강의 시간을 나타내는 lecture_time은 시간연산을 하지 않을 것 같아서 관리하기 편한 문자열로 선택했다.
    - 강의(lecture)의 정원을 나타내는 capacity 필드를 포함하여 강의의 수용 인원을 제한할 수 있도록했다. 기본값을 30으로 설정하여, 별도의 입력 없이 기본 정원을 자동으로 부여하고 필요하면 변경할 수 있도록 했다.
- registration 테이블
    - 사용자가 강의에 등록한 정보를 기록하며, user와 lecture를 연결하는 다대다(M:N) 관계를 표현한다. 사용자와 강의의 매핑을 관리할 수 있다.
    - 사용자 아이디와 강의 아이디를 Unique key로 관리해서 중복 강의 신청은 불가한 요구사항을 반영 했다.
 
# 목표 아키텍쳐

![clean_layered_architecture](https://github.com/user-attachments/assets/49ebf632-b0f5-401f-b1cf-a074dcf8933e)
- 애플리케이션의 핵심은 비즈니스 로직
- 데이터 계층 및 API 계층이 비즈니스 로직을 의존 ( 비즈니스의 Interface 활용 )
- 도메인 중심적인 계층 아키텍처
- Presentation 은 도메인을 API로 서빙, DataSource 는 도메인이 필요로 하는 기능을 서빙
- DIP 🆗 OCP 🆗

# 패키지 구조 설계
- 계층형 vs 기능형 패키지 구조
    - 계층형 패키지 구조
        
        ```
        hhpluslectureregistration/
        ├── application/
        │   ├── dto/
        │   ├── facade/
        │   └── mapper/
        ├── domain/
        │   ├── entity/
        │   ├── repository/
        │   └── service/
        ├── infrastructure/
        │   ├── JpaRepository/
        │   └── RepositoryImpl/
        └── interfaces/
            ├── controller/
            └── exception/
        ```
        
        - 장점
            - 전체적인 구조 파악이 쉽다.
            - 계층별 응집도가 높아진다.
        - 단점
            - 도메인별 응집도가 낮다. ⇒ MSA 분리가 어렵다
            - 유스케이스(사용자의 행위) 표현이 어렵다.
            - 규모가 커지면 하나의 패키지 안에 여러 클래스들이 모여서 구분이 어려워진다.
    - 기능형 패키지 구조(도메인 중심)
        
        ```
        hhpluslectureregistration/
        ├── lecture/
        │   ├── controller/
        │   ├── dto/
        │   ├── entity/
        │   ├── mapper/
        │   ├── repository/
        │   ├── service/
        │   └── facade/
        ├── registration/
        │   ├── controller/
        │   ├── dto/
        │   ├── entity/
        │   ├── mapper/
        │   ├── repository/
        │   ├── service/
        │   └── facade/
        ├── student/
        │   ├── entity/
        │   ├── repository/
        │   └── service/
        └── exception/
        ```
        
        - 장점
            - 도메인별 응집도가 높아진다.  ⇒ MSA 분리 용이
            - 유스케이스별로 세분화해서 표현이 가능하다.
        - 단점
            - 애플리케이션의 전반적인 흐름을 한눈에 파악하기가 어렵다.
            - 어느 패키지에 둘지 애매한 클래스들이 존재한다.
    
    ⇒ 계층형 패키지 구조 선택
    
    - 익숙한 구조를 가져가면서 계층간의 관계를 명확하게 하고싶었다. 그래서 계층과 패키지의 구조가 일치하는 계층형 패키지 구조를 사용하기로 했다.
 
# 각 계층의 역할과 책임
![dependencies](https://github.com/user-attachments/assets/d324442c-c2da-416f-8647-1d6c1d743637)
## **Interfaces Layer (presentaion)**

- 사용자와의 상호작용을 처리하는 계층
    - 입력 검증, 요청 파싱, 응답 포맷
    - Application layer와 통신해 데이터를 가져오거나 처리 요청을 전달
    
    **Controller**
    
     : HTTP 요청을 처리하고 요청을 Application Layer로 전달한 후 응답을 반환
    

## Application Layer

- Domain layer의 기능을 호출하여 비즈니스 로직을 구현
    - **트랜잭션관리**, 작업 흐름 관리(use case 실행)
    
    **Facade**
    
    : 여러 단계의 프로세스나 트랜잭션을 관리한다.
    
    ⇒ 요청을 처리하는 동안 여러 서비스를 호출하고 트랜잭션을 관리하기 위해서 필요하다고 생각했다. 특히 수강신청의 경우 여러 도메인의 서비스를 사용해서 도메인 서비스 내에서 순환 참조를 방지하기 위해서 상위 계층에서 호출해서 조합하는게 좋을것 같다고 생각했다.

## Domain Layer

- 핵심 비지니스 로직과 규칙을 정의하고 캡슐화
    - 비지니스 규칙, 검증, 상태 변경 로직을 처리
    - 외부 환경(DB, API에 의존하지 않는다)
    
    **Entity**
    
    : 데이터 및 상태를 캡슐화,  실제 비지니스 규칙을 메서드에 정의
    
    **Service**
    
    : 엔터티에 포함하기 어려운 로직, 복잡한 비즈니스 규칙 처리 로직 구현
    

## Infrastructure Layer (Persistence & External Services)

- 데이터베이스, 메시지 큐, 파일 시스템 등 외부 시스템과의 상호작용을 처리
    - 도메인 및 애플리케이션 레이어에서 정의한 인터페이스의 구체적 구현체를 제공
    
    **Repository**
    
    : 데이터 접근 및 조작을 담당
    
    - Adapter: 외부 API 또는 다른 시스템과의 통신을 처리
    - Configuration: 설정 정보를 관리



### 💡비지니스(도메인) 로직 구분

- 비지니스/도메인 로직
    - S/W가 풀고자 하는 현실의 문제
    - ex) 은행 App : 이자율, 잔액, 출금, 해지 등
- 그 외
    - ex) DB에 연결하기, API 호출하기, Data를 효율적으로 저장하기, 
    고화질 동영상을 캐싱해서 빠르게 로딩 하기 등

❓이 코드가 현실 문제에 대한 의사 결정을 하고 있는가?

- Yes → 비지니스(도메인) 로직
