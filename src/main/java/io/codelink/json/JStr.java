package io.codelink.json;

import static io.codelink.json.JType.STRING;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class JStr extends AbstractJVal<String> {

	private static DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	private static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
	private static DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	public JStr(String value) {
		super(value);
	}

	@Override
	public JType type() {
		return STRING;
	}

	@Override
	public String string() {
		return value;
	}

	@Override
	public Integer integer() {
		return Integer.valueOf(value);
	}

	@Override
	public Long longint() {
		return Long.valueOf(value);
	}

	@Override
	public BigDecimal decimal() {
		return new BigDecimal(value);
	}

	@Override
	public Boolean bool() {
		if (value.equals("1")) {
			return Boolean.TRUE;
		} else if (value.equals("0")) {
			return Boolean.FALSE;
		}
		return Boolean.valueOf(value);
	}

	@Override
	public LocalDate date() {
		return LocalDate.parse(value, dateFormatter);
	}

	@Override
	public LocalDateTime dateTime() {
		try {
			return LocalDateTime.parse(value, dateTimeFormatter);
		} catch (DateTimeParseException e) {
			return LocalDateTime.parse(value, dateTimeFormatter2);
		}
	}

}
