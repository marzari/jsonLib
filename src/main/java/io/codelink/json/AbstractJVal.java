package io.codelink.json;


public abstract class AbstractJVal<T> implements JVal {
	
	protected T value;

	protected AbstractJVal(T value) {
		if(value == null) throw new IllegalArgumentException("JVal cannot hold null. Use JNull instead!");
		this.value = value;
	}

	@Override
	public JVal asVal() {
		return this;
	}
	
	@Override
	public boolean isVal() {
		return true;
	}
	
}
