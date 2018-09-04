package io.codelink.json;

import static io.codelink.json.JBool.FALSE;
import static io.codelink.json.JBool.TRUE;
import static io.codelink.json.JNull.NOTHING;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;

public class Json {

	public static Function<JElement, JObject> asObject = (JElement e) -> nullSafeGet(e, e::asObject);
	public static Function<JElement, JArray> asArray = (JElement e) -> nullSafeGet(e, e::asArray);
	private static Function<JElement, JVal> asVal = (JElement e) -> nullSafeGet(e, e::asVal);
	public static Function<JElement, String> asString = asVal.andThen((JVal v) -> v == null ? null : v.string());
	public static Function<JElement, Integer> asInt = asVal.andThen((JVal v) -> v == null ? null : v.integer());
	public static Function<JElement, Long> asLong = asVal.andThen((JVal v) -> v == null ? null : v.longint());
	public static Function<JElement, BigInteger> asBigInteger = asVal.andThen((JVal v) -> v == null ? null : v.bigInteger());
	public static Function<JElement, BigDecimal> asDecimal = asVal.andThen((JVal v) -> v == null ? null : v.decimal());
	public static Function<JElement, Boolean> asBool = asVal.andThen((JVal v) -> v == null ? null : v.bool());
	public static Function<JElement, LocalDate> asLocalDate = asVal.andThen((JVal v) -> v == null ? null : v.date());
	public static Function<JElement, LocalDateTime> asLocalDateTime = asVal.andThen((JVal v) -> v == null ? null : v.dateTime());
	public static Function<JElement, byte[]> asByteArray = asVal.andThen((JVal v) -> v == null ? null : v.byteArray());
	public static Function<JElement, Float> asFloat = asVal.andThen((JVal v) -> v == null ? null : v.floatValue());
	public static Function<JElement, Class> asClass = asVal.andThen((JVal v) -> v == null ? null : v.classType());
	public static Function<JElement, Enum> asEnum = asVal.andThen((JVal v) -> v == null ? null : v.enumType());
	public static Function<JElement, Double> asDouble = asVal.andThen((JVal v) -> v == null ? null : v.doubleValue());

	public static Function<Map<?, ?>, JElement> fromMap = (Map<?, ?> m) -> nullSafeSet(m, () -> {
		JObject o = new JObject();
		m.entrySet().forEach((Entry<?, ?> e) -> o.set(e.getKey().toString(), build(e.getValue())));
		return o;
	});

	public static Function<Collection<?>, JElement> fromCollection = (Collection<?> c) -> nullSafeSet(c, () -> {
		JArray a = new JArray();
		c.forEach((Object i) -> a.add(build(i)));
		return a;
	});

	public static Function<Object, JElement> fromString = (Object s) -> s == null ? NOTHING : new JStr(s.toString());
	public static Function<Object, JElement> fromByte = (Object s) -> s == null ? NOTHING : new JByte((byte[]) s);
	public static Function<Float, JElement> fromFloat = (Float n) -> n == null ? NOTHING : new JNumber(n);

	public static Function<BigInteger, JElement> fromBigInteger = (BigInteger n) -> n == null ? NOTHING : new JNumber(n);
	public static Function<BigDecimal, JElement> fromDecimal = (BigDecimal n) -> n == null ? NOTHING : new JNumber(n);
	public static Function<Double, JElement> fromDouble = (Double n) -> n == null ? NOTHING : new JNumber(n);

	public static Function<Long, JElement> fromLong = (Long n) -> n == null ? NOTHING : new JNumber(n);
	public static Function<Integer, JElement> fromInteger = (Integer n) -> n == null ? NOTHING : new JNumber(n);
	public static Function<Boolean, JElement> fromBool = (Boolean b) -> b == null ? NOTHING : b ? TRUE : FALSE;

	public static Function<LocalDate, JElement> fromLocalDate = (LocalDate d) -> d == null ? NOTHING : new JDate(d);
	public static Function<LocalDateTime, JElement> fromLocalDateTime = (LocalDateTime dt) -> dt == null ? NOTHING : new JDate(dt);

	private static Map<Class<?>, Function<JElement, ?>> extractors = new HashMap<>();
	private static Map<Class<?>, Function<?, JElement>> factories = new HashMap<>();

	public static Function<Class, JElement> fromClass = (Class c) -> c == null ? NOTHING : new JClass(c);
	public static Function<Enum, JElement> fromEnum = (Enum e) -> e == null ? NOTHING : new JEnum(e);

	static {
		addObjectExtractorInternal(JObject.class, asObject);
		addObjectExtractorInternal(JArray.class, asArray);
		addObjectExtractorInternal(JVal.class, asVal);
		addObjectExtractorInternal(String.class, asString);
		addObjectExtractorInternal(Integer.class, asInt);
		addObjectExtractorInternal(Long.class, asLong);
		addObjectExtractorInternal(BigDecimal.class, asDecimal);
		addObjectExtractorInternal(BigInteger.class, asBigInteger);
		addObjectExtractorInternal(Boolean.class, asBool);
		addObjectExtractorInternal(LocalDate.class, asLocalDate);
		addObjectExtractorInternal(LocalDateTime.class, asLocalDateTime);
		addObjectExtractorInternal(byte[].class, asByteArray);
		addObjectExtractorInternal(Float.class, asFloat);
		addObjectExtractorInternal(Class.class, asClass);
		addObjectExtractorInternal(Enum.class, asEnum);
		addObjectExtractorInternal(Double.class, asDouble);

		addJsonBuilderInternal(HashMap.class, fromMap);
		addJsonBuilderInternal(LinkedHashMap.class, fromMap);
		addJsonBuilderInternal(TreeMap.class, fromMap);
		addJsonBuilderInternal(ArrayList.class, fromCollection);
		addJsonBuilderInternal(LinkedList.class, fromCollection);
		addJsonBuilderInternal(HashSet.class, fromCollection);
		addJsonBuilderInternal(LinkedHashSet.class, fromCollection);
		addJsonBuilderInternal(TreeSet.class, fromCollection);

		addJsonBuilderInternal(String.class, fromString);
		addJsonBuilderInternal(Integer.class, fromInteger);
		addJsonBuilderInternal(Long.class, fromLong);
		addJsonBuilderInternal(BigInteger.class, fromBigInteger);
		addJsonBuilderInternal(BigDecimal.class, fromDecimal);
		addJsonBuilderInternal(Double.class, fromDouble);
		addJsonBuilderInternal(Boolean.class, fromBool);
		addJsonBuilderInternal(LocalDate.class, fromLocalDate);
		addJsonBuilderInternal(LocalDateTime.class, fromLocalDateTime);
		addJsonBuilderInternal(byte[].class, fromByte);
		addJsonBuilderInternal(Float.class, fromFloat);
		addJsonBuilderInternal(Class.class, fromClass);
		addJsonBuilderInternal(Enum.class, fromEnum);
	}

	private static void addObjectExtractorInternal(Class<?> returnType, Function<JElement, ?> extractor) {
		extractors.put(returnType, extractor);
	}

	public static void addObjectExtractor(Class<?> returnType, Function<JElement, ?> extractor) {
		if (returnType == null || extractor == null) {
			throw new IllegalArgumentException("Neither returnType or extractor can be null!");
		}
		if (returnType == String.class || returnType == Integer.class || returnType == Long.class || returnType == BigDecimal.class || returnType == Boolean.class || returnType == JObject.class || returnType == JArray.class) {
			throw new IllegalArgumentException("Cannot overwrite default " + returnType.getName() + " extractor");
		}
		addObjectExtractorInternal(returnType, extractor);
	}

	private static <T, C extends T> void addJsonBuilderInternal(Class<C> objectType, Function<T, JElement> factory) {
		factories.put(objectType, factory);
	}

	public static <T> void addJsonBuilder(Class<T> objectType, Function<T, JElement> builder) {
		if (objectType == null || builder == null) {
			throw new IllegalArgumentException("Neither objectType or factory can be null!");
		}
		if (objectType == String.class || objectType == Integer.class || objectType == Long.class || objectType == BigDecimal.class || objectType == Boolean.class || objectType == HashMap.class || objectType == LinkedHashMap.class || objectType == TreeMap.class || objectType == ArrayList.class || objectType == LinkedList.class || objectType == HashSet.class || objectType == LinkedHashSet.class || objectType == byte[].class || objectType == TreeSet.class) {
			throw new IllegalArgumentException("Cannot overwrite default " + objectType.getName() + " factory");
		}
		addJsonBuilderInternal(objectType, builder);
	}

	@SuppressWarnings("unchecked")
	public static <T> Function<JElement, T> getExtractor(Class<T> returnType) {
		return (Function<JElement, T>) extractors.get(returnType);
	}

	private static Function<?, JElement> getFactory(Class<?> objectType) {
		return factories.get(objectType);
	}

	protected static <T extends JElement, R> R nullSafeGet(T val, Supplier<R> action) {
		return val == null ? null : action.get();
	}

	protected static JElement nullSafeSet(Object val, Supplier<? extends JElement> action) {
		return val == null ? JNull.NOTHING : action.get();
	}

	@SuppressWarnings("unchecked")
	protected static JElement build(Object element) {
		if (element == null) return JNull.NOTHING;

		Class<?> clazz = element.getClass();
		if (JElement.class.isAssignableFrom(clazz)) return (JElement) element;

		if (clazz.isEnum()) {
			return fromString.apply(element);
		}

		Function<Object, JElement> factory = (Function<Object, JElement>) getFactory(element.getClass());
		if (factory != null) {
			return factory.apply(element);
		}
		throw new IllegalArgumentException("No Json Factory for object of type " + element.getClass().getName());
	}

	public static <T extends Enum<T>> T fromEnum(Class<T> clz, String value) {
		return Enum.valueOf(clz, value.trim());
	}

	protected static <T> T extract(JElement element, Class<T> elementType) {
		if (element == JNull.NOTHING) return null;

		if (elementType.isEnum()) {
			String name = asString.apply(element);
			if (name != null) {
				for (T e : elementType.getEnumConstants()) {
					if (name.equals(e.toString())) {
						return e;
					}
				}
			}
			throw new IllegalArgumentException("No enum constant " + elementType.getCanonicalName() + "." + name);
		}

		Function<JElement, T> c = getExtractor(elementType);
		if (c != null) {
			return c.apply(element);
		}
		throw new IllegalArgumentException("Cannot extract " + elementType.getName() + " from JElement!");
	}

	public static JObject synchronizedJObject() {
		return new JObject(Collections.synchronizedMap(new LinkedHashMap<>()));
	}

	public static JArray synchronizedJArray() {
		return new JArray(Collections.synchronizedList(new ArrayList<>()));
	}
}
