package com.ymt.controller;

import com.ymt.mapper.UserMapper;
import com.ymt.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

	@Autowired
	private UserMapper userMapper;
	
	@RequestMapping("/")
	public String start() {
		User user = new User();user.setName("dave");
		userMapper.insert(user);

		return "This is a Spring Boor project! It's work";
	}
}
