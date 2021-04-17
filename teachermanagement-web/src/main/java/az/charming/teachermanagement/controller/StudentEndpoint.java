package az.charming.teachermanagement.controller;

import az.charming.teachermanagement.controller.dto.request.StudentRequestDto;
import az.charming.teachermanagement.controller.dto.response.StudentResponseDto;
import az.charming.teachermanagement.dto.StudentDto;
import az.charming.teachermanagement.service.fuctional.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/rest/students")
public class StudentEndpoint {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public List<StudentResponseDto> index(@RequestParam (required = false) String name,
                                  @RequestParam(required = false) String surname,
                                  @RequestParam(required = false) Integer age,
                                  @RequestParam(required = false) BigDecimal scholarship
    ){
        List<StudentDto> list= studentService.findAll(
                name,
                surname,
                age,
                scholarship
        );
        List<StudentResponseDto> result = new ArrayList<>();
        for(StudentDto st: list)
            result.add(StudentResponseDto.instance(st));
        return result;
    }

    @GetMapping(value = "/{id}")
    public StudentResponseDto getById(@PathVariable Integer id){
        return StudentResponseDto.instance(studentService.findById(id));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<StudentResponseDto> deleteById(@PathVariable Integer id){
        if(studentService.findById(id)==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(StudentResponseDto.instance(studentService.deleteById(id)));
    }

    @PostMapping
    public String add(@RequestBody StudentRequestDto studentRequestDto){
        studentService.save(studentRequestDto.toStudentDto());
        return "success";
    }

    @PutMapping
    public String update(@RequestBody StudentRequestDto studentRequestDto){
        studentService.save(studentRequestDto.toStudentDto());
        return "success";
    }
}
