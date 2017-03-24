package com.redhat.demo;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.springframework.stereotype.Component;

@Component
public class HL7TestRoute extends RouteBuilder {

	
	
	private RouteDefinition beanRef;

	@Override
	public void configure() throws Exception {
		from("hl7NettyListener")
			.log("${body}")
			.bean("hl7Acknowledge");
	}
}
