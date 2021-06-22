package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPage;
    }


    //4. GROUP OWNER
    @GetMapping("/forGroupOwner/{groupId}")
    public Page<Student> getStudentListForGroupOwner(@PathVariable Integer groupId,
                                                     @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroupId(groupId, pageable);
        return studentPage;
    }

    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        boolean exists = studentRepository.existsById(id);
        if (exists) {
            studentRepository.deleteById(id);
            return "Student deleted";
        }
        return "Student not found";
    }

    @PostMapping("/add")
    public String addStudent(@RequestBody StudentDto studentDto) {


        Student student = new Student();
        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
        if (optionalAddress.isPresent()) {
            Address address = optionalAddress.get();
            student.setAddress(address);
        }
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        if (optionalGroup.isPresent()) {
            Group group = optionalGroup.get();
            student.setGroup(group);
        }

        List<Integer> subjectsId = studentDto.getSubjectsId();
        List<Subject> subjects = new ArrayList<>();
        for (Integer integer : subjectsId) {
            Subject subject = subjectRepository.findById(integer)
                    .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
            subjects.add(subject);
        }
        student.setSubjects(subjects);
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());

        studentRepository.save(student);
        return "Student saved";

    }

    @PostMapping("/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {


        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();

            Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
            if (optionalAddress.isPresent()) {
                Address address = optionalAddress.get();
                student.setAddress(address);
            }
            Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
            if (optionalGroup.isPresent()) {
                Group group = optionalGroup.get();
                student.setGroup(group);
            }

            List<Integer> subjectsId = studentDto.getSubjectsId();
            List<Subject> subjects = new ArrayList<>();
            for (Integer integer : subjectsId) {
                Subject subject = subjectRepository.findById(integer)
                        .orElseThrow(() -> new IllegalArgumentException("Subject not found"));
                subjects.add(subject);
            }
            student.setSubjects(subjects);
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());

            studentRepository.save(student);
            return "Student saved";
        }
        return "Student not found";
    }


}


