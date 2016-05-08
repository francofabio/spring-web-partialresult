package br.com.binarti.spring.web.partialresult;

import java.util.Map;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.binarti.sjog.Node;
import br.com.binarti.sjog.ObjectGraph;

public class ObjectGraphJSONSerializer {

	private ObjectGraph objectGraph;
	
	public ObjectGraphJSONSerializer(ObjectGraph objectGraph) {
		this.objectGraph = objectGraph;
	}
	
	public Object serialize(final ObjectMapper mapper) {
		ObjectNode rootNode = mapper.createObjectNode();
		Object root = rootNode;
		if (objectGraph.getObject() != null) {
			if (objectGraph.getRoot().isCollection() && !objectGraph.getNodes().isEmpty()) {
				ArrayNode array = mapper.createArrayNode();
				root = array;
				serializeCollection(mapper, array, objectGraph.getRoot(), objectGraph.getRoot().getName());
			} else if (!objectGraph.getRoot().isCollection() && !(objectGraph.getObject() instanceof Map)) {
				serializeObject(mapper, rootNode, objectGraph.getRoot(), objectGraph.getRoot().getName());
			} else {
				root = objectGraph.getObject();
			}
		}
		return root;
	}

	private void serializeCollection(ObjectMapper mapper, ArrayNode array, Node root, String currentPath) {
		int collectionLength = objectGraph.getCollectionLength(currentPath);
		for (int i=0; i < collectionLength; i++) {
			//For each collection item, have a node to represent this item.
			Node itemNode = root.getChild(indexedPath(i));
			//When a node represents a primitive value, not create an object for add in array, add object direct,
			//because if it is added, jackson will include the element as an object and not its primitive value
			if (itemNode.isPrimitive()) {
				array.addPOJO(itemNode.getValue());
			} else {
				ObjectNode objectNode = mapper.createObjectNode();
				array.add(objectNode);
				serializeObject(mapper, objectNode, itemNode, itemNode.getPath().getPath());
			}
		}
	}

	private void serializeObject(ObjectMapper mapper, ObjectNode objectNode, Node root, String currentPath) {
		boolean allowNull = mapper.getSerializationConfig().getSerializationInclusion() != JsonInclude.Include.NON_NULL;
		for (Node child : root.getChildren()) {
			String path = new StringJoiner(".").add(currentPath).add(child.getName()).toString();
			Object value = objectGraph.get(path); 
			if (child.isCollection()) {
				ArrayNode array = objectNode.putArray(child.getName());
				if (value != null) {
					serializeCollection(mapper, array, child, path);
				}
			} else if (!child.getChildren().isEmpty()) {
				if (value != null || (value == null && allowNull)) {
					ObjectNode nestedNode = objectNode.putObject(child.getName());
					serializeObject(mapper, nestedNode, child, path);
				}
			} else {
				if (value != null || (value == null && allowNull)) {
					objectNode.putPOJO(child.getName(), value);
				}
			}
		}
	}

	private String indexedPath(int index) {
		return String.format("[%d]", index);
	}
	
}
