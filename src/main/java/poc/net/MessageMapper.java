package poc.net;

import poc.net.Message.Subtype;
import poc.net.Message.Type;

/**
 * <pre>
 * ID:       6 digit
 * Type:     1 char
 * Subtype:  1 digit
 * Content: 32 char
 * </pre>
 */
public class MessageMapper {

	public Message fromString(String s) {
		Message m = new Message();
		m.setId(s.substring(0, 6).trim());
		m.setType(Type.fromString(s.substring(6, 7).trim()));
		m.setSubtype(Subtype.fromString(s.substring(7, 8).trim()));
		m.setContent(s.substring(8, 40).trim());
		return m;
	}

	public String toString(Message m) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-6s", m.getId()));
		sb.append(String.format("%-1s", m.getType().toString()));
		sb.append(String.format("%-1s", m.getSubtype().toString()));
		sb.append(String.format("%-32s", m.getContent()));
		return sb.toString();
	}

}
