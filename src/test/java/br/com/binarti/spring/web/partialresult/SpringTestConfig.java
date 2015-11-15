package br.com.binarti.spring.web.partialresult;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "br.com.binarti.spring.web.partialresult")
public class SpringTestConfig {

	@Bean
	public PartialResultJSONMessageConverter partialResultJSONMessageConverter() {
		return new PartialResultJSONMessageConverter();
	}
	
}
