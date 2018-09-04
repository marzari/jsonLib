import static io.codelink.json.Json.addJsonBuilder;
import static io.codelink.json.Json.addObjectExtractor;
import static io.codelink.json.Json.asString;
import static io.codelink.json.Json.fromString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import io.codelink.json.JArray;
import io.codelink.json.JBool;
import io.codelink.json.JElement;
import io.codelink.json.JNull;
import io.codelink.json.JNumber;
import io.codelink.json.JObject;
import io.codelink.json.JStr;
import io.codelink.json.stream.JElementParser;
import io.codelink.json.stream.JElementSerializer;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

public class JsonTests {

	@Test
	public void testJsonParseFromString() throws Exception {
		String json = new String(Files.readAllBytes(Paths.get(getClass().getResource("test.json").toURI())));
		JElement e = parse(json);
		JObject root = e.asObject();		
		JObject cliente = root.object("cliente");
		assertTrue(root.bool("ativo"));
		assertEquals("Luciano \"Greiner", root.str("cliente.nome"));
		assertEquals(Integer.valueOf("-30"), (Integer) root.integer("cliente.idade"));
		assertEquals(new BigDecimal("500.50"), cliente.decimal("saldo"));
		
		JArray esportes = cliente.array("esportes");
		String snooker = esportes.str(1);
		
		assertEquals(snooker, "snooker");
		assertEquals((Integer) 3, (Integer) esportes.size());
		
		assertEquals(JObject.class, esportes.get(2).getClass());
	}
	
	@Test
	public void testParseJsonScalarValue() {
		JElement e = parse("500");
		assertEquals(JNumber.class, e.getClass());
		
		e = parse("\"500\"");
		assertEquals(JStr.class, e.getClass());
		
		e = parse("true");
		assertEquals(JBool.TRUE, e);
		
		e = parse("null");
		assertEquals(JNull.NOTHING, e);
	}
	

	@Test
	public void testObjectManipulation() {
		JObject o = new JObject();
		o.set("cliente.enumVal", "V1");
		o.set("cliente.bool", true);
		o.set("cliente.dataCriacao", LocalDateTime.now());
		
		Boolean bool = o.bool("cliente.bool");
		assertTrue(bool);
		
		TesteEnum e = o.get("cliente.enumVal", TesteEnum.class);
		assertEquals(TesteEnum.V1, e);
		
		o.set("cliente.enumVal", TesteEnum.V2);
		e = o.get("cliente.enumVal", TesteEnum.class);
		assertEquals(TesteEnum.V2, e);
		
		JArray a = new JArray();
		a.add(o);
		
		List<String> strs = a.strs("cliente.enumVal");
		
		assertEquals(1, strs.size());
		
	}
	
	@Test
	public void testJsonSerialization() throws IOException {
		JElementSerializer serializer = new JElementSerializer();
		
		JObject o = new JObject();
		o.set("cliente.nome", "Luciano eh \"O CARA\"");
		
		StringWriter writer = new StringWriter();
		serializer.serialize(o, writer);
		
		System.out.println(writer.toString());
	}
	
	private JElement parse(String json) {
		JElementParser parser = new JElementParser();
		char[] jsonData = json.toCharArray();
		return parser.parse(jsonData);
	}

	@BeforeClass
	public static void init() {
		addJsonBuilder(LocalDate.class, (LocalDate date) -> fromString.apply(date.toString()));
		addJsonBuilder(LocalDateTime.class, (LocalDateTime date) -> fromString.apply(date.toString()));
		addObjectExtractor(LocalDate.class, asString.andThen((String s) -> LocalDate.now()));
		addObjectExtractor(LocalDateTime.class, asString.andThen((String s) -> LocalDateTime.now()));
	}
	
	public static enum TesteEnum { V1, V2, V3 }
	
}
