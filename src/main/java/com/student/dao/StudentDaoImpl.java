package com.student.dao;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Repository
public class StudentDaoImpl {

	@Autowired
	DataSource dataSource; 

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	NamedParameterJdbcTemplate namedjdbcTemplate;
	
	private static final Logger logger = LoggerFactory.getLogger(StudentDaoImpl.class);

	public static String GET_STUDENT = "SELECT * FROM (SELECT DISTINCT A.STUDENT_ID,A.STUDENT_NAME,A.FIRST_NAME,A.LAST_NAME, "+
					" A.EMAIL,null AS COURSES FROM STUDENT A WHERE STUDENT_ID NOT IN (SELECT DISTINCT STUDENT_ID FROM STUDENT_COURSES) "+
					" UNION SELECT DISTINCT A.STUDENT_ID,A.STUDENT_NAME,A.FIRST_NAME,A.LAST_NAME, A.EMAIL, "+
					" (SELECT GROUP_CONCAT(COURSE_NAME) FROM COURSE WHERE COURSE_ID IN (SELECT COURSE_ID FROM STUDENT_COURSES "+ 
					" WHERE STUDENT_ID = A.STUDENT_ID)) AS COURSES FROM STUDENT A WHERE STUDENT_ID IN (SELECT DISTINCT STUDENT_ID "+ 
					" FROM STUDENT_COURSES)) TEMP WHERE 1=1 "; 
	public static String GET_COURSES = "SELECT COURSE_ID,COURSE_NAME FROM COURSE"; 




	public JSONObject getStudent(JSONObject reqJson) {
		String getStudents = GET_STUDENT;
		JSONObject resJson = new JSONObject("{'results':'[]'}");
		try {
			Map<String, Object> paramMap = new Gson().fromJson(reqJson.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
			if(reqJson.has("firstName") && !"".equals(reqJson.getString("firstName"))){
				getStudents += " AND FIRST_NAME = :firstName ";
			}
			if(reqJson.has("lastName") && !"".equals(reqJson.getString("lastName"))){
				getStudents += " AND LAST_NAME = :lastName ";
			}
			if(reqJson.has("email") && !"".equals(reqJson.getString("email"))){
				getStudents += " AND EMAIL = :email ";
			}
			if(reqJson.has("courseName") && !"".equals(reqJson.getString("courseName"))){
				getStudents += " AND COURSE_NAME = :courseName ";
			}
			getStudents += " ORDER BY STUDENT_ID";
			logger.info("::Get Final Query in getStudent ::::"+getStudents.toString());
			SqlRowSet rs = namedjdbcTemplate.queryForRowSet(getStudents,paramMap);
			JSONArray resultArray = new JSONArray();
			while (rs.next()) {
				JSONObject rowJson = new JSONObject();
				rowJson.put("studentId", rs.getString("STUDENT_ID"));
				rowJson.put("studentName", rs.getString("STUDENT_NAME"));
				rowJson.put("firstName", rs.getString("FIRST_NAME"));
				rowJson.put("lastName", rs.getString("LAST_NAME"));
				rowJson.put("email", rs.getString("EMAIL"));
				rowJson.put("courses", new JSONArray(rs.getString("COURSES") == null ? "[]" : "['"+rs.getString("COURSES").replaceAll(",", "','")+"']")); 
				resultArray.put(rowJson);
			}
			resJson.put("results", resultArray);
		} catch (Exception e) {
			logger.error("::Error in getStudent ::::"+e.getLocalizedMessage());
			return new JSONObject("{'results':'[]','exception:'"+e.getLocalizedMessage()+"'}");
		}
		return resJson;
	}




	public JSONObject getCourses() { 
		JSONObject resJson = new JSONObject("{'courses':'[]'}");
		try{
			SqlRowSet rs = jdbcTemplate.queryForRowSet(GET_COURSES);
			JSONArray resultArray = new JSONArray();
			while (rs.next()) {
				JSONObject rowJson = new JSONObject();
				rowJson.put("courseId",rs.getString("COURSE_ID"));
				rowJson.put("courseName",rs.getString("COURSE_NAME"));
				resultArray.put(rowJson);
			}
			resJson.put("courses", resultArray);
		} catch (Exception e) {
			logger.error("::Error in getCourses ::::"+e.getLocalizedMessage());
			return new JSONObject("{'courses':'[]','exception:'"+e.getLocalizedMessage()+"'}");
		}
		return resJson;
	}




	public JSONObject addStudent(JSONObject reqJson) {
		try {
			Map<String, Object> paramMap = new Gson().fromJson(reqJson.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
			namedjdbcTemplate.update("INSERT INTO STUDENT(STUDENT_NAME,FIRST_NAME,LAST_NAME,EMAIL) VALUES(:studentName,:firstName,:lastName,:email)", paramMap);
			if(reqJson.has("courses") && reqJson.getJSONArray("courses").length() > 0){
				JSONArray coursesArray = reqJson.getJSONArray("courses");
				for(int i=0;i<coursesArray.length();i++){
					jdbcTemplate.update("INSERT INTO STUDENT_COURSES VALUES ((SELECT LAST_INSERT_ID()),?)",new Object[]{ coursesArray.get(i) });
				}
			}
		} catch (Exception e) {
			logger.error("::Error in addStudent ::::"+e.getLocalizedMessage());
			return new JSONObject("{'result':'failed','exception:'"+e.getLocalizedMessage()+"'}");
		}
		return new JSONObject("{'result':'success'}");
	}
	
	public JSONObject updateStudent(JSONObject reqJson) {
		try{
			Map<String, Object> paramMap = new Gson().fromJson(reqJson.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
			namedjdbcTemplate.update("UPDATE STUDENT SET STUDENT_NAME=:studentName,FIRST_NAME=:firstName,LAST_NAME=:lastName,EMAIL=:email WHERE STUDENT_ID =:studentId", paramMap);
			namedjdbcTemplate.update("DELETE FROM STUDENT_COURSES WHERE STUDENT_ID =:studentId", paramMap);
			if(reqJson.has("courses") && reqJson.getJSONArray("courses").length() > 0){
				JSONArray coursesArray = reqJson.getJSONArray("courses");
				for(int i=0;i<coursesArray.length();i++){
					jdbcTemplate.update("INSERT INTO STUDENT_COURSES VALUES (?,?)",new Object[]{reqJson.get("studentId"), coursesArray.get(i) });
				}
			}
		} catch (Exception e) {
			logger.error("::Error in updateStudent ::::"+e.getLocalizedMessage());
			return new JSONObject("{'result':'failed','exception:'"+e.getLocalizedMessage()+"'}");
		}
		return new JSONObject("{'result':'success'}");
	}
	
	public JSONObject deleteStudent(JSONObject reqJson) {
		try{
			Map<String, Object> paramMap = new Gson().fromJson(reqJson.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
			namedjdbcTemplate.update("DELETE FROM STUDENT_COURSES WHERE STUDENT_ID =:studentId", paramMap);
			namedjdbcTemplate.update("DELETE FROM STUDENT WHERE STUDENT_ID =:studentId", paramMap);
		} catch (Exception e) {
			logger.error("::Error in deleteStudent ::::"+e.getLocalizedMessage());
			return new JSONObject("{'result':'failed','exception:'"+e.getLocalizedMessage()+"'}");
		}
		return new JSONObject("{'result':'success'}");
	}



}
