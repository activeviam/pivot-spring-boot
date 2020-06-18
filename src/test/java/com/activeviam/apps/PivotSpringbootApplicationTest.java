package com.activeviam.apps;

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
@SpringBootTest(classes = PivotSpringBootApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class PivotSpringbootApplicationTest {

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
	public void activePivotPingReturnsPong() {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/pivot/rest/v5/ping",
				String.class)).contains("pong");
	}

}

