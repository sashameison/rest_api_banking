package com.narozhnyi.banking_app;

import org.springframework.boot.SpringApplication;

public class TestBankingAppApplication {

	public static void main(String[] args) {
		SpringApplication.from(BankingAppApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
