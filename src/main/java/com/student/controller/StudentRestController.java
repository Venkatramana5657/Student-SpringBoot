package com.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.student.service.StudentServiceImpl;

@RestController
@RequestMapping("/rest")
public class StudentRestController {

	@Autowired
	private StudentServiceImpl studentService;
	
	@GetMapping("/courses")
	@RequestMapping(value={"/getCourses","/courses"}, method = RequestMethod.GET)
	public String getCourses() {
		return studentService.getCourses().toString();
	}
	
	@GetMapping("/student")
	@RequestMapping(value = {"/getStudent"}, method = RequestMethod.GET)
	public String getStudent() {
		return studentService.getStudent("{}").toString(); 
	}
	
	@GetMapping("/student")
	@RequestMapping(value = {"/getStudent"}, method = RequestMethod.POST) 
	public String getStudent(@RequestBody String reqJson) {
		return studentService.getStudent(reqJson).toString();
	}
	
	@PutMapping("/student")
	@RequestMapping(value ={"/addStudent","/student"}, method = RequestMethod.PUT)
	public String addStudent(@RequestBody String reqJson) {
		return studentService.addStudent(reqJson).toString();
	}
	
	
	@PostMapping("/student")
	@RequestMapping(value = {"/updateStudent","/student"}, method = RequestMethod.POST)
	public String updateStudent(@RequestBody String reqJson) {
		return studentService.updateStudent(reqJson).toString();
	}
	
	@DeleteMapping("/student")
	@RequestMapping(value = {"/deleteStudent","/student"}, method = RequestMethod.DELETE)
	public String deleteStudent(@RequestBody String reqJson) {
		return studentService.deleteStudent(reqJson).toString();
	}
	
	
}