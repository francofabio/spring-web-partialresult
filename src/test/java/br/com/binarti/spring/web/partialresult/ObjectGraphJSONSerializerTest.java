package br.com.binarti.spring.web.partialresult;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import br.com.binarti.spring.web.partialresult.test.model.PojoWithAllPrimitivesAndWrappers;
import br.com.binarti.spring.web.partialresult.test.model.PojoWithAllPrimitivesAndWrappers.TestPrimitiveEnum;
import br.com.binarti.spring.web.partialresult.test.model.json.Address;
import br.com.binarti.spring.web.partialresult.test.model.json.Customer;
import br.com.binarti.spring.web.partialresult.test.model.json.Group;
import br.com.binarti.spring.web.partialresult.test.model.json.HardDisk;
import br.com.binarti.spring.web.partialresult.test.model.json.Order;
import br.com.binarti.spring.web.partialresult.test.model.json.Page;
import br.com.binarti.spring.web.partialresult.test.model.json.Product;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=SpringTestConfig.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ObjectGraphJSONSerializerTest {

	static final String DATE_FORMAT = "yyyy-MM-dd";
	
    private ByteArrayOutputStream output;
    private HttpHeaders httpHeaders;
    private HttpOutputMessage outputMessage;
        
    @Autowired
    private PartialResultJSONMessageConverter jsonMessageConverter;
    
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    private String currentDateAsStr;
    private Date currentDate;
    
    @Before
    public void setup() throws Exception {
        this.output = new ByteArrayOutputStream();
        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        this.outputMessage = mock(HttpOutputMessage.class);
        when(outputMessage.getBody()).thenReturn(output);
        when(outputMessage.getHeaders()).thenReturn(httpHeaders);
        
        this.currentDate = new Date();
        this.currentDateAsStr = sdf.format(currentDate);
    }

    private String jsonResult() {
        return output.toString();
    }
    
	private <T> void serialize(Response<T> response) {
    	try {
    		jsonMessageConverter.write(response, MediaType.APPLICATION_JSON, outputMessage);
    	} catch (Exception e) {
    		throw new RuntimeException("Could not serialize Response", e);
    	}
    }

    private Group createGroup(Long id) {
        return new Group(id, "Group " + id);
    }

    private Product createProduct(Long id) {
        return new Product(id, "Product " + id, currentDate);
    }

    private Product createProductWithGroup(Long idProduct, Long idGroup) {
        Product product = createProduct(idProduct);
        product.setGroup(createGroup(idGroup));
        return product;
    }
    
    @Test
    public void shouldSerializePojo() {
        String expectedResult = "{\"creationDate\":\"" + currentDateAsStr + "\",\"id\":1,\"name\":\"Product 1\"}";
        Product product = createProduct(1L);

        serialize(ResponseBuilder.from(product).build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializeRecursivePojo() {
        String expectedResult = "{\"creationDate\":\"" + currentDateAsStr
                + "\",\"data\":\"product data\",\"id\":1,\"name\":\"Product 1\"}";
        Product product = createProduct(1L);
        product.setData("product data");

        serialize(ResponseBuilder.from(product).include("data").build());
        
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeCollectionOfPojo() {
        String expectedResult = "[{\"creationDate\":\""
                + currentDateAsStr + "\",\"id\":1,\"name\":\"Product 1\"},{\"creationDate\":\"" + currentDateAsStr
                + "\",\"id\":2,\"name\":\"Product 2\"}]";
        List<Product> products = new ArrayList<Product>();
        products.add(createProduct(1L));
        products.add(createProduct(2L));

        serialize(ResponseBuilder.from(products).build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializeEmptyCollection() {
        String expectedResult = "[]";
        List<Product> products = new ArrayList<Product>();

        serialize(ResponseBuilder.from(products).build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @SuppressWarnings("serial")
	@Test
    public void shouldSerializeEmptyCollectionWithFields() {
        String expectedResult = "[]";
         List<Order> orders = new ArrayList<Order>() {};

        serialize(ResponseBuilder.from(orders).include("products.id","products.name").build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializeMap() {
        String expectedResult = "{\"email\":\"jim.kirk@gmail.com\",\"idade\":28,\"name\":\"jim kirk\"}";
        Map<String, Object> json = new TreeMap<String, Object>();
        json.put("name", "jim kirk");
        json.put("idade", 28);
        json.put("email", "jim.kirk@gmail.com");

        serialize(ResponseBuilder.from(json).build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializeListOfPrimitiveAndHeterogeneousValues() {
        String expectedResult = "[1,2,3,4,5,6,\"json\"]";

        List<Object> list = new ArrayList<Object>();
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        list.add("json");

        serialize(ResponseBuilder.from(list).build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldIncludeField() {
        String expectedResult = "{\"creationDate\":\"" + currentDateAsStr
                + "\",\"group\":{\"id\":1,\"name\":\"Group 1\"},\"id\":1,\"name\":\"Product 1\"}";

        Product product = createProductWithGroup(1L, 1L);

        serialize(ResponseBuilder.from(product).include("group").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldIncludeFieldFromCollection() {
        String expectedResult = "{\"id\":1,\"products\":[{\"creationDate\":\""
                + currentDateAsStr
                + "\",\"group\":{\"id\":1,\"name\":\"Group 1\"},\"id\":1,\"name\":\"Product 1\"},"
                + "{\"creationDate\":\"" + currentDateAsStr 
                + "\",\"group\":{\"id\":2,\"name\":\"Group 2\"},\"id\":2,\"name\":\"Product 2\"}]}";

        Order order = new Order(1L, new Customer(1L, "James Tiberius Kirk", new Address("rua", "cidade", "9800989")));
        order.addProduct(createProductWithGroup(1L, 1L));
        order.addProduct(createProductWithGroup(2L, 2L));

        serialize(ResponseBuilder.from(order).include("products", "products.group").build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializeListWithIncludeFieldFromCollection() {
        String expectedResult = "[{\"id\":1,\"products\":[{\"creationDate\":\""
                + currentDateAsStr
                + "\",\"group\":{\"id\":1,\"name\":\"Group 1\"},\"id\":1,\"name\":\"Product 1\"},"
                + "{\"creationDate\":\"" + currentDateAsStr 
                + "\",\"group\":{\"id\":2,\"name\":\"Group 2\"},\"id\":2,\"name\":\"Product 2\"}]}]";

        Order order = new Order(1L, new Customer(1L, "James Tiberius Kirk", new Address("rua", "cidade", "9800989")));
        order.addProduct(createProductWithGroup(1L, 1L));
        order.addProduct(createProductWithGroup(2L, 2L));

        List<Order> orders = Collections.singletonList(order);
        serialize(ResponseBuilder.from(orders).include("products", "products.group").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeParentFields() {
        String expectedResult = "{\"capacity\":2987000009,\"id\":1,\"name\":\"Samsumg ZTX A9000\"}";

        HardDisk hd = new HardDisk(1L, "Samsumg ZTX A9000", 2987000009L);

        serialize(ResponseBuilder.from(hd).build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializePolymorphData() {
    	String expectedResult = "{\"id\":1,\"name\":\"Hard disk\",\"products\":[{\"capacity\":2987000009,\"id\":1,\"name\":\"Samsumg ZTX A9000\"}]}";
    	
		Group group = new Group(1L, "Hard disk");
		Product product = new HardDisk(1L, "Samsumg ZTX A9000", 2987000009L);
		product.setGroup(group);
		group.setProducts(new ArrayList<Product>());
		group.getProducts().add(product);
		
		serialize(ResponseBuilder.from(group).include("products").build());
		assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldExcludeParentField() {
        String expectedResult = "{\"capacity\":2987000009,\"name\":\"Samsumg ZTX A9000\"}";

        HardDisk hd = new HardDisk(1L, "Samsumg ZTX A9000", 2987000009L);

        serialize(ResponseBuilder.from(hd).exclude("id").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldExcludeField() {
        String expectedResult = "{\"creationDate\":\"" + currentDateAsStr
                + "\",\"group\":{\"name\":\"Group 1\"},\"id\":1,\"name\":\"Product 1\"}";

        Group group = new Group(1L, "Group 1");
        Product product = new Product(1L, "Product 1", currentDate, group);

        serialize(ResponseBuilder.from(product).include("group").exclude("group.id").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldExcudeHierarchicalField() {
        String expectedResult = "{\"customer\":{\"id\":1,\"name\":\"James Tiberius Kirk\"},"
                + "\"delivery\":{\"city\":\"Bristol\",\"street\":\"delivery street\",\"zipCode\":\"09887990\"},"
        		+ "\"id\":1,"
                + "\"products\":[{\"creationDate\":\"" + currentDateAsStr
                + "\",\"group\":{\"name\":\"Group 1\"},\"id\":1,\"name\":\"Product 1\"},{\"creationDate\":\""
                + currentDateAsStr + "\",\"group\":{\"name\":\"Group 2\"},\"id\":2,\"name\":\"Product 2\"}]}";

        Order order = new Order(1L, new Customer(1L, "James Tiberius Kirk", new Address("street", "city", "9800989")), new Address(
                "delivery street", "Bristol", "09887990"));
        order.addProduct(createProductWithGroup(1L, 1L));
        order.addProduct(createProductWithGroup(2L, 2L));

        serialize(ResponseBuilder.from(order).include("customer", "delivery", "products", "products.group")
                .exclude("products.group.id").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeWithoutRoot() {
        String expectedResult = "{\"creationDate\":\"" + currentDateAsStr
                + "\",\"group\":{\"name\":\"Group 1\"},\"id\":1,\"name\":\"Product 1\"}";

        Group group = new Group(1L, "Group 1");
        Product product = new Product(1L, "Product 1", currentDate, group);

        serialize(ResponseBuilder.from(product).include("group").exclude("group.id").build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializeGenericCollection() {
    	String expectedResult = "{\"content\":[{\"id\":1,\"name\":\"Group 1\",\"products\":["
    			+ "{\"creationDate\":\"" + currentDateAsStr + "\",\"group\":{\"name\":\"Group 1\"},\"id\":1,\"name\":\"Product 1\"},"
    			+ "{\"creationDate\":\"" + currentDateAsStr + "\",\"group\":{\"name\":\"Group 2\"},\"id\":2,\"name\":\"Product 2\"}"
    			+ "]}],\"page\":1,\"total\":1}";
    	Group group = new Group(1L, "Group 1");
    	group.setProducts(new ArrayList<Product>());
        group.getProducts().add(new Product(1L, "Product 1", currentDate, group));
        group.getProducts().add(new Product(2L, "Product 2", currentDate, new Group(2L, "Group 2")));
        
        Page<Group> page = new Page<Group>(1, 1, Collections.singletonList(group));
        serialize(ResponseBuilder.from(page)
        		.include("content")
        		.include("content.products", "content.products.group.name")
        		.build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeObjectAttribute() {
        String expectedResult = "{\"creationDate\":\"" + currentDateAsStr
                + "\",\"data\":\"data object for product\",\"id\":1,\"name\":\"Product 1\"}";

        Product product = new Product(1L, "Product 1", currentDate);
        product.setData("data object for product");

        serialize(ResponseBuilder.from(product).include("data").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeByteArrayAttribute() {
        String expectedResult = "{\"creationDate\":\"" + currentDateAsStr
                + "\",\"id\":1,\"image\":\"iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAK8AAACvABQqw0mAAAABV0RVh0Q3JlYXRpb24gVGltZQA2LzI0LzA59sFr4wAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNAay06AAAAJ3SURBVDiNlZO/S2phGMc/R+tkQnIMiZRMCgeVY4S2tUYQbS0hlUNDa1tLyyVaqhMh1NbgH9DSYEP+BQ1BS4OLBYFRqb1w9FR43rc7hHUv3eH2wDN9eeD7fH9o9Xo95jjO1dvbm8EPpq+vT/j9/knt5ubm2fiYn9wjhEAIITztdtvw+Xy4rvttn56eWF9fZ2dnB8dx/sJ8Ph/tdtvwSCnRNA0p5bdtNBqcnp5SLpfpdDq0Wi06nQ5/3vS4rotSCoDX11ceHx8JBAL09/cTCoXY398nHA7TbDbZ2toik8mwsLDA4OAgruvSI6VEKUWj0cCyLMrlMvF4nFQqRSaT4eHhAV3XKZVKFItFisUilUqFvb09pJR4unTX1tY4Pj4ml8tRrVYpFAq4rotlWRweHpJIJIhEImSzWeLx+Ocrnq4od3d3RKNRZmZmCAaDpNNp5ubmCIVChMNh5ufnCQaDLC8vk8/nP8X0SCkBODo6QtM0crkcw8PD7O7u4jgOtVqN9/d3bNum2Wxye3vLy8sLwBcDpRSmaRIIBGi1WmxubpJOp3EcB9u2qdfr6LrOyMgIhUKBlZUVlFK4rot3aWnp19DQENfX15ydndHb28vFxQXVapWpqSmi0SimaZJMJkmlUui6zujoKNPT09zf33/ZmM/nMQyDk5MTDg4OsCyLWCzG4uIiXacmJiZIJpMAnww8XXB2dhbbttne3uby8pJsNotpmiil0DQNpRRKKbxeL16vF6XUR5C6idvY2GBsbIzz83MSiQSrq6uMj4/TFflfXZBSopVKpWfAiEQi/G+hhBDUajUA0TMwMDAphLiqVCo/qqPf7xeGYUz+Bgm0dbIWFetGAAAAAElFTkSuQmCC\"," 
        		+ "\"name\":\"Product 1\"}";

        Product product = new Product(1L, "Product 1", currentDate);
        product.setImage(Base64.decodeBase64("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABHNCSVQICAgIfAhkiAAAAAlwSFlzAAAK8AAACvABQqw0mAAAABV0RVh0Q3JlYXRpb24gVGltZQA2LzI0LzA59sFr4wAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNAay06AAAAJ3SURBVDiNlZO/S2phGMc/R+tkQnIMiZRMCgeVY4S2tUYQbS0hlUNDa1tLyyVaqhMh1NbgH9DSYEP+BQ1BS4OLBYFRqb1w9FR43rc7hHUv3eH2wDN9eeD7fH9o9Xo95jjO1dvbm8EPpq+vT/j9/knt5ubm2fiYn9wjhEAIITztdtvw+Xy4rvttn56eWF9fZ2dnB8dx/sJ8Ph/tdtvwSCnRNA0p5bdtNBqcnp5SLpfpdDq0Wi06nQ5/3vS4rotSCoDX11ceHx8JBAL09/cTCoXY398nHA7TbDbZ2toik8mwsLDA4OAgruvSI6VEKUWj0cCyLMrlMvF4nFQqRSaT4eHhAV3XKZVKFItFisUilUqFvb09pJR4unTX1tY4Pj4ml8tRrVYpFAq4rotlWRweHpJIJIhEImSzWeLx+Ocrnq4od3d3RKNRZmZmCAaDpNNp5ubmCIVChMNh5ufnCQaDLC8vk8/nP8X0SCkBODo6QtM0crkcw8PD7O7u4jgOtVqN9/d3bNum2Wxye3vLy8sLwBcDpRSmaRIIBGi1WmxubpJOp3EcB9u2qdfr6LrOyMgIhUKBlZUVlFK4rot3aWnp19DQENfX15ydndHb28vFxQXVapWpqSmi0SimaZJMJkmlUui6zujoKNPT09zf33/ZmM/nMQyDk5MTDg4OsCyLWCzG4uIiXacmJiZIJpMAnww8XXB2dhbbttne3uby8pJsNotpmiil0DQNpRRKKbxeL16vF6XUR5C6idvY2GBsbIzz83MSiQSrq6uMj4/TFflfXZBSopVKpWfAiEQi/G+hhBDUajUA0TMwMDAphLiqVCo/qqPf7xeGYUz+Bgm0dbIWFetGAAAAAElFTkSuQmCC".getBytes()));

        serialize(ResponseBuilder.from(product).build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeNull() {
        String expectedResult = "{}";

        serialize(ResponseBuilder.from(null).build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeNullWithInclude() {
        String expectedResult = "{}";
        
        serialize(ResponseBuilder.from(null).include("name").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeNullWithExclude() {
        String expectedResult = "{}";
        
        serialize(ResponseBuilder.from(null).exclude("name").build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
    public void shouldSerializeNullWithoutPassType() {
        String expectedResult = "{}";

        serialize(ResponseBuilder.from(null).build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeNullWithIncludeWithoutPassType() {
        String expectedResult = "{}";
        
        serialize(ResponseBuilder.from(null).include("name").build());
        assertEquals(expectedResult, jsonResult());
    }

    @Test
    public void shouldSerializeNullWithExcludeWithoutPassType() {
        String expectedResult = "{}";
        
        serialize(ResponseBuilder.from(null).exclude("name").build());
        assertEquals(expectedResult, jsonResult());
    }
    
    @Test
	public void shouldSerializeAllPrimitivesType() throws Exception {
    	String expectedResult = "{"
    			+ "\"booleanPrimitive\":false,"
    			+ "\"booleanWrapper\":true,"
    			+ "\"bytePrimitive\":127,"
    			+ "\"byteWrapper\":127,"
    			+ "\"calendar\":\"2015-09-27\","
    			+ "\"charPrimitive\":\"a\","
    			+ "\"characterWrapper\":\"b\","
    			+ "\"date\":\"2015-09-27\","
    			+ "\"doublePrimitive\":1098.110298,"
    			+ "\"doubleWrapper\":999.99,"
    			+ "\"enumAsPrimitive\":\"VAL1\","
    			+ "\"floatPrimitive\":1.01,"
    			+ "\"floatWrapper\":99.99,"
    			+ "\"intPrimitive\":10000,"
    			+ "\"integerWrapper\":9999,"
    			+ "\"longPrimitive\":100000,"
    			+ "\"longWrapper\":999999,"
    			+ "\"shortPrimitive\":1000,"
    			+ "\"shortWrapper\":1001,"
    			+ "\"stringWrapper\":\"json string\""
    			+ "}";
		PojoWithAllPrimitivesAndWrappers pojo = new PojoWithAllPrimitivesAndWrappers();
		pojo.setBooleanPrimitive(false);
		pojo.setCharPrimitive('a');
		pojo.setBytePrimitive((byte) 127);
		pojo.setShortPrimitive((short) 1000);
		pojo.setIntPrimitive(10000);
		pojo.setLongPrimitive(100000);
		pojo.setFloatPrimitive(1.01f);
		pojo.setDoublePrimitive(1098.110298);
		pojo.setEnumAsPrimitive(TestPrimitiveEnum.VAL1);
		pojo.setBooleanWrapper(Boolean.TRUE);
		pojo.setCharacterWrapper(new Character('b'));
		pojo.setStringWrapper("json string");
		pojo.setByteWrapper(new Byte("127"));
		pojo.setShortWrapper(new Short("1001"));
		pojo.setIntegerWrapper(new Integer(9999));
		pojo.setLongWrapper(new Long(999999));
		pojo.setFloatWrapper(new Float("99.99"));
		pojo.setDoubleWrapper(new Double("999.99"));
		pojo.setDate(new SimpleDateFormat("yyyy-MM-dd").parse("2015-09-27"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse("2015-09-27"));
		pojo.setCalendar(calendar);
		
		serialize(ResponseBuilder.from(pojo).build());
		assertEquals(expectedResult, jsonResult());
	}

}
