package com.kkt.learnings.camel.examples;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;



public class CamelSend {

	public static void main(String[] args) throws Exception {

		CamelContext context=new DefaultCamelContext();
		ConnectionFactory connection = new ActiveMQConnectionFactory("tcp://localhost:61616");
		context.addComponent("test-jms", JmsComponent.jmsComponentAutoAcknowledge(connection));
		context.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:vinRq").process(new Processor() {
					@Override
					public void process(Exchange exchange) throws Exception {
						System.out.println("Exchange is:"+exchange.getIn().getBody());
					}
				}).to("activemq:queue:outbox");
				
				/*from("activemq:queue:outbox").tracing().process(new Processor() {
					@Override
					public void process(Exchange arg0) throws Exception {
						System.out.println(arg0.getIn().getBody());
					}
				}).tracing().to("file:data/outbox2").to("cxf://http://localhost:8080/allcxfwebservices/firstwebservice?serviceClass=kkt.self.learn.cxf.interfaces.CustomerSearch");
				*/
				/*from("activemq:queue:outbox").tracing().process(new Processor() {
					@Override
					public void process(Exchange arg0) throws Exception {
						System.out.println(arg0.getIn().getBody());
					}
				}).tracing().to("file:data/outboxtest").to("cxf://http://localhost:8080/allcxfwebservices/firstwebservice?dataFormat=MESSAGE&serviceName=firstwebservice&portName=CustomerSearchImplementationPort");	*/
			}
		});
		
		context.start();
		String id="123";
		
		ProducerTemplate temp=context.createProducerTemplate();
		Map<String, Object> properties = new HashMap<String, Object>();
		temp.sendBody("direct:vinRq",id);
		//temp.sendBody("bean:com.learn.beans.JAXBRouteBean", veh)
		ConsumerTemplate cons=context.createConsumerTemplate();
		cons.start();
		System.out.println("context started");
		Thread.sleep(2000);
		System.out.println("Context stopped");
		//cons.stop();
		//context.stop();
	
	}

}
