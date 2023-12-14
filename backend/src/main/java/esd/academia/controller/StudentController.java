package esd.academia.controller;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import esd.academia.model.Course;
import esd.academia.model.Student;
import esd.academia.service.StudentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/student")
@CrossOrigin("http://localhost:3000/")
public class StudentController {
	
	private StudentService studentService;	
	private static final Logger LOG = LogManager.getLogger(CourseController.class);

	public StudentController(StudentService studentService) {
		super();
		this.studentService = studentService;
	}
	
	@GetMapping("test")
	public ResponseEntity<String> test(){
		return new ResponseEntity<String>("HelloStudentController", HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/all")	
	public ResponseEntity<List<Student>> getAllStudent(){
		List<Student> res = null;
		// LOG.log(Level.INFO, "GetAllStdents Triggered");
		
		HttpStatus status = HttpStatus.ACCEPTED;
		
		try {
			List<Student> lFac = studentService.getStudents();	
			res = lFac;
		}
		catch (Exception e) {
			System.out.println("Error: "+e.getClass());
			System.out.println("Error: "+e.getMessage());
			System.out.println("Error: "+e.getStackTrace());
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}	
		
		log.trace("Logging at getAllStudent TRACE level");
		// log.debug("Logging at  DEBUG level");
		log.info("Logging at getAllStudent INFO level");
		log.warn("Logging at getAllStudent WARN level");
		log.error("Logging at getAllStudent ERROR level");
		log.debug("Found {} getAllStudent results", res.size());
		return new ResponseEntity<List<Student>>(res, status);		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Student> getStudentById(@PathVariable long id){
		Student res = null;
		HttpStatus status = HttpStatus.ACCEPTED;		
		try {
			Optional<Student> fac = studentService.getStudentById(id);
			if(fac.isPresent()) {
				res = fac.get();
			}
			else {
				status = HttpStatus.NOT_FOUND;
			}
		}
		catch (Exception e) {
			System.out.println("Error: "+e.getClass());
			System.out.println("Error: "+e.getMessage());
			status = HttpStatus.NOT_FOUND;
		}		
		log.trace("Logging at getStudentById TRACE level");
		// log.debug("Logging at  DEBUG level");
		log.info("Logging at getStudentById INFO level");
		log.warn("Logging at getStudentById WARN level");
		log.error("Logging at getStudentById ERROR level");
		log.debug("Found {} getStudentById results", res.getStudent_id());
		return new ResponseEntity<Student>(res, status);	
	}
	
	@PostMapping(path = "/add")
	public ResponseEntity<String> addStudent(@RequestBody Student student){
		System.out.println("Adding: "+student);
		// LOG.log(Level.INFO, "AddingStudent Triggered");
		String msg = "Successfully Added!";
		HttpStatus status = HttpStatus.ACCEPTED;
		try {
			Student newFac = studentService.saveStudent(student);
			System.out.println("Added: "+newFac);
		}
		catch (Exception e) {
			System.out.println("Error: "+e.getClass());
			System.out.println("Error: "+e.getMessage());
			msg = "Something went wrong - "+e.getMessage();
			status = HttpStatus.UNPROCESSABLE_ENTITY;
		}

		log.trace("Logging at addStudent TRACE level");
		// log.debug("Logging at  DEBUG level");
		log.info("Logging at addStudent INFO level");
		log.warn("Logging at addStudent WARN level");
		log.error("Logging at addStudent ERROR level");
		log.debug("Found {} addStudent results", msg);
		return new ResponseEntity<String>(msg, status);		
	}
	
	@PostMapping(path = "/addMultiple")
	public ResponseEntity<String> addMultipleStudent(@RequestBody List<Student> students){
		// LOG.log(Level.INFO, "AddMultipleStudents Triggered");
		students.forEach(stud ->{
			Student s = this.studentService.saveStudent(stud);
			System.out.println(s);			
		});

		log.trace("Logging at addMultipleStudent TRACE level");
		// log.debug("Logging at  DEBUG level");
		log.info("Logging at addMultipleStudent INFO level");
		log.warn("Logging at addMultipleStudent WARN level");
		log.error("Logging at addMultipleStudent ERROR level");
		log.debug("Found {} addMultipleStudent results", students.size());
		return new ResponseEntity<String>("Done",HttpStatus.OK);		
	}
	
	@PostMapping(path = "/update")
	public ResponseEntity<Student> updateStudent(@RequestBody Student student){
		// LOG.log(Level.INFO, "UpdateStdents Triggered");
		Student fac = new Student();		
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(student.getStudent_id()==0) {
			headers.add("issue", "Student ID is blank");
			return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .headers(headers)
	                .body(fac);				
		}
		if(student.getFirstname().isEmpty() || student.getEmail().isEmpty() || student.getRollnumber().isEmpty()) {
			headers.add("issue", "Name/Email/Roll is blank");
			return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .headers(headers)
	                .body(fac);						
		}
		System.out.println("Update Request: "+student);
		HttpStatus status;
		try {
			Student newFac = studentService.updateStudentById(student.getStudent_id(), student);
			if(newFac!=null) {
				System.out.println("Updated: "+newFac);	
				fac = newFac;
				status = HttpStatus.ACCEPTED;
			}
			else {
				System.out.println("Not updated.");	
				headers.add("issue", "DB Issue");
				return ResponseEntity
		                .status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .headers(headers)
		                .body(fac);				
			}			
		}
		catch (Exception e) {
			System.out.println("Error: "+e.getClass());
			System.out.println("Error: "+e.getMessage());
			headers.add("issue", "Could not update");
			status = HttpStatus.UNPROCESSABLE_ENTITY;
		}

		log.trace("Logging at updateStudent TRACE level");
		// log.debug("Logging at  DEBUG level");
		log.info("Logging at updateStudent INFO level");
		log.warn("Logging at updateStudent WARN level");
		log.error("Logging at updateStudent ERROR level");
		log.debug("Found {} updateStudent results", status);
		return new ResponseEntity<Student>(fac, status);		
	}


	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteStudent(@RequestParam Integer id) {
		System.out.println(id);
		String msg = "Success";
		try {
			boolean out = this.studentService.deleteStudentById(id) != null ? true : false;
			HttpStatus hs = HttpStatus.OK;
			if (out==false) { 
				msg = "Fail";
				hs = HttpStatus.INTERNAL_SERVER_ERROR;
			}
			return new ResponseEntity<String>(msg, hs);
		}
		catch (Exception e) {
			msg = "Error";
			System.out.println(e.getLocalizedMessage());
			System.out.println(e.getCause());
			System.out.println(e.getMessage());
			return new ResponseEntity<String>(msg, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


}
