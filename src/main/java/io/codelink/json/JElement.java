package io.codelink.json;


public interface JElement {

	JType type();
	
	default boolean isArray() {
		return false;
	}
	
	default boolean isObject() {
		return false;
	}
	
	default boolean isVal() {
		return false;
	}
	
	default boolean isNull() {
		return false;
	}
	
	default JArray asArray() {
		throw new IllegalStateException("JElement is not a JArray instance!");
	}
	
	default JObject asObject() {
		throw new IllegalStateException("JElement is not a JObject instance!");
	}
	
	default JVal asVal() {
		throw new IllegalStateException("JElement is not a JValue instance!");
	}
	
	default JNull asNull() {
		throw new IllegalStateException("JElement is not an JNull instance!");
	}

}
