package com.example.Task.Management.System;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/*
	TODO:: Add case-sensitive search
	       Add exact word search
	       Add categories
	       Add priority
	       Add finish task by time set
	       Add recurring task
	       Write controller test
	       Use JaCoCo or IDE coverage tools to check if I missed anything

 */

@SpringBootApplication
public class TaskManagementSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskManagementSystemApplication.class, args);
	}

}
