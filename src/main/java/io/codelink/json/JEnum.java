package io.codelink.json;

import static io.codelink.json.JType.ENUM;

public class JEnum extends AbstractJVal<Enum> {

	public JEnum(Enum e) {
		super(e);
	}

	@Override
	public JType type() {
		return ENUM;
	}

	public Enum enumType() {
		return value;
	}
}
