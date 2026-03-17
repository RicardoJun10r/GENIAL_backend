package com.genial.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Requires PostgreSQL running with the configuration from application.properties")
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
