package com.registration.lecture.hhpluslectureregistration.domain.Service;

import com.registration.lecture.hhpluslectureregistration.domain.Repository.StudentRepository;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Lecture;
import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final StudentRepository studentRepository;

    // 학생 생성
    public Student createStudent(String studentName) {
        Student student = Student.builder()
                .studentName(studentName)
                .build();
        return studentRepository.save(student);
    }

    // 학생 존재 여부 검증
    public Student validateStudentExists(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생이 존재하지 않습니다."));
    }
}
