package com.ymt.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
	
	@RequestMapping("/")
	public String start() {
		return "This is a Spring Boor project! It's work";
	}
}
