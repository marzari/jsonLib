package io.codelink.json;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.codelink.json.stream.JElementSerializer;

public class JArray implements JElement, Iterable<JElement> {

	public static final JArray EMPTY = new JArray() {
		@Override
		public JArray add(Object element) {
			throw new IllegalStateException("Cannot add elements into EMPTY JArray!");
		}
	};

	private final List<JElement> items;

	public JArray() {
		this(new LinkedList<JElement>());
	}

	public JArray(Collection<JElement> items) {
		this(new LinkedList<JElement>(items));
	}

	protected JArray(List<JElement> items) {
		this.items = items;
	}

	@Override
	public JType type() {
		return JType.ARRAY;
	}

	@Override
	public Iterator<JElement> iterator() {
		return items.iterator();
	}

	public Integer size() {
		return items.size();
	}

	public Boolean isEmpty() {
		return (items == null || items.size() == 0) ? true : false;
	}

	public JElement get(Integer index) {
		return items.get(index);
	}

	public <T> T get(Integer index, Class<T> returnType) {
		return Json.extract(get(index), returnType);
	}

	public void set(Integer index, Object element) {
		JElement je = Json.build(element);
		items.set(index, je);
	}

	public JArray add(Object element) {
		items.add(Json.build(element));
		return this;
	}

	public JArray addAll(Object... elements) {
		for (Object e : elements) {
			add(e);
		}
		return this;
	}

	public JArray addAll(JArray array) {
		for (JElement e : array) {
			add(e);
		}
		return this;
	}

	public JArray filter(Predicate<? super JElement> predicate) {
		return new JArray(items.stream().filter(predicate).collect(Collectors.toList()));
	}

	public JArray limit(int limit) {
		return new JArray(items.stream().limit(limit).collect(Collectors.toList()));
	}

	public JArray sort(Comparator<JElement> comp) {
		items.sort(comp);
		return new JArray(items);
	}

	public <T> Predicate<T> distinctByKey(Function<? super JElement, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply((JElement) t));
	}

	public JArray distinctByStrKey(String key) {
		return new JArray(items.stream().filter(distinctByKey(f -> f.asObject().str(key))).collect(Collectors.toList()));
	}

	public <T> List<T> list(Class<T> elementType) {
		return list(elementType, (List<T> list, JElement e) -> {
			list.add(Json.extract(e, elementType));
		});
	}

	public <T> List<T> list(String path, Class<T> elementType) {
		return list(elementType, (List<T> list, JElement e) -> {
			list.add(Json.extract(e.asObject().get(path), elementType));
		});
	}

	private <T> List<T> list(Class<T> elementType, BiConsumer<List<T>, JElement> consumer) {
		if (this.size() == null) return Collections.emptyList();

		List<T> list = new ArrayList<T>(this.size());
		this.forEach((JElement e) -> {
			consumer.accept(list, e);
		});

		return list;
	}

	public List<Boolean> bools() {
		return list(Boolean.class);
	}

	public List<String> strs() {
		return list(String.class);
	}

	public List<Integer> integers() {
		return list(Integer.class);
	}

	public List<Long> longints() {
		return list(Long.class);
	}

	public List<BigDecimal> decimals() {
		return list(BigDecimal.class);
	}

	public List<JObject> objects() {
		return list(JObject.class);
	}

	public List<JArray> arrays() {
		return list(JArray.class);
	}

	public List<Boolean> bools(String path) {
		return list(path, Boolean.class);
	}

	public List<String> strs(String path) {
		return list(path, String.class);
	}

	public List<Integer> integers(String path) {
		return list(path, Integer.class);
	}

	public List<Long> longints(String path) {
		return list(path, Long.class);
	}

	public List<BigDecimal> decimals(String path) {
		return list(path, BigDecimal.class);
	}

	public List<JObject> objects(String path) {
		return list(path, JObject.class);
	}

	public List<JArray> arrays(String path) {
		return list(path, JArray.class);
	}

	public Boolean bool(Integer index) {
		return get(index, Boolean.class);
	}

	public String str(Integer index) {
		return get(index, String.class);
	}

	public Integer integer(Integer index) {
		return get(index, Integer.class);
	}

	public Long longint(Integer index) {
		return get(index, Long.class);
	}

	public BigDecimal decimal(Integer index) {
		return get(index, BigDecimal.class);
	}

	public JObject object(Integer index) {
		return get(index, JObject.class);
	}

	public JArray array(Integer index) {
		return get(index, JArray.class);
	}

	public JArray delete(Integer index) {
		items.remove(index.intValue());
		return this;
	}

	@Override
	public JArray asArray() {
		return this;
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
	public boolean isArray() {
		return true;
	}

	public BigDecimal totalizaPorCampo(String campoDeCriterioDeTotalizacao) {
		BigDecimal valorFinal = new BigDecimal(0);

		for (JElement objeto : this) {
			BigDecimal auxiliar = objeto.asObject().decimal(campoDeCriterioDeTotalizacao);
			valorFinal = valorFinal.add(auxiliar);
		}
		return valorFinal;
	}

	public JArray slice(int posicaoInicial, int posicaoFinal) {
		return new JArray(this.items.subList(posicaoInicial, posicaoFinal));
	}

	//TODO: por enquanto apenas string
	//Ajustar para outros casos
	public Boolean contains(Object obj) {
		for (JElement objeto : this) {
			if (objeto.isObject() && objeto.asObject().toString().equals(obj.toString())) {
				return true;
			} else if (objeto.asVal().string().equals(obj.toString())) {
				return true;
			}
		}
		return false;
	}
}
