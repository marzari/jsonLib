package io.codelink.json;

import static io.codelink.json.JNumType.BIGINTEGER;
import static io.codelink.json.JNumType.DECIMAL;
import static io.codelink.json.JNumType.DOUBLE;
import static io.codelink.json.JNumType.FLOAT;
import static io.codelink.json.JNumType.INTEGER;
import static io.codelink.json.JNumType.LONG;
import static io.codelink.json.JType.NUMBER;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JNumber extends AbstractJVal<Number> {

	private JNumType numberType;

	public JNumber(BigDecimal value) {
		super(value);
		numberType = DECIMAL;
	}

	public JNumber(BigInteger value) {
		super(value);
		numberType = BIGINTEGER;
	}

	public JNumber(Integer value) {
		super(value);
		numberType = INTEGER;
	}

	public JNumber(Long value) {
		super(value);
		numberType = LONG;
	}

	public JNumber(Float value) {
		super(value);
		numberType = FLOAT;
	}

	public JNumber(Double value) {
		super(value);
		numberType = DOUBLE;
	}

	@Override
	public JType type() {
		return NUMBER;
	}

	@Override
	public Integer integer() {
		switch (numberType) {
		case INTEGER:
			return (Integer) value;
		default:
			return value.intValue();
		}
	}

	@Override
	public BigInteger bigInteger() {
		switch (numberType) {
		case BIGINTEGER:
			return (BigInteger) value;
		default:
			return new BigInteger(value.toString());
		}
	}

	@Override
	public BigDecimal decimal() {
		switch (numberType) {
		case DECIMAL:
			return (BigDecimal) value;
		default:
			return new BigDecimal(value.longValue());
		}
	}

	@Override
	public Long longint() {
		switch (numberType) {
		case LONG:
			return (Long) value;
		default:
			return value.longValue();
		}
	}

	@Override
	public Float floatValue() {
		switch (numberType) {
		case FLOAT:
			return (Float) value;
		default:
			return new Float(value.floatValue());
		}
	}

	@Override
	public Double doubleValue() {
		switch (numberType) {
		case DOUBLE:
			return (Double) value;
		default:
			return new Double(value.doubleValue());
		}
	}

	@Override
	public String string() {
		return value.toString();
	}

	@Override
	public Boolean bool() {
		if (value.toString().equals("1")) {
			return Boolean.TRUE;
		} else if (value.toString().equals("0")) {
			return Boolean.FALSE;
		}
		return Boolean.valueOf(value.toString());
	}

	public JNumType numberType() {
		return numberType;
	}

	@Override
	public JNumber number() {
		return this;
	}

}
