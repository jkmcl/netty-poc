package poc.net;

public class Message {

	public static final int SIZE_IN_BYTES = 40;

	public enum Type {
		SYSTEM("S"), BUSINESS("B");

		private String value;

		private Type(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static Type fromString(String s) {
			for (Type t : values()) {
				if (t.value.equals(s)) {
					return t;
				}
			}
			throw new IllegalArgumentException("Invalid value: " + s);
		}
	}

	public enum Subtype {
		REQUEST("0"), RESPONSE("1");

		private String value;

		private Subtype(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return this.value;
		}

		public static Subtype fromString(String s) {
			for (Subtype t : values()) {
				if (t.value.equals(s)) {
					return t;
				}
			}
			throw new IllegalArgumentException("Invalid value: " + s);
		}
	}

	private String id;

	private Type type;

	private Subtype subtype;

	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Subtype getSubtype() {
		return subtype;
	}

	public void setSubtype(Subtype subtype) {
		this.subtype = subtype;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
