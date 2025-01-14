package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@PostMapping("/login")
	public ModelAndView login( User user, HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mv.addObject(user);
		mv.setViewName("redirect:/");
		request.getSession().setAttribute("user",user);
		return mv;
	}
	
	@GetMapping("/login")
	public ModelAndView login(){
		return new ModelAndView("login");
	}
}
