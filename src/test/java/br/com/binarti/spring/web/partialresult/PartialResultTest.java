package br.com.binarti.spring.web.partialresult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.binarti.sjog.ObjectGraph;
import br.com.binarti.sjog.ObjectGraphBuilder;
import br.com.binarti.spring.web.partialresult.test.controller.FakeCustomerController;
import br.com.binarti.spring.web.partialresult.test.controller.FakeOrderController;
import br.com.binarti.spring.web.partialresult.test.model.Address;
import br.com.binarti.spring.web.partialresult.test.model.City;
import br.com.binarti.spring.web.partialresult.test.model.Customer;
import br.com.binarti.spring.web.partialresult.test.model.Order;

public class PartialResultTest {

	private PartialResultProvider templateFactory;
	
	@Before
	public void setup() {
		templateFactory = new PartialResultProvider();
	}
	
	private ObjectGraphBuilder template(Class<?> target, String methodName, Class<?>...parameterTypes) throws NoSuchMethodException, SecurityException {
		return template(Response.empty(), target, methodName, parameterTypes);
	}
	
	@SuppressWarnings("unchecked")
	private <T> ObjectGraphBuilder template(Response<T> response, Class<?> target, String methodName, Class<?>...parameterTypes) throws NoSuchMethodException, SecurityException {
		Method method = target.getMethod(methodName, parameterTypes);
		return templateFactory.createTemplate(method, (Response<Object>) response);
	}
	
	private FakeOrderController orderController() {
		return new FakeOrderController();
	}
	
	private FakeCustomerController customerController() {
		Customer customer = new Customer(1L, "Customer", new Address("Street A", new City("City", "ST")));
		return new FakeCustomerController(customer);
	}
	
	private void checkCreateSimpleObjectGraph(ObjectGraph graph) {
		assertEquals(2, graph.getNodes().size());
		assertNotNull(graph.getNode("id"));
		assertNotNull(graph.getNode("date"));
	}
	
	@Test
	public void shouldCreateSimpleObjectGraph() throws Exception {
		Response<Order> response = orderController().get(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "get", Long.class).build(response.getData());
		checkCreateSimpleObjectGraph(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "get", Long.class).build(response.getData());
		checkCreateSimpleObjectGraph(graphNonNullResponse);
	}
	
	@Test
	public void shouldCreateObjectGraphWithAnnotedType() throws Exception {
		Response<Order> response = orderController().getWithAnnotedType(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getWithAnnotedType", Long.class).build(response.getData());
		checkCreateSimpleObjectGraph(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getWithAnnotedType", Long.class).build(response.getData());
		checkCreateSimpleObjectGraph(graphNonNullResponse);
	}
	
	private void checkCreateObjectGraphWithNestedInclude(ObjectGraph graph) {
		assertEquals(3, graph.getNodes().size());
		assertNotNull(graph.getNode("id"));
		assertNotNull(graph.getNode("date"));
		
		assertNotNull(graph.getNode("customer"));
		assertEquals(2, graph.getNode("customer").getChildren().size());
		assertNotNull(graph.getNode("customer").getChild("id"));
		assertNotNull(graph.getNode("customer").getChild("name"));
	}
	
	@Test
	public void shouldCreateObjectGraphWithNestedInclude() throws Exception {
		Response<Order> response = orderController().getOrderWithCustomer(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrderWithCustomer", Long.class).build(response.getData());
		checkCreateObjectGraphWithNestedInclude(graphNullResponse);

		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderWithCustomer", Long.class).build(response.getData());
		checkCreateObjectGraphWithNestedInclude(graphNonNullResponse);
	}
	
	private void checkCreateObjectGraphWithoutIncludePrimitives(ObjectGraph graph) {
		assertEquals(1, graph.getNodes().size());
		assertNotNull(graph.getNode("customer"));
		assertEquals(2, graph.getNode("customer").getChildren().size());
		assertNotNull(graph.getNode("customer").getChild("id"));
		assertNotNull(graph.getNode("customer").getChild("name"));
	}
	
	@Test
	public void shouldCreateObjectGraphWithoutIncludePrimitives() throws Exception {
		Response<Order> response = orderController().getOrderWithoutIncludePrimitive(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrderWithoutIncludePrimitive", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutIncludePrimitives(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderWithoutIncludePrimitive", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutIncludePrimitives(graphNonNullResponse);
	}
	
	private void checkCreateObjectGraphWithoutIncludePrimitivesInNestedInclude(ObjectGraph graph) {
		assertEquals(3, graph.getNodes().size());
		assertNotNull(graph.getNode("id"));
		assertNotNull(graph.getNode("date"));
		
		assertNotNull(graph.getNode("customer"));
		assertEquals(1, graph.getNode("customer").getChildren().size());
		assertNotNull(graph.getNode("customer.address"));
		assertEquals(1, graph.getNode("customer.address").getChildren().size());
		assertNotNull(graph.getNode("customer.address.street"));
	}
	
	@Test
	public void shouldCreateObjectGraphWithoutIncludePrimitivesInNestedInclude() throws Exception {
		Response<Order> response = orderController().getOrderWithoutIncludePrimitiveInNestedInclude(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrderWithoutIncludePrimitiveInNestedInclude", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutIncludePrimitivesInNestedInclude(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderWithoutIncludePrimitiveInNestedInclude", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutIncludePrimitivesInNestedInclude(graphNonNullResponse);
	}
	
	private void checkCreateObjectGraphWithCollectionInInclude(ObjectGraph graph) {
		assertEquals(4, graph.getNodes().size());
		assertNotNull(graph.getNode("id"));
		assertNotNull(graph.getNode("date"));
		
		assertNotNull(graph.getNode("customer"));
		assertEquals(2, graph.getNode("customer").getChildren().size());
		assertNotNull(graph.getNode("customer.id"));
		assertNotNull(graph.getNode("customer.name"));
		
		assertNotNull(graph.getNode("itens"));
		assertTrue(graph.getNode("itens").isCollection());
		assertEquals(2, graph.getNode("itens[0]").getChildren().size());
		assertNotNull(graph.getNode("itens[0].id"));
		assertNotNull(graph.getNode("itens[0].price"));
	}
	
	@Test
	public void shouldCreateObjectGraphWithCollectionInInclude() throws Exception {
		Response<Order> response = orderController().getOrderWithItens(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrderWithItens", Long.class).build(response.getData());
		checkCreateObjectGraphWithCollectionInInclude(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderWithItens", Long.class).build(response.getData());
		checkCreateObjectGraphWithCollectionInInclude(graphNonNullResponse);
	}
	
	private void checkCreateObjectGraphWithCollectionInIncludeAndDeep(ObjectGraph graph) {
		assertEquals(4, graph.getNodes().size());
		assertNotNull(graph.getNode("id"));
		assertNotNull(graph.getNode("date"));
		
		assertNotNull(graph.getNode("customer"));
		assertEquals(2, graph.getNode("customer").getChildren().size());
		assertNotNull(graph.getNode("customer.id"));
		assertNotNull(graph.getNode("customer.name"));
		
		assertNotNull(graph.getNode("itens"));
		assertTrue(graph.getNode("itens").isCollection());
		assertEquals(3, graph.getNode("itens[0]").getChildren().size());
		assertNotNull(graph.getNode("itens[0].id"));
		assertNotNull(graph.getNode("itens[0].price"));
		
		assertNotNull(graph.getNode("itens[0].product"));
		assertEquals(2, graph.getNode("itens[0].product").getChildren().size());
		assertNotNull(graph.getNode("itens[0].product.id"));
		assertNotNull(graph.getNode("itens[0].product.name"));
	}
	
	@Test
	public void shouldCreateObjectGraphWithCollectionInIncludeAndDeep() throws Exception {
		Response<Order> response = orderController().getOrderWithItensAndDeep(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrderWithItensAndDeep", Long.class).build(response.getData());
		checkCreateObjectGraphWithCollectionInIncludeAndDeep(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderWithItensAndDeep", Long.class).build(response.getData());
		checkCreateObjectGraphWithCollectionInIncludeAndDeep(graphNonNullResponse);
	}
	
	private void checkCreateObjectGraphForCollectionResultInRoot(ObjectGraph graph) {
		assertEquals(4, graph.getNode("$root[0]").getChildren().size());
		assertNotNull(graph.getNode("$root[0].id"));
		assertNotNull(graph.getNode("$root[0].date"));
		
		assertNotNull(graph.getNode("$root[0].customer"));
		assertEquals(2, graph.getNode("$root[0].customer").getChildren().size());
		assertNotNull(graph.getNode("$root[0].customer.id"));
		assertNotNull(graph.getNode("$root[0].customer.name"));
		
		assertNotNull(graph.getNode("$root[0].itens"));
		assertTrue(graph.getNode("$root[0].itens").isCollection());
		assertEquals(3, graph.getNode("$root[0].itens[0]").getChildren().size());
		assertNotNull(graph.getNode("$root[0].itens[0].id"));
		assertNotNull(graph.getNode("$root[0].itens[0].price"));
		
		assertNotNull(graph.getNode("$root[0].itens[0].product"));
		assertEquals(2, graph.getNode("$root[0].itens[0].product").getChildren().size());
		assertNotNull(graph.getNode("$root[0].itens[0].product.id"));
		assertNotNull(graph.getNode("$root[0].itens[0].product.name"));
	}
	
	@Test
	public void shouldCreateObjectGraphForCollectionResultInRoot() throws Exception {
		Response<List<Order>> response = orderController().getOrders("john");
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrders", String.class).build(response.getData());
		checkCreateObjectGraphForCollectionResultInRoot(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrders", String.class).build(response.getData());
		checkCreateObjectGraphForCollectionResultInRoot(graphNonNullResponse);
	}
	
	private void checkCreateObjectGraphForAnonymousClass(ObjectGraph graph) {
		assertEquals(3, graph.getNodes().size());
		assertNotNull(graph.getNode("name"));
		assertNotNull(graph.getNode("age"));
		
		assertNotNull(graph.getNode("address"));
		assertEquals(2, graph.getNode("address").getChildren().size());
		assertNotNull(graph.getNode("address.street"));
		assertNotNull(graph.getNode("address.city"));
		
		assertEquals(2, graph.getNode("address.city").getChildren().size());
		assertNotNull(graph.getNode("address.city.name"));
		assertNotNull(graph.getNode("address.city.state"));
	}
	
	@Test
	public void shouldCreateObjectGraphForAnonymousClass() throws Exception {
		Response<Object> response = orderController().getAnonymousPersonObject();		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getAnonymousPersonObject").build(response.getData());
		checkCreateObjectGraphForAnonymousClass(graphNonNullResponse);
	}
	
	@Test
	public void shouldCreateObjectGraphForAnonymousClassAndNullResponse() throws Exception {
		ObjectGraph anonNullGraph = template(FakeOrderController.class, "getAnonymousPersonObject").build(null);
		assertNotNull(anonNullGraph.getNode("$root"));
		assertNull(anonNullGraph.getObject());
	}
	
	@Test
	public void shouldCreateObjectGraphForCollectionResultInRootAndObjectType() throws Exception {
		Response<List<Object>> response = orderController().getOrderAsObject();
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderAsObject").build(response.getData());
		checkCreateObjectGraphForCollectionResultInRoot(graphNonNullResponse);
	}
	
	@Test
	public void shouldCreateObjectGraphForCollectionResultInRootAndRawType() throws Exception {
		Response<List<Object>> response = orderController().getOrderAsObject();
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderAsRawList").build(response.getData());
		checkCreateObjectGraphForCollectionResultInRoot(graphNonNullResponse);
	}
	
	@Test
	public void shouldCreateObjectGraphWithInheritance() throws Exception {
		Response<Customer> response = customerController().get();		
		ObjectGraph graphNonNullResponse = template(response, FakeCustomerController.class, "get").build(response.getData());
		
		assertEquals(2, graphNonNullResponse.getNodes().size());
		assertNotNull(graphNonNullResponse.getNode("id"));
		assertNotNull(graphNonNullResponse.getNode("name"));
	}
	
	@Test
	public void shouldCreateObjectCollectionGraphWithInheritance() throws Exception {
		Response<List<Customer>> response = customerController().list();
		ObjectGraph graphNonNullResponse = template(response, FakeCustomerController.class, "list").build(response.getData());
		
		assertEquals(3, graphNonNullResponse.getNode("$root[0]").getChildren().size());
		assertNotNull(graphNonNullResponse.getNode("$root[0].id"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].name"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].address"));
		
		assertEquals(1, graphNonNullResponse.getNode("$root[0].address").getChildren().size());
		assertNotNull(graphNonNullResponse.getNode("$root[0].address.street"));
	}
	
	@Test
	public void shouldCreateObjectCollectionGraphWithInheritanceOverride() throws Exception {
		Response<List<Customer>> response = customerController().listForOverride();
		ObjectGraph graphNonNullResponse = template(response, FakeCustomerController.class, "listForOverride").build(response.getData());
		
		assertEquals(3, graphNonNullResponse.getNode("$root[0]").getChildren().size());
		assertNotNull(graphNonNullResponse.getNode("$root[0].id"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].name"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].address"));
		
		assertEquals(2, graphNonNullResponse.getNode("$root[0].address").getChildren().size());
		assertNotNull(graphNonNullResponse.getNode("$root[0].address.street"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].address.city"));
		
		assertEquals(2, graphNonNullResponse.getNode("$root[0].address.city").getChildren().size());
		assertNotNull(graphNonNullResponse.getNode("$root[0].address.city.name"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].address.city.state"));
	}
	
	@Test
	public void shouldCreateObjectCollectionGraphWithInheritanceOverridePartialResult() throws Exception {
		Response<List<Customer>> response = customerController().listForOverridePartialResult();
		ObjectGraph graphNonNullResponse = template(response, FakeCustomerController.class, "listForOverridePartialResult").build(response.getData());
		
		assertEquals(3, graphNonNullResponse.getNode("$root[0]").getChildren().size());
		assertNotNull(graphNonNullResponse.getNode("$root[0].id"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].name"));
		assertNotNull(graphNonNullResponse.getNode("$root[0].address"));
		
		assertEquals(1, graphNonNullResponse.getNode("$root[0].address").getChildren().size());
		assertNotNull(graphNonNullResponse.getNode("$root[0].address.street"));
	}

	private void checkCreateObjectGraphWithoutIncludePrimitivesAndPrimitiveIncludedExplicit(ObjectGraph graph) {
		assertEquals(2, graph.getNodes().size());
		assertNotNull(graph.getNode("date"));
		assertNotNull(graph.getNode("customer"));
		
		assertEquals(2, graph.getNode("customer").getChildren().size());
		assertNotNull(graph.getNode("customer").getChild("id"));
		assertNotNull(graph.getNode("customer").getChild("name"));
	}
	
	@Test
	public void shouldCreateObjectGraphWithoutIncludePrimitivesAndPrimitiveIncludedExplicit() throws Exception {
		Response<Order> response = orderController().getOrderWithoutIncludePrimitiveAndPrimitiveIncludeExplicit(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrderWithoutIncludePrimitiveAndPrimitiveIncludeExplicit", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutIncludePrimitivesAndPrimitiveIncludedExplicit(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderWithoutIncludePrimitiveAndPrimitiveIncludeExplicit", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutIncludePrimitivesAndPrimitiveIncludedExplicit(graphNonNullResponse);
	}

	private void checkCreateObjectGraphWithoutPrimitiveInSingleNestedInclude(ObjectGraph graph) {
		assertEquals(3, graph.getNodes().size());
		assertNotNull(graph.getNode("id"));
		assertNotNull(graph.getNode("date"));
		assertNotNull(graph.getNode("customer"));
		
		assertEquals(1, graph.getNode("customer").getChildren().size());
		assertNotNull(graph.getNode("customer.address"));
		
		assertEquals(1, graph.getNode("customer.address").getChildren().size());
		assertNotNull(graph.getNode("customer.address.street"));
	}	
	
	@Test
	public void shouldCreateObjectGraphWithoutPrimitiveInSingleNestedInclude() throws Exception {
		Response<Order> response = orderController().getOrderWithoutIncludePrimitiveInSingleNestedInclude(1L);
		ObjectGraph graphNullResponse = template(FakeOrderController.class, "getOrderWithoutIncludePrimitiveInSingleNestedInclude", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutPrimitiveInSingleNestedInclude(graphNullResponse);
		
		ObjectGraph graphNonNullResponse = template(response, FakeOrderController.class, "getOrderWithoutIncludePrimitiveInSingleNestedInclude", Long.class).build(response.getData());
		checkCreateObjectGraphWithoutPrimitiveInSingleNestedInclude(graphNonNullResponse);
	}
	
	
}
