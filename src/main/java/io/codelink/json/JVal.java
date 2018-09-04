package io.codelink.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface JVal extends JElement {

	default JNumber number() {
		throw new IllegalStateException("This JVal instance cannot be expressed as JNumber");
	}

	default String string() {
		throw new IllegalStateException("This JVal instance cannot be expressed as String");
	}

	default Boolean bool() {
		throw new IllegalStateException("This JVal instance cannot be expressed as Boolean");
	}

	default BigDecimal decimal() {
		throw new IllegalStateException("This JVal instance cannot be expressed as BigDecimal");
	}

	default BigInteger bigInteger() {
		throw new IllegalStateException("This JVal instance cannot be expressed as BigInteger");
	}

	default Integer integer() {
		throw new IllegalStateException("This JVal instance cannot be expressed as Integer");
	}

	default Long longint() {
		throw new IllegalStateException("This JVal instance cannot be expressed as Long");
	}

	default LocalDate date() {
		throw new IllegalStateException("This JVal instance cannot be expressed as LocalDate");
	}

	default LocalDateTime dateTime() {
		throw new IllegalStateException("This JVal instance cannot be expressed as LocalDateTime");
	}

	default byte[] byteArray() {
		throw new IllegalStateException("This JVal instance cannot be expressed as byte[]");
	}

	default Float floatValue() {
		throw new IllegalStateException("This JVal instance cannot be expressed as Float");
	}

	default Class classType() {
		throw new IllegalStateException("This JVal instance cannot be expressed as Class");
	}

	default Enum enumType() {
		throw new IllegalStateException("This JVal instance cannot be expressed as Enum");
	}

	default Double doubleValue() {
		throw new IllegalStateException("This JVal instance cannot be expressed as Double");
	}
}
