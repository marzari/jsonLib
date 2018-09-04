package io.codelink.json;

import static io.codelink.json.JType.CLASS;

public class JClass extends AbstractJVal<Class> {

	public JClass(Class c) {
		super(c);
	}

	@Override
	public JType type() {
		return CLASS;
	}

	public Class classType() {
		return value;
	}
}
