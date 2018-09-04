package io.codelink.json;

public class JNull implements JElement {

	public static final JNull NOTHING = new JNull();

	private JNull() {}

	@Override
	public JType type() {
		return JType.NULL;
	}

	public JNull asNull() {
		return NOTHING;
	}

	@Override
	public boolean isNull() {
		return true;
	}

}
