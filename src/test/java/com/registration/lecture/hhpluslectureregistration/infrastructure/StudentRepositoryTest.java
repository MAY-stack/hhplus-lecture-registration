package com.registration.lecture.hhpluslectureregistration.infrastructure;

import com.registration.lecture.hhpluslectureregistration.domain.entity.Student;
import com.registration.lecture.hhpluslectureregistration.infrastructure.RepositoryImpl.StudentRepositoryImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yml")
@Import(StudentRepositoryImpl.class)
class StudentRepositoryTest {

    @Autowired
    private StudentRepositoryImpl studentRepository;

    @Test
    void 학생을_저장_하면_자동으로_아이디가_생성되고_아이디로_검색할_수있다() {
        // Given
        Student newStudent = Student.builder()
                .studentName("김학생")
                .build();

        // When
        studentRepository.save(newStudent);

        // Then
        Student foundStudent = studentRepository.findById(1L)
                .orElseThrow(IllegalArgumentException::new);
        Assertions.assertEquals(foundStudent.getStudentName(), "김학생");
    }

    @Test
    void 여러_학생을_저장하고_각각_조회할_수_있다() {
        // Given
        Student student1 = Student.builder()
                .studentName("학생1")
                .build();

        Student student2 = Student.builder()
                .studentName("학생2")
                .build();

        // When
        studentRepository.save(student1);
        studentRepository.save(student2);

        // Then
        Student foundStudent1 = studentRepository.findById(1L)
                .orElseThrow(IllegalArgumentException::new);
        Student foundStudent2 = studentRepository.findById(2L)
                .orElseThrow(IllegalArgumentException::new);

        Assertions.assertEquals(foundStudent1.getStudentName(), "학생1");
        Assertions.assertEquals(foundStudent2.getStudentName(), "학생2");
    }

    @Test
    void 존재하지_않는_학생을_조회하면_빈값을_반환한다() {
        // Given
        Long nonExistentStudentId = 999L;

        // When
        Optional<Student> result = studentRepository.findById(nonExistentStudentId);

        // Then
        Assertions.assertTrue(result.isEmpty());
    }
}
