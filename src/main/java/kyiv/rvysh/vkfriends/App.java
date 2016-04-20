package kyiv.rvysh.vkfriends;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		LOGGER.info("Starting");
		new ClassPathXmlApplicationContext("spring/spring-root.xml").registerShutdownHook();
		LOGGER.info("Started");
	}
}
