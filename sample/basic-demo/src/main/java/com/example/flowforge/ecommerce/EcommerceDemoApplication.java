package com.example.flowforge.ecommerce;

import com.example.flowforge.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceDemoApplication implements CommandLineRunner {

	@Autowired
	OrderService orderService;

	public static void main(String[] args) {
		SpringApplication.run(EcommerceDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(orderService.getOrderDetails("order-123").block());
	}
}
