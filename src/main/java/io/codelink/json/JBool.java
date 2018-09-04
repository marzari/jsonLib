package io.codelink.json;

import static io.codelink.json.JType.BOOLEAN;

public class JBool extends AbstractJVal<Boolean> {
	
	public static final JBool TRUE = new JBool(true);
	
	public static final JBool FALSE = new JBool(false);

	private JBool(Boolean value) {
		super(value);
	}

	@Override
	public JType type() {
		return BOOLEAN;
	}
	
	@Override
	public Boolean bool() {
		return value;
	}

	@Override
	public String string() {
		return value.toString();
	}
}