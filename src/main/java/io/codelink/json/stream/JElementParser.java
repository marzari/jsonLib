package io.codelink.json.stream;

import java.io.Writer;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;
import org.json.XML;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

import io.codelink.json.JArray;
import io.codelink.json.JBool;
import io.codelink.json.JElement;
import io.codelink.json.JNull;
import io.codelink.json.JNumber;
import io.codelink.json.JObject;
import io.codelink.json.JStr;
import io.codelink.json.JVal;

public class JElementParser {

	public static final boolean XML_ATTRIBUTES_STRING = true;

	char[] charArray;

	private int __index;
	private char __currentChar;

	private int lastIndex;

	public JElement parse(String json) {
		char[] chars = json.toCharArray();
		lastIndex = chars.length - 1;
		__index = 0;
		charArray = chars;

		return decodeValue();
	}

	public JElement parse(char[] chars) {
		lastIndex = chars.length - 1;
		__index = 0;
		charArray = chars;

		return decodeValue();
	}

	protected JElement decodeValue() {
		JElement value = null;
		skipWhiteSpaceIfNeeded();
		switch (__currentChar) {
		case '"':
			value = new JStr(decodeString());
			break;
		case 't':
			value = decodeTrue();
			break;
		case 'f':
			value = decodeFalse();
			break;
		case 'n':
			value = decodeNull();
			break;
		case '[':
			value = decodeJsonArray();
			break;
		case '{':
			value = decodeJObject();
			break;
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
		case '-':
		case '+':
			value = decodeNumber();
			break;
		default:
			throw new IllegalArgumentException("Cannot parse element!");
		}
		return value;
	}

	protected JObject decodeJObject() {
		if (__currentChar == '{') {
			__index++;
		}
		JObject object = new JObject();
		for (; __index < this.charArray.length; __index++) {
			skipWhiteSpaceIfNeeded();
			if (__currentChar == '"') {
				String key = decodeString();
				skipWhiteSpaceIfNeeded();
				if (__currentChar != ':') {
					throw new IllegalArgumentException("expecting current character to be " + __currentChar + "\n");
				}

				__index++;
				skipWhiteSpaceIfNeeded();
				JElement value = decodeValue();
				skipWhiteSpaceIfNeeded();
				object.set(key, value);
			}
			if (__currentChar == '}') {
				__index++;
				break;
			} else if (__currentChar == ',') {
				continue;
			} else {
				throw new IllegalArgumentException("expecting '}' or ',' but got current char " + __currentChar);
			}
		}
		return object;
	}

	protected String decodeString() {
		char[] array = charArray;
		int index = __index;
		char c = array[index];
		if (index < array.length && c == '"') {
			index++;
		}

		boolean escape = false;
		StringBuilder str = new StringBuilder(32);

		for (; index < array.length; index++) {
			c = array[index];
			if (c == '"') {
				if (!escape) break;
				escape = false;
			} else if (c == 92 && array[index + 1] != '\"' && array[index + 2] != '\"') {
				//escape = true;
				str.append("\\\\");
				continue;
			} else if (c == 92 && array[index + 1] != '\"') {
				if (array.length > (index + 3) && array[index + 2] == '\"' && array[index + 3] == ',') {
					//escape = true;
					str.append("\\\\");
					continue;
				}
			} else if (c == 92 && array[index + 1] == '\"' && array[index + 2] == ',') {
				if (array.length > (index + 3) && array[index + 3] == '\"') {
					str.append("\\\\\"");
					index++;
					continue;
				}
			} else if (c == 92 && array[index + 1] == '\"' && array[index + 2] != ',') {
				str.append("\\\\\"");
				index++;
				continue;
			} else if (c == '\r') {
				str.append("\\r");
				continue;
			} else if (c == '\n') {
				str.append("\\n");
				continue;
			} else if (c == '\t') {
				str.append("\\t");
				continue;
			} else if (c == '\b') {
				str.append("\\b");
				continue;
			} else if (c == '\f') {
				str.append("\\f");
				continue;
			}
			str.append(c);
		}

		if (index < charArray.length) {
			index++;
		}
		__index = index;

		return str.toString();
	}

	private final JVal decodeNumber() {
		char[] array = charArray;
		int index = __index, decimalIdx = -1;
		char currentChar = array[index];

		boolean signed = false;
		boolean cientific = false;
		boolean first = true;
		int plusCount = 0;
		int startIndex = index;
		for (; index < array.length; index++) {
			currentChar = array[index];
			if (currentChar == '-') {
				if (signed || !first) throw new NumberFormatException("Number signed twice!");
				signed = true;
			} else if (currentChar == '+') {
				if (plusCount > 2 || (!cientific && signed) || (cientific && !first && plusCount == 1 && !signed)) throw new NumberFormatException("Number signed twice!");
				plusCount++;
				signed = true;
				if (!cientific) startIndex++;
			} else if (currentChar < 33 || isDelimiter(currentChar)) {
				index--;
				break;
			} else if (currentChar == '.') {
				if (decimalIdx > -1) throw new NumberFormatException("Decimal separator found twice!");
				decimalIdx = index;
			} else if (!Character.isDigit(currentChar)) {
				if (currentChar == 'e' || currentChar == 'E') {
					if (cientific) throw new NumberFormatException("Found invalid digit char!");
					cientific = true;
				} else {
					throw new NumberFormatException("Found invalid digit char!");
				}

			}
			first = false;
		}

		if (index < charArray.length) {
			index++;
		}
		__index = index;

		if (decimalIdx > -1) {
			return new JNumber(new BigDecimal(array, startIndex, (index - startIndex)));
		}

		String value = new String(array, startIndex, (index - startIndex));
		Long number = Long.valueOf(value);

		if (number > Integer.MAX_VALUE) {
			return new JNumber(number);
		}

		return new JNumber(number.intValue());
	}

	public static boolean isDelimiter(int c) {
		return c == ',' || c == '}' || c == ']';
	}

	protected final JArray decodeJsonArray() {
		JArray list = null;
		boolean foundEnd = false;
		char[] charArray = this.charArray;

		if (__currentChar == '[') {
			__index++;
		}
		skipWhiteSpaceIfNeeded();
		/* the list might be empty  */
		if (__currentChar == ']') {
			__index++;
			return JArray.EMPTY;
		}
		list = new JArray();
		char c;
		loop: while (this.hasMore()) {
			JElement arrayItem = decodeValue();
			list.add(arrayItem);
			while (true) {
				c = charArray[__index];
				if (c == ',') {
					__index++;
					continue loop;
				} else if (c == ']') {
					foundEnd = true;
					__index++;
					break loop;
				} else if (c <= 32) {
					__index++;
					continue;
				} else {
					break;
				}
			}
			c = charArray[__index];
			if (c == ',') {
				__index++;
				continue;
			} else if (c == ']') {
				__index++;
				foundEnd = true;
				break;
			} else {
				throw new IllegalArgumentException(String.format("expecting a ',' or a ']', " + " but got \nthe current character of  %s " + " on array index of %s \n", __currentChar, list.size()));
			}
		}

		if (!foundEnd) {
			throw new IllegalArgumentException("Did not find end of Json Array");
		}
		return list;
	}

	protected final JNull decodeNull() {

		if (__index + 4 <= charArray.length) {
			if (charArray[__index] == 'n' && charArray[++__index] == 'u' && charArray[++__index] == 'l' && charArray[++__index] == 'l') {
				__index++;
				return JNull.NOTHING;
			}
		}
		throw new IllegalArgumentException("null not parse properly");
	}

	protected final JBool decodeTrue() {
		if (__index + 4 <= charArray.length) { // 4 == true len
			if (charArray[__index] == 't' && charArray[++__index] == 'r' && charArray[++__index] == 'u' && charArray[++__index] == 'e') {

				__index++;
				return JBool.TRUE;
			}
		}
		throw new IllegalArgumentException("true not parsed properly");
	}

	protected final JBool decodeFalse() {
		if (__index + 5 <= charArray.length) {
			if (charArray[__index] == 'f' && charArray[++__index] == 'a' && charArray[++__index] == 'l' && charArray[++__index] == 's' && charArray[++__index] == 'e') {
				__index++;
				return JBool.FALSE;
			}
		}
		throw new IllegalArgumentException("false not parsed properly");
	}

	protected final void skipWhiteSpaceIfNeeded() {
		for (; __index < charArray.length; __index++) {
			this.__currentChar = this.charArray[__index];
			if (this.__currentChar > 32) break;
		}
	}

	protected final boolean hasMore() {
		return __index < lastIndex;
	}

	protected final boolean hasCurrent() {
		return __index <= lastIndex;
	}

	public JElement objectToJElement(Object object, String alias, Class clazz) {
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver() {
			public HierarchicalStreamWriter createWriter(Writer writer) {
				return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
			}
		});

		xstream.setMode(XStream.NO_REFERENCES);
		xstream.alias(alias, clazz);

		JElementParser parser = new JElementParser();
		JElement retorno = parser.parse(xstream.toXML(object).toCharArray());
		return retorno;
	}

	public JElement xmlToJElement(String xml) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		String json = xmlJSONObj.toString();
		JElement retorno = parse(json);
		return retorno;
	}

	public JElement xmlToJElement(String xml, JObject mapper, boolean keepString, boolean removeQuotes) {
		if (removeQuotes) {
			List<String> tags = new ArrayList<String>();
			tags.add("infCpl");
			for (String tag : tags) {
				xml = repair(xml, tag);
			}
		}
		return xmlToJElement(xml, mapper, keepString);
	}

	public static String repair(String xml, String tag) {
		Pattern pattern = Pattern.compile("<" + tag + ">(.*?)</" + tag + ">");
		Matcher m = pattern.matcher(xml);
		while (m.find()) {
			String escaped = m.group().replace("\"", "");
			xml = xml.replace(m.group(), "");
		}
		return xml;
	}

	public JElement xmlToJElement(String xml, boolean keepString) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml, keepString);
		String json = xmlJSONObj.toString();
		JElement retorno = parse(json);
		return retorno;
	}

	public JElement xmlToJElement(String xml, JObject mapper) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		String json = xmlJSONObj.toString();
		JElement retorno = parse(json);
		retorno = JObject.mergeWithMapper(mapper, retorno.asObject());
		return retorno;
	}

	public JElement xmlToJElement(String xml, JObject mapper, boolean keepString) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml, keepString);
		String json = xmlJSONObj.toString();
		JElement retorno = parse(json);
		retorno = JObject.mergeWithMapper(mapper, retorno.asObject());
		return retorno;
	}

	public JElement xmlToJElement(String xml, String jsonMapper) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml);
		String json = xmlJSONObj.toString();
		JElement retorno = parse(json);
		retorno = JObject.merge(json, retorno.asObject());
		return retorno;
	}

	public JElement xmlToJElement(String xml, String jsonMapper, boolean keepString) {
		JSONObject xmlJSONObj = XML.toJSONObject(xml, keepString);
		String json = xmlJSONObj.toString();
		JElement retorno = parse(json);
		retorno = JObject.merge(json, retorno.asObject());
		return retorno;
	}

	public String objectToJSON(Object object) {
		XStream xstream = new XStream(new JsonHierarchicalStreamDriver() {
			public HierarchicalStreamWriter createWriter(Writer writer) {
				return new JsonWriter(writer, JsonWriter.DROP_ROOT_MODE);
			}
		});
		return xstream.toXML(object);
	}
}
