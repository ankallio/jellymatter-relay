package io.kall.jellymatter.jellymatterrelay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class JellymatterRelayApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(JellymatterRelayApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(JellymatterRelayApplication.class);
	}
}
