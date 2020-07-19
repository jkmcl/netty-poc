package poc.net;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class MessageMapperTests {

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Test
	void test() {
		Message req = new Message();
		req.setId("1");
		req.setType(Message.Type.BUSINESS);
		req.setSubtype(Message.Subtype.REQUEST);
		req.setContent("Hello world");

		MessageMapper mapper = new MessageMapper();
		String str = new MessageMapper().toString(req);
		log.info("Message: {}", str);

		Message res = mapper.fromString(str);
		assertEquals(req.getId(), res.getId());
		assertEquals(req.getType(), res.getType());
		assertEquals(req.getSubtype(), res.getSubtype());
		assertEquals(req.getContent(), res.getContent());
	}

}
