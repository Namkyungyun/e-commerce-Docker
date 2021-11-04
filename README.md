# user-service 

1. project 생성

	1) dependency
		: Lombok - setter, getter를 만들어주고, 생성자 재정의, 로그 파일 출력할 수 있는 객체를 포함하고 있음. 코드의 양이 줄어드는 장점이 있음.
		  Spring Boot DevTools - applicaion을 재배포하고 종료했다가 다시 키지않은 상태에서 reload할수 있는 간단한 기능을 가지고있음.
		  Spring Web - REST API를 만들 때 필요한 dependency
		  Eureka Discovery Client - Eureka 서버에 서비스의 정보를 등록해야하므로 필요한 dependency

2. application.yml 에 있는 정보를 Controller에 가져오는 방법

	1) Environment 객체 사용
		- @Autowired를 이용해 직접 필드에 주입하는 방법도 있지만,
		  생성자를 사용하는 것이 좋다.
		  
private Environment env;

    @Autowired
    public UserController(Environment env) {
        this.env = env;
    }

	위의 과정을 통해 Environment 객체가 만들어진다.


 @GetMapping("/welcome")
    public String welcome(){
        return env.getProperty("greeting.message");
    }

	사용하고자 하는 메소드내에서 getProperty를 이용하여 application.yml의 내용을 불러온다.
	
	
2) @Value 사용
	- vo에서 yml파일에서 가져오고자하는 데이터값을 저장한다.
	- controller에서 저장된 값을 불러오기 위해 autowired 어노테이션을 이용해
	  vo를 불러와 해당 메소드안에서 return 시킨다.  
		  
   @GetMapping("/welcome")
    public String welcome(){
//        return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

# H2 DB 연결하기
	- mvn repository에서 h2 dependency 복사 후, pom.xml에 추가
	- scope이 test로 되어 있으므로(실행했을때 결과를 확인할 수 없음)
	 	 runtime으로 변경한다.
	- application.yml에 h2에 대한 연결 설정 기입하기
	
spring:
  application:
    name: user-service
  h2:
    console:
      enabled: true
      settings:
        web-allow-others: true
      path: /h2-console

	- 서비스 기동 후, eureka server에 등록된 ip+port로 들어가게 되면
	  위에서 설정한 path의 값을 port뒤의 uri로 추가해서 들어가면, h2접속창이 뜬다.
	- h2 접속창의 JDBC URL:은 테이블을 생성해달라는 명령어와 같다고 볼 수 있다.
	  (h2 dependency에서 1.4.x 버전의 경우 스프링부트가 기동될대 테이블을 자동으로
 	   생성하지 못하므로, 1.3.x 버전으로 바꾼 후 재기동하면 테이블을 생성되어 접속이
               진행되어진다.)
       
 jdbc:h2:mem:testdb

# user-service 기능

<user Microservice - 기능1: 신규 회원 등록>

1. usermember라는 vo 생성
2. 사용자로부터 들어오는 입력값에 대한 validation을 추가할 수 있다.
=>  Validation check
	1) json으로 클라이언트의 값을 받아오고
	2) 위의 값이 비즈니스로직에 의해서 처리가 되어야하는데
		controller, service, repository
	3) Database인 JPA와 매핑될수 있는 객체가 필요한데
	그를 위한 ENTITY를 생성할 것임.
	**JPA : 쿼리없이 데이터를 다룰 수 있게 도와주는 API로
	[Java Persistence API]	
	그 바탕에서 Hibernate가 있다.
	client에서 입력된 데이터를 저장한 객체 -> JPA ->
	자동으로 sql코드로 변환시켜서 dbms까지 저장,생성,삭제, 수정
	이 진행되게 된다.

3. UserEntity, UserRepository 생성
	: UserEntity클래스의 경우 Id, GenerateValue 어노테이션을 사용하고
	완성된 UserEntity는 UserRepository에서 상속받을
	CrudRepository의 key값으로 들어갈 것이다.
	CrudRepository은 기본적으로 저장, 삭제, 수정, 검색 등의
	메소드를 기본으로 사용할 수 있게끔 해준다.
=> 하나의 클래스가 다른 클래스 변형되기 위해서
     사용되는 간단한 library가 'modelmapper' : 2.3.8 버전
     [UserDTO를 UserEntity로 변형시켜 새로운 UserEntity에 isntance시킬 수 있는 것이다]

< 작업 순서 >
1. vo패키지 내에 RequestUser 생성(요청받을 객체)
2. 위의 RequestUser를 저장할 수 있는 DTO 생성해야함
   위의 정보를 데이터베이스에 저장하고 이동시킬 수 있는 용도로
   변환시키는 클래스를 등록함( DTO ; UserDTO)
3. jpa를 다룰 클래스를 만들기에 앞서 pom.xml

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
            <version>2.5.4</version>
        </dependency>
추가하여야 한다.
그런 다음, jpa 패키지내에서 Entity 클래스를 만든다.
Entity는 데이터베이스로 만들어져야하는 요소이다.
따라서  @Data 뿐만아니라, @Entity를 추가하여야한다.
즉, 이 Entity클래스는 DB에 저장될 요소들이다.

4. UserRepository 만들기
=> CRUD 작업을 하는 클래스 (Entity값을)
상속을 받은  CrudRepository을 통해서 find, save 등의 작업도 가능

5. ReqeustUser를 UserDTO로, UserDTO를 UserEntity로 바꾸는 작업에서
   하나의 객체를 또 다른 객체로 바꿔주는 작업에서
   하나의 클래스가 가지고 있는 정보를 다른 클래스의 정보로 변환시켜주는
   맵퍼의 library를 이용(클래스) -> dependency 추가
         <dependency>
            <groupId>org.modelmapper</groupId>
            <artifactId>modelmapper</artifactId>
            <version>2.3.8</version>
        </dependency>

6. 각 객체를 다루는 각 클래스의 변환들을 mapper를 통해 진행하고
   controller를 통해 받아야하므로, controller 작업을 마무리한다.

7. application.yml 파일에서 h2 관련된 데이터소스 설정을 추가하고
   postman에서 post방식으로 body에 json 데이터를 입력한 뒤,
   이를 전송하여 데이터가 생성되게끔 진행
8. h2-console을 통해 들어간 주소에서 해당 데이터베이스가 생성되었는지
   확인을 진행한다.

9. 성공할 시 status200이 뜨는데 실질적으로 200보다는 201이 더 옳은
   create에대한 표현이므로, status 201으로 변경하기 위해
   controller의 method를 ResponseEntity로 반환되게끔 변경한다.
   == 201번 성공코드 반환

10. return 값으로 201번코드 값을 반환해 body에서 반환되는 내용이 없으므로,
     반환내용을 위해서 ResponseUser를 생성한다.
     유저에게 보여줄 값들만 정의함

11. 위의 ResponseUser 클래스를 controller의 createUser메소드에서 
     부른 뒤, ModelMapper를 이용해서 dto를 responseuser클래스로 변환시킨다.
     return에서도 해당내용을 실기위해서 body에 실을 코드를 추가.

12. serviceimpl에서 Dto를 반환시키는 값을 null로 지정해놨었기 때문에
     이를 변경시켜야하므로, return하는용의 dto를 부르고 
     entity를 modelmapper를 이용해 반환용 dto로 변환시킨다.

<회원가입을 할 때 입력했던 pwd를 암호화하는 작업 진행>
-Spring Security =>을 이용해서 아래의 기능을 진행
 : Authentication + Authorization (인증과 권한)

1. 애플리케이션에 spring security jar을 dependency에 추가
2. WebSecurityConfigurerAdapter를 상속받는 security Configuration 클래스 생성 : WebSecurity
3. Security Configruation 클래스에 @EnableWebSecurity 추가
4. Authentication -> configure(AuthenticationManagerBuilder auth)메서드를 재정의
5. Password encode를 위한 BCryptPasswordEncoder 빈 정의
6. Authorizaion -> configure(HttpSecurity http:파라미터)메서드를 재정의

//@Configuration의 경우 다른 빈들보다 우선순위를 앞에해서 등록을 하게 되는 것

//1번의 디펜던시추가에 의해서 run을 할 시, 

Using generated security password: 4f1f225f-3f18-4820-8447-0b81d9afc063

가 표시하는데, 당장 사용할 일은 없지만, 스프링부트프로젝트에서 인증작업을 하기 위해서
(아이디와 패스워드가 지원하지 않은 상황) 위의 패스워드를 사용해서 인증작업을
처리할 수 있다.


// BCryptPasswordEncoder : 여러번의 해쉬작업을 적용하여 랜덤한 암호글씨로 변경
	=> serviceimpl에서 처리(생성자 이용)
	     기존의 재정의를 하여 주입시켰던 것에서 생성자로 주입시키는 것으로 변경


//어노테이션 오토와이어드는 사용자에 의해서 인스턴스가 만들어지는 것이 아니라
스프링의 컨텍스트가 기동이되면서 자동으로 등록할수 있는 빈을 찾아서 자동으로 메모리에 
등록해주는 것인데, 등록할 때 = 인스턴스가 만들어질 때 생성자가 호출이 되어지고
생성자는 정의한 생성자로 불러지게 된다. 그렇기에 생성자안에 있는 내용들도 초기화가
되어져야 어노테이션어토와이어드의 역할인 주입이 진행되는 것이다. 
유저 레퍼지토리는 빈으로 등록을 시켜놨지만, BCryptPasswordEncoder의 경우 빈으로
등록하지 않았기 때문에, 가장먼저 처음 호출되는 기동 클래스에다가 해당 클래스를 넣어야한다.
따라서 기동되는 클래스인 Application.java에다가 BCryptPasswordEncoder를 빈으로 등록해야한다.


<user Microservice - 기능2: 상세 정보 확인, 주문 내역 확인>

성숙도 레벨2에 해당하는 내용으로 uri는 같으나 http method를 다르게 하여 기능을 다르게 할 수 있다.
(ex) POST = 등록 // GET = 조회

전체 사용자 조회 		: /user-service/users,		/users,		get방식
사용자 정보, 주문 내역 조회 :	 /user-service/users/{user_id},	/users/{user_id}	get방식

random.port를 통해서 내용을 조회하는 것에 불편함이 있기때문에 이제부터는
api gateway를 이용하여 조회할 것이다. 
우선 api gateway-service의 application.yml에 user-service route를 추가한다.



# 사용자 조회(전체 사용자 조회 / 개별 사용자 조회)

유저에게 보여줄 데이터를 ResponseUser에 담아 두었기때문에
ResponseUser에다가 주문데이터값을 반환하기 위해서 
LIst로 코드로 추가


//@JsonInclude의 경우 null이 존재한다면 보여줄 필요가 없기때문에
null을 처리하기 위해서 주입한다.

# Catalog Microservice

사용자가 주문하기 전에 상품목록을 조회하기 위한 용도
상품목록 확인이 가능한 정도로만 만듦

1. dependency 
	: { Lombok, dev tools, Web, JPA, Eureka Client, h2database, modelmapper }

2. yml
	: 랜덤포트, h2연결, jpa설정(초기에 만들어줘야할 데이터들을 sql파일에 등록해놓고
	  해당 sql을 기동과 함게 자동으로 insert하게끔), 나머지 설정은 user-service와 동일 }
3. 초기 생성 db를 위해 'resources'파일 내부에 data.sql파일을 생성해
    쿼리문을 넣어 놓는다.

# Order Microservice

사용자 주문, 주문내역 기능을 위한 용도
//직렬화를 넣는 이유: 가지고 있는 객체를 전송하거나 데이터베이스에 보관하기 위해서 사용하는 것.

1. dependency 
	: { Lombok, dev tools, Web, jpa, eureka client, h2 database(버전 추가하기), modelmapper }

2. yml
	: 랜덤포트, h2연결, 

