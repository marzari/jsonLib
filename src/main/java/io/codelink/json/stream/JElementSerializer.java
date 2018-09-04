package io.codelink.json.stream;

import io.codelink.json.JArray;
import io.codelink.json.JBool;
import io.codelink.json.JByte;
import io.codelink.json.JClass;
import io.codelink.json.JDate;
import io.codelink.json.JElement;
import io.codelink.json.JNull;
import io.codelink.json.JNumber;
import io.codelink.json.JObject;
import io.codelink.json.JStr;

import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;

public class JElementSerializer {

	private static final char[] EMPTY_OBJECT_CHARS = { '{', '}' };
	private static final char[] EMPTY_ARRAY_CHARS = { '[', ']' };

	public void serializeJObject(JObject instance, Writer writer) throws IOException {
		if (instance.size() == 0) {
			writer.write(EMPTY_OBJECT_CHARS);
			return;
		}
		writer.write('{');
		boolean hasPrevious = false;
		for (Entry<String, JElement> entry : instance) {
			if (hasPrevious) {
				writer.write(',');
			}
			serializeQuotedString(entry.getKey(), writer);
			writer.write(':');
			serialize(entry.getValue(), writer);
			hasPrevious = true;
		}
		;
		writer.write('}');
	}

	public void serializeJArray(JArray instance, Writer writer) throws IOException {
		if (instance.size() == 0) {
			writer.write(EMPTY_ARRAY_CHARS);
			return;
		}
		writer.write('[');
		boolean hasPrevious = false;
		for (JElement entry : instance) {
			if (hasPrevious) {
				writer.write(',');
			}
			serialize(entry, writer);
			hasPrevious = true;
		}
		writer.write(']');
	}

	public void serializeJNull(JNull instance, Writer writer) throws IOException {
		writer.write("null");
	}

	public void serializeJStr(JStr instance, Writer writer) throws IOException {
		serializeQuotedString(instance.string(), writer);
	}

	public void serializeJNumber(JNumber instance, Writer writer) throws IOException {
		writer.write(instance.string());
	}

	public void serializeJBool(JBool instance, Writer writer) throws IOException {
		writer.write(instance.string());
	}

	public void serializeJByte(JByte instance, Writer writer) throws IOException {
		writer.write(new String(instance.byteArray()));
	}

	private void serializeString(CharSequence seq, Writer writer) throws IOException {
		char c;
		int len = seq.length();
		for (int i = 0; i < len; i++) {
			c = seq.charAt(i);
			if (c == '"') {
				writer.write('\\');
			}
			writer.write(c);
		}
	}

	private void serializeQuotedString(CharSequence seq, Writer writer) throws IOException {
		writer.write('"');
		serializeString(seq, writer);
		writer.write('"');
	}

	private void serializeDate(JDate instance, Writer writer) throws IOException {
		try {
			writer.write(instance.date().toString());
		} catch (Exception e) {
			writer.write(instance.dateTime().toString());
		}
	}

	private void serializeClass(JClass instance, Writer writer) throws IOException {
		writer.write(instance.classType().toString());
	}

	public void serialize(JElement instance, Writer writer) throws IOException {
		switch (instance.type()) {
		case ARRAY:
			serializeJArray((JArray) instance, writer);
			break;
		case OBJECT:
			serializeJObject((JObject) instance, writer);
			break;
		case NUMBER:
			serializeJNumber((JNumber) instance, writer);
			break;
		case STRING:
			serializeJStr((JStr) instance, writer);
			break;
		case NULL:
			serializeJNull((JNull) instance, writer);
			break;
		case BOOLEAN:
			serializeJBool((JBool) instance, writer);
			break;
		case BYTEARRAY:
			serializeJByte((JByte) instance, writer);
			break;
		case DATE:
			serializeDate((JDate) instance, writer);
			break;
		case CLASS:
			serializeClass((JClass) instance, writer);
			break;
		}
	}

}
