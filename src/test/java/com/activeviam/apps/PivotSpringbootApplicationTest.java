package com.activeviam.apps;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import static com.qfs.QfsWebUtils.url;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.PING_SUFFIX;
import static com.qfs.server.cfg.impl.ActivePivotRestServicesConfig.REST_API_URL_PREFIX;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
		classes = PivotSpringBootApplication.class,
		webEnvironment = WebEnvironment.RANDOM_PORT)
class PivotSpringbootApplicationTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@BeforeEach
	public void setupAuth() {
		restTemplate = restTemplate.withBasicAuth("admin", "admin");
	}

	@Test
	void contextLoads() {
	}

	@Test
	void activePivotPingReturnsPong() {
		var pingUrl = url("http://localhost:" + port, REST_API_URL_PREFIX, PING_SUFFIX);
		assertThat(this.restTemplate.getForObject(pingUrl, String.class)).contains("pong");
	}

}

