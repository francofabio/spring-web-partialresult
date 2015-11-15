# spring-web-partialresult

Enables the configuration of which properties of a given object should be returned on a call to a RESTful service implemented using Spring MVC.<br/>
For now, only JSON response are supported.

## How to use
To use this library, you need install the package in your maven local repository:
```
mvn install
```

And add this dependency in your pom.xml

```xml
<dependency>
    <groupId>br.com.binarti</groupId>
    <artifactId>spring-web-partialresult</artifactId>
    <version>1.0</version> <!-- or the last version -->
</dependency>
```

This library is not available yet in maven central repository, it is coming soon.<br/>
If you not use maven. You can also build the jar and add in classpath of the Java app.<br/>
To build jar package, you need install maven and execute the command:
```
mvn clean package
```
The jar package is available in target/ directory.

<strong>Java 1.8 is required.</strong>

## Quick start
This framework uses the <a href="https://github.com/binartecnologia/sjog">Simple Java Object-Graph</a> library to resolve the properties that will be used to generate JSON response. Therefore the same rules used in that library will be applied by this framework. 

To configure your spring to use spring-web-partialresult, you should include the package br.com.binarti.spring.web.partialresult in your component scan and enable AspectJ.

```java
//Spring configuration
@Configuration
@ComponentScan(basePackages = {
  "mypackage.service",
  "mypackage.repository",
  "mypackage.controller",
  "br.com.binarti.spring.web.partialresult"
})
@EnableAspectJAutoProxy
public class AppConfiguration implements WebMvcConfigurerAdapter {
    @Bean
  public PartialResultJSONMessageConverter partialResultMessageConverter() {
    return new PartialResultJSONMessageConverter();
  }
  
  @Override
  public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
    converters.add(partialResultMessageConverter());
  }
}

//Customer.java
public class Customer {
  private Long id;
  private String name;
  //constructors, getters and setters ommited
}

//Order.java
public class Order {
  private Long id;
  private Date date;
  private Customer customer;
  private double amount;
  //constructors, getters and setters ommited
}

//OrderController.java
import br.com.binarti.spring.web.partialresult.annotation.PartialResult;
import br.com.binarti.spring.web.partialresult.Response;
...

@RestController
@RequestMapping(value="/{apiVersion}/orders", produces=MediaType.APPLICATION_JSON_VALUE)
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;
    
    /**
     * This service has to return a JSON like this:
     * {
     *   "id": 1,
     *   "date": "2015-10-16T23:18:30",
     *   "amount": 100.10
     * }
     */
    @RequestMapping(value="/", method = RequestMethod.GET)
    @PartialResult
    public Response<List<Order>> getAllOrders() {
        return Response.ok(orderRepository.findAll());
    }
    
    /**
     * This service has to return a JSON like this:
     * {
     *   "id": 1,
     *   "date": "2015-10-16T23:18:30",
     *   "amount": 100.10,
     *   "customer": {
     *     "id": 190,
     *     "name": "John Galt"
     *   }
     * }
     */
    @RequestMapping(value="/all-orders-with-customer", method = RequestMethod.GET)
    @PartialResult(includes = {
        @Include("customer")
    })
    public Response<List<Order>> getAllOrdersWithCustomer() {
        return Response.ok(orderRepository.findAll());
    }
    
    /**
     * This service has to return a JSON like this:
     * {
     *   "id": 1,
     *   "date": "2015-10-16T23:18:30",
     *   "customer": {
     *     "id": 190,
     *     "name": "John Galt"
     *   }
     * }
     */
    @RequestMapping(value="/all-orders-with-customer-private", method = RequestMethod.GET)
    @PartialResult(includes = {
        @Include("customer")
    }, excludes="amount")
    public Response<List<Order>> getAllOrdersWithCustomerWithouAmount() {
        return Response.ok(orderRepository.findAll());
    }
}
```
