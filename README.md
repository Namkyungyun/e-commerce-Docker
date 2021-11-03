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




