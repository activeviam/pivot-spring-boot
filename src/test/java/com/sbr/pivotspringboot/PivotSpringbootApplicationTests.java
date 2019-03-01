package com.sbr.pivotspringboot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PivotSpringbootApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setupAuth() {
		restTemplate = restTemplate.withBasicAuth("admin", "admin");
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void helloController() {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/hello",
				String.class)).contains("Hello from ActivePivotSpringBoot!");
	}

	@Test
	public void activePivotPingReturnsPong() {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/pivot/rest/v4/ping",
				String.class)).contains("pong");
	}

}

