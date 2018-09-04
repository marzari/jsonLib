package io.codelink.json;

public class JByte extends AbstractJVal<byte[]> {

	public JByte(byte[] value) {
		super(value);
	}
	
	@Override
	public JType type() {
		return JType.BYTEARRAY;
	}
	
	@Override
	public byte[] byteArray() {
		return value;
	}
}
