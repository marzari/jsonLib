package io.codelink.json;

import static io.codelink.json.JType.OBJECT;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.codelink.json.stream.JElementParser;
import io.codelink.json.stream.JElementSerializer;

public class JObject implements JElement, Iterable<Entry<String, JElement>> {

	Map<String, JElement> properties;

	String alias;

	public JObject() {
		this(new LinkedHashMap<>());
	}

	protected JObject(Map<String, JElement> properties) {
		this.properties = properties;
	}

	public JObject(String alias) {
		this(new LinkedHashMap<>());
		this.alias = alias;
	}

	@Override
	public JType type() {
		return OBJECT;
	}

	@Override
	public JObject asObject() {
		return this;
	}

	public JType typeOf(String path) {
		return execute(path, false, (JObject o, String property) -> {
			JElement e = o.getLocal(property);
			return e == null ? null : e.type();
		}, null);
	}

	public boolean has(String path) {
		return execute(path, false, (JObject e, String property) -> e.properties.containsKey(property), false);
	}

	public boolean hasNull(String path) {
		return !has(path) || get(path) == null || get(path) instanceof JNull;
	}

	public boolean hasNotNull(String path) {
		return !hasNull(path);
	}

	public boolean empty(String path) {
		return hasNull(path) || str(path).equals("");
	}

	public boolean notEmpty(String path) {
		return !empty(path);
	}

	public JElement get(String path) {
		try {
			return execute(path, false, (JObject e, String property) -> e.getLocal(property), null);
		} catch (Exception e) {
			return null;
		}
	}

	public <T> T get(String path, Class<T> resultType) {
		try {
			JElement element = get(path);
			return Json.extract(element, resultType);
		} catch (Exception e) {
			return null;
		}
	}

	public Integer size() {
		return properties.size();
	}

	public <R> R get(String path, Function<? super JElement, R> extractor) {
		return extractor.apply(get(path));
	}

	public Boolean bool(String path) {
		return get(path, Boolean.class);
	}

	public String str(String path) {
		return get(path, String.class);
	}

	public Integer integer(String path) {
		return get(path, Integer.class);
	}

	public BigInteger bigInteger(String path) {
		return get(path, BigInteger.class);
	}

	public Long longint(String path) {
		return get(path, Long.class);
	}

	public BigDecimal decimal(String path) {
		return get(path, BigDecimal.class);
	}

	public Float floatValue(String path) {
		return get(path, Float.class);
	}

	public Double doubleValue(String path) {
		return get(path, Double.class);
	}

	public JVal val(String path) {
		return get(path, JVal.class);
	}

	public JObject object(String path) {
		try {
			return get(path, JObject.class);
		} catch (Exception e) {
			return null;
		}
	}

	public JArray array(String path) {
		return get(path, JArray.class);
	}

	public LocalDate date(String path) {
		return get(path, LocalDate.class);
	}

	public LocalDateTime dateTime(String path) {
		return get(path, LocalDateTime.class);
	}

	public byte[] byteArray(String path) {
		return get(path, byte[].class);
	}

	public Class getClass(String path) {
		return get(path, Class.class);
	}

	public JObject set(String path, Object element) {
		JElement je = Json.build(element);
		return execute(path, true, (JObject e, String property) -> {
			e.setLocal(property, je);
			return JObject.this;
		}, this);
	}

	@Override
	public Iterator<Entry<String, JElement>> iterator() {
		return properties.entrySet().iterator();
	}

	public JObject delete(String property) {
		properties.remove(property);
		return this;
	}

	private <R> R execute(String path, boolean create, BiFunction<JObject, String, R> action, R otherwise) {
		if (path == null) {
			return otherwise;
		}
		int dotIndex = path.indexOf('.');
		if (dotIndex > -1) {
			String property = path.substring(0, dotIndex);
			JElement e = getLocal(property);
			if (e == null && create) {
				e = new JObject();
				this.setLocal(property, e);
			}
			if (e != null) {
				if (e.type() == OBJECT) {
					JObject o = e.asObject();
					return o.execute(path.substring(dotIndex + 1), create, action, otherwise);
				}
				throw new IllegalArgumentException(property + " is not a JObject instance");
			}
			return otherwise;
		}
		return action.apply(this, path);
	}

	public static JObject merge(JObject... objects) {
		JObject object = new JObject();
		for (JObject o : objects) {
			for (Entry<String, JElement> e : o) {
				object.set(e.getKey(), e.getValue());
			}
		}
		return object;
	}

	public static JObject mergeWithMapper(JObject mapper, JObject object) {
		for (Entry<String, JElement> ent : mapper) {
			if (ent.getValue() instanceof JObject) {
				if (object.hasNotNull(ent.getKey())) {
					mergeWithMapper(ent.getValue().asObject(), object.get(ent.getKey()).asObject());
					if (ent.getValue().asObject().alias != null && !ent.getKey().equals(ent.getValue().asObject().alias)) {
						mapper.set(ent.getValue().asObject().alias, mapper.get(ent.getKey()));
						mapper.delete(ent.getKey());
					}
				}
			} else if (ent.getValue() instanceof JArray) {
				if (ent.getValue().asArray().size() > 0) { //existe mapper
					if (object.get(ent.getKey()).isArray()) {
						mapper.set(ent.getKey(), mergeJarrayObjects(object.get(ent.getKey()).asArray(), ent.getValue().asArray().get(0).asObject()));
					} else {
						mapper.set(ent.getKey(), merge(ent.getValue().asArray().get(0).asObject(), object.get(ent.getKey()).asObject()));
					}
				} else {
					mapper.set(ent.getKey(), object.get(ent.getKey()).asArray());
				}
			} else {
				if (mapper.hasNotNull(ent.getKey()) && !mapper.str(ent.getKey()).equals("")) {
					mapper.set(mapper.str(ent.getKey()), object.get(ent.getKey()));
					mapper.delete(ent.getKey());
				} else {
					mapper.set(ent.getKey(), object.get(ent.getKey()));
				}
			}
		}
		return mapper;
	}

	private static JArray mergeJarrayObjects(JArray jArrayObject, JObject mapper) {
		JArray retorno = new JArray();
		for (JElement el : jArrayObject) {
			JObject object = merge(mapper.clone(), el.asObject());
			retorno.add(object);
		}
		return retorno;
	}

	public static JObject merge(String json, JObject object) {
		JObject mapper = new JElementParser().parse(json.toCharArray()).asObject();
		for (Entry<String, JElement> ent : mapper) {
			if (ent.getValue() instanceof JObject) {
				if (object.hasNotNull(ent.getKey())) {
					merge(ent.getValue().asObject(), object.get(ent.getKey()).asObject());
				}
			} else {
				if (!mapper.str(ent.getKey()).equals("")) {
					mapper.set(mapper.str(ent.getKey()), object.get(ent.getKey()));
					mapper.delete(ent.getKey());
				} else {
					mapper.set(ent.getKey(), object.get(ent.getKey()));
				}
			}
		}
		return mapper;
	}

	public static JObject mergeWithTemplate(JObject template, JObject dados) {
		for (Entry<String, JElement> e : template) {
			if (dados.hasNotNull(e.getKey()) && !dados.empty(e.getKey())) {
				template.set(e.getKey(), dados.get(e.getKey()));
			}
		}
		return template;
	}

	private JElement getLocal(String property) {
		return properties.get(property);
	}

	private void setLocal(String property, JElement element) {
		properties.put(property, element);
	}

	@Override
	public String toString() {
		JElementSerializer serializer = new JElementSerializer();
		StringWriter archive = null;
		try {
			archive = new StringWriter();
			serializer.serialize(this, archive);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return archive.toString();
	}

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public JObject clone() {
		return new JElementParser().parse(this.toString()).asObject();
	}

	/**
	 * Método responsável por validar as chaves existentes dentro do JObject
	 *
	 * @param chaves
	 * 			Chaves a serem validadas
	 * @throws JObjectParameterException
	 */
	public void validate(String... chaves) throws JObjectParameterException {
		for (String chave : chaves) {
			if (!has(chave)) {
				throw new JObjectParameterException(chave + ": chave não informada!");
			}
		}
	}

	/**
	 * Método responsável por validar as chaves existentes dentro do JObject
	 *
	 * @param chaves
	 * 			Chaves a serem validadas
	 * @throws JObjectParameterException
	 */
	public void validate(JArray chaves) throws JObjectParameterException {
		for (JElement chaveElement : chaves) {
			String chave = chaveElement.asVal().string();
			if (!has(chave)) {
				throw new JObjectParameterException(chave + ": chave não informada!");
			}
		}
	}

}
