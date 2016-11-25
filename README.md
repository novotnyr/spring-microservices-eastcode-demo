Spring Microservices App
=========================
About
-----
Let's build a microservice oriented architecture based on the **Spring Boot**. We will create two microservices: the first one shall handle file uploads and the second one shall process these files with sophisticated algorithms (actually counting file sizes).


Requirements
-------------

### Hashicorp Consul

*	run Hashicorp Consul on localhost
	
		./consul agent -server -bootstrap -data-dir=/tmp -advertise=127.0.0.1 -ui

### RabbitMQ

*	run RabbitMQ Broker on localhost
*	make sure that the default user `guest` is available with access to `/` vhost.
*	create a queue named `file`

File Uploader Module
--------------------

### Initialize an IntelliJ IDEA module


*	Use IntellJ IDEA to create a *Spring Initializer* Module
*	Introduce the following modules:
	*	**Web / Web** to enable REST API with Spring MVC support
	*	**I/O / AMQP** to enable RabbitMQ client
	*	**Cloud Discovery / Consul Discovery** to allow service registration in Consul 
	*	**Ops / Actuator** to enable HTTP healthchecks

### Review dependencies

The `pom.xml` must contain at least the following dependencies:

*	`spring-boot-starter-web`
*	`spring-boot-starter-amqp`
*	`spring-cloud-starter-consul-discovery`
*	`spring-boot-starter-actuator`

### Run the app

Run the microservice via 

	mvn spring-boot:run

This will start an embedded Tomcat.

Alternative package the microservice into an executable JAR:

	mvn package

The microservice JAR is standalone 33MB file.
	
Run the microservice

	java -jar target/uploader-0.0.1-SNAPSHOT.jar


### Create a REST controller

*	use `@RestController` to indicate RESTful resource (or "controller" in the Spring MVC lingo)
*	inject the RabbitMQ client object via `@Autowire`d `RabbitTemplate`
*	use `@PostMapping` to handle HTTP POST methods
*	

	@RestController
	public class RestApi {
	   @Autowired
	   private RabbitTemplate rabbitTemplate;
	
	   @PostMapping("/files")
	   public void handleFile(MultipartFile file) throws IOException {
	       rabbitTemplate.convertAndSend("file-uploaded", file.getBytes());
	   }
	}
	
*	The application will send bytes of the file to the RabbitMQ queue named `file-uploaded`.
	*		Note: this is neither the most exact explanation, nor the best practice.

## Use REST controller

*	use **Postman** to upload file via form-based HTP POST request to `http://localhost:8080/files`

## Integrate with Consul

*	Add `@EnableDiscoveryClient	` on the `UploaderApplication` to integrate the microservice with Consul.
*	Run the microservice
*	Observe Consul UI having green "application" service registered



File Handler Module
--------------------

### Initialize an IntelliJ IDEA module


*	Use IntellJ IDEA to create a *Spring Initializer* Module
*	Introduce the following modules:
	*	**I/O / AMQP** to enable RabbitMQ client
	*	**Cloud Discovery / Consul Discovery** to allow service registration in Consul 
	*	**Ops / Actuator** to enable HTTP healthchecks

### Review dependencies

The `pom.xml` must contain at least the following dependencies:

*	`spring-boot-starter-amqp`
*	`spring-cloud-starter-consul-discovery`
*	`spring-boot-starter-actuator`

### Run the app

Run the microservice via 

	mvn spring-boot:run

The microservice will fail since the Healthcheck endpoint runs on 8080, being in conflict with File Uploader microservice.

Rerun the service with a random HTTP port

	mvn spring-boot:run -Dserver.port=0

### Configure the `HandlerApplication`

*	enable Consul Integration via `@EnableDiscoveryClient` on `HandlerApplication`

### Introduce the RabbitMQ handler


*	enable `@RabbitListener` to retrieve files from the `file` queue
*	use SLF4J logger to log messags


@Component
public class FileHandler {
    public final Logger logger = LoggerFactory.getLogger(getClass());

    @RabbitListener(queues = "file")
    public void handleFile(byte[] fileBytes) {
        logger.info("Handling file [{} bytes]", fileBytes.length);
    }

}

Scaling
--------
### Adjust microservice instance naming

*	Configure both microservices with specific application names in order to register them in Consul
	*	Add `spring.application.name=uploader` to the *File Uploader* `application.properties` file
	*	Add `spring.application.name=handler` to the *File Handler* `application.properties` file


*	Run one instance of *File Uploader*
*	Run two instances of *File Handler* (remember to use `-Dserver.port=0`)
*	Submit multiple HTTP requests to the *File Uploader*. Notice how both instances of the *File Handler* handle requests.
*	Observe how Consul registers 1 instance of *File Uploader* and 2 instances of *File Handler*

### Failure

*	Stop one of the two instances of *File Handler*.
*	Observe Consul and its instance states for `handler`
*	Observe that message still fly around despite one instance being dead.






