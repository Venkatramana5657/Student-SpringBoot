package com.student.service;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.student.dao.StudentDaoImpl;

@Service
public class StudentServiceImpl {

    @Autowired
    private StudentDaoImpl studentDao;
    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);

    public JSONObject getCourses() {
		return studentDao.getCourses();
	}

	public JSONObject getStudent(String reqJsonStr) {
		logger.info("::::getStudent::::"+reqJsonStr);
		JSONObject reqJson = new JSONObject(reqJsonStr);
		return studentDao.getStudent(reqJson);
	}
	
	public JSONObject addStudent(String reqJsonStr) {
		logger.info("::::addStudent::::"+reqJsonStr);
		JSONObject reqJson = new JSONObject(reqJsonStr);
		return studentDao.addStudent(reqJson);
	}
	
	public JSONObject updateStudent(String reqJsonStr) {
		logger.info("::::updateStudent::::"+reqJsonStr);
		JSONObject reqJson = new JSONObject(reqJsonStr);
		return studentDao.updateStudent(reqJson);
	}
	
	public JSONObject deleteStudent(String reqJsonStr) {
		logger.info("::::deleteStudent::::"+reqJsonStr);
		JSONObject reqJson = new JSONObject(reqJsonStr);
		return studentDao.deleteStudent(reqJson);
	}

}
